package com.rekindled.embers.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;

/**
 * NeoForge removed INBTSerializable in 26.1. The mod keeps the same contract for its own capability data.
 */
public interface INBTSerializable<T extends Tag> {
	T serializeNBT(HolderLookup.Provider provider);

	void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
