package com.rekindled.embers.network.message;

import java.util.Random;

import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.util.EmbersColors;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageBeamCannonFX implements CustomPacketPayload {

	public static Random random = new Random();
	double posX = 0, posY = 0, posZ = 0;
	double dX = 0, dY = 0, dZ = 0;

	public static final CustomPacketPayload.Type<MessageBeamCannonFX> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:beam_cannon_fx"));

	public static final StreamCodec<FriendlyByteBuf, MessageBeamCannonFX> STREAM_CODEC =
		StreamCodec.ofMember(MessageBeamCannonFX::write, MessageBeamCannonFX::read);

	public MessageBeamCannonFX() {
	}

	public MessageBeamCannonFX(double x, double y, double z, double dX, double dY, double dZ) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
		buf.writeDouble(dX);
		buf.writeDouble(dY);
		buf.writeDouble(dZ);
	}

	public static MessageBeamCannonFX read(FriendlyByteBuf buf) {
		return new MessageBeamCannonFX(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public static void handle(MessageBeamCannonFX msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> spawnParticles(msg));
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("resource")
	public static void spawnParticles(MessageBeamCannonFX msg) {
		Level world = com.rekindled.embers.client.ClientAccess.level();
		double distance = Math.sqrt(msg.dX * msg.dX + msg.dY * msg.dY + msg.dZ * msg.dZ);
		double segments = distance * 4;
		GlowParticleOptions options = new GlowParticleOptions(EmbersColors.EMBER_ID, 5.0F);
		StarParticleOptions star = new StarParticleOptions(EmbersColors.EMBER_ID, 5.0F);
		for (double i = 0; i < segments; i++) {
			for (int j = 0; j < 5; j++) {
				msg.posX += 0.2 * msg.dX / segments;
				msg.posY += 0.2 * msg.dY / segments;
				msg.posZ += 0.2 * msg.dZ / segments;
				world.addParticle(star, msg.posX, msg.posY, msg.posZ, 0, 0.000001, 0);
			}
		}
		for (int k = 0; k < 80; k++) {
			world.addParticle(options, msg.posX, msg.posY, msg.posZ, 2.5f * (random.nextFloat() - 0.5f), 2.5f * (random.nextFloat() - 0.5f), 2.5f * (random.nextFloat() - 0.5f));
		}
	}
}
