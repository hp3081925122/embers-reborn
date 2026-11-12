package com.rekindled.embers.research.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import com.rekindled.embers.util.INBTSerializable;

public class ResearchCapabilityProvider implements INBTSerializable<CompoundTag> {
	private IResearchCapability capability;

	public ResearchCapabilityProvider() {
		capability = new DefaultResearchCapability();
	}

	public ResearchCapabilityProvider(IResearchCapability capability) {
		this.capability = capability;
	}

	public IResearchCapability getCapability() { return capability; }

	@Override
	public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag compound = new CompoundTag();
		capability.writeToNBT(compound);
		return compound;
	}

	@Override
	public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag compound) {
		capability.readFromNBT(compound);
	}
}
