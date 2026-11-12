package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ClockworkPickaxeItem extends ClockworkToolItem {

	public ClockworkPickaxeItem(Properties properties) {
		super(EmbersTiers.CLOCKWORK_PICK, EmbersBlockTags.MINABLE_WITH_PICKAXE_SHOVEL, properties.attributes(DiggerItem.createAttributes(EmbersTiers.CLOCKWORK_PICK, 1.0F, -2.8F)));
	}

	@Override
	public boolean canPerformAction(net.minecraft.world.item.ItemStack stack, net.neoforged.neoforge.common.ItemAbility ability) {
		return hasEmber(stack) && (ability == net.neoforged.neoforge.common.ItemAbilities.PICKAXE_DIG || ability == net.neoforged.neoforge.common.ItemAbilities.SHOVEL_DIG);
	}
}
