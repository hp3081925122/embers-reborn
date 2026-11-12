package com.rekindled.embers.network.message;

import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageResearchTick implements CustomPacketPayload {

	public Identifier research;
	public boolean ticked;

	public static final CustomPacketPayload.Type<MessageResearchTick> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:research_tick"));

	public static final StreamCodec<FriendlyByteBuf, MessageResearchTick> STREAM_CODEC =
		StreamCodec.ofMember(MessageResearchTick::write, MessageResearchTick::read);

	public MessageResearchTick(Identifier research, boolean ticked) {
		this.research = research;
		this.ticked = ticked;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeIdentifier(research);
		buf.writeBoolean(ticked);
	}

	public static MessageResearchTick read(FriendlyByteBuf buf) {
		return new MessageResearchTick(buf.readIdentifier(), buf.readBoolean());
	}

	public static void handle(MessageResearchTick msg, IPayloadContext ctx) {
		if (!ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) ctx.player();
				IResearchCapability research = ResearchManager.getPlayerResearch(player);
				if (research != null) {
					research.setCheckmark(msg.research, msg.ticked);
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
