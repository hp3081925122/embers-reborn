package com.rekindled.embers.recipe;

import java.util.function.Consumer;

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

public class BoilingRecipeBuilder {

	public Identifier id;
	public FluidIngredient input;
	public FluidOutput output;

	public static BoilingRecipeBuilder create(FluidStack fluidStack) {
		BoilingRecipeBuilder builder = new BoilingRecipeBuilder();
		builder.output = new FluidOutput(net.neoforged.neoforge.fluids.FluidStackTemplate.fromNonEmptyStack(fluidStack));
		builder.id = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
		return builder;
	}

	public static BoilingRecipeBuilder create(Fluid fluid, int amount) {
		BoilingRecipeBuilder builder = new BoilingRecipeBuilder();
		builder.output = new FluidOutput(fluid, amount);
		builder.id = BuiltInRegistries.FLUID.getKey(fluid);
		return builder;
	}

	public static BoilingRecipeBuilder create(TagKey<Fluid> tag, int amount) {
		BoilingRecipeBuilder builder = new BoilingRecipeBuilder();
		builder.output = new FluidOutput(tag, amount);
		builder.id = tag.location();
		return builder;
	}

	public BoilingRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public BoilingRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public BoilingRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public BoilingRecipeBuilder input(FluidIngredient fluid) {
		this.input = fluid;
		return this;
	}

	public BoilingRecipeBuilder input(Fluid fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public BoilingRecipeBuilder input(FluidStack stack) {
		input(FluidIngredient.of(stack));
		return this;
	}

	public BoilingRecipeBuilder input(TagKey<Fluid> fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public BoilingRecipeBuilder input(FluidIngredient... ingredients) {
		input(FluidIngredient.of(ingredients));
		return this;
	}

	public BoilingRecipeBuilder output(FluidStack output) {
		this.output = new FluidOutput(net.neoforged.neoforge.fluids.FluidStackTemplate.fromNonEmptyStack(output));
		return this;
	}

	public BoilingRecipeBuilder output(Fluid fluid, int amount) {
		this.output = new FluidOutput(fluid, amount);
		return this;
	}

	public BoilingRecipeBuilder output(TagKey<Fluid> tag, int amount) {
		this.output = new FluidOutput(tag, amount);
		return this;
	}

	public BoilingRecipe build() {
		return new BoilingRecipe(id, input, output);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId()), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
