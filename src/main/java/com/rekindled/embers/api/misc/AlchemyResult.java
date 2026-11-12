package com.rekindled.embers.api.misc;

import java.util.List;

import com.rekindled.embers.recipe.IAlchemyRecipe.PedestalContents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class AlchemyResult {

	public List<PedestalContents> contents;
	public ItemStack result;
	public int blackPins;
	public int whitePins;
	/** Per-slot correctness in recipe input order, or null when slot hints are disabled. */
	public byte[] correct;

	public AlchemyResult(List<PedestalContents> contents, ItemStack result, int blackPins, int whitePins) {
		this.contents = contents;
		this.result = result;
		this.blackPins = blackPins;
		this.whitePins = whitePins;
	}

	public ItemStack createResultStack(ItemStack stack) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("blackPins", blackPins);
		nbt.putInt("whitePins", whitePins);

		nbt.put("result", result.saveOptional(net.minecraft.core.RegistryAccess.EMPTY));

		ListTag aspectNBT = new ListTag();
		ListTag inputNBT = new ListTag();
		for (PedestalContents contents : contents) {
			aspectNBT.add(contents.aspect.saveOptional(net.minecraft.core.RegistryAccess.EMPTY));
			inputNBT.add(contents.input.saveOptional(net.minecraft.core.RegistryAccess.EMPTY));
		}
		nbt.put("aspects", aspectNBT);
		nbt.put("inputs", inputNBT);
		if (correct != null)
			nbt.putByteArray("correct", correct);

		stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(nbt));
		return stack;
	}
}
