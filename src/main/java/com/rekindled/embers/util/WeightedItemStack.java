package com.rekindled.embers.util;

import net.minecraft.world.item.ItemStack;

/** 26.1 removed WeightedEntry; WeightedRandom now takes a weight getter (WeightedItemStack::weight). */
public class WeightedItemStack {

	public static WeightedItemStack EMPTY = new WeightedItemStack(ItemStack.EMPTY, 0);

	ItemStack stack;
	final int weight;

	public WeightedItemStack(ItemStack stack, int itemWeightIn) {
		this.weight = itemWeightIn;
		this.stack = stack;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int weight() {
		return weight;
	}
}
