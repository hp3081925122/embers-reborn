package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.misc.AlchemyResult;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface IAlchemyRecipe extends Recipe<AlchemyContext> {

	public ArrayList<Ingredient> getCode(long seed);

	public boolean matchesCorrect(AlchemyContext context, Level pLevel);

	public AlchemyResult getResult(AlchemyContext context);

	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.ALCHEMY_TABLET_ITEM.get());
	}

	@Override
	public default RecipeType<IAlchemyRecipe> getType() {
		return RegistryManager.ALCHEMY.get();
	}

	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return getResultItem();
	}

	public Ingredient getCenterInput();

	public List<Ingredient> getInputs();

	public List<Ingredient> getAspects();

	public ItemStack getResultItem();

	public ItemStack getfailureItem();

	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class PedestalContents {
		public ItemStack aspect;
		public ItemStack input;

		public PedestalContents(ItemStack aspect, ItemStack input) {
			this.aspect = aspect;
			this.input = input;
		}
	}

	@Override
	public default ItemStack assemble(AlchemyContext input) {
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
