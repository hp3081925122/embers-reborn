package com.rekindled.embers.item;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberItemCapability;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public abstract class EmberStorageItem extends Item {

	public EmberStorageItem(Properties properties) {
		super(properties);
	}

	public static ItemStack withFill(Item item, double ember) {
		ItemStack stack = new ItemStack(item);
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap == null && item instanceof EmberStorageItem storage) {
			cap = new DefaultEmberItemCapability(stack, storage.getCapacity());
		}
		if (cap == null) {
			return stack;
		}
		cap.setEmber(ember);
		return stack;
	}

	public abstract double getCapacity();

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null) != null;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap != null) {
			return Math.round(13.0F - (float) (cap.getEmberCapacity()-cap.getEmber()) * 13.0F / (float) cap.getEmberCapacity());
		}
		return 0;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFF6600;
	}

	/* initCapabilities removed - use RegisterCapabilitiesEvent */

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag isAdvanced) {
		List<Component> tooltip = new com.rekindled.embers.util.TooltipList(tooltipAdder);
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap != null) {
			DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item.ember", emberFormat.format(cap.getEmber()),  emberFormat.format(cap.getEmberCapacity())).withStyle(ChatFormatting.GRAY));
		}
	}
}
