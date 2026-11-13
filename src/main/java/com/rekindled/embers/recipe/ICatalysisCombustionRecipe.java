package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface ICatalysisCombustionRecipe extends Recipe<CatalysisCombustionContext> {

	public int getBurnTIme(CatalysisCombustionContext context);

	public double getmultiplier(CatalysisCombustionContext context);

	public int process(CatalysisCombustionContext context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.IGNEM_REACTOR_ITEM.get());
	}

	@Override
	public default RecipeType<ICatalysisCombustionRecipe> getType() {
		return RegistryManager.CATALYSIS_COMBUSTION.get();
	}

	public Ingredient getDisplayInput();

	public Ingredient getDisplayMachine();

	public int getDisplayTime();

	public double getDisplayMultiplier();

	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default ItemStack assemble(CatalysisCombustionContext context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public default ItemStack assemble(CatalysisCombustionContext input) {
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
