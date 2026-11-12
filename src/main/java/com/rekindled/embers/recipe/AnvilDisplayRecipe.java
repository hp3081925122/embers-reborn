package com.rekindled.embers.recipe;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilDisplayRecipe implements IDawnstoneAnvilRecipe {

	public final ResourceLocation id;

	public List<ItemStack> outputs;
	public List<ItemStack> inputs;
	public Ingredient ingredient;

	public AnvilDisplayRecipe(ResourceLocation id, List<ItemStack> outputs, List<ItemStack> inputs, Ingredient ingredient) {
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
		return List.of(ingredient.getItems());
	}

	@Override
	public List<ItemStack> getDisplayOutput() {
		return outputs;
	}
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AnvilRepairRecipe.SERIALIZER;
	}
	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
