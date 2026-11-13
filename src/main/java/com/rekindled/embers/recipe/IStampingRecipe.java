package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;


public interface IStampingRecipe extends Recipe<StampingContext> {

	public ItemStack getOutput(net.minecraft.world.item.crafting.RecipeInput context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.STAMPER_ITEM.get());
	}

	@Override
	public default RecipeType<IStampingRecipe> getType() {
		return RegistryManager.STAMPING.get();
	}

	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return getResultItem();
	}

	public ItemStack getResultItem();

	public FluidIngredient getDisplayInputFluid();

	public Ingredient getDisplayInput();

	public Ingredient getDisplayStamp();

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public default ItemStack assemble(StampingContext input) {
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
