package com.rekindled.embers.network.message;

import com.rekindled.embers.api.tile.IDialEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageDialUpdateRequest implements CustomPacketPayload {

	public long pos = 0;
	public int maxLines;

	public static final CustomPacketPayload.Type<MessageDialUpdateRequest> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:dial_update_request"));

	public static final StreamCodec<FriendlyByteBuf, MessageDialUpdateRequest> STREAM_CODEC =
		StreamCodec.ofMember(MessageDialUpdateRequest::write, MessageDialUpdateRequest::read);

	public MessageDialUpdateRequest(long pos, int maxLines) {
		this.pos = pos;
		this.maxLines = maxLines;
	}

	public MessageDialUpdateRequest(BlockPos pos, int maxLines) {
		this.pos = pos.asLong();
		this.maxLines = maxLines;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeLong(pos);
		buf.writeInt(maxLines);
	}

	public static MessageDialUpdateRequest read(FriendlyByteBuf buf) {
		return new MessageDialUpdateRequest(buf.readLong(), buf.readInt());
	}

	public static void handle(MessageDialUpdateRequest msg, IPayloadContext ctx) {
		if (!ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) ctx.player();
				if (player != null && player.level() != null) {
					BlockEntity blockEntity = player.level().getBlockEntity(BlockPos.of(msg.pos));
					if (blockEntity instanceof IDialEntity dial) {
						Packet<ClientGamePacketListener> packet = dial.getUpdatePacket(msg.maxLines);
						if (packet != null) {
							player.connection.send(packet);
						}
					}
				}
			});
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
