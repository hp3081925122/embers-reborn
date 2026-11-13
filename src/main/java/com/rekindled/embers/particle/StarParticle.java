package com.rekindled.embers.particle;

import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;

public class StarParticle extends SingleQuadParticle {

	public float rBase = 1.0F;
	public float gBase = 1.0F;
	public float bBase = 1.0F;

	public float rotScale = random.nextFloat() * 0.1f + 0.05f;

	public StarParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, StarParticleOptions pOptions, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite);
		this.friction = 1.0F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.hasPhysics = false;
		this.xd = pXSpeed;
		this.yd = pYSpeed;
		this.zd = pZSpeed;
		this.xd *= (double)0.1F;
		this.yd *= (double)0.1F;
		this.zd *= (double)0.1F;
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.rBase = pOptions.getColor().x();
		this.gBase = pOptions.getColor().y();
		this.bBase = pOptions.getColor().z();
		this.roll = rotScale;
		this.quadSize *= 0.75F * pOptions.getScale();
		double i = 6.0D / (this.random.nextDouble() * 0.5D + 0.5D);
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

	public static class Provider implements net.minecraft.client.particle.ParticleProvider<StarParticleOptions> {
		private final net.minecraft.client.particle.SpriteSet sprites;

		public Provider(net.minecraft.client.particle.SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public net.minecraft.client.particle.Particle createParticle(StarParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, net.minecraft.util.RandomSource random) {
			return new StarParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, sprites.get(random));
		}
	}
}
