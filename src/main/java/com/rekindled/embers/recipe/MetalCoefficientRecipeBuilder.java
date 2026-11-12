package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

public class MetalCoefficientRecipeBuilder {

	public ResourceLocation id;
	public ResourceLocation blockTag;
	public double coefficient;

	public static MetalCoefficientRecipeBuilder create(ResourceLocation tag) {
		MetalCoefficientRecipeBuilder builder = new MetalCoefficientRecipeBuilder();
		builder.blockTag = tag;
		return builder;
	}

	public static MetalCoefficientRecipeBuilder create(TagKey<Block> tag) {
		MetalCoefficientRecipeBuilder builder = create(tag.location());
		builder.id = tag.location();
		return builder;
	}

	public MetalCoefficientRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public MetalCoefficientRecipeBuilder domain(String domain) {
		this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public MetalCoefficientRecipeBuilder folder(String folder) {
		this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public MetalCoefficientRecipeBuilder coefficient(double coefficient) {
		this.coefficient = coefficient;
		return this;
	}

	public MetalCoefficientRecipe build() {
		return new MetalCoefficientRecipe(id, BlockTags.create(blockTag), coefficient);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
