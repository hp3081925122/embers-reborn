package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IEmberActivationRecipe extends Recipe<net.minecraft.world.item.crafting.RecipeInput> {

	public int getOutput(net.minecraft.world.item.crafting.RecipeInput context);

	public int process(net.minecraft.world.item.crafting.RecipeInput context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.EMBER_ACTIVATION.get();
	}

	public Ingredient getDisplayInput();

	public int getDisplayOutput();

	@Override
	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
