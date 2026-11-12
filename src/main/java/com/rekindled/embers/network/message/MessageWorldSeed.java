package com.rekindled.embers.network.message;

import com.rekindled.embers.EmbersClientEvents;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageWorldSeed implements CustomPacketPayload {

	long seed;

	public static final CustomPacketPayload.Type<MessageWorldSeed> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:world_seed"));

	public static final StreamCodec<FriendlyByteBuf, MessageWorldSeed> STREAM_CODEC =
		StreamCodec.ofMember(MessageWorldSeed::write, MessageWorldSeed::read);

	public MessageWorldSeed(long seed) {
		this.seed = seed;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeLong(seed);
	}

	public static MessageWorldSeed read(FriendlyByteBuf buf) {
		return new MessageWorldSeed(buf.readLong());
	}

	public static void handle(MessageWorldSeed msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> EmbersClientEvents.seed = msg.seed);
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
