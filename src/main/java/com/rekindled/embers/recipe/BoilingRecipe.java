package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class BoilingRecipe implements IBoilingRecipe {

	public static final RecipeSerializer<BoilingRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public final FluidIngredient input;
	public final FluidOutput output;

	public BoilingRecipe(Identifier id, FluidIngredient input, FluidOutput output) {
		this.id = id;
		this.input = input;
		this.output = output;
	}

	@Override
	public boolean matches(FluidHandlerContext context, Level pLevel) {
		for (FluidStack stack : input.getAllFluids()) {
			if (input.test(context.fluid.drain(stack, IFluidHandler.FluidAction.SIMULATE))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public FluidStack getOutput(FluidHandlerContext context) {
		return output.getStack();
	}

	@Override
	public FluidStack process(FluidHandlerContext context, int amount) {
		int trueAmount = amount;
		for (FluidStack stack : input.getAllFluids()) {
			FluidStack drainStack = new net.neoforged.neoforge.fluids.FluidStack(stack.getFluid(), stack.getAmount() * amount);
			if (input.test(context.fluid.drain(drainStack, IFluidHandler.FluidAction.SIMULATE))) {
				trueAmount = context.fluid.drain(drainStack, IFluidHandler.FluidAction.EXECUTE).getAmount() / stack.getAmount();
				break;
			}
		}
		return new net.neoforged.neoforge.fluids.FluidStack(output.getStack().getFluid(), output.getStack().getAmount() * trueAmount);
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends BoilingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public FluidIngredient getDisplayInput() {
		return input;
	}

	@Override
	public FluidStack getDisplayOutput() {
		return output.getStack();
	}

	public static class Serializer {
		public BoilingRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable BoilingRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, BoilingRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<BoilingRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				com.rekindled.embers.recipe.FluidIngredient.CODEC.fieldOf("input").forGetter(r -> r.input),
				com.rekindled.embers.util.FluidOutput.CODEC.fieldOf("output").forGetter(r -> r.output)
			).apply(inst, (input, output) -> new BoilingRecipe(null, input, output)));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, BoilingRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				com.rekindled.embers.recipe.FluidIngredient.STREAM_CODEC, r -> r.input,
				com.rekindled.embers.util.FluidOutput.STREAM_CODEC, r -> r.output,
				(input, output) -> new BoilingRecipe(null, input, output));
		}
	}
}
