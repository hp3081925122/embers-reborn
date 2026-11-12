package com.rekindled.embers.particle;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rekindled.embers.render.EmbersRenderTypes;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AlchemyCircleParticle extends SingleQuadParticle {

	public float rotScale = 0.02f;
	public static final Quaternionf QUAT = Axis.XP.rotationDegrees(90);

	public AlchemyCircleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, AlchemyCircleParticleOptions pOptions, net.minecraft.client.renderer.texture.TextureAtlasSprite sprite) {
		super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, sprite);
		this.hasPhysics = false;
		this.xd = 0;
		this.yd = 0;
		this.zd = 0;
		this.rCol = pOptions.getColor().x();
		this.gCol = pOptions.getColor().y();
		this.bCol = pOptions.getColor().z();
		this.roll = rotScale;
		this.quadSize = pOptions.getScale();
		this.lifetime = pOptions.getLifetime();
		this.setSize(2.0f, 0.1f);
	}

	public float getQuadSize(float pScaleFactor) {
		if (this.age < 20)
			return this.quadSize * (float) Math.sin((this.age + pScaleFactor) * Math.PI / 40.0);
		if (this.age > this.lifetime - 20)
			return this.quadSize - this.quadSize * (float) Math.sin((this.age + 20 - this.lifetime + pScaleFactor) * Math.PI / 40.0);
		return this.quadSize;
	}

	public void tick() {
		super.tick();
		if (this.age < 20)
			this.alpha = (float) Math.sin((this.age) * Math.PI / 40.0);
		if (this.age > this.lifetime - 20)
			this.alpha = 1.0f - (float) Math.sin((this.age + 20 - this.lifetime) * Math.PI / 40.0);
		this.oRoll = this.roll;
		this.roll += rotScale;
	}

	/** Flat circle lying on the ground, drawn from both sides like the 1.21.1 render(). */
	@Override
	public void extract(net.minecraft.client.renderer.state.level.QuadParticleRenderState state, Camera camera, float partialTicks) {
		Quaternionf quaternion = new Quaternionf(QUAT);
		if (this.roll != 0.0F)
			quaternion.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
		this.extractRotatedQuad(state, camera, quaternion, partialTicks);
		this.extractRotatedQuad(state, camera, new Quaternionf(quaternion).rotateY((float) Math.PI), partialTicks);
	}

	@Override
	public int getLightCoords(float partialTicks) {
		return 0xF000F0;
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return com.rekindled.embers.render.EmbersParticleLayers.EMBER_HARD;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements net.minecraft.client.particle.ParticleProvider<AlchemyCircleParticleOptions> {
		private final net.minecraft.client.particle.SpriteSet sprites;

		public Provider(net.minecraft.client.particle.SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public net.minecraft.client.particle.Particle createParticle(AlchemyCircleParticleOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, net.minecraft.util.RandomSource random) {
			return new AlchemyCircleParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, sprites.get(random));
		}
	}
}
