package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.blockentity.ExcavationBucketsBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ExcavationBucketsBlockEntityRenderer implements BlockEntityRenderer<ExcavationBucketsBlockEntity> {

	public static BakedModel wheel;

	public ExcavationBucketsBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(ExcavationBucketsBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null && blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).hasProperty(BlockStateProperties.FACING)) {
			BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
			Direction facing = blockState.getValue(BlockStateProperties.FACING);
			BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
			float angle = blockEntity.angle;
			float lastAngle = blockEntity.lastAngle;
			RenderSystem.enableCull();
			poseStack.pushPose();
			poseStack.translate(0.5D, 0.5D, 0.5D);
			poseStack.mulPose(facing.getRotation());


			poseStack.mulPose(Axis.XP.rotationDegrees(partialTick * angle + (1 - partialTick) * lastAngle));

			poseStack.translate(-0.5D, -0.5D, -0.5D);
			if (wheel != null)
				blockrendererdispatcher.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, wheel, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

			poseStack.popPose();
		}
	}
}
