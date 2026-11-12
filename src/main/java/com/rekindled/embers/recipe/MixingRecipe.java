package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.HashSet;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MixingRecipe implements IMixingRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final Identifier id;

	public final ArrayList<FluidIngredient> inputs;
	public final FluidOutput output;

	public MixingRecipe(Identifier id, ArrayList<FluidIngredient> inputs, FluidOutput output) {
		this.id = id;
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public boolean matches(MixingContext context, Level pLevel) {
		HashSet<FluidIngredient> remaining = new HashSet<>();
		remaining.addAll(inputs);

		for (IFluidHandler handler : context.fluids) {
			boolean matched = false;
			for (FluidIngredient fluid : remaining) {
				for (FluidStack stack : fluid.getAllFluids()) {
					if (fluid.test(handler.drain(stack, IFluidHandler.FluidAction.SIMULATE))) {
						remaining.remove(fluid);
						matched = true;
						break;
					}
				}
				if (matched)
					break;
			}
			if (!matched && !handler.drain(1, IFluidHandler.FluidAction.SIMULATE).isEmpty())
				return false;
		}
		return remaining.isEmpty();
	}

	@Override
	public FluidStack getOutput(MixingContext context) {
		return output.getStack();
	}

	@Override
	public FluidStack process(MixingContext context) {
		HashSet<FluidIngredient> remaining = new HashSet<>();
		remaining.addAll(inputs);

		for (IFluidHandler handler : context.fluids) {
			for (FluidIngredient fluid : remaining) {
				boolean matched = false;
				for (FluidStack stack : fluid.getAllFluids()) {
					if (fluid.test(handler.drain(stack, IFluidHandler.FluidAction.SIMULATE))) {
						handler.drain(stack, IFluidHandler.FluidAction.EXECUTE);
						matched = true;
						break;
					}
				}
				if (matched)
					break;
			}
		}
		return output.getStack();
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public ArrayList<FluidIngredient> getDisplayInputFluids() {
		return inputs;
	}

	@Override
	public FluidStack getDisplayOutput() {
		return output.getStack();
	}

	public static class Serializer implements RecipeSerializer<MixingRecipe> {
		public MixingRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable MixingRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, MixingRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<MixingRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				com.rekindled.embers.recipe.FluidIngredient.CODEC.listOf().fieldOf("inputs").forGetter(r -> r.inputs),
				com.rekindled.embers.util.FluidOutput.CODEC.fieldOf("output").forGetter(r -> r.output)
			).apply(inst, (inputs, output) -> new MixingRecipe(null, new java.util.ArrayList<>(inputs), output)));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, MixingRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				com.rekindled.embers.recipe.FluidIngredient.STREAM_CODEC.apply(net.minecraft.network.codec.ByteBufCodecs.list()), r -> r.inputs,
				com.rekindled.embers.util.FluidOutput.STREAM_CODEC, r -> r.output,
				(inputs, output) -> new MixingRecipe(null, new java.util.ArrayList<>(inputs), output));
		}
	}
}
