package com.rekindled.embers.recipe;

import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

public class ExcavationRecipe extends BoringRecipe {

	public static final RecipeSerializer<ExcavationRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec());

	public ExcavationRecipe(BoringRecipe recipe) {
		super(recipe.id, recipe.result, recipe.minHeight, recipe.maxHeight, recipe.dimensions, recipe.biomes, recipe.requiredBlock, recipe.amountRequired, recipe.chance);
	}

	public ExcavationRecipe(Identifier id, WeightedItemStack result, int minHeight, int maxHeight, HashSet<Identifier> dimensions, HashSet<Identifier> biomes, TagKey<Block> requiredBlock, int amountRequired, double chance) {
		super(id, result, minHeight, maxHeight, dimensions, biomes, requiredBlock, amountRequired, chance);
	}

	@Override
	public RecipeType<IBoringRecipe> getType() {
		return RegistryManager.EXCAVATION.get();
	}

	@Override
	public RecipeSerializer<? extends ExcavationRecipe> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer {
		public ExcavationRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable ExcavationRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, ExcavationRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<ExcavationRecipe> codec() {
			return BoringRecipe.SERIALIZER.codec().xmap(
				b -> new ExcavationRecipe(b),
				e -> new BoringRecipe(null, e.result, e.minHeight, e.maxHeight, e.dimensions, e.biomes, e.requiredBlock, e.amountRequired, e.chance));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ExcavationRecipe> streamCodec() {
			return BoringRecipe.SERIALIZER.streamCodec().map(
				b -> new ExcavationRecipe(b),
				e -> new BoringRecipe(null, e.result, e.minHeight, e.maxHeight, e.dimensions, e.biomes, e.requiredBlock, e.amountRequired, e.chance));
		}
	}
}
