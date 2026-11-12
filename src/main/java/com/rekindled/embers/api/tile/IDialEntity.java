package com.rekindled.embers.api.tile;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public interface IDialEntity {
	public Packet<ClientGamePacketListener> getUpdatePacket(int maxLines);
}
