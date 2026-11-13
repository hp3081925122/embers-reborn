package com.rekindled.embers.particle;

import java.util.Locale;

import org.joml.Vector3f;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

public class XRayGlowParticleOptions implements ParticleOptions {

	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 4.0F;
	protected final Identifier colorId;
	protected final Vector3f color;
	protected final Vec3 motion;
	protected final float scale;
	protected final int lifetime;
	public static final XRayGlowParticleOptions EMBER = new XRayGlowParticleOptions(EmbersColors.EMBER_ID, 2.0F);
	public static final XRayGlowParticleOptions EMBER_NOMOTION = new XRayGlowParticleOptions(EmbersColors.EMBER_ID, new Vec3(0.0, 0.000001, 0.0), 2.0F);
	public static final XRayGlowParticleOptions EMBER_BIG_NOMOTION = new XRayGlowParticleOptions(EmbersColors.EMBER_ID, new Vec3(0.0, 0.000001, 0.0), 6.0F, 200);

	public static final Codec<XRayGlowParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(Identifier.CODEC.fieldOf("color_id").forGetter((p_175797_) -> {
			return p_175797_.colorId;
		}), ExtraCodecs.VECTOR3F.<org.joml.Vector3f>xmap(org.joml.Vector3f::new, v -> v).fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), Vec3.CODEC.fieldOf("motion").forGetter((p_175797_) -> {
			return p_175797_.motion;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		}), Codec.INT.fieldOf("lifetime").forGetter((p_175795_) -> {
			return p_175795_.lifetime;
		})).apply(p_175793_, XRayGlowParticleOptions::new);
	});

	public XRayGlowParticleOptions(Identifier pColorId, Vector3f pColor, Vec3 pMotion, float pScale, int plifetime) {
		this.colorId = pColorId;
		this.color = pColor;
		this.motion = pMotion;
		this.scale = pScale;
		this.lifetime = plifetime;
	}

	public XRayGlowParticleOptions(Vector3f pColor, Vec3 pMotion, float pScale, int plifetime) {
		this(EmbersColors.CUSTOM_ID, pColor, pMotion, pScale, plifetime);
	}

	public XRayGlowParticleOptions(Identifier pColorId, Vec3 pMotion, float pScale, int plifetime) {
		this(pColorId, EmbersColors.EMBER, pMotion, pScale, plifetime);
	}

	public XRayGlowParticleOptions(Vector3f pColor, Vec3 pMotion, float pScale) {
		this(pColor, pMotion, pScale, -1);
	}

	public XRayGlowParticleOptions(Vector3f pColor, float pScale, int plifetime) {
		this(pColor, Vec3.ZERO, pScale, plifetime);
	}

	public XRayGlowParticleOptions(Vector3f pColor, float pScale) {
		this(pColor, Vec3.ZERO, pScale);
	}

	public XRayGlowParticleOptions(Identifier pColorId, Vec3 pMotion, float pScale) {
		this(pColorId, pMotion, pScale, -1);
	}

	public XRayGlowParticleOptions(Identifier pColorId, float pScale, int plifetime) {
		this(pColorId, Vec3.ZERO, pScale, plifetime);
	}

	public XRayGlowParticleOptions(Identifier pColorId, float pScale) {
		this(pColorId, Vec3.ZERO, pScale);
	}

	public static Vector3f readVector3f(StringReader pStringInput) throws CommandSyntaxException {
		pStringInput.expect(' ');
		float f = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f1 = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f2 = pStringInput.readFloat();
		return new Vector3f(f, f1, f2);
	}

	public static Vec3 readVec3(StringReader pStringInput) throws CommandSyntaxException {
		pStringInput.expect(' ');
		double f = pStringInput.readDouble();
		pStringInput.expect(' ');
		double f1 = pStringInput.readDouble();
		pStringInput.expect(' ');
		double f2 = pStringInput.readDouble();
		return new Vec3(f, f1, f2);
	}

	public static Vector3f readVector3f(FriendlyByteBuf pBuffer) {
		return new Vector3f(pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat());
	}

	public static Vec3 readVec3(FriendlyByteBuf pBuffer) {
		return new Vec3(pBuffer.readDouble(), pBuffer.readDouble(), pBuffer.readDouble());
	}

	public void writeToNetwork(FriendlyByteBuf pBuffer) {
		pBuffer.writeIdentifier(this.colorId);
		pBuffer.writeFloat(this.color.x());
		pBuffer.writeFloat(this.color.y());
		pBuffer.writeFloat(this.color.z());
		pBuffer.writeDouble(this.motion.x());
		pBuffer.writeDouble(this.motion.y());
		pBuffer.writeDouble(this.motion.z());
		pBuffer.writeFloat(this.scale);
		pBuffer.writeInt(this.lifetime);
	}

	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.motion.x(), this.motion.y(), this.motion.z(), this.scale);
	}

	public Vector3f getColor() {
		return EmbersAPI.getColor(this.colorId, this.color);
	}

	public Vec3 getMotion() {
		return this.motion;
	}

	public float getScale() {
		return this.scale;
	}

	public int getLifetime() {
		return this.lifetime;
	}

	@Override
	public ParticleType<?> getType() {
		return RegistryManager.XRAY_GLOW_PARTICLE.get();
	}
}
