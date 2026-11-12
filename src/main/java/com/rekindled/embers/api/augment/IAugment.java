package com.rekindled.embers.api.augment;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public interface IAugment {

	public Identifier getName();

	public double getCost();

	public default boolean countTowardsTotalLevel() {
		return true;
	}

	public default boolean canRemove() {
		return true;
	}

	public default boolean shouldRenderTooltip() {
		return true;
	}

	public default void onApply(ItemStack stack) {
		//NOOP
	}

	public default void onRemove(ItemStack stack) {
		//NOOP
	}
}
