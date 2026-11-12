package com.rekindled.embers.blockentity;

import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTransferBlockEntity extends FluidPipeBlockEntityBase {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public static final int PRIORITY_TRANSFER = -10;
	public FluidStack filterFluid = FluidStack.EMPTY;
	Random random = new Random();
	public boolean syncFilter = true;
	IFluidHandler outputSide;
	// outputHolder holder removed (LazyOptional not used in NeoForge 1.21.1)

	public FluidTransferBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FLUID_TRANSFER_ENTITY.get(), pPos, pBlockState);
		syncConnections = false;
		saveConnections = false;
	}

	@Override
	protected void initFluidTank() {
		tank = new FluidTank(getCapacity()) {
			@Override
			protected void onContentsChanged() {
				FluidTransferBlockEntity.this.setChanged();
			}

			@Override
			public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
				if (!filterFluid.isEmpty()) {
					if(resource != null) {
						if (!filterFluid.getComponents().isEmpty() ? net.neoforged.neoforge.fluids.FluidStack.isSameFluidSameComponents(resource, filterFluid) : resource.getFluid() == filterFluid.getFluid()) {
							return super.fill(resource, action);
						}
					}
					return 0;
				}
				return super.fill(resource, action);
			}
		};
		outputSide = Misc.makeRestrictedFluidHandler(tank, false, true);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("filter")) {
			filterFluid = FluidStack.parseOptional(provider, nbt.getCompound("filter"));
		}
	}

	@Override
	protected boolean requiresSync() {
		return syncFilter || super.requiresSync();
	}

	@Override
	protected void resetSync() {
		super.resetSync();
		syncFilter = false;
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		writeFilter(nbt);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		if (syncFilter)
			writeFilter(nbt);
		return nbt;
	}

	private void writeFilter(CompoundTag nbt) {
		nbt.put("filter", filterFluid.save(net.minecraft.core.RegistryAccess.EMPTY, new net.minecraft.nbt.CompoundTag()));
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidTransferBlockEntity blockEntity) {
		if (level instanceof ServerLevel && blockEntity.clogged && blockEntity.isAnySideUnclogged()) {
			Random posRand = new Random(pos.asLong());
			double angleA = posRand.nextDouble() * Math.PI * 2;
			double angleB = posRand.nextDouble() * Math.PI * 2;
			float xOffset = (float) (Math.cos(angleA) * Math.cos(angleB));
			float yOffset = (float) (Math.sin(angleA) * Math.cos(angleB));
			float zOffset = (float) Math.sin(angleB);
			float speed = 0.1f;
			float vx = xOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vy = yOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vz = zOffset * speed + posRand.nextFloat() * speed * 0.3f;
			((ServerLevel) level).sendParticles(new VaporParticleOptions(EmbersColors.VAPOR_ID, new Vec3(vx, vy, vz), 1.0f), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0, 0, 0, 1.0);
		}
		FluidPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public int getCapacity() {
		return 240;
	}

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_TRANSFER;
	}

	@Override
	public PipeConnection getConnection(Direction facing) {
		BlockState state = level.getBlockState(this.getBlockPos());
		return state.hasProperty(BlockStateProperties.FACING) && state.getValue(BlockStateProperties.FACING).getAxis() == facing.getAxis() ? PipeConnection.PIPE : PipeConnection.NONE;
	}

	@Override
	protected boolean isFrom(Direction facing) {
		return level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING) == facing;
	}

	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
	}
}
