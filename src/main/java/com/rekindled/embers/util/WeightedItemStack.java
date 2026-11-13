package com.rekindled.embers.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

/** 26.1 removed WeightedEntry; WeightedRandom now takes a weight getter (WeightedItemStack::weight). */
public class WeightedItemStack {

	public static WeightedItemStack EMPTY = new WeightedItemStack(null, 0);

	@Nullable
	final ItemStackTemplate template;
	final int weight;

	public WeightedItemStack(@Nullable ItemStackTemplate template, int itemWeightIn) {
		this.weight = itemWeightIn;
		this.template = template;
	}

	public ItemStackTemplate getTemplate() {
		return template;
	}

	public ItemStack getStack() {
		return template == null ? ItemStack.EMPTY : template.create();
	}

	public int weight() {
		return weight;
	}
}
