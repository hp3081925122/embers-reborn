package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * Builds a recipe consumer wrapper, which adds extra conditions to wrap recipes.
 * Note: In 1.21.1, RecipeOutput.accept() takes (RL, Recipe, AdvancementHolder).
 * Conditions are now handled via ConditionalOps or ICondition on the recipe itself.
 * This class is a stub for recipe conditions support.
 */
public class ConsumerWrapperBuilder {
	private final List<ICondition> conditions = new ArrayList<>();
	@Nullable
	private final RecipeSerializer<?> override;
	@Nullable
	private final ResourceLocation overrideName;

	private ConsumerWrapperBuilder(@Nullable RecipeSerializer<?> override, @Nullable ResourceLocation overrideName) {
		this.override = override;
		this.overrideName = overrideName;
	}

	public static ConsumerWrapperBuilder wrap() {
		return new ConsumerWrapperBuilder(null, null);
	}

	public static ConsumerWrapperBuilder wrap(RecipeSerializer<?> override) {
		return new ConsumerWrapperBuilder(override, null);
	}

	public static ConsumerWrapperBuilder wrap(ResourceLocation override) {
		return new ConsumerWrapperBuilder(null, override);
	}

	public ConsumerWrapperBuilder addCondition(ICondition condition) {
		conditions.add(condition);
		return this;
	}

	/** Build a RecipeOutput that delegates to the original consumer. */
	public RecipeOutput build(RecipeOutput consumer) {
		return new RecipeOutput() {
			@Override
			public void accept(ResourceLocation id, net.minecraft.world.item.crafting.Recipe<?> recipe, @Nullable net.minecraft.advancements.AdvancementHolder advancement, net.neoforged.neoforge.common.conditions.ICondition... iConditions) {
				consumer.accept(id, recipe, advancement, iConditions);
			}
			@Override
			public net.minecraft.advancements.Advancement.Builder advancement() {
				return consumer.advancement();
			}
		};
	}
}