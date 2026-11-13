package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MechanicalPumpBottomBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalPumpBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<MechanicalPumpBottomBlockEntity> {

	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart pistonBottom;
	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart pistonTop;

	public MechanicalPumpBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(MechanicalPumpBottomBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockPos pos = blockEntity.getBlockPos().below();
		Level level = blockEntity.getLevel();

		double progress = blockEntity.totalProgress * partialTick + blockEntity.lastProgress * (1-partialTick);
		double amountUp = Math.abs(Math.sin((Math.PI * progress)/400.0));

		if (level.getBlockState(pos).isAir())
			packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));

		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
		if (blockState.getBlock() == RegistryManager.MECHANICAL_PUMP.get()) {
			poseStack.pushPose();
			poseStack.translate(0.0D, 1.0D + amountUp * 0.25D, 0.0D);

			if (pistonBottom != null)
				model(poseStack, pistonBottom, packedLight, packedOverlay);

			poseStack.translate(0.0D, amountUp * 0.25D, 0.0D);
			if (pistonTop != null)
				model(poseStack, pistonTop, packedLight, packedOverlay);
			poseStack.popPose();
		}
	}
}
