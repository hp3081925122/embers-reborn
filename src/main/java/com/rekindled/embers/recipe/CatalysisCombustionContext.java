package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandlerModifiable;


public class CatalysisCombustionContext implements net.minecraft.world.item.crafting.RecipeInput {

	public ItemStack machine;

	public CatalysisCombustionContext(IItemHandlerModifiable inv, ItemStack machine) {
				this.machine = machine;
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

}
