package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateContext implements net.minecraft.world.item.crafting.RecipeInput {

	public BlockState state;

	public BlockStateContext(BlockState state) {
		this.state = state;
	}

	@Override
	public int size() { return 0; }

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return ItemStack.EMPTY;
	}
}
