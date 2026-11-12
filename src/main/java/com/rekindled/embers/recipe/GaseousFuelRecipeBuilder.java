package com.rekindled.embers.recipe;

import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class GaseousFuelRecipeBuilder {

	public Identifier id;
	public FluidIngredient input;
	public int burnTime;
	public double powerMultiplier;

	public static GaseousFuelRecipeBuilder create(Identifier id) {
		GaseousFuelRecipeBuilder builder = new GaseousFuelRecipeBuilder();
		builder.id = id;
		return builder;
	}

	public static GaseousFuelRecipeBuilder create(FluidStack fluidStack) {
		GaseousFuelRecipeBuilder builder = create(BuiltInRegistries.FLUID.getKey(fluidStack.getFluid()));
		builder.input = FluidIngredient.of(fluidStack);
		return builder;
	}

	public static GaseousFuelRecipeBuilder create(Fluid fluid, int amount) {
		return create(new FluidStack(fluid, amount));
	}

	public GaseousFuelRecipeBuilder id(Identifier id) {
		this.id = id;
		return this;
	}

	public GaseousFuelRecipeBuilder domain(String domain) {
		this.id = Identifier.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public GaseousFuelRecipeBuilder folder(String folder) {
		this.id = Identifier.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidIngredient fluid) {
		this.input = fluid;
		return this;
	}

	public GaseousFuelRecipeBuilder input(Fluid fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidStack stack) {
		input(FluidIngredient.of(stack));
		return this;
	}

	public GaseousFuelRecipeBuilder input(TagKey<Fluid> fluid, int amount) {
		input(FluidIngredient.of(fluid, amount));
		return this;
	}

	public GaseousFuelRecipeBuilder input(FluidIngredient... ingredients) {
		input(FluidIngredient.of(ingredients));
		return this;
	}

	public GaseousFuelRecipeBuilder burnTime(int burnTime) {
		this.burnTime = burnTime;
		return this;
	}

	public GaseousFuelRecipeBuilder powerMultiplier(double powerMultiplier) {
		this.powerMultiplier = powerMultiplier;
		return this;
	}

	public GaseousFuelRecipe build() {
		return new GaseousFuelRecipe(id, input, burnTime, powerMultiplier);
	}

	public void save(RecipeOutput consumer) {
		consumer.accept(build().getId(), build(), null);
	}

	// Finished inner class removed - RecipeOutput.accept(RL, Recipe, Advancement) in 1.21.1
}
