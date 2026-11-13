package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.EmberBoreBladeRenderEvent;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EmberBoreBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<EmberBoreBlockEntity> {

	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart blades;

	public EmberBoreBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

	}

	@Override
	public void render(EmberBoreBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		float angle = blockEntity.angle;
		float lastAngle = blockEntity.lastAngle;
		BlockPos pos = blockEntity.getBlockPos().below();
		Level level = blockEntity.getLevel();

		if (level.getBlockState(pos).isAir())
			packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));

		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
		if (blockState.getBlock() == RegistryManager.EMBER_BORE.get()) {
			poseStack.pushPose();
			poseStack.translate(0.5D, -0.5D, 0.5D);
			if (blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X) {
				poseStack.mulPose(Axis.ZP.rotationDegrees(partialTick * angle + (1 - partialTick) * lastAngle));
			} else {
				poseStack.mulPose(Axis.XP.rotationDegrees(partialTick * angle + (1 - partialTick) * lastAngle));
				poseStack.mulPose(Axis.YP.rotationDegrees(90));
			}
			poseStack.translate(-0.5D, -0.5D, -0.5D);
			if (blades != null)
				model(poseStack, blades, packedLight, packedOverlay);

			UpgradeUtil.throwEvent(blockEntity, new EmberBoreBladeRenderEvent(blockEntity, poseStack, blockState, bufferSource, packedLight, packedOverlay), blockEntity.upgrades);

			poseStack.popPose();
		}
	}
}
