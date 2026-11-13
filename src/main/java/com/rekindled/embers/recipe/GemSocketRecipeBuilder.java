package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class GemSocketRecipeBuilder {

	public Identifier id;
	public Ingredient ingredient;

	public static GemSocketRecipeBuilder create(Ingredient ingredient) {
		GemSocketRecipeBuilder builder = new GemSocketRecipeBuilder();
		builder.ingredient = ingredient;
		return builder;
	}

	public static GemSocketRecipeBuilder create(TagKey<Item> tag) {
		GemSocketRecipeBuilder builder = create(com.rekindled.embers.util.Ingredients.tag(tag));
		builder.id = tag.location();
		return builder;
	}

	public static GemSocketRecipeBuilder create(ItemStack itemStack) {
		return create(itemStack.getItem());
	}

	public static GemSocketRecipeBuilder create(Item item) {
		GemSocketRecipeBuilder builder = create(Ingredient.of(item));
		builder.id = BuiltInRegistries.ITEM.getKey(item);
		return builder;
	}

	public GemSocketRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public GemSocketRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public GemSocketRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public GemSocketRecipe build() {
		return new GemSocketRecipe(id, ingredient);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId()), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
