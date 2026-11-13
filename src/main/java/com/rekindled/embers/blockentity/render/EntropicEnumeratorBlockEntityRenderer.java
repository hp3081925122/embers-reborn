package com.rekindled.embers.blockentity.render;

import org.joml.Quaterniond;
import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.blockentity.EntropicEnumeratorBlockEntity;
import com.rekindled.embers.blockentity.EntropicEnumeratorBlockEntity.Cubie;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntropicEnumeratorBlockEntityRenderer extends com.rekindled.embers.render.LegacyBlockEntityRenderer<EntropicEnumeratorBlockEntity> {

	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart[][][] cubies = new net.minecraft.client.renderer.block.dispatch.BlockStateModelPart[2][2][2];
	public static Quaternionf quat = new Quaternionf();

	public EntropicEnumeratorBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
	}

	@Override
	public void render(EntropicEnumeratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Level level = blockEntity.getLevel();
		BlockState blockState = level.getBlockState(blockEntity.getBlockPos());

		float turnTicks = (level.getGameTime() - blockEntity.nextMoveTime) + partialTick;

		poseStack.translate(0.5, 0.5, 0.5);
		if (turnTicks > 0 && blockEntity.moveQueue.length > 0) {
			int currentMove = blockEntity.moveQueue.length - 1;

			for (int i = 0; i < blockEntity.moveQueue.length - 1; i++) {
				turnTicks -= blockEntity.offsetQueue[i];
				if (turnTicks < (float) blockEntity.offsetQueue[i + 1]) {
					currentMove = i;
					break;
				}
			}
			if (currentMove == blockEntity.moveQueue.length - 1)
				turnTicks -= blockEntity.offsetQueue[blockEntity.moveQueue.length - 1];

			float turnAmount;
			if (blockEntity.solving) {
				turnAmount = Math.min(2.0f * turnTicks / (float) blockEntity.moveQueue[currentMove].length, 1.0f);
			} else {
				turnAmount = Math.min(turnTicks / (float) blockEntity.moveQueue[currentMove].length, 1.0f);
			}

			if (currentMove != blockEntity.previousMove) {
				if (blockEntity.previousMove + 1 == currentMove) {
					blockEntity.moveQueue[blockEntity.previousMove].makeMove(blockEntity.visualCube);
				} else {
					for (int x = 0; x < 2; x++) {
						for (int y = 0; y < 2; y++) {
							for (int z = 0; z < 2; z++) {
								blockEntity.visualCube[x][y][z] = new Cubie(blockEntity.cube[x][y][z].basePosition, new Quaterniond(blockEntity.cube[x][y][z].rotation));
							}
						}
					}
					for (int i = 0; i < currentMove; i++) {
						blockEntity.moveQueue[i].makeMove(blockEntity.visualCube);
					}
				}
				blockEntity.previousMove = currentMove;
			}

			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						poseStack.pushPose();
						poseStack.mulPose(blockEntity.moveQueue[currentMove].makePartialMove(quat.set(blockEntity.visualCube[x][y][z].rotation), blockEntity.visualCube[x][y][z].getPos(), turnAmount));
						poseStack.translate(-0.5,- 0.5, -0.5);
						if (cubies[x][y][z] != null)
							model(poseStack, cubies[x][y][z], packedLight, packedOverlay);
						poseStack.popPose();
					}
				}
			}
		} else {
			for (int x = 0; x < 2; x++) {
				for (int y = 0; y < 2; y++) {
					for (int z = 0; z < 2; z++) {
						poseStack.pushPose();
						poseStack.mulPose(quat.set(blockEntity.cube[x][y][z].rotation));
						poseStack.translate(-0.5,- 0.5, -0.5);
						if (cubies[x][y][z] != null)
							model(poseStack, cubies[x][y][z], packedLight, packedOverlay);
						poseStack.popPose();
					}
				}
			}
		}
	}
}
