package com.rekindled.embers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rekindled.embers.util.LegacyRemoveBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockEntity.class)
public abstract class BlockEntityRemoveMixin {
	@Inject(method = "preRemoveSideEffects", at = @At("HEAD"))
	private void embers$legacyRemove(BlockPos pos, BlockState state, CallbackInfo ci) {
		BlockEntity self = (BlockEntity) (Object) this;
		if (self.getLevel() != null && state.getBlock() instanceof LegacyRemoveBlock block)
			block.onRemoveLegacy(state, self.getLevel(), pos, Blocks.AIR.defaultBlockState(), false);
	}
}
