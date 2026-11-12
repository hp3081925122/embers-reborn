package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;

public class SingleRecipeInput implements net.minecraft.world.item.crafting.RecipeInput {

	public ItemStack stack;

	public SingleRecipeInput(ItemStack stack) {
		this.stack = stack;
	}

	// RecipeInput interface
	@Override
	public int size() { return 1; }

	@Override
	public ItemStack getItem(int pSlot) {
		return pSlot == 0 ? stack : ItemStack.EMPTY;
	}
}
