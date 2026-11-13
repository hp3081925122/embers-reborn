package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.util.Misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
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

	public final Identifier id;

	public final Ingredient stamp;
	public final Ingredient input;
	public final FluidIngredient fluid;

	public final Either<net.minecraft.world.item.ItemStackTemplate, TagAmount> output;

	public StampingRecipe(Identifier id, Ingredient stamp, Ingredient input, FluidIngredient fluid, TagAmount output) {
		this(id, stamp, input, fluid, Either.right(output));
	}

	public StampingRecipe(Identifier id, Ingredient stamp, Ingredient input, FluidIngredient fluid, net.minecraft.world.item.ItemStackTemplate output) {
		this(id, stamp, input, fluid, Either.left(output));
	}

	public StampingRecipe(Identifier id, Ingredient stamp, Ingredient input, FluidIngredient fluid, Either<net.minecraft.world.item.ItemStackTemplate, TagAmount> output) {
		this.id = id;
		this.stamp = stamp;
		this.input = input;
		this.fluid = fluid;
		this.output = output;
	}

	@Override
	public boolean matches(StampingContext context, Level pLevel) {
		for (int i = 0; i < context.size(); i++) {
			if (com.rekindled.embers.util.Ingredients.test(input, context.getItem(i))) {
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

	public ItemStack assemble(StampingContext context, net.minecraft.core.HolderLookup.Provider registry) {
		for (int i = 0; i < context.size(); i++) {
			if (com.rekindled.embers.util.Ingredients.test(input, context.getItem(i))) {
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

	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<? extends StampingRecipe> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public ItemStack getResultItem() {
		if (output.left().isPresent())
			return output.left().get().create();
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
	public static final com.mojang.serialization.Codec<Either<net.minecraft.world.item.ItemStackTemplate, TagAmount>> OUTPUT_CODEC =
		com.mojang.serialization.Codec.either(
			net.minecraft.world.item.ItemStackTemplate.CODEC,
			com.mojang.serialization.codecs.RecordCodecBuilder.<TagAmount>create(inst -> inst.group(
				net.minecraft.resources.Identifier.CODEC.fieldOf("tag").forGetter(ta -> ta.tag.location()),
				com.mojang.serialization.Codec.INT.optionalFieldOf("count", 1).forGetter(ta -> ta.amount)
			).apply(inst, (tag, count) -> new TagAmount(ItemTags.create(tag), count))));

	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, Either<net.minecraft.world.item.ItemStackTemplate, TagAmount>> OUTPUT_STREAM_CODEC =
		net.minecraft.network.codec.ByteBufCodecs.<net.minecraft.network.RegistryFriendlyByteBuf, net.minecraft.world.item.ItemStackTemplate, TagAmount>either(
			net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC,
			net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.network.codec.ByteBufCodecs.fromCodec(net.minecraft.resources.Identifier.CODEC), ta -> ta.tag.location(),
				net.minecraft.network.codec.ByteBufCodecs.VAR_INT, ta -> ta.amount,
				(tag, count) -> new TagAmount(ItemTags.create(tag), count)));

	public static final RecipeSerializer<StampingRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec());

	public static class Serializer {
		public StampingRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable StampingRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, StampingRecipe recipe) {}

		
		public com.mojang.serialization.MapCodec<StampingRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC.fieldOf("stamp").forGetter(r -> r.stamp),
				net.minecraft.world.item.crafting.Ingredient.CODEC.optionalFieldOf("input").forGetter(r -> com.rekindled.embers.util.Ingredients.optional(r.input)),
				FluidIngredient.CODEC.optionalFieldOf("fluid", FluidIngredient.EMPTY).forGetter(r -> r.fluid),
				StampingRecipe.OUTPUT_CODEC.fieldOf("output").forGetter(r -> r.output)
			).apply(inst, (s, i, f, o) -> new StampingRecipe(null, s, i.orElse(null), f, o)));
		}

		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, StampingRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.stamp,
				net.minecraft.world.item.crafting.Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, r -> com.rekindled.embers.util.Ingredients.optional(r.input),
				FluidIngredient.STREAM_CODEC.cast(), r -> r.fluid,
				StampingRecipe.OUTPUT_STREAM_CODEC, r -> r.output,
				(s, i, f, o) -> new StampingRecipe(null, s, i.orElse(null), f, o));
		}
	}
}
