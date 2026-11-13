package com.rekindled.embers.blockentity.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.DawnstoneAnvilBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class DawnstoneAnvilBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<DawnstoneAnvilBlockEntity> {

	static Random random = new Random();

	public DawnstoneAnvilBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(DawnstoneAnvilBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			random.setSeed(blockEntity.getBlockPos().hashCode());
			//render items
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
				poseStack.translate(0.5D, (double)(0.25F * f2) + 0.8937D, 0.5D);
				poseStack.mulPose(Axis.YP.rotation(random.nextFloat((float) (Math.PI * 2.0f))));
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}
			if (!blockEntity.inventory.getStackInSlot(1).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(1);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
				poseStack.translate(0.5D, (double)(0.25F * f2) + 0.9245D, 0.5D);
				poseStack.mulPose(Axis.YP.rotation(random.nextFloat((float) (Math.PI * 2.0f))));
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}
		}
	}
}
