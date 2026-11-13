package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersItemTags;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilRepairMateriaRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final RecipeSerializer<AnvilRepairMateriaRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();
	Ingredient materia = Ingredient.of(RegistryManager.ISOLATED_MATERIA.get());

	public AnvilRepairMateriaRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		ItemStack tool = context.getItem(0);
		return tool.isDamageableItem() && tool.isDamaged() && !tool.is(EmbersItemTags.MATERIA_BLACKLIST) && materia.test(context.getItem(1));
	}

	@Override
	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		ItemStack result = context.getItem(0).copy();
		result.setDamageValue(Math.max(0, result.getDamageValue() - result.getMaxDamage()));
		return List.of(result);
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		visualRecipes.clear();
		for (Holder<Item> holder : BuiltInRegistries.ITEM.asHolderIdMap()) {
			ItemStack toolStack = new ItemStack(holder.value());
			if (toolStack.isDamageableItem() && !toolStack.is(EmbersItemTags.MATERIA_BLACKLIST)) {
				ItemStack brokenTool = toolStack.copy();
				brokenTool.setDamageValue(brokenTool.getMaxDamage() / 2);
				visualRecipes.add(new AnvilDisplayRecipe(id, List.of(toolStack), List.of(brokenTool), materia));
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
	public RecipeSerializer<? extends AnvilRepairMateriaRecipe> getSerializer() {
		return SERIALIZER;
	}

	public static class Serializer {
		public AnvilRepairMateriaRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable AnvilRepairMateriaRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AnvilRepairMateriaRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<AnvilRepairMateriaRecipe> codec() {
			return com.mojang.serialization.MapCodec.unit(new AnvilRepairMateriaRecipe(null));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AnvilRepairMateriaRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of((buf, recipe) -> {}, buf -> new AnvilRepairMateriaRecipe(null));
		}
	}
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
