package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

public class AnvilBreakdownRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final Identifier id;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();
	Ingredient blacklist = Ingredient.of(EmbersItemTags.BREAKDOWN_BLACKLIST);

	public AnvilBreakdownRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		ItemStack tool = context.getItem(0);
		return tool.isRepairable() && !blacklist.test(tool) && context.getItem(1).isEmpty() && !AugmentUtil.hasHeat(tool) && !Misc.getRepairIngredient(tool.getItem()).isEmpty();
	}

	@Override
	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		Ingredient repairMaterial = Misc.getRepairIngredient(context.getItem(0).getItem());
		if (!repairMaterial.isEmpty())
			return List.of(Misc.getPreferredItem(repairMaterial.getItems()));
		return List.of();
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		visualRecipes.clear();
		for (Holder<Item> holder : BuiltInRegistries.ITEM.asHolderIdMap()) {
			Ingredient repairMaterial = Misc.getRepairIngredient(holder.value());
			ItemStack toolStack = new ItemStack(holder.value());
			if (!repairMaterial.isEmpty() && !repairMaterial.isEmpty() && toolStack.isRepairable() && !blacklist.test(toolStack)) {
				ItemStack brokenTool = toolStack.copy();
				brokenTool.setDamageValue(brokenTool.getMaxDamage() / 2);
				visualRecipes.add(new AnvilDisplayRecipe(id, List.of(Misc.getPreferredItem(repairMaterial.getItems())), List.of(brokenTool), Ingredient.EMPTY));
			}
		}
		return visualRecipes;
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

	public static class Serializer implements RecipeSerializer<AnvilBreakdownRecipe> {
		public AnvilBreakdownRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable AnvilBreakdownRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AnvilBreakdownRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<AnvilBreakdownRecipe> codec() {
			return com.mojang.serialization.MapCodec.unit(new AnvilBreakdownRecipe(null));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AnvilBreakdownRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of((buf, recipe) -> {}, buf -> new AnvilBreakdownRecipe(null));
		}
	}
	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
