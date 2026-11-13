package com.rekindled.embers.network;

import com.rekindled.embers.network.message.MessageBeamCannonFX;
import com.rekindled.embers.network.message.MessageCasterOrb;
import com.rekindled.embers.network.message.MessageCrystalCellGrowFX;
import com.rekindled.embers.network.message.MessageDialUpdateRequest;
import com.rekindled.embers.network.message.MessageEmberGenOffset;
import com.rekindled.embers.network.message.MessageEmberRayFX;
import com.rekindled.embers.network.message.MessageItemSound;
import com.rekindled.embers.network.message.MessageResearchData;
import com.rekindled.embers.network.message.MessageResearchOpen;
import com.rekindled.embers.network.message.MessageResearchTick;
import com.rekindled.embers.network.message.MessageScalesData;
import com.rekindled.embers.network.message.MessageWorldSeed;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * NeoForge 1.21.1 packet handler — all messages implement CustomPacketPayload.
 * Registration is done via RegisterPayloadHandlersEvent (mod bus event).
 */
public class PacketHandler {

	/**
	 * Register all custom payloads. Called from EmbersReborn constructor
	 * via the RegisterPayloadHandlersEvent mod bus event.
	 */
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		// Clientbound (server → client): FX messages, data sync
		registrar.playToClient(MessageBeamCannonFX.TYPE, MessageBeamCannonFX.STREAM_CODEC, MessageBeamCannonFX::handle);
		registrar.playToClient(MessageCrystalCellGrowFX.TYPE, MessageCrystalCellGrowFX.STREAM_CODEC, MessageCrystalCellGrowFX::handle);
		registrar.playToClient(MessageEmberRayFX.TYPE, MessageEmberRayFX.STREAM_CODEC, MessageEmberRayFX::handle);
		registrar.playToClient(MessageEmberGenOffset.TYPE, MessageEmberGenOffset.STREAM_CODEC, MessageEmberGenOffset::handle);
		registrar.playToClient(MessageResearchData.TYPE, MessageResearchData.STREAM_CODEC, MessageResearchData::handle);
		registrar.playToClient(MessageScalesData.TYPE, MessageScalesData.STREAM_CODEC, MessageScalesData::handle);
		registrar.playToClient(MessageWorldSeed.TYPE, MessageWorldSeed.STREAM_CODEC, MessageWorldSeed::handle);
		registrar.playToClient(MessageItemSound.TYPE, MessageItemSound.STREAM_CODEC, MessageItemSound::handle);

		// Serverbound (client → server): requests, actions
		registrar.playToServer(MessageCasterOrb.TYPE, MessageCasterOrb.STREAM_CODEC, MessageCasterOrb::handle);
		registrar.playToServer(MessageDialUpdateRequest.TYPE, MessageDialUpdateRequest.STREAM_CODEC, MessageDialUpdateRequest::handle);
		registrar.playToServer(MessageResearchTick.TYPE, MessageResearchTick.STREAM_CODEC, MessageResearchTick::handle);
		registrar.playToServer(MessageResearchOpen.TYPE, MessageResearchOpen.STREAM_CODEC, MessageResearchOpen::handle);
	}

	/** Send a payload to the server from client side. */
	public static void sendToServer(CustomPacketPayload payload) {
		net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
	}

	/** Send a payload to a specific player. */
	public static void sendToPlayer(CustomPacketPayload payload, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, payload);
	}

	/** Send a payload to all players. */
	public static void sendToAll(CustomPacketPayload payload) {
		PacketDistributor.sendToAllPlayers(payload);
	}
}
