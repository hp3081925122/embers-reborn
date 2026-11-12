package com.rekindled.embers.recipe;

import java.util.List;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IMetalCoefficientRecipe extends Recipe<BlockStateContext> {

	public double getCoefficient(BlockStateContext context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.PRESSURE_REFINERY_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.METAL_COEFFICIENT.get();
	}

	public List<ItemStack> getDisplayInput();

	public double getDisplayCoefficient();

	@Override
	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Deprecated
	public default ItemStack assemble(BlockStateContext context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
