package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GenericRecipeBuilder {

	public Recipe<?> recipe;

	public static GenericRecipeBuilder create(Recipe<?> recipe) {
		GenericRecipeBuilder builder = new GenericRecipeBuilder();
		builder.recipe = recipe;
		return builder;
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceLocation.parse("embers_reborn:generic_recipe_" + System.nanoTime()), recipe, null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
