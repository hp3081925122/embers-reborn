package com.rekindled.embers.render;

import java.util.function.Function;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rekindled.embers.Embers;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

/**
 * World render types for block entity renderers and the emitter highlight, rebuilt on 26.1's RenderPipeline + RenderSetup.
 * Blend, cull and depth match the 1.21.1 RenderType definitions. Particle layers live in EmbersParticleLayers.
 *
 * Not portable to 26.1 and dropped on purpose:
 *  - the mithril block reflection shader: 26.1 chunk rendering only has fixed section layers (no named render types),
 *    so mithril blocks render as plain solid blocks;
 *  - the *_FALLBACK variants: the custom additive shader was vanilla position_tex_color plus additive blend, which a
 *    pipeline expresses directly, so the fallback and the normal type are now the same object;
 *  - BEAM: had no shader (PTLC_SHADER returned null) and its only caller was commented out.
 */
public final class EmbersRenderTypes {

	private EmbersRenderTypes() {}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Embers.MODID, path);
	}

	// --- pipelines ---

	/** position_tex_color, additive (SRC_ALPHA, ONE), no culling, depth test and write: crystal cell and field chart. */
	public static final RenderPipeline ADDITIVE_TEXTURED_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(id("pipeline/additive_textured"))
			.withVertexShader("core/position_tex_color")
			.withFragmentShader("core/position_tex_color")
			.withSampler("Sampler0")
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.withCull(false)
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
			.withDepthStencilState(DepthStencilState.DEFAULT)
			.build();

	/** entity_solid drawn as triangles: crystal seeds. */
	public static final RenderPipeline ENTITY_SOLID_TRIANGLES_PIPELINE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(id("pipeline/entity_solid_triangles"))
			.withSampler("Sampler1")
			.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.TRIANGLES)
			.build();

	/** GUI quads with additive blend: the glowing part of the heat bar in tooltips. Use with GuiGraphicsExtractor. */
	public static final RenderPipeline GLOW_GUI_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(id("pipeline/glow_gui"))
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true))
			.build();

	/** Textured GUI quads with additive blend: the Codex ember glow on the index page. */
	public static final RenderPipeline GLOW_GUI_TEXTURED_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
			.withLocation(id("pipeline/glow_gui_textured"))
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.build();

	/** Opaque textured GUI quads: the heat bar end caps. Use with GuiGraphicsExtractor. */
	public static final RenderPipeline HEAT_BAR_ENDS_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
			.withLocation(id("pipeline/heat_bar_ends"))
			.withColorTargetState(ColorTargetState.DEFAULT)
			.build();

	/** World text with additive blend: glowing text on the codex and tooltips. */
	public static final RenderPipeline GLOW_TEXT_PIPELINE = RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET)
			.withLocation(id("pipeline/glow_text"))
			.withVertexShader("core/rendertype_text")
			.withFragmentShader("core/rendertype_text")
			.withSampler("Sampler0")
			.withSampler("Sampler2")
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.build();

	public static final Identifier HEAT_BAR_TEXTURE = id("textures/gui/heat_bar.png");

	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ADDITIVE_TEXTURED_PIPELINE);
		event.registerPipeline(ENTITY_SOLID_TRIANGLES_PIPELINE);
		event.registerPipeline(GLOW_GUI_PIPELINE);
		event.registerPipeline(GLOW_GUI_TEXTURED_PIPELINE);
		event.registerPipeline(HEAT_BAR_ENDS_PIPELINE);
		event.registerPipeline(GLOW_TEXT_PIPELINE);
	}

	// --- render types ---

	/** Fluids drawn by FluidRenderer: entity translucent with culling on the block atlas, lightmap and overlay. */
	public static final RenderType FLUID = RenderType.create(Embers.MODID + ":fluid",
			RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
			.withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.sortOnUpload()
			.createRenderSetup());

	public static final RenderType CRYSTAL = RenderType.create(Embers.MODID + ":crystal",
			RenderSetup.builder(ADDITIVE_TEXTURED_PIPELINE)
			.withTexture("Sampler0", id("textures/block/crystal_material.png"))
			.affectsCrumbling()
			.createRenderSetup());
	public static final RenderType CRYSTAL_FALLBACK = CRYSTAL;

	public static final RenderType FIELD_CHART = RenderType.create(Embers.MODID + ":field_chart",
			RenderSetup.builder(ADDITIVE_TEXTURED_PIPELINE)
			.withTexture("Sampler0", id("textures/block/field_square.png"))
			.sortOnUpload()
			.createRenderSetup());
	public static final RenderType FIELD_CHART_FALLBACK = FIELD_CHART;

	public static final Function<Identifier, RenderType> CRYSTAL_SEED = Util.memoize(texture -> RenderType.create(Embers.MODID + ":crystal_seed",
			RenderSetup.builder(ENTITY_SOLID_TRIANGLES_PIPELINE)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.createRenderSetup()));

	/** Emitter/receiver highlight lines. 26.1 lines carry their width per vertex: callers set it with setLineWidth(GLOW_LINE_WIDTH). */
	public static final float GLOW_LINE_WIDTH = 6.0F;
	public static final RenderType GLOW_LINES = RenderType.create(Embers.MODID + ":glow_lines",
			RenderSetup.builder(RenderPipelines.LINES)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup());

	public static final Function<Identifier, RenderType> GLOW_TEXT = Util.memoize(texture -> RenderType.create(Embers.MODID + ":glow_text",
			RenderSetup.builder(GLOW_TEXT_PIPELINE)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.createRenderSetup()));

	public static final RenderType NOTE_BACKGROUND = getNote(id("textures/gui/alchemical_note.png"));
	public static final RenderType NOTE_PEDESTAL = getNote(id("textures/gui/alchemical_note_pedestal.png"));

	/** Alchemical note item: translucent entity quads without culling, like the 1.21.1 type. */
	public static RenderType getNote(Identifier location) {
		return RenderTypes.entityTranslucent(location);
	}
}
