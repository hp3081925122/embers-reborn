package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;


public class StampingContext implements net.minecraft.world.item.crafting.RecipeInput {

	public IFluidHandler fluids;
	public ItemStack stamp;

	public StampingContext(IItemHandlerModifiable inv, IFluidHandler fluids, ItemStack stamp) {
		this.items = inv;
		this.fluids = fluids;
		this.stamp = stamp;
	}
	public net.neoforged.neoforge.items.IItemHandlerModifiable items;

	@Override
	public ItemStack getItem(int slot) {
		return items != null && slot < items.getSlots() ? items.getStackInSlot(slot) : ItemStack.EMPTY;
	}

	@Override
	public int size() {
		return items != null ? items.getSlots() : 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
