package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.power.ITargetable;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class EmberEmitterBlockEntity extends BlockEntity implements IEmberPacketProducer, ITargetable, IExtraCapabilityInformation {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			EmberEmitterBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return false;
		}
	};

	public static final double TRANSFER_RATE = 40.0;
	public static final double PULL_RATE = 10.0;

	public BlockPos target = null;
	public long ticksExisted = 0;
	public Random random = new Random();
	public int offset = random.nextInt(40);
	public HashSet<ChunkPos> trajectoryChunks = null;

	public EmberEmitterBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_EMITTER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(200);
		capability.setEmber(0);
	}

	public EmberEmitterBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("targetX")){
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
		capability.deserializeNBT(provider, nbt.getCompound("ember"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		if (target != null){
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		nbt.put("ember", capability.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		if (target != null){
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		return nbt;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
		if (trajectoryChunks == null)
			trajectoryChunks = new HashSet<ChunkPos>();
		Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING)));
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberEmitterBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockEntity attachedTile = level.getBlockEntity(pos.relative(facing, -1));
		if (blockEntity.ticksExisted % 5 == 0 && attachedTile != null) {
			IEmberCapability cap = (attachedTile.getLevel() != null ? attachedTile.getLevel().getCapability(EmbersCapabilities.EMBER_CAPABILITY, attachedTile.getBlockPos(), facing) : null);
			if (cap != null) {
				if (cap.getEmber() > 0 && blockEntity.capability.getEmber() < blockEntity.capability.getEmberCapacity()){
					double removed = cap.removeAmount(PULL_RATE, true);
					blockEntity.capability.addAmount(removed, true);
				}
			}
		}
		if ((blockEntity.ticksExisted + blockEntity.offset) % 20 == 0 && blockEntity.canSendBurst() && blockEntity.capability.getEmber() > PULL_RATE) {
			BlockEntity targetTile = level.getBlockEntity(blockEntity.target);
			if (targetTile instanceof IEmberPacketReceiver) {
				if (((IEmberPacketReceiver) targetTile).hasRoomFor(TRANSFER_RATE)) {
					EmberPacketEntity packet = RegistryManager.EMBER_PACKET.get().create(blockEntity.level);
					Vec3 velocity = getBurstVelocity(facing);
					packet.initCustom(pos, blockEntity.target, velocity.x, velocity.y, velocity.z, Math.min(TRANSFER_RATE, blockEntity.capability.getEmber()));
					blockEntity.capability.removeAmount(Math.min(TRANSFER_RATE, blockEntity.capability.getEmber()), true);
					blockEntity.level.addFreshEntity(packet);
					level.playSound(null, pos, EmbersSounds.EMBER_EMIT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
				}
			}
		}
	}

	public boolean canSendBurst() {
		if (level.hasNeighborSignal(worldPosition) && target != null && level.isLoaded(target) && !level.isClientSide) {
			if (trajectoryChunks == null) {
				trajectoryChunks = new HashSet<ChunkPos>();
				Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING)));
			}
			if (level instanceof ServerLevel serverLevel) {
				for (ChunkPos chunk : trajectoryChunks) {
					if (!serverLevel.isNaturalSpawningAllowed(chunk))
						return false;
				}
			}
			return true;
		}
		return false;
	}

	public static Vec3 getBurstVelocity(Direction facing) {
		switch(facing) {
		case DOWN:
			return new Vec3(0, -0.5, 0);
		case UP:
			return new Vec3(0, 0.5, 0);
		case NORTH:
			return new Vec3(0, -0.01, -0.5);
		case SOUTH:
			return new Vec3(0, -0.01, 0.5);
		case WEST:
			return new Vec3(-0.5, -0.01, 0);
		case EAST:
			return new Vec3(0.5, -0.01, 0);
		default:
			return Vec3.ZERO;
		}
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		capability.invalidate();
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		target = pos;
		this.setChanged();
	}

	@Override
	public Vec3 getEmittingDirection(Direction side) {
		return getBurstVelocity(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING));
	}

	@Override
	public BlockPos getTarget(Direction side) {
		BlockState state = level.getBlockState(worldPosition);
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if (side != facing)
				return null;
		}
		return target;
	}

	@Override
	public void addOtherDescription(List<Component> strings, Direction facing) {
		strings.add(Component.translatable(Embers.MODID + ".tooltip.goggles.redstone_signal"));
	}
}
