package com.rekindled.embers.recipe;

import java.util.HashSet;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BoringRecipeBuilder {

	public Identifier id;
	public ItemStack result;
	public int weight = 20;
	public int minHeight = Integer.MIN_VALUE;
	public int maxHeight = Integer.MAX_VALUE;
	public HashSet<Identifier> dimensions = new HashSet<>();
	public HashSet<Identifier> biomes = new HashSet<>();
	public TagKey<Block> requiredBlock = null;
	public int amountRequired = 0;
	public double chance = -1.0;
	public RecipeSerializer<?> type = RegistryManager.BORING_SERIALIZER.get();

	public static BoringRecipeBuilder create(ItemStack itemStack) {
		BoringRecipeBuilder builder = new BoringRecipeBuilder();
		builder.result = itemStack;
		builder.id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		return builder;
	}

	public static BoringRecipeBuilder create(Item item) {
		return create(new ItemStack(item));
	}

	public BoringRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public BoringRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public BoringRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public BoringRecipeBuilder type(RecipeSerializer<?> type) {
		this.type = type;
		return this;
	}

	public BoringRecipeBuilder weight(int weight) {
		this.weight = weight;
		return this;
	}

	public BoringRecipeBuilder minHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public BoringRecipeBuilder maxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public BoringRecipeBuilder dimensions(HashSet<Identifier> dimensions) {
		this.dimensions = dimensions;
		return this;
	}

	public BoringRecipeBuilder biomes(HashSet<Identifier> biomes) {
		this.biomes = biomes;
		return this;
	}

	public BoringRecipeBuilder dimension(Identifier... dimensions) {
		for (Identifier dimension : dimensions) {
			if (this.dimensions == null) this.dimensions = new java.util.HashSet<>();
			this.dimensions.add(dimension);
		}
		return this;
	}

	public BoringRecipeBuilder biome(Identifier... biomes) {
		for (Identifier biome : biomes)
			this.biomes.add(biome);
		return this;
	}

	public BoringRecipeBuilder require(TagKey<Block> requiredBlock, int amountRequired) {
		this.requiredBlock = requiredBlock;
		this.amountRequired = amountRequired;
		return this;
	}

	public BoringRecipeBuilder chance(double chance) {
		this.chance = chance;
		return this;
	}

	public BoringRecipe build() {
		return new BoringRecipe(id, new WeightedItemStack(result, weight), minHeight, maxHeight, dimensions, biomes, requiredBlock, amountRequired, chance);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId()), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
