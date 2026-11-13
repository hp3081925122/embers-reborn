package com.rekindled.embers.blockentity;

import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ReservoirBlockEntity extends OpenTankBlockEntity {

	int ticksExisted = 0;
	public float renderOffset;
	int previousFluid;
	public int height = 0;
	public boolean capped = false;

	public ReservoirBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.RESERVOIR_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(Integer.MAX_VALUE) {
			@Override
			public void onContentsChanged() {
				ReservoirBlockEntity.this.setChanged();
			}

			@Override
			public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
				if (!capped && Misc.isGaseousFluid(resource)) {
					ReservoirBlockEntity.this.setEscapedFluid(resource);
					return resource.getAmount();
				}
				int filled = super.fill(resource, action);
				return filled;
			}
		};
	}

	public AABB getRenderBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(-1, 1, -1)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(2, 1 + height, 2)));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		saveAdditionalNbt(nbt, provider);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public int getCapacity() {
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
	}


	public void updateCapacity() {
		int capacity = 0;
		height = 0;
		boolean previouslyCapped = capped;
		capped = false;
		for (int i = 1; true; i++) {
			BlockState state = level.getBlockState(worldPosition.above(i));
			if (state.is(EmbersBlockTags.RESERVOIR_CAP)) {
				capped = true;
			}
			if (state.is(EmbersBlockTags.RESERVOIR_EXPANSION)) {
				capacity += ConfigManager.RESERVOIR_CAPACITY.get();
				height++;
			} else {
				break;
			}
			if (state.is(EmbersBlockTags.RESERVOIR_END)) {
				break;
			}
		}
		if (previouslyCapped && !capped && Misc.isGaseousFluid(tank.getFluid())) {
			ReservoirBlockEntity.this.setEscapedFluid(tank.drain(tank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE));
			this.setChanged();
		}
		if (tank.getCapacity() != capacity) {
			this.tank.setCapacity(capacity);
			int amount = tank.getFluidAmount();
			if (amount > capacity) {
				tank.drain(amount - capacity, IFluidHandler.FluidAction.EXECUTE);
			}
			this.setChanged();
		}
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, ReservoirBlockEntity blockEntity) {
		commonTick(level, pos, state, blockEntity);

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

	public static void commonTick(Level level, BlockPos pos, BlockState state, ReservoirBlockEntity blockEntity) {
		blockEntity.ticksExisted++;

		if (blockEntity.ticksExisted % 20 == 0)
			blockEntity.updateCapacity();
	}

	@SuppressWarnings("resource")
	@Override
	protected void updateEscapeParticles() {
		Vector3f color = com.rekindled.embers.fluidtypes.EmbersFluidClient.fogColor(lastEscaped.getFluid());
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			float xOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			float yOffset = height + 0.9f;
			float zOffset = 0.5f + (random.nextFloat() - 0.5f) * 2 * 0.2f;
			level.addParticle(new VaporParticleOptions(color, 2.0f), worldPosition.getX() + xOffset, worldPosition.getY() + yOffset, worldPosition.getZ() + zOffset, 0, 1 / 5f, 0);
		}
	}
}
