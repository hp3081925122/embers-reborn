package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.upgrade.ClockworkAttenuatorUpgrade;
import net.neoforged.neoforge.capabilities.BlockCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ClockworkAttenuatorBlockEntity extends BlockEntity {

	public ClockworkAttenuatorUpgrade upgrade;
	public double activeSpeed = 0, inactiveSpeed = 1;

	public double[] validSpeeds = new double[]{0.0, 0.0625, 0.125, 0.25, 0.5, 1.0};

	public ClockworkAttenuatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CLOCKWORK_ATTENUATOR_ENTITY.get(), pPos, pBlockState);
		upgrade = new ClockworkAttenuatorUpgrade(this);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		activeSpeed = nbt.getDouble("active_speed");
		inactiveSpeed = nbt.getDouble("inactive_speed");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.putDouble("active_speed", activeSpeed);
		nbt.putDouble("inactive_speed", inactiveSpeed);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putDouble("active_speed", activeSpeed);
		nbt.putDouble("inactive_speed", inactiveSpeed);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	public double getSpeed() {
		return level.hasNeighborSignal(worldPosition) ? activeSpeed : inactiveSpeed;
	}

	public double getNext(double current) {
		for (int i = 0; i < validSpeeds.length - 1; i++) {
			double a = validSpeeds[i];
			double b = validSpeeds[i + 1];

			if (b > current && a <= current)
				return b;
		}
		return current;
	}

	public double getPrevious(double current) {
		for (int i = 0; i < validSpeeds.length - 1; i++) {
			double a = validSpeeds[i];
			double b = validSpeeds[i + 1];

			if (b >= current && a < current)
				return a;
		}
		return current;
	}

	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		upgrade.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}
}
