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
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap != null) {
			DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item.ember", emberFormat.format(cap.getEmber()),  emberFormat.format(cap.getEmberCapacity())).withStyle(ChatFormatting.GRAY));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class ColorHandler implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex == 0) {
				IEmberCapability capability = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
				if (capability != null) {
					float coeff = (float)(capability.getEmber() / capability.getEmberCapacity());
					float timerSine = ((float)Math.sin(8.0*Math.toRadians(EmbersClientEvents.ticks % 360))+1.0f)/2.0f;
					int r = 255;
					int g = (int)(255.0f*(1.0f-coeff) + (64.0f*timerSine+64.0f)*coeff);
					int b = (int)(255.0f*(1.0f-coeff) + 16.0f*coeff);
					return Misc.intColor(r, g, b);
				}
			}
			return 0xFFFFFF;
		}
	}
}
