package com.rekindled.embers.api.tile;

import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.minecraft.network.chat.Component;

public interface IExtraCapabilityInformation {
	default boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return false;
	}

	default void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		//NOOP
	}

	default void addOtherDescription(List<Component> strings, Direction facing) {
		//NOOP
	}

	static Component formatCapability(EnumIOType ioType, String type, Component filter) {
		Component typeString = filter == null ? Component.translatable(type) : Component.translatable(Embers.MODID + ".tooltip.goggles.filter", Component.translatable(type), filter);
		switch (ioType) {
		case NONE:
			return null;
		case INPUT:
			return Component.translatable(Embers.MODID + ".tooltip.goggles.input", typeString);
		case OUTPUT:
			return Component.translatable(Embers.MODID + ".tooltip.goggles.output", typeString);
		default:
			return Component.translatable(Embers.MODID + ".tooltip.goggles.storage", typeString);
		}
	}

	enum EnumIOType {
		NONE,
		INPUT,
		OUTPUT,
		BOTH
	}
}
