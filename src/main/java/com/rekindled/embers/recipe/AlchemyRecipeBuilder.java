package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AlchemyRecipeBuilder {

	public Identifier id;
	public ItemStack output;
	public ItemStack failure = new ItemStack(RegistryManager.ALCHEMICAL_WASTE.get());
	public Ingredient tablet;
	public ArrayList<Ingredient> aspects = new ArrayList<Ingredient>();
	public ArrayList<Ingredient> inputs = new ArrayList<Ingredient>();
	public boolean babbyGames = false;

	public static AlchemyRecipeBuilder create(ItemStack itemStack) {
		AlchemyRecipeBuilder builder = new AlchemyRecipeBuilder();
		builder.output = itemStack;
		builder.id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		return builder;
	}

	public static AlchemyRecipeBuilder create(ItemLike item) {
		return create(new ItemStack(item));
	}

	public AlchemyRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public AlchemyRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public AlchemyRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public AlchemyRecipeBuilder tablet(Ingredient tablet) {
		this.tablet = tablet;
		return this;
	}

	public AlchemyRecipeBuilder tablet(ItemLike... tablet) {
		tablet(Ingredient.of(tablet));
		return this;
	}

	public AlchemyRecipeBuilder tablet(TagKey<Item> tag) {
		tablet(Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(tag)));
		return this;
	}

	public AlchemyRecipeBuilder output(ItemStack output) {
		this.output = output;
		return this;
	}

	public AlchemyRecipeBuilder output(Item item) {
		output(new ItemStack(item));
		return this;
	}

	public AlchemyRecipeBuilder failure(ItemStack failure) {
		this.failure = failure;
		return this;
	}

	public AlchemyRecipeBuilder failure(Item item) {
		failure(new ItemStack(item));
		return this;
	}

	public AlchemyRecipeBuilder aspects(ArrayList<Ingredient> aspects) {
		this.aspects = aspects;
		return this;
	}

	public AlchemyRecipeBuilder inputs(ArrayList<Ingredient> inputs) {
		this.inputs = inputs;
		return this;
	}

	public AlchemyRecipeBuilder aspects(Ingredient... aspects) {
		for (Ingredient aspect : aspects)
			this.aspects.add(aspect);
		return this;
	}

	public AlchemyRecipeBuilder inputs(Ingredient... inputs) {
		for (Ingredient input : inputs)
			this.inputs.add(input);
		return this;
	}

	public AlchemyRecipeBuilder aspects(ItemLike... aspects) {
		for (ItemLike aspect : aspects)
			this.aspects.add(Ingredient.of(aspect));
		return this;
	}

	public AlchemyRecipeBuilder inputs(ItemLike... inputs) {
		for (ItemLike input : inputs)
			this.inputs.add(Ingredient.of(input));
		return this;
	}

	@SafeVarargs
	public final AlchemyRecipeBuilder aspects(TagKey<Item>... aspects) {
		for (TagKey<Item> aspect : aspects)
			this.aspects.add(Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(aspect)));
		return this;
	}

	@SafeVarargs
	public final AlchemyRecipeBuilder inputs(TagKey<Item>... inputs) {
		for (TagKey<Item> input : inputs)
			this.inputs.add(Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(input)));
		return this;
	}

	public AlchemyRecipeBuilder setBabbyGames(boolean babbyGames) {
		this.babbyGames = babbyGames;
		return this;
	}

	public AlchemyRecipeBase build() {
		if (babbyGames)
			return new AlchemyRecipeForBabies(id, tablet, aspects, inputs, output, failure);
		return new AlchemyRecipe(id, tablet, aspects, inputs, output, failure);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId()), build(), null);
	}

	// Finished inner class removed — RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
