package com.rekindled.embers.util;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 26.1 forbids empty Ingredients, so "no ingredient" is represented by null.
 * test() keeps the 1.20.1 Ingredient.EMPTY semantics: an empty ingredient matches only an empty stack.
 */
public final class Ingredients {
	private Ingredients() {}

	public static boolean isEmpty(@Nullable Ingredient ingredient) {
		return ingredient == null || ingredient.isEmpty();
	}

	public static boolean test(@Nullable Ingredient ingredient, ItemStack stack) {
		if (isEmpty(ingredient))
			return stack.isEmpty();
		return ingredient.test(stack);
	}

	/** Like test(), but an empty ingredient never matches (for "is this a valid repair item" checks). */
	public static boolean testNonEmpty(@Nullable Ingredient ingredient, ItemStack stack) {
		return !isEmpty(ingredient) && ingredient.test(stack);
	}

	public static ItemStack[] stacks(@Nullable Ingredient ingredient) {
		if (isEmpty(ingredient))
			return new ItemStack[0];
		return ingredient.items().map(ItemStack::new).toArray(ItemStack[]::new);
	}

	public static Optional<Ingredient> optional(@Nullable Ingredient ingredient) {
		return isEmpty(ingredient) ? Optional.empty() : Optional.of(ingredient);
	}
}
