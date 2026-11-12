package com.rekindled.embers.util;

import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class EmberWorldData extends SavedData {
	
	private static final String NAME = Embers.MODID + "_data";

	public EmberWorldData(CompoundTag nbt) {
		EmberGenUtil.offX = nbt.getInt("offX");
		EmberGenUtil.offZ = nbt.getInt("offZ");
	}

	public static EmberWorldData get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(new net.minecraft.world.level.saveddata.SavedData.Factory<>(() -> new EmberWorldData(new CompoundTag()), (tag, prov) -> new EmberWorldData(tag), null), NAME);
	}

	@Override
	public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
		tag.putInt("offX", EmberGenUtil.offX);
		tag.putInt("offZ", EmberGenUtil.offZ);
		return tag;
	}
}
