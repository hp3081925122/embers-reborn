package com.rekindled.embers.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 26.1 split Block#onRemove into BlockEntity#preRemoveSideEffects (block entity still present) and
 * Block#affectNeighborsAfterRemoval (block entity already gone). Blocks keep their 1.21.1 onRemove logic here;
 * it runs from preRemoveSideEffects for blocks with a block entity (see BlockEntityRemoveMixin) and from
 * affectNeighborsAfterRemoval otherwise. Both hooks only fire when the block actually changed, so newState is
 * always a different block (air is passed in).
 */
public interface LegacyRemoveBlock {
	void onRemoveLegacy(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving);
}
