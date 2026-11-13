package com.rekindled.embers.item;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AlchemyHintItem extends Item {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public AlchemyHintItem(Properties pProperties) {
		super(pProperties);
	}

	public static int getBlackPins(ItemStack stack) {
		if (_hasTag(stack)) {
			return _getTag(stack).getInt("blackPins");
		}
		return 0;
	}

	public static int getWhitePins(ItemStack stack) {
		if (_hasTag(stack)) {
			return _getTag(stack).getInt("whitePins");
		}
		return 0;
	}

	public static ArrayList<ItemStack> getAspects(ItemStack stack) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (_hasTag(stack) && _getTag(stack).contains("aspects")) {
			ListTag list = _getTag(stack).getList("aspects", Tag.TAG_COMPOUND);
			for (Tag nbt : list) {
				ItemStack.parse(com.rekindled.embers.util.RegistryHelper.registries(), (CompoundTag) nbt).ifPresent(items::add);
			}
		}
		return items;
	}

	public static ArrayList<ItemStack> getInputs(ItemStack stack) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (_hasTag(stack) && _getTag(stack).contains("inputs")) {
			ListTag list = _getTag(stack).getList("inputs", Tag.TAG_COMPOUND);
			for (Tag nbt : list) {
				ItemStack.parse(com.rekindled.embers.util.RegistryHelper.registries(), (CompoundTag) nbt).ifPresent(items::add);
			}
		}
		return items;
	}

	/** @return per-slot correctness recorded on the note, or null for notes made with slot hints disabled */
	public static byte[] getCorrect(ItemStack stack) {
		if (_hasTag(stack) && _getTag(stack).contains("correct"))
			return _getTag(stack).getByteArray("correct");
		return null;
	}

	public static ItemStack getResult(ItemStack stack) {
		if (_hasTag(stack) && _getTag(stack).contains("result")) {
			return ItemStack.parse(com.rekindled.embers.util.RegistryHelper.registries(), _getTag(stack).getCompound("result")).orElse(ItemStack.EMPTY);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		int blackPins = getBlackPins(stack);
		int whitePins = getWhitePins(stack);
		if (blackPins != 0)
			tooltip.add(Component.translatable(blackPins == 1 ? Embers.MODID + ".alchemy_hint.black.one" : Embers.MODID + ".alchemy_hint.black", blackPins).withStyle(ChatFormatting.GRAY));
		if (whitePins != 0)
			tooltip.add(Component.translatable(whitePins == 1 ? Embers.MODID + ".alchemy_hint.white.one" : Embers.MODID + ".alchemy_hint.white", whitePins).withStyle(ChatFormatting.GRAY));
		if (blackPins == 0 && whitePins == 0)
			tooltip.add(Component.translatable(Embers.MODID + ".alchemy_hint.none").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide)
			return InteractionResultHolder.consume(stack);
		int blackPins = getBlackPins(stack);
		int whitePins = getWhitePins(stack);
		String black = blackPins == 1 ? I18n.get(Embers.MODID + ".alchemy_hint.black.one") : I18n.get(Embers.MODID + ".alchemy_hint.black", blackPins);
		String white = whitePins == 1 ? I18n.get(Embers.MODID + ".alchemy_hint.white.one") : I18n.get(Embers.MODID + ".alchemy_hint.white", whitePins);
		if (blackPins != 0) {
			if (whitePins != 0) {
				player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", I18n.get(Embers.MODID + ".alchemy_hint.and", black, white)), false);
				return InteractionResultHolder.consume(stack);
			}
			player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", black), false);
			return InteractionResultHolder.consume(stack);
		}
		if (whitePins != 0) {
			player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", white), false);
			return InteractionResultHolder.consume(stack);
		}
		player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", I18n.get(Embers.MODID + ".alchemy_hint.none")), false);
		return InteractionResultHolder.consume(stack);
	}
}
