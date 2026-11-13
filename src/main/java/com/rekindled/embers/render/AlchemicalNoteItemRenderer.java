package com.rekindled.embers.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.item.AlchemyHintItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Alchemical note held like a map, framed, or as a GUI icon with its result on top.
 * 26.1 has no per-item BEWLR, so this is a special item model ("embers_reborn:alchemical_note").
 * The item definition picks the mode per display context; the drawing code is the 1.20.1 renderer,
 * with nested items resolved into ItemStackRenderStates when the note is extracted.
 */
public class AlchemicalNoteItemRenderer implements SpecialModelRenderer<AlchemicalNoteItemRenderer.NoteData> {

	public enum Mode implements StringRepresentable {
		FIRST_PERSON_RIGHT("first_person_right"),
		FIRST_PERSON_LEFT("first_person_left"),
		FIXED("fixed"),
		GUI("gui");

		public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
		private final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public record NoteData(ItemStack stack, ItemStackRenderState result, List<ItemStackRenderState> aspects, List<ItemStackRenderState> inputs) {}

	private final Mode mode;
	private final Minecraft minecraft = Minecraft.getInstance();

	public AlchemicalNoteItemRenderer(Mode mode) {
		this.mode = mode;
	}

	private ItemStackRenderState resolve(ItemStack stack) {
		ItemStackRenderState state = new ItemStackRenderState();
		minecraft.getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GUI, minecraft.level, null, 0);
		return state;
	}

	@Override
	public @Nullable NoteData extractArgument(ItemStack stack) {
		List<ItemStackRenderState> aspects = new ArrayList<>();
		List<ItemStackRenderState> inputs = new ArrayList<>();
		if (mode != Mode.GUI) {
			for (ItemStack aspect : AlchemyHintItem.getAspects(stack))
				aspects.add(resolve(aspect));
			for (ItemStack input : AlchemyHintItem.getInputs(stack))
				inputs.add(resolve(input));
		}
		return new NoteData(stack, resolve(AlchemyHintItem.getResult(stack)), aspects, inputs);
	}

