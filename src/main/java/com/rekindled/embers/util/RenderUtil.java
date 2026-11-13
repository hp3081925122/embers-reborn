package com.rekindled.embers.util;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class RenderUtil {

	public static void renderWavyEmberLine(GlowMesh b, double x1, double y1, double x2, double y2, double thickness) {
		renderWavyEmberLine(b, x1, y1, x2, y2, thickness, 1.0, EmbersColors.EMBER);
	}

	public static void renderWavyEmberLine(GlowMesh b, double x1, double y1, double x2, double y2, double thickness, double density, Vector3f color) {
		double dx = x2-x1;
		double dy = y2-y1;
		double angleRads = Math.atan2(y2-y1, x2-x1);
		double dist = Math.sqrt(dx*dx+dy*dy);
		double orthoX = Math.cos(angleRads+(Math.PI/2.0));
		double orthoY = Math.sin(angleRads+(Math.PI/2.0));
		//double rayX = Math.cos(angleRads);
		//double rayY = Math.sin(angleRads);
		for (int i = 0; i <= 10; i ++) {
			float coeff = (float)i / 10f;
			double thickCoeff = Math.min(1.0, 1.4f*Mth.sqrt(2.0f*(0.5f-Math.abs((coeff-0.5f)))));
			//double thickCoeff = 1.0+0.25*Math.sin(coeff*Math.PI*2.0f);
			double tx = x1*(1.0f-coeff) + x2*coeff;
			double ty = NoiseGenUtil.interpolate((float)y1, (float)y2, coeff);
			float tick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false) + EmbersClientEvents.ticks;
			int offX = (int)(6f*tick);
			int offZ = (int)(6f*tick);
			float sine = (float)Math.sin(coeff*Math.PI*2.0f + 0.25f*(tick)) + 0.25f*(float)Math.sin(coeff*Math.PI*3.47f + 0.25f*(tick));
			float sineOff = (4.0f + (float)thickness)/3.0f;
			float densityCoeff = (float)(0.5+0.5*Math.sin(coeff*Math.PI*2.0*dist*0.01 + tick * 0.2));
			float minusDensity = (float)density * densityCoeff * EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
			float plusDensity = (float)density * densityCoeff * EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
			b.addVertex((float)(tx-thickness*(0.5f+minusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff), (float)(ty-thickness*(0.5f+minusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff), (float)(0)).setColor(color.x(), color.y(), color.z(), (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*minusDensity,1));
			b.addVertex((float)(tx+thickness*(0.5f+plusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff), (float)(ty+thickness*(0.5f+plusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff), (float)(0)).setColor(color.x(), color.y(), color.z(), (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*plusDensity,1));
		}
	}

	public static void renderHighlightCircle(GlowMesh b, double x1, double y1, double thickness) {
		renderHighlightCircle(b, x1, y1, thickness, 0, EmbersColors.EMBER);
	}

	public static void renderHighlightCircle(GlowMesh b, double x1, double y1, double thickness, double z, Vector3f color) {
		for (int i = 0; i < 40; i ++) {
			float coeff = (float)i / 40f;
			int i2 = i+1;
			if (i2 == 40){
				i2 = 0;
			}
			float coeff2 = (float)(i2) / 40f;
			double angle = Math.PI * 2.0 * coeff;
			double angle2 = Math.PI * 2.0 * coeff2;
			float tick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false) + EmbersClientEvents.ticks;
			double calcAngle2 = angle2;
			float density1 = EmberGenUtil.getEmberDensity(4, (int)x1+(int)(480.0*angle), (int)y1+4*(int)tick + (int)(4.0f*thickness));
			float density2 = EmberGenUtil.getEmberDensity(4, (int)x1+(int)(480.0*calcAngle2), (int)y1+4*(int)tick + (int)(4.0f*thickness));
			double tx = x1 + Math.sin(angle+0.03125f*tick)*(thickness - (thickness * 0.5f * density1));
			double ty = y1 + Math.cos(angle+0.03125f*tick)*(thickness - (thickness * 0.5f * density1));
			double tx2 = x1 + Math.sin(angle2+0.03125f*tick)*(thickness - (thickness * 0.5f * density2));
			double ty2 = y1 + Math.cos(angle2+0.03125f*tick)*(thickness - (thickness * 0.5f * density2));
			b.addVertex((float) x1, (float) y1, (float) z).setColor(color.x(), color.y(), color.z(), 1.0f);
			b.addVertex((float) tx, (float) ty, (float) z).setColor(color.x(), color.y(), color.z(), 0.0f);
			b.addVertex((float) tx2, (float) ty2, (float) z).setColor(color.x(), color.y(), color.z(), 0.0f);
		}
	}

	/**
	 * Additive four-colour quad on the 26.1 GUI render state. Vertex order matches 1.20.1:
	 * colour 1 bottom-left, 2 bottom-right, 3 top-right, 4 top-left. Float coordinates are kept
	 * because the heat bar slices are sub-pixel wide.
	 */
	public static void drawColorRectBatched(GuiGraphicsExtractor graphics, float x, float y, float widthIn, float heightIn,
			float r1, float g1, float b1, float a1,
			float r2, float g2, float b2, float a2,
			float r3, float g3, float b3, float a3,
			float r4, float g4, float b4, float a4)
	{
		graphics.submitGuiElementRenderState(new GlowQuadRenderState(new Matrix3x2f(graphics.pose()), x, y, widthIn, heightIn,
				ARGB.colorFromFloat(a1, r1, g1, b1), ARGB.colorFromFloat(a2, r2, g2, b2),
				ARGB.colorFromFloat(a3, r3, g3, b3), ARGB.colorFromFloat(a4, r4, g4, b4),
				graphics.peekScissorStack()));
	}

	/** Heat bar end cap; UVs are fractions of the 16x16 heat_bar.png like in 1.20.1. */
	public static void drawHeatBarEnd(GuiGraphicsExtractor graphics, int x, int y, int widthIn, int heightIn, float minU, float minV, float maxU, float maxV) {
		graphics.blit(EmbersRenderTypes.HEAT_BAR_ENDS_PIPELINE, EmbersRenderTypes.HEAT_BAR_TEXTURE, x, y, minU * 16, minV * 16, widthIn, heightIn,
				Math.round((maxU - minU) * 16), Math.round((maxV - minV) * 16), 16, 16);
	}

	public record GlowQuadRenderState(Matrix3x2fc pose, float x, float y, float w, float h, int c1, int c2, int c3, int c4,
			@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

		public GlowQuadRenderState(Matrix3x2fc pose, float x, float y, float w, float h, int c1, int c2, int c3, int c4, @Nullable ScreenRectangle scissorArea) {
			this(pose, x, y, w, h, c1, c2, c3, c4, scissorArea, computeBounds(pose, x, y, w, h, scissorArea));
		}

		private static ScreenRectangle computeBounds(Matrix3x2fc pose, float x, float y, float w, float h, @Nullable ScreenRectangle scissorArea) {
			int x0 = Mth.floor(x);
			int y0 = Mth.floor(y);
			ScreenRectangle rect = new ScreenRectangle(x0, y0, Math.max(1, Mth.ceil(x + w) - x0), Math.max(1, Mth.ceil(y + h) - y0)).transformMaxBounds(pose);
			return scissorArea != null ? scissorArea.intersection(rect) : rect;
		}

		@Override
		public void buildVertices(VertexConsumer consumer) {
			consumer.addVertexWith2DPose(pose, x, y + h).setColor(c1);
			consumer.addVertexWith2DPose(pose, x + w, y + h).setColor(c2);
			consumer.addVertexWith2DPose(pose, x + w, y).setColor(c3);
			consumer.addVertexWith2DPose(pose, x, y).setColor(c4);
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

	public static void renderAlchemyCircle(VertexConsumer buf, Matrix4f matrix4f, float x, float y, float z, float r, float g, float b, float a, float radius, float angle) {
		float sign = 1;
		/*if (Minecraft.getInstance().player.position().y+Minecraft.getInstance().player.getEyeHeight() < y) {
			sign = -1;
		}*/
		int lightx = 0xF000F0;
		int lighty = 0xF000F0;
		for (float i = 0; i < 360; i += 10) {
			float tx = (float) Math.sin(Math.toRadians(i+angle));
			float tz = (float) Math.cos(Math.toRadians(i+angle));
			float tx2 = (float) Math.sin(Math.toRadians(i+angle+10));
			float tz2 = (float) Math.cos(Math.toRadians(i+angle+10));
			buf.addVertex(matrix4f, x+radius*tx, y, z+radius*tz).setUv(0, 0).setUv2(lightx, lighty).setColor(r, g, b, a);
			buf.addVertex(matrix4f, x+(radius+0.25f)*tx, y, z+(radius+0.25f)*tz).setUv(0, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
			buf.addVertex(matrix4f, x+(radius+0.25f)*tx2, y, z+(radius+0.25f)*tz2).setUv(1, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
			buf.addVertex(matrix4f, x+radius*tx2, y, z+radius*tz2).setUv(1, 0).setUv2(lightx, lighty).setColor(r, g, b, a);
		}
		float ax = (float) ((radius+0.24)*Math.sin(Math.toRadians(0+angle)));
		float az = (float) ((radius+0.24)*Math.cos(Math.toRadians(0+angle)));
		float adx = (float) ((0.1875)*Math.cos(Math.toRadians(0+angle)));
		float adz = (float) ((0.1875)*-Math.sin(Math.toRadians(0+angle)));
		float bx = (float) ((radius+0.24)*Math.sin(Math.toRadians(120+angle)));
		float bz = (float) ((radius+0.24)*Math.cos(Math.toRadians(120+angle)));
		float bdx = (float) ((0.1875)*Math.cos(Math.toRadians(120+angle)));
		float bdz = (float) ((0.1875)*-Math.sin(Math.toRadians(120+angle)));
		float cx = (float) ((radius+0.24)*Math.sin(Math.toRadians(240+angle)));
		float cz = (float) ((radius+0.24)*Math.cos(Math.toRadians(240+angle)));
		float cdx = (float) ((0.1875)*Math.cos(Math.toRadians(240+angle)));
		float cdz = (float) ((0.1875)*-Math.sin(Math.toRadians(240+angle)));
		buf.addVertex(matrix4f, x+(ax-adx), y+0.00005f*sign, z+(az-adz)).setUv(0, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(ax+adx), y+0.00005f*sign, z+(az+adz)).setUv(0, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(bx-bdx), y+0.00005f*sign, z+(bz-bdz)).setUv(1, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(bx+bdx), y+0.00005f*sign, z+(bz+bdz)).setUv(1, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);

		buf.addVertex(matrix4f, x+(bx-bdx), y+0.0001f*sign, z+(bz-bdz)).setUv(0, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(bx+bdx), y+0.0001f*sign, z+(bz+bdz)).setUv(0, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(cx-cdx), y+0.0001f*sign, z+(cz-cdz)).setUv(1, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(cx+cdx), y+0.0001f*sign, z+(cz+cdz)).setUv(1, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);

		buf.addVertex(matrix4f, x+(ax-adx), y+0.00015f*sign, z+(az-adz)).setUv(0, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(ax+adx), y+0.00015f*sign, z+(az+adz)).setUv(0, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(cx-cdx), y+0.00015f*sign, z+(cz-cdz)).setUv(1, 1).setUv2(lightx, lighty).setColor(r, g, b, a);
		buf.addVertex(matrix4f, x+(cx+cdx), y+0.00015f*sign, z+(cz+cdz)).setUv(1, 0.5f).setUv2(lightx, lighty).setColor(r, g, b, a);
	}
}
