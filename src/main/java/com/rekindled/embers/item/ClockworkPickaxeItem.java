package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ClockworkPickaxeItem extends ClockworkToolItem {

	public ClockworkPickaxeItem(Properties properties) {
		super(EmbersTiers.CLOCKWORK_PICK,  EmbersBlockTags.MINABLE_WITH_PICKAXE_SHOVEL, properties);
	}

	// canPerformAction removed: ToolAction removed in NeoForge 1.21.1

	// canApplyAtEnchantingTable removed in 1.21.1
}