	@Override
	public void submit(@Nullable NoteData data, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor) {
		if (data == null || minecraft.player == null)
			return;
		switch (mode) {
		case FIRST_PERSON_RIGHT, FIRST_PERSON_LEFT -> {
			boolean flag = (mode == Mode.FIRST_PERSON_RIGHT) == (minecraft.player.getMainArm() == HumanoidArm.RIGHT);
			HumanoidArm humanoidarm = flag ? minecraft.player.getMainArm() : minecraft.player.getMainArm().getOpposite();
			InteractionHand interactionhand = MoreObjects.firstNonNull(minecraft.player.swingingArm, InteractionHand.MAIN_HAND);
			float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
			ItemInHandRenderer hands = minecraft.gameRenderer.itemInHandRenderer;
			float pitch = Mth.lerp(partialTick, minecraft.player.xRotO, minecraft.player.getXRot());
			float swingProgress = interactionhand == InteractionHand.MAIN_HAND ? minecraft.player.getAttackAnim(partialTick) : 0.0F;
			float equippedProgress = interactionhand == InteractionHand.MAIN_HAND ? 1.0F - Mth.lerp(partialTick, hands.oMainHandHeight, hands.mainHandHeight) : 0.0F;

			poseStack.pushPose();
			poseStack.scale(64.0F, 64.0F, 64.0F);
			if (flag && (minecraft.player.getOffhandItem().isEmpty() || ItemStack.isSameItemSameComponents(minecraft.player.getOffhandItem(), data.stack()))) {
				renderTwoHandedNote(hands, poseStack, collector, packedLight, packedOverlay, outlineColor, pitch, equippedProgress, swingProgress, data);
			} else {
				renderOneHandedNote(hands, poseStack, collector, packedLight, packedOverlay, outlineColor, equippedProgress, humanoidarm, swingProgress, data);
			}
			poseStack.popPose();
		}
		case FIXED -> {
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.49);
			poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
			poseStack.scale(4.0F, 4.0F, 4.0F);
			renderNote(poseStack, collector, packedLight, packedOverlay, outlineColor, data);
			poseStack.popPose();
		}
		case GUI -> {
			// the note icon itself comes from the composite item model; this only adds the result on top
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.0);
			poseStack.scale(0.5F, 0.5F, 0.5F);
			poseStack.translate(0.5, -0.5, 0.3);
			data.result().submit(poseStack, collector, packedLight, packedOverlay, outlineColor);
			poseStack.popPose();
		}
		}
	}

	private void renderOneHandedNote(ItemInHandRenderer hands, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, int outlineColor, float equippedProgress, HumanoidArm hand, float swingProgress, NoteData data) {
		float f = hand == HumanoidArm.RIGHT ? 1.0F : -1.0F;
		poseStack.translate(f * 0.125F, -0.125F, 0.0F);
		if (!minecraft.player.isInvisible()) {
			poseStack.pushPose();
			poseStack.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
			hands.renderPlayerArm(poseStack, collector, packedLight, equippedProgress, swingProgress, hand);
			poseStack.popPose();
		}
		poseStack.pushPose();
		poseStack.translate(f * 0.51F, -0.08F + equippedProgress * -1.2F, -0.75F);
		float f1 = Mth.sqrt(swingProgress);
		float f2 = Mth.sin(f1 * (float)Math.PI);
		float f3 = -0.5F * f2;
		float f4 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
		float f5 = -0.3F * Mth.sin(swingProgress * (float)Math.PI);
		poseStack.translate(f * f3, f4 - 0.3F * f2, f5);
		poseStack.mulPose(Axis.XP.rotationDegrees(f2 * -45.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(f * f2 * -30.0F));
		renderNote(poseStack, collector, packedLight, packedOverlay, outlineColor, data);
		poseStack.popPose();
	}

	private void renderTwoHandedNote(ItemInHandRenderer hands, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, int outlineColor, float pitch, float equippedProgress, float swingProgress, NoteData data) {
		float f = Mth.sqrt(swingProgress);
		float f1 = -0.2F * Mth.sin(swingProgress * (float)Math.PI);
		float f2 = -0.4F * Mth.sin(f * (float)Math.PI);
		poseStack.translate(0.0F, -f1 / 2.0F, f2);
		float f3 = hands.calculateMapTilt(pitch);
		poseStack.translate(0.0F, 0.04F + equippedProgress * -1.2F + f3 * -0.5F, -0.72F);
		poseStack.mulPose(Axis.XP.rotationDegrees(f3 * -85.0F));
		if (!minecraft.player.isInvisible()) {
			poseStack.pushPose();
			poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
			hands.renderMapHand(poseStack, collector, packedLight, HumanoidArm.RIGHT);
			hands.renderMapHand(poseStack, collector, packedLight, HumanoidArm.LEFT);
			poseStack.popPose();
		}
		float f4 = Mth.sin(f * (float)Math.PI);
		poseStack.mulPose(Axis.XP.rotationDegrees(f4 * 20.0F));
		poseStack.scale(2.0F, 2.0F, 2.0F);
		renderNote(poseStack, collector, packedLight, packedOverlay, outlineColor, data);
	}

	private static void quad(PoseStack poseStack, SubmitNodeCollector collector, net.minecraft.client.renderer.rendertype.RenderType type, float x0, float y0, float x1, float y1, int packedLight, int packedOverlay) {
		collector.submitCustomGeometry(poseStack, type, (pose, consumer) -> {
			vertex(consumer, pose, x0, y1, 1.0F, 0.0F, packedLight, packedOverlay);
			vertex(consumer, pose, x1, y1, 0.0F, 0.0F, packedLight, packedOverlay);
			vertex(consumer, pose, x1, y0, 0.0F, 1.0F, packedLight, packedOverlay);
			vertex(consumer, pose, x0, y0, 1.0F, 1.0F, packedLight, packedOverlay);
		});
	}

	private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float u, float v, int packedLight, int packedOverlay) {
		consumer.addVertex(pose.pose(), x, y, 0).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0.5f, -1);
	}

	public void renderNote(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, int packedOverlay, int outlineColor, NoteData data) {
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(0.38F, 0.38F, 0.38F);
		poseStack.translate(-0.5F, -0.5F, 0.0F);
		poseStack.scale(0.015625F, 0.015625F, 0.015625F);
		quad(poseStack, collector, EmbersRenderTypes.NOTE_BACKGROUND, 0, 0, 64, 64, packedLight, packedOverlay);

		if (data.inputs().size() == data.aspects().size()) {
			for (int j = 0; j < data.inputs().size(); ++j) {
				int pedestal = 10;
				int margin = 4;
				int width = (64 - pedestal - 2 * margin) / data.inputs().size();
				poseStack.translate(0, 0, -0.01);
				renderPedestal(poseStack, margin + width * j + width / 2 + 1, 10, collector, packedLight, packedOverlay, outlineColor, data.aspects().get(j), data.inputs().get(j));
			}
		}

		poseStack.translate(32, 44, -0.2);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.last().pose().scale(16, 16, 1); //scaling the posestack normally messes up the normal
		poseStack.last().normal().rotate(Axis.XP.rotationDegrees(-22.5F)); //rotate the normal slightly to make the items appear brighter
		data.result().submit(poseStack, collector, packedLight, packedOverlay, outlineColor);
	}

	public void renderPedestal(PoseStack poseStack, int x, int y, SubmitNodeCollector collector, int combinedLight, int packedOverlay, int outlineColor, ItemStackRenderState aspect, ItemStackRenderState ingredient) {
		quad(poseStack, collector, EmbersRenderTypes.NOTE_PEDESTAL, x, y, x + 10, y + 14, combinedLight, packedOverlay);
		poseStack.pushPose();
		poseStack.translate(x + 5, y + 7, -0.2);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.last().pose().scale(8, 8, 1); //scaling the posestack normally messes up the normal
		poseStack.last().normal().rotate(Axis.XP.rotationDegrees(-22.5F)); //rotate the normal slightly to make the items appear brighter
		aspect.submit(poseStack, collector, combinedLight, packedOverlay, outlineColor);
		poseStack.translate(0, 1.25, 0);
		ingredient.submit(poseStack, collector, combinedLight, packedOverlay, outlineColor);
		poseStack.popPose();
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output) {
		output.accept(new Vector3f(0, 0, 0));
		output.accept(new Vector3f(1, 1, 1));
	}

	public record Unbaked(Mode mode) implements SpecialModelRenderer.Unbaked<NoteData> {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Mode.CODEC.fieldOf("mode").forGetter(Unbaked::mode)
		).apply(inst, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public AlchemicalNoteItemRenderer bake(SpecialModelRenderer.BakingContext context) {
			return new AlchemicalNoteItemRenderer(mode);
		}
	}
}
