package com.rekindled.embers.network.message;

import java.util.Random;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCrystalCellGrowFX implements CustomPacketPayload {

	public static Random random = new Random();
	public BlockPos pos = BlockPos.ZERO;
	double capacity = 0;

	public static final CustomPacketPayload.Type<MessageCrystalCellGrowFX> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:crystal_cell_grow_fx"));

	public static final StreamCodec<FriendlyByteBuf, MessageCrystalCellGrowFX> STREAM_CODEC =
		StreamCodec.ofMember(MessageCrystalCellGrowFX::write, MessageCrystalCellGrowFX::read);

	public MessageCrystalCellGrowFX() {
	}

	public MessageCrystalCellGrowFX(BlockPos pos, double capacity) {
		this.pos = pos;
		this.capacity = capacity;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeDouble(capacity);
	}

	public static MessageCrystalCellGrowFX read(FriendlyByteBuf buf) {
		return new MessageCrystalCellGrowFX(buf.readBlockPos(), buf.readDouble());
	}

	public static void handle(MessageCrystalCellGrowFX msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> spawnParticles(msg));
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	@SuppressWarnings("resource")
	public static void spawnParticles(MessageCrystalCellGrowFX msg) {
		Level level = Minecraft.getInstance().level;
		double angle = random.nextDouble() * 2.0 * Math.PI;
		double x = msg.pos.getX() + 0.5 + 0.5 * Math.sin(angle);
		double z = msg.pos.getZ() + 0.5 + 0.5 * Math.cos(angle);
		double x2 = msg.pos.getX() + 0.5;
		double z2 = msg.pos.getZ() + 0.5;
		float layerHeight = 0.25f;
		float numLayers = 2 + (float) Math.floor(msg.capacity / 120000.0f);
		float height = layerHeight * numLayers;
		for (float i = 0; i < 72; i++) {
			float coeff = i / 72.0f;
			level.addParticle(GlowParticleOptions.EMBER_NOMOTION, x * (1.0f - coeff) + x2 * coeff, msg.pos.getY() + (1.0f - coeff) + (height / 2.0f + 1.5f) * coeff, z * (1.0f - coeff) + z2 * coeff, 0, 0, 0);
		}
		level.playLocalSound(x, msg.pos.getY() + 0.5, z, EmbersSounds.CRYSTAL_CELL_GROW.get(), SoundSource.BLOCKS, 1.0f, 1.0f + random.nextFloat(), false);
	}
}
