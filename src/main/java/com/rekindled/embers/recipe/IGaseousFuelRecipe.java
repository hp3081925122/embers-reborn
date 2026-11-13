package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IGaseousFuelRecipe extends Recipe<FluidHandlerContext> {

	public int getBurnTime(FluidHandlerContext context);

	public double getPowerMultiplier(FluidHandlerContext context);

	public int process(FluidHandlerContext context, int amount);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.WILDFIRE_STIRLING_ITEM.get());
	}

	@Override
	public default RecipeType<IGaseousFuelRecipe> getType() {
		return RegistryManager.GASEOUS_FUEL.get();
	}

	public FluidIngredient getDisplayInput();

	public int getDisplayBurnTime();

	public double getDisplayMultiplier();

	@Deprecated
	public default ItemStack assemble(FluidHandlerContext context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public default ItemStack assemble(FluidHandlerContext input) {
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
