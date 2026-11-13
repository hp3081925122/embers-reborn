package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.AlchemyTabletBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AlchemyTabletBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<AlchemyTabletBlockEntity> {

	//public Identifier texture = Identifier.parse(Embers.MODID + ":textures/entity/alchemy_circle.png");

	public AlchemyTabletBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(AlchemyTabletBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render item
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
				poseStack.translate(0.5D, (double)(0.25F * f2) + 0.8D, 0.5D);
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}
			//render alchemy circle
			//due to transparency issues, the alchemy circle is now a particle
/*
			if (blockEntity.process != 0) {
				float processSign = (blockEntity.progress > 0) ? 1 : -1;
				if (blockEntity.process == 20) {
					processSign = 0;
				}

				RenderSystem.setShaderTexture(0, texture);
				RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
				RenderSystem.disableCull();
				//RenderSystem.enableTexture();


				RenderSystem.depthMask(false);
				//RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);


				Tesselator tesselator = Tesselator.getInstance();
				BufferBuilder bufferbuilder = tesselator.getBuilder();
				bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);


				//VertexConsumer buffer = bufferSource.getBuffer(EmbersRenderTypes.BEAM);

				float x = 0;//blockEntity.getBlockPos().getX();
				float y = 0;//blockEntity.getBlockPos().getY();
				float z = 0;//blockEntity.getBlockPos().getZ();

				float scale = (float) Math.sin((blockEntity.process + (partialTick*processSign)) * Math.PI / 40.0);
				poseStack.translate(0.5, 1.0001, 0.5);


				poseStack.mulPose(Axis.YP.rotationDegrees(partialTick + EmbersClientEvents.ticks % 360));
				poseStack.scale(scale, scale, scale);

				float r = 1.0f;
				float g = 0.25f;
				float b = 0.0625f;
				float a = scale / 2.0f;
				int lightx = 0xF000F0;
				int lighty = 0xF000F0;

				Matrix4f matrix4f = poseStack.last().pose();
				for (float i = 0; i < 8; i ++) {
				bufferbuilder.addVertex(matrix4f, -1, 0, -1).setColor(r, g, b, a).setUv(0, 0).setUv2(lightx, lighty);
				bufferbuilder.addVertex(matrix4f, -1, 0, 1).setColor(r, g, b, a).setUv(0, 1).setUv2(lightx, lighty);
				bufferbuilder.addVertex(matrix4f, 1, 0, 1).setColor(r, g, b, a).setUv(1, 1).setUv2(lightx, lighty);
				bufferbuilder.addVertex(matrix4f, 1, 0, -1).setColor(r, g, b, a).setUv(1, 0).setUv2(lightx, lighty);




				//RenderUtil.renderAlchemyCircle(buffer, matrix4f, x+0.5f, y+1.0f+(i/1000f), z+0.5f, 1.0f, 0.25f, 0.0625f, visualProcess/40.0f, 0.4f*visualProcess/10.0f, EmbersClientEvents.ticks+partialTick);
				}
				RenderSystem.depthMask(true);
				BufferUploader.drawWithShader(bufferbuilder.end());
				RenderSystem.enableCull();
			}*/
		}
	}
}
