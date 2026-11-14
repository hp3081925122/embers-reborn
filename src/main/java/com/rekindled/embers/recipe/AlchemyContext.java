package com.rekindled.embers.recipe;

import java.util.List;

import com.rekindled.embers.recipe.IAlchemyRecipe.PedestalContents;

import net.minecraft.world.item.ItemStack;

public class AlchemyContext implements net.minecraft.world.item.crafting.RecipeInput {

	public ItemStack tablet;
	public List<PedestalContents> contents;
	public long seed;

	public AlchemyContext(ItemStack tablet, List<PedestalContents> contents, long seed) {
		this.tablet = tablet;
		this.contents = contents;
		this.seed = seed;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return ItemStack.EMPTY;
	}
}
