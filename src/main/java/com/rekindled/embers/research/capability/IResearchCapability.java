package com.rekindled.embers.research.capability;

import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public interface IResearchCapability {
	void setCheckmark(Identifier research, boolean checked);
	boolean isChecked(Identifier research);
	boolean hasCheckmark(Identifier research);
	Map<Identifier, Boolean> getCheckmarks();
	void writeToNBT(CompoundTag tag);
	void readFromNBT(CompoundTag tag);
}
