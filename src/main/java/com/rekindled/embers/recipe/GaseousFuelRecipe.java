package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class GaseousFuelRecipe implements IGaseousFuelRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final FluidIngredient input;
	public final int burnTime;
	public final double powerMultiplier;

	public GaseousFuelRecipe(ResourceLocation id, FluidIngredient input, int burnTime, double powerMultiplier) {
		this.id = id;
		this.input = input;
		this.burnTime = burnTime;
		this.powerMultiplier = powerMultiplier;
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
	public int getBurnTime(FluidHandlerContext context) {
		return burnTime;
	}

	@Override
	public double getPowerMultiplier(FluidHandlerContext context) {
		return powerMultiplier;
	}

	@Override
	public int process(FluidHandlerContext context, int amount) {
		int trueAmount = amount;
		for (FluidStack stack : input.getAllFluids()) {
			FluidStack drainStack = new net.neoforged.neoforge.fluids.FluidStack(stack.getFluid(), stack.getAmount() * amount);
			if (input.test(context.fluid.drain(drainStack, IFluidHandler.FluidAction.SIMULATE))) {
				trueAmount = context.fluid.drain(drainStack, IFluidHandler.FluidAction.EXECUTE).getAmount() / stack.getAmount();
				break;
			}
		}
		return burnTime * trueAmount;
	}
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public FluidIngredient getDisplayInput() {
		return input;
	}

	@Override
	public int getDisplayBurnTime() {
		return burnTime;
	}

	@Override
	public double getDisplayMultiplier() {
		return powerMultiplier;
	}

	public static class Serializer implements RecipeSerializer<GaseousFuelRecipe> {
		public GaseousFuelRecipe fromJson(ResourceLocation recipeId, JsonObject json) {return null; }
		public @Nullable GaseousFuelRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, GaseousFuelRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<GaseousFuelRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				com.rekindled.embers.recipe.FluidIngredient.CODEC.fieldOf("input").forGetter(r -> r.input),
				com.mojang.serialization.Codec.INT.fieldOf("burn_time").forGetter(r -> r.burnTime),
				com.mojang.serialization.Codec.DOUBLE.fieldOf("power_multiplier").forGetter(r -> r.powerMultiplier)
			).apply(inst, (input, bt, pm) -> new GaseousFuelRecipe(null, input, bt, pm)));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GaseousFuelRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				com.rekindled.embers.recipe.FluidIngredient.STREAM_CODEC, r -> r.input,
				net.minecraft.network.codec.ByteBufCodecs.INT, r -> r.burnTime,
				net.minecraft.network.codec.ByteBufCodecs.DOUBLE, r -> r.powerMultiplier,
				(input, bt, pm) -> new GaseousFuelRecipe(null, input, bt, pm));
		}
	}
}
