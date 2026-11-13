package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CatalysisCombustionRecipe implements ICatalysisCombustionRecipe {

	public static final RecipeSerializer<CatalysisCombustionRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public final Ingredient ingredient;
	public final Ingredient machine;
	public final int burnTime;
	public final double multiplier;

	public CatalysisCombustionRecipe(Identifier id, Ingredient ingredient, Ingredient machine, int burnTime, double multiplier) {
		this.id = id;
		this.ingredient = ingredient;
		this.machine = machine;
		this.burnTime = burnTime;
		this.multiplier = multiplier;
	}

	@Override
	public boolean matches(CatalysisCombustionContext context, Level pLevel) {
		if (machine.test(context.machine)) {
			for (int i = 0; i < context.size(); i++) {
				if (ingredient.test(context.getItem(i)))
					return true;
			}
		}
		return false;
	}

	@Override
	public int getBurnTIme(CatalysisCombustionContext context) {
		return burnTime;
	}

	@Override
	public double getmultiplier(CatalysisCombustionContext context) {
		return multiplier;
	}

	@Override
	public int process(CatalysisCombustionContext context) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		return burnTime;
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends CatalysisCombustionRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	@Override
	public Ingredient getDisplayMachine() {
		return machine;
	}

	@Override
	public int getDisplayTime() {
		return burnTime;
	}

	@Override
	public double getDisplayMultiplier() {
		return multiplier;
	}

	public static class Serializer {
		public CatalysisCombustionRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable CatalysisCombustionRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, CatalysisCombustionRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<CatalysisCombustionRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("input").forGetter(r -> r.ingredient),
				net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("machine").forGetter(r -> r.machine),
				com.mojang.serialization.Codec.INT.fieldOf("burn_time").forGetter(r -> r.burnTime),
				com.mojang.serialization.Codec.DOUBLE.fieldOf("multiplier").forGetter(r -> r.multiplier)
			).apply(inst, (ing, mach, bt, mul) -> new CatalysisCombustionRecipe(null, ing, mach, bt, mul)));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, CatalysisCombustionRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.machine,
				net.minecraft.network.codec.ByteBufCodecs.VAR_INT, r -> r.burnTime,
				net.minecraft.network.codec.ByteBufCodecs.DOUBLE, r -> r.multiplier,
				(ing, mach, bt, mul) -> new CatalysisCombustionRecipe(null, ing, mach, bt, mul));
		}
	}
}
