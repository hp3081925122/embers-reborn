package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ClockworkHammerItem extends ClockworkToolItem {

	public ClockworkHammerItem(Properties properties) {
		super(EmbersTiers.CLOCKWORK_HAMMER, EmbersBlockTags.MINABLE_WITH_HAMMER, properties.attributes(DiggerItem.createAttributes(EmbersTiers.CLOCKWORK_HAMMER, 3.0F, -3.2F)));
	}

	@Override
	public boolean canPerformAction(net.minecraft.world.item.ItemStack stack, net.neoforged.neoforge.common.ItemAbility ability) {
		return hasEmber(stack) && (ability == net.neoforged.neoforge.common.ItemAbilities.PICKAXE_DIG || ability == net.neoforged.neoforge.common.ItemAbilities.SHOVEL_DIG || ability == net.neoforged.neoforge.common.ItemAbilities.AXE_DIG);
	}

	@Override
	protected boolean allowsEnchantment(net.minecraft.core.Holder<Enchantment> enchant) {
		return super.allowsEnchantment(enchant) && !enchant.is(Enchantments.SILK_TOUCH) && !enchant.is(Enchantments.FORTUNE);
	}
}
