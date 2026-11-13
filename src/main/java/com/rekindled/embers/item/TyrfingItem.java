package com.rekindled.embers.item;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.item.ITyrfingWeapon;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.TyrfingParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class TyrfingItem extends Item implements ITyrfingWeapon {

	public TyrfingItem(Properties prop) {
		super(prop);
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag isAdvanced) {
		List<Component> tooltip = new com.rekindled.embers.util.TooltipList(tooltipAdder);
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.tyrfing").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void attack(LivingIncomingDamageEvent event, double armor) {
		if (armor > 0) {
			event.getEntity().playSound(EmbersSounds.TYRFING_HIT.get(),1.0f,1.0f);
			if (event.getEntity().level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(TyrfingParticleOptions.TYRFING, event.getEntity().position().x, event.getEntity().position().y + event.getEntity().getBbHeight() / 2.0f, event.getEntity().position().z, 80, 0.1, 0.1, 0.1, 0.0);
			}
			event.setAmount((event.getAmount() / 4.0f) * (4.0f + (float) armor * 1.0f));
		}
	}
}
