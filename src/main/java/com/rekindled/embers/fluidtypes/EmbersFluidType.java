package com.rekindled.embers.fluidtypes;

import java.awt.Color;
import java.util.function.Consumer;

import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rekindled.embers.Embers;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;

public class EmbersFluidType extends FluidType {

	public final Identifier RENDER_OVERLAY;
	public final Identifier TEXTURE_STILL;
	public final Identifier TEXTURE_FLOW;
	public final Identifier TEXTURE_OVERLAY;
	public final Vector3f FOG_COLOR;
	public final float fogStart;
	public final float fogEnd;

	public EmbersFluidType(Properties properties, FluidInfo info) {
		super(properties);
		RENDER_OVERLAY = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/overlay/" + info.name + ".png");
		TEXTURE_STILL = Identifier.fromNamespaceAndPath(Embers.MODID, "block/fluid/" + info.name + "_still");
		TEXTURE_FLOW = Identifier.fromNamespaceAndPath(Embers.MODID, "block/fluid/" + info.name + "_flow");
		TEXTURE_OVERLAY = Identifier.fromNamespaceAndPath(Embers.MODID, "block/fluid/" + info.name + "_overlay");
		Color colorObject = new Color(info.color);
		FOG_COLOR = new Vector3f(colorObject.getRed()/255F, colorObject.getGreen()/255F, colorObject.getBlue()/255F);
		fogStart = info.fogStart;
		fogEnd = info.fogEnd;
	}

	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public Identifier getStillTexture() {
				return TEXTURE_STILL;
			}

			@Override
			public Identifier getFlowingTexture() {
				return TEXTURE_FLOW;
			}

			@Override
			public Identifier getOverlayTexture() {
				return TEXTURE_OVERLAY;
			}

			@Override
			public Identifier getRenderOverlayTexture(Minecraft mc) {
				return RENDER_OVERLAY;
			}

			@Override
			public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return FOG_COLOR;
			}

			@Override
			public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
				RenderSystem.setShaderFogStart(fogStart);
				RenderSystem.setShaderFogEnd(fogEnd);
			}
		});
	}

	public static class FluidInfo {

		public String name;
		public int color;
		public float fogStart;
		public float fogEnd;

		public FluidInfo(String name, int color, float fogStart, float fogEnd) {
			this.name = name;
			this.color = color;
			this.fogStart = fogStart;
			this.fogEnd = fogEnd;
		}
	}
}