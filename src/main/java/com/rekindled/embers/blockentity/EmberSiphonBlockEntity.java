package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.upgrade.EmberSiphonUpgrade;
import net.neoforged.neoforge.capabilities.BlockCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EmberSiphonBlockEntity extends BlockEntity {

	public EmberSiphonUpgrade upgrade;

	public EmberSiphonBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_SIPHON_ENTITY.get(), pPos, pBlockState);
		upgrade = new EmberSiphonUpgrade(this);
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}

}
