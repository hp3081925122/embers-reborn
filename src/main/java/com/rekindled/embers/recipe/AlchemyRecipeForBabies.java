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

public class AlchemyRecipeForBabies extends AlchemyRecipeBase {

	public static final RecipeSerializer<AlchemyRecipeForBabies> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec());

	public AlchemyRecipeForBabies(Identifier id, Ingredient tablet, ArrayList<Ingredient> aspects, ArrayList<Ingredient> inputs, net.minecraft.world.item.ItemStackTemplate output, net.minecraft.world.item.ItemStackTemplate failure) {
		super(id, tablet, aspects, inputs, output, failure);
	}

	public Long cachedSeed = null;
	public ArrayList<Ingredient> code = null;

	@Override
	public ArrayList<Ingredient> getCode(long seed) {
		if (cachedSeed == null || cachedSeed != seed) {
			int incr = 0;
			boolean incorrectCode = true;
			while (incorrectCode) {
				code = super.getCode(seed + incr);
				incorrectCode = false;
				for (Ingredient ingredient : aspects) {
					//only return this recipe if it contains all possible aspecti
					if (!code.contains(ingredient)) {
						incorrectCode = true;
						break;
					}
				}
				incr++;
			}
			cachedSeed = seed;
		}
		return code;
	}

	@Override
	public RecipeSerializer<? extends AlchemyRecipeForBabies> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer {
		
		public com.mojang.serialization.MapCodec<AlchemyRecipeForBabies> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				Ingredient.CODEC.fieldOf("tablet").forGetter(r -> r.tablet),
				Ingredient.CODEC.listOf().fieldOf("aspects").forGetter(r -> r.aspects),
				Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(r -> r.inputs),
				net.minecraft.world.item.ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output),
				net.minecraft.world.item.ItemStackTemplate.CODEC.optionalFieldOf("failure").forGetter(r -> java.util.Optional.of(r.failure))
			).apply(inst, (tablet, aspects, inputs, output, failure) -> new AlchemyRecipeForBabies(null, tablet, new ArrayList<>(aspects), new ArrayList<>(inputs), output, failure.orElseGet(() -> new net.minecraft.world.item.ItemStackTemplate(RegistryManager.ALCHEMICAL_WASTE.get())))));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AlchemyRecipeForBabies> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of((buf, r) -> toNetwork(buf, r), buf -> fromNetwork(null, buf));
		}

				public AlchemyRecipeForBabies fromJson(Identifier recipeId, JsonObject json) {
			Ingredient tablet = Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, json.get("tablet")).result().orElse(null);

			ArrayList<Ingredient> inputs = new ArrayList<>();
			JsonArray inputJson = GsonHelper.getAsJsonArray(json, "inputs", null);
			if (inputJson != null) {
				for (JsonElement element : inputJson) {
					inputs.add(Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, element).result().orElse(null));
				}
			}
			ArrayList<Ingredient> aspects = new ArrayList<>();
			JsonArray aspectJson = GsonHelper.getAsJsonArray(json, "aspects", null);
			if (aspectJson != null) {
				for (JsonElement element : aspectJson) {
					aspects.add(Ingredient.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, element).result().orElse(null));
				}
			}
			net.minecraft.world.item.ItemStackTemplate output = net.minecraft.world.item.ItemStackTemplate.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "output")).getOrThrow();
			net.minecraft.world.item.ItemStackTemplate failure;
			if (json.has("failure")) {
				failure = net.minecraft.world.item.ItemStackTemplate.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, GsonHelper.getAsJsonObject(json, "failure")).getOrThrow();
			} else {
				failure = new net.minecraft.world.item.ItemStackTemplate(RegistryManager.ALCHEMICAL_WASTE.get());
			}
			return new AlchemyRecipeForBabies(recipeId, tablet, aspects, inputs, output, failure);
		}

				public @Nullable AlchemyRecipeForBabies fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {
			Ingredient tablet = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			ArrayList<Ingredient> aspects = buffer.readCollection((i) -> new ArrayList<>(), (buf) -> Ingredient.CONTENTS_STREAM_CODEC.decode((net.minecraft.network.RegistryFriendlyByteBuf) buf));
			ArrayList<Ingredient> inputs = buffer.readCollection((i) -> new ArrayList<>(), (buf) -> Ingredient.CONTENTS_STREAM_CODEC.decode((net.minecraft.network.RegistryFriendlyByteBuf) buf));
			net.minecraft.world.item.ItemStackTemplate output = net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.decode(buffer);
			net.minecraft.world.item.ItemStackTemplate failure = net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.decode(buffer);

			return new AlchemyRecipeForBabies(recipeId, tablet, aspects, inputs, output, failure);
		}

				public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AlchemyRecipeForBabies recipe) {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tablet);
			buffer.writeCollection(recipe.aspects, (buf, input) -> Ingredient.CONTENTS_STREAM_CODEC.encode((net.minecraft.network.RegistryFriendlyByteBuf) buf, input));
			buffer.writeCollection(recipe.inputs, (buf, input) -> Ingredient.CONTENTS_STREAM_CODEC.encode((net.minecraft.network.RegistryFriendlyByteBuf) buf, input));
			net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.output);
			net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.failure);
		}
	}
}
