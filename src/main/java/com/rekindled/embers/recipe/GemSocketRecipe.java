package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class GemSocketRecipe implements CraftingRecipe {

	public static final RecipeSerializer<GemSocketRecipe> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec());

	public final Identifier id;

	public final Ingredient ingredient;

	public GemSocketRecipe(Identifier id, Ingredient ingredient) {
		this.id = id;
		this.ingredient = ingredient;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.CraftingInput container, Level level) {
		ItemStack cloak = ItemStack.EMPTY;
		int cloaks = 0;
		int strings = 0;
		int gems = 0;
		for (int i = 0; i < container.size(); i ++) {
			ItemStack stack = container.getItem(i);
			if (stack.getItem() instanceof IInflictorGemHolder && ((IInflictorGemHolder) stack.getItem()).getAttachedGemCount(stack) == 0) {
				cloak = stack;
			}
		}
		for (int i = 0; i < container.size(); i ++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IInflictorGemHolder) {
					cloaks++;
				} else if (ingredient.test(stack)) {
					strings++;
				} else if (!cloak.isEmpty() && ((IInflictorGemHolder) cloak.getItem()).canAttachGem(cloak,stack)) {
					gems++;
				} else {
					return false;
				}
			}
		}
		return !cloak.isEmpty() && cloaks == 1 && strings == 1 && gems > 0 && gems <= ((IInflictorGemHolder)cloak.getItem()).getGemSlots(cloak);
	}

	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput container, net.minecraft.core.HolderLookup.Provider registryAccess) {
		ItemStack capeStack = ItemStack.EMPTY;
		for (int i = 0; i < container.size(); i ++) {
			if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof IInflictorGemHolder) {
				capeStack = container.getItem(i).copy();
			}
		}
		if (!capeStack.isEmpty()) {
			int counter = 0;
			for (int i = 0; i < container.size(); i ++) {
				ItemStack stack = container.getItem(i);
				if (!stack.isEmpty() && stack.getItem() instanceof IInflictorGem) {
					((IInflictorGemHolder)capeStack.getItem()).attachGem(capeStack, stack, counter);
					counter++;
				}
			}
			return capeStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 3;
	}

	@Override
	public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registry) {
		return new ItemStack(RegistryManager.ASHEN_CLOAK.get());
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}
	public Identifier getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.EQUIPMENT;
	}

	public static class Serializer {
		public GemSocketRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable GemSocketRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, GemSocketRecipe recipe) {}
	
		
		public com.mojang.serialization.MapCodec<GemSocketRecipe> codec() {
			return com.mojang.serialization.codecs.RecordCodecBuilder.mapCodec(inst -> inst.group(
				net.minecraft.world.item.crafting.Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(r -> r.ingredient)
			).apply(inst, ing -> new GemSocketRecipe(null, ing)));
		}
		
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GemSocketRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.composite(
				net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
				ing -> new GemSocketRecipe(null, ing));
		}
	}
}
