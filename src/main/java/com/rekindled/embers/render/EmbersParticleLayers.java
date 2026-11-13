package com.rekindled.embers.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.rekindled.embers.Embers;

import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

/**
 * 26.1 particle layers replacing the 1.21.1 ParticleRenderType sheets of EmbersRenderTypes.
 *
 * The ember colour maths of the old particle_ember / particle_ember_fab shaders is kept in
 * shaders/core/particle_ember.fsh (FAB define = the old fab variant, ALPHA_CUTOFF = the old AlphaCutoff uniform).
 * Blending stays SRC_ALPHA/ONE without depth write. Not kept: the soft fade against solid blocks, which sampled a
 * per-frame copy of the depth buffer; particle layers cannot bind extra textures in 26.1, so particles use a normal
 * depth test (the 1.21.1 "fast graphics" behaviour). The x-ray layer ignores depth, as before.
 */
public final class EmbersParticleLayers {
	private static final DepthStencilState NO_WRITE = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final DepthStencilState NO_DEPTH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);
	private static final Identifier EMBER_SHADER = id("core/particle_ember");

	public static final RenderPipeline ADDITIVE_PIPELINE = ember("additive_particle", false, 0.0F, NO_WRITE);
	public static final RenderPipeline EMBER_ROUGH_PIPELINE = ember("ember_rough_particle", false, 0.1F, NO_WRITE);
	public static final RenderPipeline EMBER_HARD_PIPELINE = ember("ember_hard_particle", true, 0.0F, NO_WRITE);
	public static final RenderPipeline ADDITIVE_XRAY_PIPELINE = ember("additive_xray_particle", false, 0.0F, NO_DEPTH);
	public static final RenderPipeline TRANSLUCENT_PIPELINE = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
			.withLocation(id("pipeline/translucent_particle"))
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(NO_WRITE)
			.build();

	public static final SingleQuadParticle.Layer ADDITIVE = layer(ADDITIVE_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER = layer(ADDITIVE_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER_ROUGH = layer(EMBER_ROUGH_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER_HARD = layer(EMBER_HARD_PIPELINE);
	public static final SingleQuadParticle.Layer ADDITIVE_XRAY = layer(ADDITIVE_XRAY_PIPELINE);
	public static final SingleQuadParticle.Layer TRANSLUCENT_NODEPTH = layer(TRANSLUCENT_PIPELINE);

	private EmbersParticleLayers() {}

	private static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Embers.MODID, path);
	}

	private static RenderPipeline ember(String name, boolean fab, float alphaCutoff, DepthStencilState depth) {
		RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
				.withLocation(id("pipeline/" + name))
				.withVertexShader(EMBER_SHADER)
				.withFragmentShader(EMBER_SHADER)
				.withShaderDefine("ALPHA_CUTOFF", alphaCutoff)
				.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
				.withDepthStencilState(depth);
		if (fab)
			builder.withShaderDefine("FAB");
		return builder.build();
	}

	private static SingleQuadParticle.Layer layer(RenderPipeline pipeline) {
		return new SingleQuadParticle.Layer(true, TextureAtlas.LOCATION_PARTICLES, pipeline);
	}

	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ADDITIVE_PIPELINE);
		event.registerPipeline(EMBER_ROUGH_PIPELINE);
		event.registerPipeline(EMBER_HARD_PIPELINE);
		event.registerPipeline(ADDITIVE_XRAY_PIPELINE);
		event.registerPipeline(TRANSLUCENT_PIPELINE);
	}
}
