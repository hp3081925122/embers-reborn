package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class GemUnsocketRecipe implements CraftingRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final Identifier id;

	public GemUnsocketRecipe(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean matches(net.minecraft.world.item.crafting.CraftingInput container, Level level) {
		ItemStack cloak = ItemStack.EMPTY;
		int cloaks = 0;
		for (int i = 0; i < container.size(); i++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IInflictorGemHolder) {
					if (((IInflictorGemHolder) stack.getItem()).getAttachedGemCount(stack) > 0) {
						cloak = stack;
					}
					cloaks++;
				} else {
					return false;
				}
			}
		}
		return !cloak.isEmpty() && cloaks == 1 && container.size() >= ((IInflictorGemHolder) cloak.getItem()).getAttachedGemCount(cloak);
	}

	@Override
	public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput container, net.minecraft.core.HolderLookup.Provider registryAccess) {
		ItemStack capeStack = ItemStack.EMPTY;
		for (int i = 0; i < container.size(); i++) {
			if (!container.getItem(i).isEmpty() && container.getItem(i).getItem() instanceof IInflictorGemHolder) {
				capeStack = container.getItem(i).copy();
			}
		}
		if (!capeStack.isEmpty()) {
			((IInflictorGemHolder) capeStack.getItem()).clearGems(capeStack);
		}
		return capeStack;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(net.minecraft.world.item.crafting.CraftingInput container) {
		NonNullList<ItemStack> gems = NonNullList.withSize(container.size(), ItemStack.EMPTY);
		int index = 0;
		for (int i = 0; i < container.size(); i++) {
			ItemStack stack = container.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof IInflictorGemHolder) {
					for (ItemStack gem : ((IInflictorGemHolder) stack.getItem()).getAttachedGems(stack)) {
						if (!gem.isEmpty()) {
							gems.set(index, gem);
							index++;
						}
					}
				}
			}
		}
		return gems;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 1;
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

	public static class Serializer implements RecipeSerializer<GemUnsocketRecipe> {
		public GemUnsocketRecipe fromJson(Identifier recipeId, JsonObject json) {return null; }
		public @Nullable GemUnsocketRecipe fromNetwork(Identifier recipeId, net.minecraft.network.RegistryFriendlyByteBuf buffer) {return null; }
		public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer, GemUnsocketRecipe recipe) {}
	
		@Override
		public com.mojang.serialization.MapCodec<GemUnsocketRecipe> codec() {
			return com.mojang.serialization.MapCodec.unit(new GemUnsocketRecipe(null));
		}
		@Override
		public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GemUnsocketRecipe> streamCodec() {
			return net.minecraft.network.codec.StreamCodec.of((buf, recipe) -> {}, buf -> new GemUnsocketRecipe(null));
		}
	}
}
