package com.rekindled.embers.network.message;

import com.rekindled.embers.augment.ShiftingScalesAugment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageScalesData implements CustomPacketPayload {

	public int scales;

	public static final CustomPacketPayload.Type<MessageScalesData> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.parse("embers_reborn:scales_data"));

	public static final StreamCodec<FriendlyByteBuf, MessageScalesData> STREAM_CODEC =
		StreamCodec.ofMember(MessageScalesData::write, MessageScalesData::read);

	public MessageScalesData() {
		this.scales = 0;
	}

	public MessageScalesData(int scales) {
		this.scales = scales;
	}

	public MessageScalesData(double scales) {
		this.scales = (int) Math.ceil(scales);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(scales);
	}

	public static MessageScalesData read(FriendlyByteBuf buf) {
		return new MessageScalesData(buf.readInt());
	}

	public static void handle(MessageScalesData msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> ShiftingScalesAugment.scales = msg.scales);
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
