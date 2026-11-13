package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class AnvilAugmentRecipe implements IDawnstoneAnvilRecipe, IVisuallySplitRecipe<IDawnstoneAnvilRecipe> {

	public static final RecipeSerializer<AnvilAugmentRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public final Ingredient tool;
	public final Ingredient input;
	public final IAugment augment;

	public static List<IDawnstoneAnvilRecipe> visualRecipes = new ArrayList<IDawnstoneAnvilRecipe>();

	public AnvilAugmentRecipe(Identifier id, Ingredient tool, Ingredient input, IAugment augment) {
		this.id = id;
		this.tool = tool;
		this.input = input;
		this.augment = augment;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.RecipeInput context, Level pLevel) {
		if (augment.countTowardsTotalLevel()) {
			ItemStack toolStack = context.getItem(0);
			return tool.test(toolStack) && input.test(context.getItem(1)) && AugmentUtil.getLevel(toolStack) > AugmentUtil.getTotalAugmentLevel(toolStack);
		} else 
			return tool.test(context.getItem(0)) && input.test(context.getItem(1));
	}

	@Override
	public List<ItemStack> getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		ItemStack result = context.getItem(0).copy();
		AugmentUtil.addAugment(result, context.getItem(1), augment);
		return List.of(result);
	}

	@Override
	public List<IDawnstoneAnvilRecipe> getVisualRecipes() {
		visualRecipes.clear();
		for (ItemStack stack : tool.getItems()) {
			ItemStack leveledTool = stack.copy();
			if (augment.countTowardsTotalLevel()) {
				AugmentUtil.setLevel(leveledTool, AugmentUtil.getLevel(leveledTool) + 1);
			}
			ItemStack augmentedTool = leveledTool.copy();
			AugmentUtil.addAugment(augmentedTool, ItemStack.EMPTY, augment);
			visualRecipes.add(new AnvilDisplayRecipe(id, List.of(augmentedTool), List.of(leveledTool), input));
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

	public static class Serializer {
		public AnvilAugmentRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable AnvilAugmentRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, AnvilAugmentRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<AnvilAugmentRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("tool").forGetter(r -> r.tool),
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
				net.minecraft.resources.Identifier.CODEC.fieldOf("augment").forGetter(r -> r.augment.getName())
			).apply(inst, (tool, input, augRL) -> new AnvilAugmentRecipe(null, tool, input, com.rekindled.embers.api.augment.AugmentUtil.getAugment(augRL))));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, AnvilAugmentRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.tool,
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
				net.minecraft.network.codec.ByteBufCodecs.fromCodec(net.minecraft.resources.Identifier.CODEC), r -> r.augment.getName(),
				(tool, input, augRL) -> new AnvilAugmentRecipe(null, tool, input, com.rekindled.embers.api.augment.AugmentUtil.getAugment(augRL)));
		}
	}
	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.RecipeInput input, net.minecraft.core.HolderLookup.Provider provider) { return getResultItem(provider); }
	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider provider) { return net.minecraft.world.item.ItemStack.EMPTY; }
}
