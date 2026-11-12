package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class AnvilAugmentRecipeBuilder {

	public ResourceLocation id;
	public Ingredient tool;
	public Ingredient input;
	public IAugment augment;

	public static AnvilAugmentRecipeBuilder create(IAugment augment) {
		AnvilAugmentRecipeBuilder builder = new AnvilAugmentRecipeBuilder();
		builder.augment = augment;
		builder.id = augment.getName();
		return builder;
	}

	public AnvilAugmentRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public AnvilAugmentRecipeBuilder domain(String domain) {
		this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public AnvilAugmentRecipeBuilder folder(String folder) {
		this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public AnvilAugmentRecipeBuilder tool(Ingredient tool) {
		this.tool = tool;
		return this;
	}

	public AnvilAugmentRecipeBuilder tool(ItemLike... tool) {
		tool(Ingredient.of(tool));
		return this;
	}

	public AnvilAugmentRecipeBuilder tool(TagKey<Item> tag) {
		tool(Ingredient.of(tag));
		return this;
	}

	public AnvilAugmentRecipeBuilder input(Ingredient input) {
		this.input = input;
		return this;
	}

	public AnvilAugmentRecipeBuilder input(ItemLike... input) {
		input(Ingredient.of(input));
		return this;
	}

	public AnvilAugmentRecipeBuilder input(TagKey<Item> tag) {
		input(Ingredient.of(tag));
		return this;
	}

	public AnvilAugmentRecipe build() {
		return new AnvilAugmentRecipe(id, tool, input, augment);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed
}
