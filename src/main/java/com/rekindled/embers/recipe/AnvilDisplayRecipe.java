package com.rekindled.embers.recipe;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilDisplayRecipe implements IDawnstoneAnvilRecipe {

	public final Identifier id;

	public List<ItemStack> outputs;
	public List<ItemStack> inputs;
	public Ingredient ingredient;

	public AnvilDisplayRecipe(Identifier id, List<ItemStack> outputs, List<ItemStack> inputs, Ingredient ingredient) {
		this.id = id;
		this.outputs = outputs;
		this.inputs = inputs;
		this.ingredient = ingredient;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		return false;
	}

	@Override
	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputBottom() {
		return inputs;
	}

	@Override
	public List<ItemStack> getDisplayInputTop() {
		return List.of(com.rekindled.embers.util.Ingredients.stacks(ingredient));
	}

	@Override
	public List<ItemStack> getDisplayOutput() {
		return outputs;
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends IDawnstoneAnvilRecipe> getSerializer() {
		return AnvilRepairRecipe.SERIALIZER;
	}
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
