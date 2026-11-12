package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.recipe.IGaseousFuelRecipe;
import com.rekindled.embers.upgrade.WildfireStirlingUpgrade;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class WildfireStirlingBlockEntity extends BlockEntity implements ISoundController, IExtraCapabilityInformation {

	public static final int SOUND_OFF = 1;
	public static final int SOUND_ON = 2;
	public static final int[] SOUND_IDS = new int[]{SOUND_OFF,SOUND_ON};

	public int activeTicks = 0;
	public int burnTime = 0;
	public WildfireStirlingUpgrade upgrade;
	public FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME * 4) {
		@Override
		public void onContentsChanged() {
			WildfireStirlingBlockEntity.this.setChanged();
		}
	};
	private static Random random = new Random();
	public IGaseousFuelRecipe cachedRecipe = null;

	HashSet<Integer> soundsPlaying = new HashSet<>();
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public WildfireStirlingBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.WILDFIRE_STIRLING_ENTITY.get(), pPos, pBlockState);
		upgrade = new WildfireStirlingUpgrade(this);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		tank.readFromNBT(provider, nbt.getCompound("tank"));
		activeTicks = nbt.getInt("active");
		burnTime = nbt.getInt("burnTime");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("tank", tank.writeToNBT(provider, new net.minecraft.nbt.CompoundTag()));
		nbt.putInt("active", activeTicks);
		nbt.putInt("burnTime", burnTime);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.put("tank", tank.writeToNBT(provider, new net.minecraft.nbt.CompoundTag()));
		nbt.putInt("active", activeTicks);
		nbt.putInt("burnTime", burnTime);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public void setActive(int ticks) {
		activeTicks = Math.max(ticks, activeTicks);
		setChanged();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, WildfireStirlingBlockEntity blockEntity) {
		blockEntity.activeTicks--;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, WildfireStirlingBlockEntity blockEntity) {
		blockEntity.handleSound();
		blockEntity.activeTicks--;

		if (blockEntity.activeTicks > 0 && state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING).getOpposite();
			float frontoffset = -0.6f;
			float yoffset = 0.2f;
			float wideoffset = 0.5f;
			float breadthoffset = 0.4f;
			Vec3 frontOffset = new Vec3(0.5 - facing.getNormal().getX() * frontoffset, 0.5 - facing.getNormal().getY() * frontoffset, 0.5 - facing.getNormal().getZ() * frontoffset);
			Vec3 baseOffset = new Vec3(0.5 - facing.getNormal().getX() * yoffset, 0.5 - facing.getNormal().getY() * yoffset, 0.5 - facing.getNormal().getZ() * yoffset);
			Direction[] planars;
			switch (facing.getAxis()) {
			case X:
				planars = new Direction[] {Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH}; break;
			case Y:
				planars = new Direction[] {Direction.EAST,Direction.WEST,Direction.NORTH,Direction.SOUTH}; break;
			case Z:
				planars = new Direction[] {Direction.DOWN,Direction.UP,Direction.EAST,Direction.WEST}; break;
			default:
				planars = null; break;
			}
			for (Direction planar : planars) {
				BlockState sideState = level.getBlockState(pos.relative(planar));
				if (!sideState.getFaceOcclusionShape(level, pos.relative(planar), planar.getOpposite()).isEmpty())
					continue;
				Direction cross = facing.getClockWise(planar.getAxis());
				float x1 = pos.getX() + (float) baseOffset.x + planar.getNormal().getX() * wideoffset;
				float y1 = pos.getY() + (float) baseOffset.y + planar.getNormal().getY() * wideoffset;
				float z1 = pos.getZ() + (float) baseOffset.z + planar.getNormal().getZ() * wideoffset;
				float x2 = pos.getX() + (float) frontOffset.x + planar.getNormal().getX() * wideoffset + cross.getNormal().getX() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				float y2 = pos.getY() + (float) frontOffset.y + planar.getNormal().getY() * wideoffset + cross.getNormal().getY() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				float z2 = pos.getZ() + (float) frontOffset.z + planar.getNormal().getZ() * wideoffset + cross.getNormal().getZ() * (random.nextFloat()-0.5f) * 2 * breadthoffset;
				int lifetime = 24 + random.nextInt(8);
				//float motionx = facing.getNormal().getX() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				//float motiony = facing.getNormal().getY() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				//float motionz = facing.getNormal().getZ() * (1.0f/lifetime) - 0.01f + random.nextFloat() * 0.02f;
				float motionx = (x2 - x1) / lifetime;
				float motiony = (y2 - y1) / lifetime;
				float motionz = (z2 - z1) / lifetime;

				level.addParticle(new VaporParticleOptions(EmbersColors.EMBER_ID, new Vec3(motionx, motiony, motionz), lifetime / 16.0f), x1, y1, z1, 0, 0, 0);
			}
			float x = pos.getX() + (float) frontOffset.x;
			float y = pos.getY() + (float) frontOffset.y;
			float z = pos.getZ() + (float) frontOffset.z;
			int lifetime = 16 + random.nextInt(16);
			float motionx = (Math.abs(facing.getNormal().getX()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;
			float motiony = (Math.abs(facing.getNormal().getY()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;
			float motionz = (Math.abs(facing.getNormal().getZ()) - 1) * (random.nextFloat()-0.5f) * 2 * wideoffset / lifetime;

			level.addParticle(new VaporParticleOptions(EmbersColors.EMBER_ID, new Vec3(motionx, motiony, motionz), lifetime / 16.0f), x, y, z, 0, 0, 0);
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
		// holder.invalidate() removed
		upgrade.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public void playSound(int id) {
		float soundX = (float) worldPosition.getX() + 0.5f;
		float soundY = (float) worldPosition.getY() + 0.5f;
		float soundZ = (float) worldPosition.getZ() + 0.5f;
		switch (id) {
		case SOUND_ON:
			EmbersSounds.playMachineSound(this, SOUND_ON, EmbersSounds.WILDFIRE_STIRLING_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			level.playLocalSound(soundX, soundY, soundZ, EmbersSounds.WILDFIRE_STIRLING_START.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
			break;
		case SOUND_OFF:
			EmbersSounds.playMachineSound(this, SOUND_OFF, EmbersSounds.WILDFIRE_STIRLING_LOOP_READY.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		if (id == SOUND_ON) {
			level.playLocalSound(worldPosition, EmbersSounds.WILDFIRE_STIRLING_STOP.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		}
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		boolean isWorking = activeTicks > 0;

		switch (id)
		{
		case SOUND_OFF: return !isWorking && tank.getFluidAmount() > 0;
		case SOUND_ON: return isWorking;
		default: return false;
		}
	}

	@Override
	public float getCurrentVolume(int id, float volume) {
		boolean isWorking = activeTicks > 0;

		switch (id)
		{
		case SOUND_OFF: return !isWorking ? 1.0f : 0.0f;
		case SOUND_ON: return isWorking ? 1.0f : 0.0f;
		default: return 0f;
		}
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.FLUID_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.FLUID_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", Component.translatable(Embers.MODID + ".tooltip.goggles.fluid.steam")));
	}
}
