package com.rekindled.embers.network.message;

import java.util.Random;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageEmberRayFX implements CustomPacketPayload {

	public static Random random = new Random();
	double posX = 0, posY = 0, posZ = 0;
	double dX = 0, dY = 0, dZ = 0;
	double hitDistance = Double.POSITIVE_INFINITY;
	int packedColor;
	ResourceLocation colorId;

	public static final CustomPacketPayload.Type<MessageEmberRayFX> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.parse("embers_reborn:ember_ray_fx"));

	public static final StreamCodec<FriendlyByteBuf, MessageEmberRayFX> STREAM_CODEC =
		StreamCodec.ofMember(MessageEmberRayFX::write, MessageEmberRayFX::read);

	public MessageEmberRayFX() {
	}

	public MessageEmberRayFX(double x, double y, double z, double dX, double dY, double dZ, double hitDistance, int packedColor, ResourceLocation colorId) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
		this.hitDistance = hitDistance;
		this.packedColor = packedColor;
		this.colorId = colorId;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeDouble(dX);
		buf.writeDouble(dY);
		buf.writeDouble(dZ);
		buf.writeDouble(hitDistance);
		buf.writeInt(packedColor);
		buf.writeResourceLocation(colorId);
	}

	public static MessageEmberRayFX read(FriendlyByteBuf buf) {
		return new MessageEmberRayFX(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readInt(), buf.readResourceLocation());
	}

	public static void handle(MessageEmberRayFX msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> spawnParticles(msg));
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("resource")
	public static void spawnParticles(MessageEmberRayFX msg) {
		Level world = Minecraft.getInstance().level;
		double distance = Math.sqrt(msg.dX * msg.dX + msg.dY * msg.dY + msg.dZ * msg.dZ);
		double segments = distance * 4;
		GlowParticleOptions options = new GlowParticleOptions(EmbersAPI.getColor(msg.colorId, Misc.colorFromInt(msg.packedColor)), 2.0F);
		for (double i = 0; i < segments; i++) {
			if (i >= msg.hitDistance * 4) {
				for (int k = 0; k < 80; k++) {
					world.addParticle(options, msg.posX, msg.posY, msg.posZ, 1.125f * (random.nextFloat() - 0.5f), 1.125f * (random.nextFloat() - 0.5f), 1.125f * (random.nextFloat() - 0.5f));
				}
				break;
			}
			for (int j = 0; j < 5; j++) {
				msg.posX += 0.2 * msg.dX / segments;
				msg.posY += 0.2 * msg.dY / segments;
				msg.posZ += 0.2 * msg.dZ / segments;
				world.addParticle(options, msg.posX, msg.posY, msg.posZ, 0, 0.000001, 0);
			}
		}
	}
}
