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

public class EmberActivationRecipeBuilder {

	public Identifier id;
	public Ingredient ingredient;
	public int ember;

	public static EmberActivationRecipeBuilder create(Ingredient ingredient) {
		EmberActivationRecipeBuilder builder = new EmberActivationRecipeBuilder();
		builder.ingredient = ingredient;
		return builder;
	}

	public static EmberActivationRecipeBuilder create(TagKey<Item> tag) {
		EmberActivationRecipeBuilder builder = create(com.rekindled.embers.util.Ingredients.tag(tag));
		builder.id = tag.location();
		return builder;
	}

	public static EmberActivationRecipeBuilder create(ItemStack itemStack) {
		return create(itemStack.getItem());
	}

	public static EmberActivationRecipeBuilder create(Item item) {
		EmberActivationRecipeBuilder builder = create(Ingredient.of(item));
		builder.id = BuiltInRegistries.ITEM.getKey(item);
		return builder;
	}

	public EmberActivationRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public EmberActivationRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public EmberActivationRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public EmberActivationRecipeBuilder ember(int ember) {
		this.ember = ember;
		return this;
	}

	public EmberActivationRecipe build() {
		return new EmberActivationRecipe(id, ingredient, ember);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId()), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
