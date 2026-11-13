package com.rekindled.embers.recipe;

import java.util.ArrayList;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IMixingRecipe extends Recipe<MixingContext> {

	public FluidStack getOutput(MixingContext context);

	public FluidStack process(MixingContext context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MIXER_CENTRIFUGE_ITEM.get());
	}

	@Override
	public default RecipeType<IMixingRecipe> getType() {
		return RegistryManager.MIXING.get();
	}

	public ArrayList<FluidIngredient> getDisplayInputFluids();

	public FluidStack getDisplayOutput();

	@Deprecated
	public default ItemStack assemble(MixingContext context, net.minecraft.core.HolderLookup.Provider registry) {
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
	public default ItemStack assemble(MixingContext input) {
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
