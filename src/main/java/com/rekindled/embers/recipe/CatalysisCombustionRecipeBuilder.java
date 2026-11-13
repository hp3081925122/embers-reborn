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

public class CatalysisCombustionRecipeBuilder {

	public Identifier id;
	public Ingredient ingredient;
	public Ingredient machine;
	public int burnTime;
	public double multiplier;

	public static CatalysisCombustionRecipeBuilder create(Ingredient ingredient) {
		CatalysisCombustionRecipeBuilder builder = new CatalysisCombustionRecipeBuilder();
		builder.ingredient = ingredient;
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(TagKey<Item> tag) {
		CatalysisCombustionRecipeBuilder builder = create(Ingredient.of(tag));
		builder.id = tag.location();
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(ItemStack itemStack) {
		CatalysisCombustionRecipeBuilder builder = create(Ingredient.of(itemStack));
		builder.id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		return builder;
	}

	public static CatalysisCombustionRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public CatalysisCombustionRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public CatalysisCombustionRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public CatalysisCombustionRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public CatalysisCombustionRecipeBuilder catalysis() {
		this.machine = Ingredient.of(RegistryManager.CATALYSIS_CHAMBER_ITEM.get());
		return this;
	}

	public CatalysisCombustionRecipeBuilder combustion() {
		this.machine = Ingredient.of(RegistryManager.COMBUSTION_CHAMBER_ITEM.get());
		return this;
	}

	public CatalysisCombustionRecipeBuilder burnTime(int burnTime) {
		this.burnTime = burnTime;
		return this;
	}

	public CatalysisCombustionRecipeBuilder multiplier(double multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	public CatalysisCombustionRecipe build() {
		return new CatalysisCombustionRecipe(id, ingredient, machine, burnTime, multiplier);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
