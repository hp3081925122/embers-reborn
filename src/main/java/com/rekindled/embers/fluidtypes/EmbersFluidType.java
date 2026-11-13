package com.rekindled.embers.fluidtypes;

import java.awt.Color;

import org.joml.Vector3f;

import com.rekindled.embers.Embers;

import net.minecraft.resources.Identifier;
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