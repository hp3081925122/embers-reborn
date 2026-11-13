package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.blockentity.CinderPlinthBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CinderPlinthBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<CinderPlinthBlockEntity> {


	public CinderPlinthBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(CinderPlinthBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				float f2 = groundScaleY(stack, blockEntity.getLevel());
				poseStack.translate(0.5D, 0.4F * f2 + 0.9D, 0.5D);
				if (f2 > 0.4f)
					poseStack.scale(0.75f, 0.75f, 0.75f);
				poseStack.mulPose(Axis.YP.rotation(((float)EmbersClientEvents.ticks + partialTick) / 20.0F));
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}
		}
	}
}
