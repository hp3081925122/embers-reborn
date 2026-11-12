package com.rekindled.embers.recipe;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BoringContext implements net.minecraft.world.item.crafting.RecipeInput {

	public ResourceLocation dimension;
	public ResourceLocation biome;
	public int height;
	public List<BlockState> blockStates;

	public BoringContext(ResourceLocation dimension, ResourceLocation biome, int height) {
		this(dimension, biome, height, null);
	}

	public BoringContext(ResourceLocation dimension, ResourceLocation biome, int height, List<BlockState> blockStates) {
		this.dimension = dimension;
		this.biome = biome;
		this.height = height;
		this.blockStates = blockStates;
	}

	@Override
	public int size() { return 0; }

	@Override
	public ItemStack getItem(int pSlot) {
		return ItemStack.EMPTY;
	}
}
