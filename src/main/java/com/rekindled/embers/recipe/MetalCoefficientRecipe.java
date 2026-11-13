package com.rekindled.embers.recipe;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class MetalCoefficientRecipe implements IMetalCoefficientRecipe {

	public static final RecipeSerializer<MetalCoefficientRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec()); 

	public final Identifier id;

	public final TagKey<Block> blockTag;
	public final double coefficient;

	public MetalCoefficientRecipe(Identifier id, TagKey<Block> blockTag, double coefficient) {
		this.id = id;
		this.blockTag = blockTag;
		this.coefficient = coefficient;
	}

	@Override
	public boolean matches(BlockStateContext context, Level pLevel) {
		return context.state.is(blockTag);
	}

	public double getCoefficient(BlockStateContext context) {
		return coefficient;
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends MetalCoefficientRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public List<ItemStack> getDisplayInput() {
		List<ItemStack> list = Lists.newArrayList();
		for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag)) {
			list.add(new ItemStack(holder.value()));
		}
		return list;
	}

	@Override
	public double getDisplayCoefficient() {
		return coefficient;
	}

	public static class Serializer {
		public MetalCoefficientRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable MetalCoefficientRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, MetalCoefficientRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<MetalCoefficientRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.resources.Identifier.CODEC.fieldOf("block_tag").forGetter(r -> r.blockTag.location()),
				com.mojang.serialization.Codec.DOUBLE.fieldOf("coefficient").forGetter(r -> r.coefficient)
			).apply(inst, (tag, coeff) -> new MetalCoefficientRecipe(null, net.minecraft.tags.BlockTags.create(tag), coeff)));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, MetalCoefficientRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.network.codec.ByteBufCodecs.fromCodec(net.minecraft.resources.Identifier.CODEC), r -> r.blockTag.location(),
				net.minecraft.network.codec.ByteBufCodecs.DOUBLE, r -> r.coefficient,
				(tag, coeff) -> new MetalCoefficientRecipe(null, net.minecraft.tags.BlockTags.create(tag), coeff));
		}
	}
}
