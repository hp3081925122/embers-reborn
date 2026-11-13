package com.rekindled.embers.blockentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.MithrilBlockEntity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MithrilBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<MithrilBlockEntity> {


	public MithrilBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(MithrilBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if (blockEntity != null) {
			ItemStack block = new ItemStack(blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).getBlock());
			int seed = block.isEmpty() ? 187 : Item.getId(block.getItem()) + block.getDamageValue();
			poseStack.translate(0.5, 0.5, 0.5);
			item(block, ItemDisplayContext.NONE, poseStack, packedLight, packedOverlay, blockEntity.getLevel(), seed);
		}
	}
}
