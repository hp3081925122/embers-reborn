package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.FluidOutput;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MixingRecipeBuilder {

	public Identifier id;
	public ArrayList<FluidIngredient> inputs = new ArrayList<>();
	public FluidOutput output;

	public static MixingRecipeBuilder create(FluidStack fluidStack) {
		MixingRecipeBuilder builder = new MixingRecipeBuilder();
		builder.output = new FluidOutput(fluidStack);
		builder.id = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
		return builder;
	}

	public static MixingRecipeBuilder create(Fluid fluid, int amount) {
		return create(new FluidStack(fluid, amount));
	}

	public static MixingRecipeBuilder create(TagKey<Fluid> tag, int amount) {
		MixingRecipeBuilder builder = new MixingRecipeBuilder();
		builder.output = new FluidOutput(tag, amount);
		builder.id = tag.identifier();
		return builder;
	}

	public MixingRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public MixingRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public MixingRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public MixingRecipeBuilder input(FluidIngredient fluid) {
		this.inputs.add(fluid);
		return this;
	}

	public MixingRecipeBuilder input(Fluid fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public MixingRecipeBuilder input(FluidStack stack) {
		input(FluidIngredient.of(stack));
		return this;
	}

	public MixingRecipeBuilder input(TagKey<Fluid> fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public MixingRecipeBuilder input(FluidIngredient... ingredients) {
		input(FluidIngredient.of(ingredients));
		return this;
	}

	public MixingRecipeBuilder output(FluidStack output) {
		this.output = new FluidOutput(output);
		return this;
	}

	public MixingRecipeBuilder output(Fluid fluid, int amount) {
		output(new FluidStack(fluid, amount));
		return this;
	}

	public MixingRecipeBuilder output(TagKey<Fluid> tag, int amount) {
		this.output = new FluidOutput(tag, amount);
		return this;
	}

	public MixingRecipe build() {
		return new MixingRecipe(id, inputs, output);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
