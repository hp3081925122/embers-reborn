package com.rekindled.embers.recipe;

import java.util.List;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IDawnstoneAnvilRecipe extends Recipe<net.minecraft.world.item.crafting.RecipeInput> {

	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.DAWNSTONE_ANVIL_ITEM.get());
	}

	@Override
	public default RecipeType<IDawnstoneAnvilRecipe> getType() {
		return RegistryManager.DAWNSTONE_ANVIL_RECIPE.get();
	}

	public List<ItemStack> getDisplayInputBottom();

	public List<ItemStack> getDisplayInputTop();

	public List<ItemStack> getDisplayOutput();


	@Deprecated
	public default ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput context, net.minecraft.core.HolderLookup.Provider pRegistryAccess) {
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
