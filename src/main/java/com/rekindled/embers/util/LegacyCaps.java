package com.rekindled.embers.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

/**
 * Block capability lookups that hand back the legacy IItemHandler / IFluidHandler views,
 * so machine logic written against the old interfaces keeps working on 26.1.
 */
@SuppressWarnings("removal")
public final class LegacyCaps {
	public static final BlockCapability<ResourceHandler<ItemResource>, @Nullable Direction> ITEM_BLOCK = Capabilities.Item.BLOCK;
	public static final BlockCapability<ResourceHandler<FluidResource>, @Nullable Direction> FLUID_BLOCK = Capabilities.Fluid.BLOCK;

	private LegacyCaps() {}

	@Nullable
	public static IItemHandler item(@Nullable Level level, BlockPos pos, @Nullable Direction side) {
		if (level == null)
			return null;
		ResourceHandler<ItemResource> handler = level.getCapability(ITEM_BLOCK, pos, side);
		return handler == null ? null : IItemHandler.of(handler);
	}

	@Nullable
	public static IItemHandler item(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side) {
		ResourceHandler<ItemResource> handler = ITEM_BLOCK.getCapability(level, pos, state, blockEntity, side);
		return handler == null ? null : IItemHandler.of(handler);
	}

	@Nullable
	public static IFluidHandler fluid(@Nullable Level level, BlockPos pos, @Nullable Direction side) {
		if (level == null)
			return null;
		ResourceHandler<FluidResource> handler = level.getCapability(FLUID_BLOCK, pos, side);
		return handler == null ? null : IFluidHandler.of(handler);
	}
}
