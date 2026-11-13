package com.rekindled.embers.render;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Runs a 1.21.1-style immediate block entity renderer inside 26.1's extract/submit renderer.
 *
 * extractRenderState keeps a reference to the block entity and the partial tick; submit calls the old render method
 * with a MultiBufferSource that records every vertex per render type and hands them to submitCustomGeometry afterwards.
 * Vertices are already transformed by the pose stack when they are recorded, so they are replayed with an identity pose.
 * Item and block models cannot be written as raw vertices any more: renderers call item() and model(), which submit
 * them through the collector at the current pose.
 */
public abstract class LegacyBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T, LegacyBlockEntityRenderer.State<T>> {

	private static @Nullable SubmitNodeCollector current;

	public static class State<T> extends BlockEntityRenderState {
		@Nullable
		public T blockEntity;
		public float partialTick;
	}

	public abstract void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay);

	@Override
	public State<T> createRenderState() {
		return new State<>();
	}

	@Override
	public void extractRenderState(T blockEntity, State<T> state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.blockEntity = blockEntity;
		state.partialTick = partialTicks;
	}

	@Override
	public void submit(State<T> state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		T blockEntity = state.blockEntity;
		state.blockEntity = null;
		if (blockEntity == null || blockEntity.isRemoved() || blockEntity.getLevel() == null)
			return;
		Recorder recorder = new Recorder();
		SubmitNodeCollector previous = current;
		current = collector;
		try {
			render(blockEntity, state.partialTick, poseStack, recorder, state.lightCoords, OverlayTexture.NO_OVERLAY);
		} finally {
			current = previous;
		}
		recorder.submit(collector);
	}

	/** Replacement for ItemRenderer#render inside render(). */
	/**
	 * 26.1 has no BakedModel to ask; 3D (block-lit) item models are what 1.20.1 called gui3d.
	 */
	public static boolean isGui3d(ItemStack stack, @Nullable Level level) {
		net.minecraft.client.renderer.item.ItemStackRenderState state = new net.minecraft.client.renderer.item.ItemStackRenderState();
		net.minecraft.client.Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GROUND, level, null, 0);
		return state.usesBlockLight();
	}

	/**
	 * Y scale of the GROUND display transform: 0.25 for block/block models, 0.5 for item/generated,
	 * the two vanilla parents nearly every item uses. The transform itself is private in 26.1.
	 */
	public static float groundScaleY(ItemStack stack, @Nullable Level level) {
		return isGui3d(stack, level) ? 0.25F : 0.5F;
	}

	public static void item(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, int light, int overlay, @Nullable Level level, int seed) {
		if (current == null || stack.isEmpty())
			return;
		ItemStackRenderState itemState = new ItemStackRenderState();
		Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemState, stack, context, level, null, seed);
		if (!itemState.isEmpty())
			itemState.submit(poseStack, current, light, overlay, 0);
	}

	/** Replacement for ModelBlockRenderer#renderModel of a standalone model part inside render(). */
	public static void model(PoseStack poseStack, @Nullable BlockStateModelPart part, int light, int overlay) {
		if (current == null || part == null)
			return;
		current.submitBlockModel(poseStack, Sheets.cutoutBlockSheet(), List.of(part), new int[0], light, overlay, 0);
	}

	/** Collects the vertices written through getBuffer(type), one buffer per render type. */
	public static final class Recorder implements MultiBufferSource {
		private final Map<RenderType, RecordingConsumer> buffers = new LinkedHashMap<>();

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			return buffers.computeIfAbsent(renderType, type -> new RecordingConsumer());
		}

		void submit(SubmitNodeCollector collector) {
			PoseStack identity = new PoseStack();
			buffers.forEach((type, buffer) -> {
				if (!buffer.vertices.isEmpty())
					collector.submitCustomGeometry(identity, type, (pose, out) -> buffer.replay(out));
			});
		}
	}

	private static final class Vertex {
		float x, y, z, u, v, nx, ny, nz, lineWidth;
		int color = 0xFFFFFFFF, uv1U, uv1V, uv2U, uv2V;
		boolean hasColor, hasUv, hasUv1, hasUv2, hasNormal, hasLineWidth;
	}

	private static final class RecordingConsumer implements VertexConsumer {
		final List<Vertex> vertices = new ArrayList<>();

		private Vertex last() {
			return vertices.get(vertices.size() - 1);
		}

		@Override
		public VertexConsumer addVertex(float x, float y, float z) {
			Vertex vertex = new Vertex();
			vertex.x = x;
			vertex.y = y;
			vertex.z = z;
			vertices.add(vertex);
			return this;
		}

		@Override
		public VertexConsumer setColor(int r, int g, int b, int a) {
			return setColor((a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255));
		}

		@Override
		public VertexConsumer setColor(int color) {
			Vertex vertex = last();
			vertex.color = color;
			vertex.hasColor = true;
			return this;
		}

		@Override
		public VertexConsumer setUv(float u, float v) {
			Vertex vertex = last();
			vertex.u = u;
			vertex.v = v;
			vertex.hasUv = true;
			return this;
		}

		@Override
		public VertexConsumer setUv1(int u, int v) {
			Vertex vertex = last();
			vertex.uv1U = u;
			vertex.uv1V = v;
			vertex.hasUv1 = true;
			return this;
		}

		@Override
		public VertexConsumer setUv2(int u, int v) {
			Vertex vertex = last();
			vertex.uv2U = u;
			vertex.uv2V = v;
			vertex.hasUv2 = true;
			return this;
		}

		@Override
		public VertexConsumer setNormal(float x, float y, float z) {
			Vertex vertex = last();
			vertex.nx = x;
			vertex.ny = y;
			vertex.nz = z;
			vertex.hasNormal = true;
			return this;
		}

		@Override
		public VertexConsumer setLineWidth(float width) {
			Vertex vertex = last();
			vertex.lineWidth = width;
			vertex.hasLineWidth = true;
			return this;
		}

		void replay(VertexConsumer out) {
			for (Vertex vertex : vertices) {
				out.addVertex(vertex.x, vertex.y, vertex.z);
				if (vertex.hasColor)
					out.setColor(vertex.color);
				if (vertex.hasUv)
					out.setUv(vertex.u, vertex.v);
				if (vertex.hasUv1)
					out.setUv1(vertex.uv1U, vertex.uv1V);
				if (vertex.hasUv2)
					out.setUv2(vertex.uv2U, vertex.uv2V);
				if (vertex.hasNormal)
					out.setNormal(vertex.nx, vertex.ny, vertex.nz);
				if (vertex.hasLineWidth)
					out.setLineWidth(vertex.lineWidth);
			}
		}
	}
}
