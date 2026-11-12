package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ClockworkHammerItem extends ClockworkToolItem {

	public ClockworkHammerItem(Properties properties) {
		super(EmbersTiers.CLOCKWORK_HAMMER,  EmbersBlockTags.MINABLE_WITH_HAMMER, properties);
	}

	// canPerformAction removed: ToolAction removed in NeoForge 1.21.1

	// canApplyAtEnchantingTable removed in 1.21.1
}
