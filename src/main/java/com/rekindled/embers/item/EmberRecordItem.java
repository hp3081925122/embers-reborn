package com.rekindled.embers.item;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.rekindled.embers.util.GlowingTextTooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class EmberRecordItem extends net.minecraft.world.item.Item {

	public EmberRecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Item.Properties builder, int lengthInTicks) {
		super(builder.stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack pStack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag pFlag) {
		List<Component> pTooltip = new com.rekindled.embers.util.TooltipList(tooltipAdder);

	}

}
