package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GenericRecipeBuilder {

	public Identifier id;
	public Recipe<?> recipe;

	public static GenericRecipeBuilder create(Identifier id, Recipe<?> recipe) {
		GenericRecipeBuilder builder = new GenericRecipeBuilder();
		builder.id = id;
		builder.recipe = recipe;
		return builder;
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), recipe, null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
