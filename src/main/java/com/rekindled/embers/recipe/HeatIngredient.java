package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
public class HeatIngredient implements ICustomIngredient {

	public static net.neoforged.neoforge.common.crafting.IngredientType<HeatIngredient> TYPE;

	/** MapCodec: {"base":<ingredient>,"inverted":<bool>} */
	public static final com.mojang.serialization.MapCodec<HeatIngredient> CODEC =
		com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
			net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("base").forGetter((HeatIngredient h) -> h.base),
			com.mojang.serialization.Codec.BOOL.optionalFieldOf("inverted", false).forGetter((HeatIngredient h) -> h.inverted)
		).apply(inst, (base, inverted) -> new HeatIngredient(base, inverted)));

	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, HeatIngredient> STREAM_CODEC =
		net.minecraft.network.codec.StreamCodec.composite(
			net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, (HeatIngredient h) -> h.base,
			net.minecraft.network.codec.ByteBufCodecs.BOOL, (HeatIngredient h) -> h.inverted,
			(base, inverted) -> new HeatIngredient(base, inverted));

	private final Ingredient base;
	private ItemStack[] heatedMatchingStacks;
	private IntList packedMatchingStacks;
	private boolean inverted;

	public HeatIngredient(Ingredient base, boolean inverted) {
		this.base = base;
		this.inverted = inverted;
	}

	public static HeatIngredient of(Ingredient base, boolean inverted) {
		return new HeatIngredient(base, inverted);
	}

	public static HeatIngredient of(Ingredient base) {
		return new HeatIngredient(base, false);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return base.test(stack) && (inverted ^ AugmentUtil.hasHeat(stack));
	}

	@Override
	public java.util.stream.Stream<net.minecraft.world.item.ItemStack> getItems() {
		if (!inverted) {
			if (this.heatedMatchingStacks == null) {
				ItemStack[] items = base.getItems();
				this.heatedMatchingStacks = new ItemStack[items.length];
				for (int i = 0; i < items.length; i ++) {
					ItemStack stack = items[i].copy();
					AugmentUtil.setHeat(stack, 0);
					this.heatedMatchingStacks[i] = stack;
				}
			}
			return java.util.Arrays.stream(heatedMatchingStacks);
		}
		return java.util.Arrays.stream(base.getItems());
	}


	@Override
	public boolean isSimple() {
		return base.isSimple();
	}




	@Override
	public net.neoforged.neoforge.common.crafting.IngredientType<HeatIngredient> getType() { return com.rekindled.embers.RegistryManager.HEAT_INGREDIENT_TYPE.get(); }

}
