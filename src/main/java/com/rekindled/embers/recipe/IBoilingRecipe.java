package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IBoilingRecipe extends Recipe<FluidHandlerContext> {

	public FluidStack getOutput(FluidHandlerContext context);

	public FluidStack process(FluidHandlerContext context, int amount);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MINI_BOILER_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.BOILING.get();
	}

	public FluidIngredient getDisplayInput();

	public FluidStack getDisplayOutput();

	@Deprecated
	public default ItemStack assemble(FluidHandlerContext context, net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
