package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class MeltingRecipe implements IMeltingRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final FluidOutput output;
	public final FluidOutput bonus;

	public MeltingRecipe(ResourceLocation id, Ingredient ingredient, FluidOutput output, FluidOutput bonus) {
		this.id = id;
		this.ingredient = ingredient;
		this.output = output;
		this.bonus = bonus;
	}

	public MeltingRecipe(ResourceLocation id, Ingredient ingredient, FluidOutput output) {
		this(id, ingredient, output, FluidOutput.EMPTY);
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
	public FluidStack getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		return output.getStack();
	}

	@Override
	public FluidStack getBonus() {
		return bonus.getStack();
	}

	@Override
	public FluidStack process(net.minecraft.world.item.crafting.RecipeInput context) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		return output.getStack();
	}
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public FluidStack getDisplayOutput() {
		return output.getStack();
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	public static class Serializer implements RecipeSerializer<MeltingRecipe> {
		public MeltingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {return null; }
		public @Nullable MeltingRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, MeltingRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<MeltingRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.ingredient),
				com.rekindled.embers.util.FluidOutput.CODEC.fieldOf("output").forGetter(r -> r.output),
				com.rekindled.embers.util.FluidOutput.CODEC.optionalFieldOf("bonus", com.rekindled.embers.util.FluidOutput.EMPTY).forGetter(r -> r.bonus)
			).apply(inst, (ingredient, output, bonus) -> new MeltingRecipe(null, ingredient, output, bonus)));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
				com.rekindled.embers.util.FluidOutput.STREAM_CODEC, r -> r.output,
				com.rekindled.embers.util.FluidOutput.STREAM_CODEC, r -> r.bonus,
				(ingredient, output, bonus) -> new MeltingRecipe(null, ingredient, output, bonus));
		}
	}
}
