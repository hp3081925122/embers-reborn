package com.rekindled.embers.network.message;

import com.rekindled.embers.util.EmberGenUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageEmberGenOffset implements CustomPacketPayload {

	public int offX = 0;
	public int offZ = 0;

	public static final CustomPacketPayload.Type<MessageEmberGenOffset> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.parse("embers_reborn:ember_gen_offset"));

	public static final StreamCodec<FriendlyByteBuf, MessageEmberGenOffset> STREAM_CODEC =
		StreamCodec.ofMember(MessageEmberGenOffset::write, MessageEmberGenOffset::read);

	public MessageEmberGenOffset() {
	}

	public MessageEmberGenOffset(int x, int z) {
		this.offX = x;
		this.offZ = z;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(offX);
		buf.writeInt(offZ);
	}

	public static MessageEmberGenOffset read(FriendlyByteBuf buf) {
		return new MessageEmberGenOffset(buf.readInt(), buf.readInt());
	}

	public static void handle(MessageEmberGenOffset msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				EmberGenUtil.offX = msg.offX;
				EmberGenUtil.offZ = msg.offZ;
			});
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
