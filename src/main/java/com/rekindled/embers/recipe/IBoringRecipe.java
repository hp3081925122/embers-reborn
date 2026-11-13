package com.rekindled.embers.recipe;

import java.util.Collection;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IBoringRecipe extends Recipe<BoringContext> {

	public WeightedItemStack getOutput(BoringContext context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_BORE_ITEM.get());
	}

	@Override
	public default RecipeType<IBoringRecipe> getType() {
		return RegistryManager.BORING.get();
	}

	public int getMinHeight();

	public int getMaxHeight();

	public Collection<Identifier> getDimensions();

	public Collection<Identifier> getBiomes();

	public double getChance();

	public WeightedItemStack getDisplayOutput();

	public List<ItemStack> getDisplayInput();

	@Deprecated
	public default ItemStack assemble(BoringContext context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public default ItemStack assemble(BoringContext input) {
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
