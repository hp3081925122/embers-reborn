package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.StampingRecipe.TagAmount;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class StampingRecipeBuilder {

	public ResourceLocation id;
	public Ingredient stamp;
	public Ingredient input = Ingredient.EMPTY;
	public FluidIngredient fluid = FluidIngredient.EMPTY;
	public Either<ItemStack, TagAmount> output;

	public static StampingRecipeBuilder create(ItemStack itemStack) {
		StampingRecipeBuilder builder = new StampingRecipeBuilder();
		builder.output = Either.left(itemStack);
		builder.id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		return builder;
	}

	public static StampingRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public static StampingRecipeBuilder create(TagKey<Item> tag, int amount) {
		StampingRecipeBuilder builder = new StampingRecipeBuilder();
		builder.output = Either.right(new TagAmount(tag, amount));
		builder.id = tag.location();
		return builder;
	}

	public static StampingRecipeBuilder create(TagKey<Item> tag) {
		return create(tag, 1);
	}

	public StampingRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public StampingRecipeBuilder domain(String domain) {
		this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public StampingRecipeBuilder folder(String folder) {
		this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public StampingRecipeBuilder stamp(Ingredient stamp) {
		this.stamp = stamp;
		return this;
	}

	public StampingRecipeBuilder stamp(ItemLike... stamp) {
		stamp(Ingredient.of(stamp));
		return this;
	}

	public StampingRecipeBuilder stamp(TagKey<Item> tag) {
		stamp(Ingredient.of(tag));
		return this;
	}

	public StampingRecipeBuilder input(Ingredient input) {
		this.input = input;
		return this;
	}

	public StampingRecipeBuilder input(ItemLike... input) {
		input(Ingredient.of(input));
		return this;
	}

	public StampingRecipeBuilder input(TagKey<Item> tag) {
		input(Ingredient.of(tag));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidIngredient fluid) {
		this.fluid = fluid;
		return this;
	}

	public StampingRecipeBuilder fluid(Fluid fluid, int amount) {
		fluid(FluidIngredient.of(fluid, amount));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidStack stack) {
		fluid(FluidIngredient.of(stack));
		return this;
	}

	public StampingRecipeBuilder fluid(TagKey<Fluid> fluid, int amount) {
		fluid(FluidIngredient.of(fluid, amount));
		return this;
	}

	public StampingRecipeBuilder fluid(FluidIngredient... ingredients) {
		fluid(FluidIngredient.of(ingredients));
		return this;
	}

	public StampingRecipe build() {
		return new StampingRecipe(id, stamp, input, fluid, output);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
