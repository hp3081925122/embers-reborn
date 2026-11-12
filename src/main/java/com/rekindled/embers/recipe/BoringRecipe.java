package com.rekindled.embers.recipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.util.WeightedItemStack;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BoringRecipe implements IBoringRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final WeightedItemStack result;
	public final int minHeight;
	public final int maxHeight;
	public final HashSet<ResourceLocation> dimensions;
	public final HashSet<ResourceLocation> biomes;
	public final TagKey<Block> requiredBlock;
	public final int amountRequired;
	public final double chance;

	public BoringRecipe(ResourceLocation id, WeightedItemStack result, int minHeight, int maxHeight, HashSet<ResourceLocation> dimensions, HashSet<ResourceLocation> biomes, TagKey<Block> requiredBlock, int amountRequired, double chance) {
		this.id = id;
		this.result = result;
		this.maxHeight = maxHeight;
		this.minHeight = minHeight;
		this.dimensions = dimensions;
		this.biomes = biomes;
		this.requiredBlock = requiredBlock;
		this.amountRequired = amountRequired;
		this.chance = chance;
	}

	@Override
	public boolean matches(BoringContext context, Level pLevel) {
		if (!dimensions.isEmpty() && !dimensions.contains(context.dimension))
			return false;
		if (!biomes.isEmpty() && !biomes.contains(context.biome))
			return false;
		if (amountRequired > 0) {
			int amountLeft = amountRequired;
			for (BlockState state : context.blockStates) {
				if (state.is(requiredBlock))
					amountLeft--;
				if (amountLeft < 1)
					break;
			}
			if (amountLeft > 0)
				return false;
		}
		return context.height >= minHeight && context.height <= maxHeight;
	}

	@Override
	public WeightedItemStack getOutput(BoringContext context) {
		return result;
	}

	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return result.getStack();
	}
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public int getMinHeight() {
		return minHeight;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	@Override
	public Collection<ResourceLocation> getDimensions() {
		return dimensions;
	}

	@Override
	public Collection<ResourceLocation> getBiomes() {
		return biomes;
	}

	@Override
	public double getChance() {
		return chance;
	}

	@Override
	public WeightedItemStack getDisplayOutput() {
		return result;
	}

	public List<ItemStack> getDisplayInput() {
		List<ItemStack> list = Lists.newArrayList();
		for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(requiredBlock)) {
			list.add(new ItemStack(holder.value(), amountRequired));
		}
		return list;
	}

	public static class Serializer implements RecipeSerializer<BoringRecipe> {
		public BoringRecipe fromJson(ResourceLocation recipeId, JsonObject json) {return null; }
		public @Nullable BoringRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, BoringRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<BoringRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.<BoringRecipe>mapCodec(inst -> inst.group(
				net.minecraft.world.item.ItemStack.CODEC.fieldOf("output").forGetter((BoringRecipe r) -> r.result.getStack()),
				com.mojang.serialization.Codec.INT.fieldOf("weight").forGetter((BoringRecipe r) -> r.result.getWeight().asInt()),
				com.mojang.serialization.Codec.INT.optionalFieldOf("min_height", Integer.MIN_VALUE).forGetter((BoringRecipe r) -> r.minHeight),
				com.mojang.serialization.Codec.INT.optionalFieldOf("max_height", Integer.MAX_VALUE).forGetter((BoringRecipe r) -> r.maxHeight),
				net.minecraft.resources.ResourceLocation.CODEC.listOf().xmap(java.util.HashSet::new, java.util.ArrayList::new).optionalFieldOf("dimensions", new java.util.HashSet<>()).forGetter((BoringRecipe r) -> r.dimensions),
				net.minecraft.resources.ResourceLocation.CODEC.listOf().xmap(java.util.HashSet::new, java.util.ArrayList::new).optionalFieldOf("biomes", new java.util.HashSet<>()).forGetter((BoringRecipe r) -> r.biomes),
				net.minecraft.resources.ResourceLocation.CODEC.optionalFieldOf("block_tag", net.minecraft.resources.ResourceLocation.parse("minecraft:air")).forGetter((BoringRecipe r) -> r.requiredBlock != null ? r.requiredBlock.location() : net.minecraft.resources.ResourceLocation.parse("minecraft:air")),
				com.mojang.serialization.Codec.INT.optionalFieldOf("amount_required", 0).forGetter((BoringRecipe r) -> r.amountRequired),
				com.mojang.serialization.Codec.DOUBLE.optionalFieldOf("chance", -1.0).forGetter((BoringRecipe r) -> r.chance)
			).apply(inst, (stack, weight, minH, maxH, dims, biomes2, blockTag, amt, chance2) ->
				new BoringRecipe(null, new com.rekindled.embers.util.WeightedItemStack(stack, weight), minH, maxH, dims, biomes2, net.minecraft.tags.BlockTags.create(blockTag), amt, chance2)));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, BoringRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of(
				(buf, r) -> {
					net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, r.result.getStack());
					buf.writeVarInt(r.result.getWeight().asInt());
					buf.writeVarInt(r.minHeight);
					buf.writeVarInt(r.maxHeight);
					buf.writeCollection(r.dimensions, net.minecraft.network.FriendlyByteBuf::writeResourceLocation);
					buf.writeCollection(r.biomes, net.minecraft.network.FriendlyByteBuf::writeResourceLocation);
					buf.writeResourceLocation(r.requiredBlock != null ? r.requiredBlock.location() : net.minecraft.resources.ResourceLocation.parse("minecraft:air"));
					buf.writeVarInt(r.amountRequired);
					buf.writeDouble(r.chance);
				},
				buf -> {
					net.minecraft.world.item.ItemStack stack = net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
					int weight = buf.readVarInt();
					int minH = buf.readVarInt();
					int maxH = buf.readVarInt();
					java.util.HashSet<net.minecraft.resources.ResourceLocation> dims = buf.readCollection(i -> new java.util.HashSet<>(), net.minecraft.network.FriendlyByteBuf::readResourceLocation);
					java.util.HashSet<net.minecraft.resources.ResourceLocation> biomes = buf.readCollection(i -> new java.util.HashSet<>(), net.minecraft.network.FriendlyByteBuf::readResourceLocation);
					net.minecraft.resources.ResourceLocation blockTag = buf.readResourceLocation();
					int amt = buf.readVarInt();
					double chance = buf.readDouble();
					return new BoringRecipe(null, new com.rekindled.embers.util.WeightedItemStack(stack, weight), minH, maxH, dims, biomes, net.minecraft.tags.BlockTags.create(blockTag), amt, chance);
				});
		}
	}
}
