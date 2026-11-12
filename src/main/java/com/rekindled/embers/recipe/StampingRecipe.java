package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.util.Misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;


public class StampingRecipe implements IStampingRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final Ingredient stamp;
	public final Ingredient input;
	public final FluidIngredient fluid;

	public final Either<ItemStack, TagAmount> output;

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, TagAmount output) {
		this(id, stamp, input, fluid, Either.right(output));
	}

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, ItemStack output) {
		this(id, stamp, input, fluid, Either.left(output));
	}

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, Either<ItemStack, TagAmount> output) {
		this.id = id;
		this.stamp = stamp;
		this.input = input;
		this.fluid = fluid;
		this.output = output;
	}

	@Override
	public boolean matches(StampingContext context, Level pLevel) {
		for (int i = 0; i < context.size(); i++) {
			if (input.test(context.getItem(i))) {
				if (stamp.test(context.stamp)) {
					if (fluid.test(context.fluids.getFluidInTank(0)))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemStack getOutput(net.minecraft.world.item.crafting.RecipeInput context) {
		return getResultItem();
	}

	@Override
	public ItemStack assemble(StampingContext context, net.minecraft.core.HolderLookup.Provider registry) {
		for (int i = 0; i < context.size(); i++) {
			if (input.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		for (FluidStack stack : fluid.getAllFluids()) {
			if (fluid.test(context.fluids.drain(stack, IFluidHandler.FluidAction.SIMULATE))) {
				context.fluids.drain(stack, IFluidHandler.FluidAction.EXECUTE);
				break;
			}
		}
		return this.getOutput(context);
	}

	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public ItemStack getResultItem() {
		if (output.left().isPresent())
			return output.left().get();
		return new ItemStack(Misc.getTaggedItem(output.right().get().tag), output.right().get().amount);
	}

	@Override
	public FluidIngredient getDisplayInputFluid() {
		return fluid;
	}

	@Override
	public Ingredient getDisplayInput() {
		return input;
	}

	@Override
	public Ingredient getDisplayStamp() {
		return stamp;
	}

	public static class TagAmount {
		public TagKey<Item> tag;
		public int amount;

		public TagAmount(TagKey<Item> tag, int amount) {
			this.tag = tag;
			this.amount = amount;
		}
	}

	// Output Either codec / stream codec — declared before Serializer so inner class can see them
	public static final com.mojang.serialization.Codec<Either<ItemStack, TagAmount>> OUTPUT_CODEC =
		com.mojang.serialization.Codec.either(
			net.minecraft.world.item.ItemStack.CODEC,
			com.mojang.serialization.codecs.RecordCodecBuilder.<TagAmount>create(inst -> inst.group(
				net.minecraft.resources.ResourceLocation.CODEC.fieldOf("tag").forGetter(ta -> ta.tag.location()),
				com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(ta -> ta.amount)
			).apply(inst, (tag, count) -> new TagAmount(ItemTags.create(tag), count))));

	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, Either<ItemStack, TagAmount>> OUTPUT_STREAM_CODEC =
		net.minecraft.network.codec.ByteBufCodecs.<net.minecraft.network.RegistryFriendlyByteBuf, ItemStack, TagAmount>either(
			net.minecraft.world.item.ItemStack.OPTIONAL_STREAM_CODEC,
			net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.network.codec.ByteBufCodecs.fromCodec(net.minecraft.resources.ResourceLocation.CODEC), ta -> ta.tag.location(),
				net.minecraft.network.codec.ByteBufCodecs.VAR_INT, ta -> ta.amount,
				(tag, count) -> new TagAmount(ItemTags.create(tag), count)));

	public static class Serializer implements RecipeSerializer<StampingRecipe> {
		public StampingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {return null; }
		public @Nullable StampingRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, StampingRecipe recipe) {}

		@Override
		public com.mojang.serialization.MapCodec<StampingRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("stamp").forGetter(r -> r.stamp),
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.optionalFieldOf("input", net.minecraft.world.item.crafting.Ingredient.EMPTY).forGetter(r -> r.input),
				FluidIngredient.CODEC.optionalFieldOf("fluid", FluidIngredient.EMPTY).forGetter(r -> r.fluid),
				StampingRecipe.OUTPUT_CODEC.fieldOf("output").forGetter(r -> r.output)
			).apply(inst, (s, i, f, o) -> new StampingRecipe(null, s, i, f, o)));
		}

		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, StampingRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.stamp,
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
				FluidIngredient.STREAM_CODEC.cast(), r -> r.fluid,
				StampingRecipe.OUTPUT_STREAM_CODEC, r -> r.output,
				(s, i, f, o) -> new StampingRecipe(null, s, i, f, o));
		}
	}
}
