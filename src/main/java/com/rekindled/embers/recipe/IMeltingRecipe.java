package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IMeltingRecipe extends Recipe<net.minecraft.world.item.crafting.RecipeInput> {

	public FluidStack getOutput(net.minecraft.world.item.crafting.RecipeInput context);

	public FluidStack getBonus();

	public FluidStack process(net.minecraft.world.item.crafting.RecipeInput context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MELTER_ITEM.get());
	}

	@Override
	public default RecipeType<IMeltingRecipe> getType() {
		return RegistryManager.MELTING.get();
	}

	public FluidStack getDisplayOutput();

	public Ingredient getDisplayInput();

	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public default ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input) {
		return ItemStack.EMPTY;
	}

	/** Machine recipe, not a recipe book entry; also keeps RecipeManager from discarding it as unplaceable. */
	@Override
	public default boolean isSpecial() {
		return true;
	}

	@Override
	public default boolean showNotification() {
		return false;
	}

	@Override
	public default String group() {
		return "";
	}

	@Override
	public default net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
		return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public default net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
		return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
	}
}
