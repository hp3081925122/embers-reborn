package com.rekindled.embers.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public class DawnstoneMailItem extends Item implements IEmbersCurioItem {

	public DawnstoneMailItem(Properties properties) {
		super(properties);
	}

	public SoundEvent equipSound() {
		return SoundEvents.ARMOR_EQUIP_CHAIN.value();
	}

	public com.google.common.collect.Multimap<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
		com.google.common.collect.ImmutableMultimap.Builder<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeModifier> builder = com.google.common.collect.ImmutableMultimap.builder();
		builder.put(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.ResourceLocation.parse("embers_reborn:curio_kb_resistance"), 1.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
		return builder.build();
	}
}
