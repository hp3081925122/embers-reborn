package com.rekindled.embers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rekindled.embers.util.RegCtx;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PropertiesIdMixin {
	private PropertiesIdMixin() {}

	@Mixin(Item.Properties.class)
	public abstract static class ItemProps {
		@Inject(method = "<init>", at = @At("TAIL"))
		private void embers$setId(CallbackInfo ci) {
			if (RegCtx.currentItem() != null)
				((Item.Properties) (Object) this).setId(RegCtx.currentItem());
		}
	}

	@Mixin(BlockBehaviour.Properties.class)
	public abstract static class BlockProps {
		@Inject(method = "<init>", at = @At("TAIL"))
		private void embers$setId(CallbackInfo ci) {
			if (RegCtx.currentBlock() != null)
				((BlockBehaviour.Properties) (Object) this).setId(RegCtx.currentBlock());
		}
	}
}
