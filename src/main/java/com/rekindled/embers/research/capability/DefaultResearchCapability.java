package com.rekindled.embers.research.capability;

import java.util.HashMap;
import java.util.Map;

import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class DefaultResearchCapability implements IResearchCapability, com.rekindled.embers.util.INBTSerializable<CompoundTag> {
	Map<Identifier, Boolean> checkmarks = new HashMap<>();

	@Override
	public void setCheckmark(Identifier research, boolean checked) {
		checkmarks.put(research,checked);
	}

	@Override
	public boolean isChecked(Identifier research) {
		return checkmarks.getOrDefault(research,false);
	}

	@Override
	public boolean hasCheckmark(Identifier research) {
		return checkmarks.containsKey(research);
	}

	@Override
	public Map<Identifier, Boolean> getCheckmarks() {
		return checkmarks;
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		CompoundTag checkmarksTag = new CompoundTag();
		for (Map.Entry<Identifier, Boolean> entry : checkmarks.entrySet()) {
			checkmarksTag.putBoolean(entry.getKey().toString(), entry.getValue());
		}
		tag.put("checkmarks", checkmarksTag);
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		CompoundTag checkmarksTag = tag.getCompoundOrEmpty("checkmarks");
		checkmarks.clear();
		for (String key : checkmarksTag.keySet()) {
			if (key.contains(":")) {
				checkmarks.put(Identifier.parse(key), checkmarksTag.getBooleanOr(key, false));
			} else {
				checkmarks.put(Identifier.fromNamespaceAndPath(Embers.MODID, key), checkmarksTag.getBooleanOr(key, false));
			}
		}
	}

	@Override
	public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		writeToNBT(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag tag) {
		readFromNBT(tag);
	}
}
