package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidHandlerContext implements net.minecraft.world.item.crafting.RecipeInput {

	public IFluidHandler fluid;

	public FluidHandlerContext(IFluidHandler fluid) {
		this.fluid = fluid;
	}

	// RecipeInput interface
	@Override
	public int size() { return 0; }

	@Override
	public ItemStack getItem(int pSlot) {
		return ItemStack.EMPTY;
	}
}
