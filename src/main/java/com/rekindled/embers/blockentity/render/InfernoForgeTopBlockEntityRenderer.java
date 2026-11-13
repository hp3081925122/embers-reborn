package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.InfernoForgeTopBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class InfernoForgeTopBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<InfernoForgeTopBlockEntity> {

	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart hatch;

	public InfernoForgeTopBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(InfernoForgeTopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Level level = blockEntity.getLevel();
		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			Direction.Axis axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
			if (axis == Direction.Axis.Z)
				poseStack.mulPose(Axis.YP.rotationDegrees(90));

			double openTime = 7.0;
			double openAmount = 0.0D;
			double openTicks = level.getGameTime() + ((double) partialTick) - blockEntity.lastToggle;

			if (openTicks <= 0) {
				if (!blockEntity.open)
					openAmount = 1.0D;
			} else if (openTicks <= openTime) {
				if (blockEntity.open)
					openAmount = Math.sin(openTicks * Math.PI / (openTime * 2.0));
				else
					openAmount = 1.0D - Math.sin(openTicks * Math.PI / (openTime * 2.0));
			} else if (blockEntity.open)
				openAmount = 1.0D;

			poseStack.translate(-0.5D, -0.5D, -0.5D);
			if (hatch != null) {
				poseStack.translate(0, 0, -openAmount * 0.4375);
				model(poseStack, hatch, packedLight, packedOverlay);

				poseStack.translate(1.0D, 0, 1.0D + openAmount * 2.0 * 0.4375);
				poseStack.mulPose(Axis.YP.rotationDegrees(180));
				model(poseStack, hatch, packedLight, packedOverlay);
			}
			poseStack.popPose();
		}
	}
}
