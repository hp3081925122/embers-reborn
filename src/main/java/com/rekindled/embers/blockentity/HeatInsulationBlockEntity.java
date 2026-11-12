package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.upgrade.HeatInsulationUpgrade;
import net.neoforged.neoforge.capabilities.BlockCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class HeatInsulationBlockEntity extends BlockEntity {

	public HeatInsulationUpgrade upgrade;

	public HeatInsulationBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.HEAT_INSULATION_ENTITY.get(), pPos, pBlockState);
		upgrade = new HeatInsulationUpgrade(this);
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}

}
