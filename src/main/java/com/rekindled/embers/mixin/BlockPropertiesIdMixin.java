package com.rekindled.embers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rekindled.embers.util.RegCtx;

import net.minecraft.world.level.block.state.BlockBehaviour;

/** Stamps the block id being registered (see RegCtx) onto every BlockBehaviour.Properties created during registration. */
@Mixin(BlockBehaviour.Properties.class)
public abstract class BlockPropertiesIdMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void embers$setId(CallbackInfo ci) {
		if (RegCtx.currentBlock() != null)
			((BlockBehaviour.Properties) (Object) this).setId(RegCtx.currentBlock());
	}
}
