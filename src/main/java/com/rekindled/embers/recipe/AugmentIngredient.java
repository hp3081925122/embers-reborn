package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
public class AugmentIngredient implements ICustomIngredient {

	public static net.neoforged.neoforge.common.crafting.IngredientType<AugmentIngredient> TYPE;

	/** MapCodec: {"base":<ingredient>,"augment":<RL>,"level":<int>,"inverted":<bool>} */
	public static final com.mojang.serialization.MapCodec<AugmentIngredient> CODEC =
		com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
			net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("base").forGetter((AugmentIngredient a) -> a.base),
			net.minecraft.resources.Identifier.CODEC.fieldOf("augment").forGetter((AugmentIngredient a) -> a.augment.getName()),
			com.mojang.serialization.Codec.INT.optionalFieldOf("level", 1).forGetter((AugmentIngredient a) -> a.level),
			com.mojang.serialization.Codec.BOOL.optionalFieldOf("inverted", false).forGetter((AugmentIngredient a) -> a.inverted)
		).apply(inst, (base, augRL, level, inverted) ->
			new AugmentIngredient(base, com.rekindled.embers.api.augment.AugmentUtil.getAugment(augRL), level, inverted)));

	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AugmentIngredient> STREAM_CODEC =
		net.minecraft.network.codec.StreamCodec.composite(
			net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, (AugmentIngredient a) -> a.base,
			net.minecraft.network.codec.ByteBufCodecs.fromCodec(net.minecraft.resources.Identifier.CODEC), (AugmentIngredient a) -> a.augment.getName(),
			net.minecraft.network.codec.ByteBufCodecs.VAR_INT, (AugmentIngredient a) -> a.level,
			net.minecraft.network.codec.ByteBufCodecs.BOOL, (AugmentIngredient a) -> a.inverted,
			(base, augRL, level, inverted) -> new AugmentIngredient(base, com.rekindled.embers.api.augment.AugmentUtil.getAugment(augRL), level, inverted));

	private final Ingredient base;
	private final IAugment augment;
	private final int level;
	private boolean inverted;

	public AugmentIngredient(Ingredient base, IAugment augment, int level, boolean inverted) {
		this.base = base;
		this.augment = augment;
		this.level = level;
		this.inverted = inverted;
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, int level, boolean inverted) {
		return new AugmentIngredient(base, augment, level, inverted);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, boolean inverted) {
		return new AugmentIngredient(base, augment, 1, inverted);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, int level) {
		return new AugmentIngredient(base, augment, level, false);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment) {
		return new AugmentIngredient(base, augment, 1, false);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return base.test(stack) && AugmentUtil.hasHeat(stack) && (inverted ^ AugmentUtil.getAugmentLevel(stack, augment) >= level);
	}

	@Override
	public java.util.stream.Stream<net.minecraft.core.Holder<net.minecraft.world.item.Item>> items() {
		return base.items();
	}

	/** JEI/recipe-book display: base items with the augment applied (or heat cleared if inverted), like 1.20.1 getItems(). */
	@Override
	public net.minecraft.world.item.crafting.display.SlotDisplay display() {
		java.util.List<net.minecraft.world.item.crafting.display.SlotDisplay> list = new java.util.ArrayList<>();
		base.items().forEach(h -> {
			ItemStack stack = new ItemStack(h);
			if (inverted)
				AugmentUtil.setHeat(stack, 0);
			else {
				AugmentUtil.setLevel(stack, AugmentUtil.getLevel(stack) + level);
				AugmentUtil.addAugment(stack, ItemStack.EMPTY, augment);
				AugmentUtil.setAugmentLevel(stack, augment, level);
			}
			list.add(new net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(stack)));
		});
		return new net.minecraft.world.item.crafting.display.SlotDisplay.Composite(list);
	}

	/** Matching depends on augment data components, so vanilla must call test(); never simple. */
	@Override
	public boolean isSimple() {
		return false;
	}




	@Override
	public net.neoforged.neoforge.common.crafting.IngredientType<AugmentIngredient> getType() { return com.rekindled.embers.RegistryManager.AUGMENT_INGREDIENT_TYPE.get(); }

}
