package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.List;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import org.joml.Vector3f;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class StampBaseBlockEntity extends OpenTankBlockEntity implements IExtraCapabilityInformation {

	int ticksExisted = 0;
	public float renderOffset;
	int previousFluid;

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			StampBaseBlockEntity.this.setChanged();
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public StampBaseBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.STAMP_BASE_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(ConfigManager.STAMP_BASE_CAPACITY.get()) {
			@Override
			public void onContentsChanged() {
				StampBaseBlockEntity.this.setChanged();
			}

			@Override
			public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
				if (Misc.isGaseousFluid(resource)) {
					StampBaseBlockEntity.this.setEscapedFluid(resource);
					return resource.getAmount();
				}
				int filled = super.fill(resource, action);
				return filled;
			}
		};
	}

	@Override
	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditionalNbt(nbt, provider);
		inventory.deserializeNBT(provider, nbt.getCompoundOrEmpty("inventory"));
	}

	@Override
	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditionalNbt(nbt, provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public int getCapacity(){
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, StampBaseBlockEntity blockEntity) {
		blockEntity.ticksExisted++;

		//I know I'm supposed to use onLoad for stuff on the first tick but the tank isn't synced to the client yet when that happens
		if (blockEntity.ticksExisted == 1)
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
		if (blockEntity.tank.getFluidAmount() != blockEntity.previousFluid) {
			blockEntity.renderOffset = blockEntity.renderOffset + blockEntity.tank.getFluidAmount() - blockEntity.previousFluid;
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
		}

		if (blockEntity.shouldEmitParticles())
			blockEntity.updateEscapeParticles();
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		// holder.invalidate() removed
	}

	@SuppressWarnings("resource")
	@Override
	protected void updateEscapeParticles() {
		Vector3f color = IClientFluidTypeExtensions.of(lastEscaped.getFluid().getFluidType()).modifyFogColor(Minecraft.getInstance().gameRenderer.getMainCamera(), 0, (ClientLevel) this.level, 6, 0, new Vector3f(1, 1, 1));
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			float xOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			float yOffset = 0.9f;
			float zOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			level.addParticle(new VaporParticleOptions(color, 2.0f), worldPosition.getX() + xOffset, worldPosition.getY() + yOffset, worldPosition.getZ() + zOffset, 0, 1 / 5f, 0);
		}
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.FLUID_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.FLUID_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", Component.translatable(Embers.MODID + ".tooltip.goggles.fluid.metal")));
	}
}
