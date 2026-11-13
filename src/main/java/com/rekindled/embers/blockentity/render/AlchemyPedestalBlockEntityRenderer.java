package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.AlchemyPedestalBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AlchemyPedestalBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<AlchemyPedestalBlockEntity> {

	public static double dist = 0.171875D;

	public AlchemyPedestalBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(AlchemyPedestalBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render item
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				poseStack.translate(0.5D, 0.5D, dist);
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.translate(0.0D, 0.0D, 1.0D - 2 * dist);
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);

				poseStack.translate(dist - 0.5D, 0.0D, dist - 0.5D);
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.translate(0.0D, 0.0D, 1.0D - 2 * dist);
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}
		}
	}
}
