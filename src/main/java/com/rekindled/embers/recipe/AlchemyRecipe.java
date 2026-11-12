package com.rekindled.embers.recipe;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class AlchemyRecipe extends AlchemyRecipeBase {

	public static final Serializer SERIALIZER = new Serializer();

	public AlchemyRecipe(Identifier id, Ingredient tablet, ArrayList<Ingredient> aspects, ArrayList<Ingredient> inputs, ItemStack output, ItemStack failure) {
		super(id, tablet, aspects, inputs, output, failure);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer implements RecipeSerializer<AlchemyRecipe> {
		public AlchemyRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable AlchemyRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AlchemyRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<AlchemyRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("tablet").forGetter(r -> r.tablet),
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.listOf().fieldOf("aspects").forGetter(r -> r.aspects),
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.listOf().fieldOf("inputs").forGetter(r -> r.inputs),
				net.minecraft.world.item.ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
				net.minecraft.world.item.ItemStack.CODEC.optionalFieldOf("failure", new net.minecraft.world.item.ItemStack(com.rekindled.embers.RegistryManager.ALCHEMICAL_WASTE.get())).forGetter(r -> r.failure)
			).apply(inst, (tablet, aspects, inputs, output, failure) -> new AlchemyRecipe(null, tablet, new java.util.ArrayList<>(aspects), new java.util.ArrayList<>(inputs), output, failure)));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AlchemyRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.tablet,
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), r -> r.aspects,
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), r -> r.inputs,
				net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC, r -> r.output,
				net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC, r -> r.failure,
				(tablet, aspects, inputs, output, failure) -> new AlchemyRecipe(null, tablet, new java.util.ArrayList<>(aspects), new java.util.ArrayList<>(inputs), output, failure));
		}
	}
}
