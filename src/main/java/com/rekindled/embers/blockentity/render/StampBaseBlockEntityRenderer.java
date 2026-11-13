package com.rekindled.embers.blockentity.render;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.StampBaseBlockEntity;
import com.rekindled.embers.render.FluidCuboid;
import com.rekindled.embers.render.FluidRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class StampBaseBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<StampBaseBlockEntity> {


	FluidCuboid cube = new FluidCuboid(new Vector3f(4, 12, 4), new Vector3f(12, 15, 12), FluidCuboid.DEFAULT_FACES);

	public StampBaseBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(StampBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			//render item
			if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
				poseStack.pushPose();
				ItemStack stack = blockEntity.inventory.getStackInSlot(0);
				int seed = stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
				float f2 = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
				poseStack.translate(0.5D, (double)(0.25F * f2) + 0.75D, 0.5D);
				item(stack, ItemDisplayContext.GROUND, poseStack, packedLight, OverlayTexture.NO_OVERLAY, blockEntity.getLevel(), seed);
				poseStack.popPose();
			}

			//render fluid
			FluidStack fluidStack = blockEntity.getFluidStack();
			int capacity = blockEntity.getCapacity();
			if (!fluidStack.isEmpty() && capacity > 0) {
				float offset = blockEntity.renderOffset;
				if (offset > 1.2f || offset < -1.2f) {
					offset = offset - ((offset / 12f + 0.1f) * partialTick);
					blockEntity.renderOffset = offset;
				} else {
					blockEntity.renderOffset = 0;
				}
				FluidRenderer.renderScaledCuboid(poseStack, bufferSource, cube, fluidStack, offset, capacity, packedLight, packedOverlay, false);
			} else {
				blockEntity.renderOffset = 0;
			}
		}
	}
}
