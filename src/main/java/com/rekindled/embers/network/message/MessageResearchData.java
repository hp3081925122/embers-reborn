package com.rekindled.embers.network.message;

import java.util.HashMap;
import java.util.Map;

import com.rekindled.embers.research.ResearchManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageResearchData implements CustomPacketPayload {

	public static final int NAME_MAX_LENGTH = 64;
	Map<Identifier, Boolean> ticks;

	public static final CustomPacketPayload.Type<MessageResearchData> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:research_data"));

	public static final StreamCodec<FriendlyByteBuf, MessageResearchData> STREAM_CODEC =
		StreamCodec.ofMember(MessageResearchData::write, MessageResearchData::read);

	public MessageResearchData() {
		this.ticks = new HashMap<>();
	}

	public MessageResearchData(Map<Identifier, Boolean> ticks) {
		this.ticks = new HashMap<>(ticks);
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(ticks.size());
		for (Map.Entry<Identifier, Boolean> entry : ticks.entrySet()) {
			buf.writeIdentifier(entry.getKey());
			buf.writeBoolean(entry.getValue());
		}
	}

	public static MessageResearchData read(FriendlyByteBuf buf) {
		Map<Identifier, Boolean> ticks = new HashMap<>();
		int entries = buf.readInt();
		for (int i = 0; i < entries; i++) {
			Identifier key = buf.readIdentifier();
			boolean value = buf.readBoolean();
			ticks.put(key, value);
		}
		return new MessageResearchData(ticks);
	}

	public static void handle(MessageResearchData msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> ResearchManager.receiveResearchData(msg.ticks));
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
