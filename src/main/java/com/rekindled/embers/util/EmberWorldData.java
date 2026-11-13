package com.rekindled.embers.util;

import com.mojang.serialization.Codec;
import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class EmberWorldData extends SavedData {

	private static final Codec<EmberWorldData> CODEC = CompoundTag.CODEC.xmap(EmberWorldData::new, data -> data.save(new CompoundTag()));
	// 26.1 stores namespaced saved data under data/<namespace>/<path>.dat
	public static final SavedDataType<EmberWorldData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(Embers.MODID, "data"), () -> new EmberWorldData(new CompoundTag()), CODEC);

	public EmberWorldData(CompoundTag nbt) {
		EmberGenUtil.offX = nbt.getIntOr("offX", 0);
		EmberGenUtil.offZ = nbt.getIntOr("offZ", 0);
	}

	public static EmberWorldData get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(TYPE);
	}

	public CompoundTag save(CompoundTag tag) {
		tag.putInt("offX", EmberGenUtil.offX);
		tag.putInt("offZ", EmberGenUtil.offZ);
		return tag;
	}
}
