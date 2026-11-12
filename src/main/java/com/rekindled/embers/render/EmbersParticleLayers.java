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
 * 26.1 particle layers replacing the 1.21.1 ParticleRenderType sheets in EmbersRenderTypes.
 * Blend and depth match the old sheets: SRC_ALPHA/ONE (LIGHTNING), no depth write; the ember sheets also skipped
 * the depth test. The old sheets used the custom ember particle shader; that shader is ported separately, these
 * layers use the vanilla particle shader until then.
 */
public final class EmbersParticleLayers {
	private static final DepthStencilState NO_WRITE = new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false);
	private static final DepthStencilState NO_DEPTH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);

	public static final RenderPipeline ADDITIVE_PIPELINE = pipeline("additive_particle", BlendFunction.LIGHTNING, NO_WRITE);
	public static final RenderPipeline ADDITIVE_NODEPTH_PIPELINE = pipeline("additive_particle_nodepth", BlendFunction.LIGHTNING, NO_DEPTH);
	public static final RenderPipeline TRANSLUCENT_NODEPTH_PIPELINE = pipeline("translucent_particle_nodepth", BlendFunction.TRANSLUCENT, NO_DEPTH);

	public static final SingleQuadParticle.Layer ADDITIVE = layer(ADDITIVE_PIPELINE);
	public static final SingleQuadParticle.Layer ADDITIVE_XRAY = layer(ADDITIVE_NODEPTH_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER = layer(ADDITIVE_NODEPTH_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER_ROUGH = layer(ADDITIVE_NODEPTH_PIPELINE);
	public static final SingleQuadParticle.Layer EMBER_HARD = layer(ADDITIVE_NODEPTH_PIPELINE);
	public static final SingleQuadParticle.Layer TRANSLUCENT_NODEPTH = layer(TRANSLUCENT_NODEPTH_PIPELINE);

	private EmbersParticleLayers() {}

	private static RenderPipeline pipeline(String name, BlendFunction blend, DepthStencilState depth) {
		return RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
				.withLocation(Identifier.fromNamespaceAndPath(Embers.MODID, "pipeline/" + name))
				.withColorTargetState(new ColorTargetState(blend))
				.withDepthStencilState(depth)
				.build();
	}

	private static SingleQuadParticle.Layer layer(RenderPipeline pipeline) {
		return new SingleQuadParticle.Layer(true, TextureAtlas.LOCATION_PARTICLES, pipeline);
	}

	public static void registerPipelines(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(ADDITIVE_PIPELINE);
		event.registerPipeline(ADDITIVE_NODEPTH_PIPELINE);
		event.registerPipeline(TRANSLUCENT_NODEPTH_PIPELINE);
	}
}
