package com.rekindled.embers.recipe;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilAugmentRemoveRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final RecipeSerializer<AnvilAugmentRemoveRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public AnvilAugmentRemoveRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		if (context.getItem(1).isEmpty()) {
			for (IAugment augment : AugmentUtil.getAugments(context.getItem(0))) {
				if (augment.canRemove())
					return true;
			}
		}
		return false;
	}

	@Override
	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		ItemStack tool = context.getItem(0).copy();
		List<ItemStack> outputs = AugmentUtil.removeAllAugments(tool);
		outputs.add(tool);
		return outputs;
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputBottom() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayInputTop() {
		return List.of();
	}

	@Override
	public List<ItemStack> getDisplayOutput() {
		return List.of();
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer {
		public AnvilAugmentRemoveRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable AnvilAugmentRemoveRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AnvilAugmentRemoveRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<AnvilAugmentRemoveRecipe> codec() {
			return com.mojang.serialization.MapCodec.unit(new AnvilAugmentRemoveRecipe(null));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AnvilAugmentRemoveRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of((buf, recipe) -> {}, buf -> new AnvilAugmentRemoveRecipe(null));
		}
	}
	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
