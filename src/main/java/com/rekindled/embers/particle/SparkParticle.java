package com.rekindled.embers.particle;

import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SparkParticle extends SingleQuadParticle {

	public float rBase = 1.0F;
	public float gBase = 1.0F;
	public float bBase = 1.0F;

	public float rotScale = random.nextFloat() * 0.1f + 0.05f;

	public SparkParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SparkParticleOptions pOptions, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite);
		this.friction = 0.96F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.gravity = 0.04f;
		this.yd = 0.12 * random.nextFloat();
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.rBase = pOptions.getColor().x();
		this.gBase = pOptions.getColor().y();
		this.bBase = pOptions.getColor().z();
		this.oRoll = random.nextFloat();
		this.roll = rotScale;
		this.quadSize *= 0.75F * pOptions.getScale();
		double i = 20.0D / (this.random.nextDouble() * 0.5D + 0.5D);
		this.lifetime = (int)(i * pOptions.getScale());
	}

	public float getQuadSize(float pScaleFactor) {
		return this.quadSize - this.quadSize * (((float)this.age + pScaleFactor) / (float)this.lifetime);
	}

	public void tick() {
		super.tick();
		this.alpha = 1.0f - (float)this.age / (float)this.lifetime;
		float brightness = 1.0f - (float)this.age / (float)this.lifetime;
		this.rCol = this.rBase * brightness;
		this.gCol = this.gBase * brightness;
		this.bCol = this.bBase * brightness;
		this.oRoll = this.roll;
		this.roll += rotScale;
	}

	@Override
	public int getLightCoords(float partialTicks) {
		return 0xF000F0;
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return com.rekindled.embers.render.EmbersParticleLayers.EMBER;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements net.minecraft.client.particle.ParticleProvider<SparkParticleOptions> {
		private final net.minecraft.client.particle.SpriteSet sprites;

		public Provider(net.minecraft.client.particle.SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public net.minecraft.client.particle.Particle createParticle(SparkParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, net.minecraft.util.RandomSource random) {
			return new SparkParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, sprites.get(random));
		}
	}
}
