package com.rekindled.embers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rekindled.embers.util.RegCtx;

import net.minecraft.world.item.Item;

/** Stamps the item id being registered (see RegCtx) onto every Item.Properties created during registration. */
@Mixin(Item.Properties.class)
public abstract class ItemPropertiesIdMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void embers$setId(CallbackInfo ci) {
		if (RegCtx.currentItem() != null)
			((Item.Properties) (Object) this).setId(RegCtx.currentItem());
	}
}
