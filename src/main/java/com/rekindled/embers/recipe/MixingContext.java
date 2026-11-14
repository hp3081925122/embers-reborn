package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MixingContext implements net.minecraft.world.item.crafting.RecipeInput {

	public IFluidHandler[] fluids;

	public MixingContext(IFluidHandler... fluids) {
		this.fluids = fluids;
	}

	@Override
	public int size() { return 0; }

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return ItemStack.EMPTY;
	}
}
