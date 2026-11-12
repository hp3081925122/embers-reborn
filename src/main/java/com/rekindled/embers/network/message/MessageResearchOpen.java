package com.rekindled.embers.network.message;

import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageResearchOpen implements CustomPacketPayload {

	public Identifier research;

	public static final CustomPacketPayload.Type<MessageResearchOpen> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:research_open"));

	public static final StreamCodec<FriendlyByteBuf, MessageResearchOpen> STREAM_CODEC =
		StreamCodec.ofMember(MessageResearchOpen::write, MessageResearchOpen::read);

	public MessageResearchOpen(Identifier research) {
		this.research = research;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeIdentifier(research);
	}

	public static MessageResearchOpen read(FriendlyByteBuf buf) {
		return new MessageResearchOpen(buf.readIdentifier());
	}

	public static void handle(MessageResearchOpen msg, IPayloadContext ctx) {
		if (!ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) ctx.player();
				IResearchCapability research = ResearchManager.getPlayerResearch(player);
				if (research == null)
					return;
				if (!research.hasCheckmark(msg.research)) {
					research.setCheckmark(msg.research, true);
					ResearchManager.sendResearchData(player);
				} else if (!research.isChecked(msg.research)) {
					ResearchManager.sendResearchData(player);
				}
			});
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
