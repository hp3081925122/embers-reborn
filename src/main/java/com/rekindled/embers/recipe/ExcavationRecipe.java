package com.rekindled.embers.recipe;

import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class ExcavationRecipe extends BoringRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public ExcavationRecipe(BoringRecipe recipe) {
		super(recipe.id, recipe.result, recipe.minHeight, recipe.maxHeight, recipe.dimensions, recipe.biomes, recipe.requiredBlock, recipe.amountRequired, recipe.chance);
	}

	public ExcavationRecipe(ResourceLocation id, WeightedItemStack result, int minHeight, int maxHeight, HashSet<ResourceLocation> dimensions, HashSet<ResourceLocation> biomes, TagKey<Block> requiredBlock, int amountRequired, double chance) {
		super(id, result, minHeight, maxHeight, dimensions, biomes, requiredBlock, amountRequired, chance);
	}

	@Override
	public RecipeType<?> getType() {
		return RegistryManager.EXCAVATION.get();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<ExcavationRecipe> {
		public ExcavationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {return null; }
		public @Nullable ExcavationRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, ExcavationRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<ExcavationRecipe> codec() {
			return BoringRecipe.SERIALIZER.codec().xmap(
				b -> new ExcavationRecipe(b),
				e -> new BoringRecipe(null, e.result, e.minHeight, e.maxHeight, e.dimensions, e.biomes, e.requiredBlock, e.amountRequired, e.chance));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ExcavationRecipe> streamCodec() {
			return BoringRecipe.SERIALIZER.streamCodec().map(
				b -> new ExcavationRecipe(b),
				e -> new BoringRecipe(null, e.result, e.minHeight, e.maxHeight, e.dimensions, e.biomes, e.requiredBlock, e.amountRequired, e.chance));
		}
	}
}
