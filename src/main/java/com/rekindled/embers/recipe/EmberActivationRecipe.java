package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class EmberActivationRecipe implements IEmberActivationRecipe {

	public static final RecipeSerializer<EmberActivationRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public final Ingredient ingredient;
	public final int ember;

	public EmberActivationRecipe(Identifier id, Ingredient ingredient, int ember) {
		this.id = id;
		this.ingredient = ingredient;
		this.ember = ember;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i)))
				return true;
		}
		return false;
	}

	@Override
	public int getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		return ember;
	}

	@Override
	public int process(net.minecraft.world.item.crafting.RecipeInput context) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		return ember;
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	@Override
	public int getDisplayOutput() {
		return ember;
	}

	public static class Serializer {
		public EmberActivationRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable EmberActivationRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, EmberActivationRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<EmberActivationRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("input").forGetter(r -> r.ingredient),
				com.mojang.serialization.Codec.INT.fieldOf("ember").forGetter(r -> r.ember)
			).apply(inst, (ing, ember) -> new EmberActivationRecipe(null, ing, ember)));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, EmberActivationRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
				net.minecraft.network.codec.ByteBufCodecs.VAR_INT, r -> r.ember,
				(ing, ember) -> new EmberActivationRecipe(null, ing, ember));
		}
	}
}
