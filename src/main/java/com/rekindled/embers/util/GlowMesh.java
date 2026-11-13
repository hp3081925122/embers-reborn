package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

/**
 * Collects the Codex glow geometry (1.20.1 drew it with Tesselator as TRIANGLES or TRIANGLE_STRIP,
 * POSITION_COLOR, additive blend) and submits it to the 26.1 GUI as additive quads.
 * The 26.1 GUI pipelines only take quads, so each triangle becomes a quad with a repeated last vertex
 * and each strip segment becomes one quad. Keeps the old addVertex(...).setColor(...) call shape.
 */
public class GlowMesh {

	private final boolean strip;
	private final List<float[]> vertices = new ArrayList<>();
	private float[] current;

	private GlowMesh(boolean strip) {
		this.strip = strip;
	}

	public static GlowMesh triangles() {
		return new GlowMesh(false);
	}

	public static GlowMesh strip() {
		return new GlowMesh(true);
	}

	public GlowMesh addVertex(float x, float y, float z) {
		current = new float[] {x, y, 1, 1, 1, 1};
		vertices.add(current);
		return this;
	}

	public GlowMesh setColor(float r, float g, float b, float a) {
		current[2] = r;
		current[3] = g;
		current[4] = b;
		current[5] = a;
		return this;
	}

	/** Vertex indices in quad order. */
	private List<int[]> quads() {
		List<int[]> quads = new ArrayList<>();
		if (strip) {
			for (int i = 0; i + 3 < vertices.size(); i += 2)
				quads.add(new int[] {i, i + 1, i + 3, i + 2});
		} else {
			for (int i = 0; i + 2 < vertices.size(); i += 3)
				quads.add(new int[] {i, i + 1, i + 2, i + 2});
		}
		return quads;
	}

	public void submit(GuiGraphicsExtractor graphics) {
		List<int[]> quads = quads();
		if (quads.isEmpty())
			return;
		float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
		for (float[] v : vertices) {
			minX = Math.min(minX, v[0]);
			minY = Math.min(minY, v[1]);
			maxX = Math.max(maxX, v[0]);
			maxY = Math.max(maxY, v[1]);
		}
		Matrix3x2f pose = new Matrix3x2f(graphics.pose());
		ScreenRectangle scissor = graphics.peekScissorStack();
		int x0 = Mth.floor(minX);
		int y0 = Mth.floor(minY);
		ScreenRectangle rect = new ScreenRectangle(x0, y0, Math.max(1, Mth.ceil(maxX) - x0), Math.max(1, Mth.ceil(maxY) - y0)).transformMaxBounds(pose);
		ScreenRectangle bounds = scissor != null ? scissor.intersection(rect) : rect;
		graphics.submitGuiElementRenderState(new State(pose, List.copyOf(vertices), quads, scissor, bounds));
	}

	private record State(Matrix3x2fc pose, List<float[]> vertices, List<int[]> quads,
			@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

		@Override
		public void buildVertices(VertexConsumer consumer) {
			for (int[] quad : quads) {
				for (int index : quad) {
					float[] v = vertices.get(index);
					consumer.addVertexWith2DPose(pose, v[0], v[1]).setColor(ARGB.colorFromFloat(v[5], v[2], v[3], v[4]));
				}
			}
		}

		@Override
		public RenderPipeline pipeline() {
			return EmbersRenderTypes.GLOW_GUI_PIPELINE;
		}

		@Override
		public TextureSetup textureSetup() {
			return TextureSetup.noTexture();
		}
	}
}
