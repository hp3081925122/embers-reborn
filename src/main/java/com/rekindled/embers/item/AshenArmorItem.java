package com.rekindled.embers.item;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rekindled.embers.Embers;
import com.rekindled.embers.model.AshenArmorModel;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class AshenArmorItem extends net.minecraft.world.item.Item {

	public AshenArmorItem(Properties properties) {
		super(properties);
	}

public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, Math.min(damage, getMaxDamage(stack) - 1));
	}

	public boolean isBroken(ItemStack armor) {
		return armor.getDamageValue() >= armor.getMaxDamage() - 1;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag isAdvanced) {
		List<Component> tooltip = new com.rekindled.embers.util.TooltipList(tooltipAdder);
		super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, isAdvanced);
		if (isBroken(stack))
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.broken").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(AshenArmorModel.ARMOR_MODEL_GETTER);
	}
}
