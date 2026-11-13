package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.StamperBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class StamperBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<StamperBlockEntity> {

	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart arm;

	public StamperBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(StamperBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		if (blockState.getBlock() == RegistryManager.STAMPER.get()) {
			poseStack.pushPose();
			float magnitude = 0;
			if (!blockEntity.powered){
				if (blockEntity.prevPowered) {
					magnitude = 1.0f - partialTick;
				}
			} else {
				if (!blockEntity.prevPowered) {
					magnitude = partialTick;
				} else {
					magnitude = 1;
				}
			}
			poseStack.translate(0, -0.001D - magnitude, 0);
			if (arm != null)
				model(poseStack, arm, packedLight, packedOverlay);

			if (!blockEntity.stamp.getStackInSlot(0).isEmpty()) {
				ItemStack stack = blockEntity.stamp.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				poseStack.translate(0.5D, -0.0234375D, 0.5);
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
				poseStack.scale(0.75f, 0.75f, 0.75f);
				item(stack, ItemDisplayContext.FIXED, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
			}
			poseStack.popPose();
		}
	}
}
