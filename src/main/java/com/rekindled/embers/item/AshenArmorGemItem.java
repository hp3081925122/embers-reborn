package com.rekindled.embers.item;

import java.util.List;
import java.util.function.Supplier;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AshenArmorGemItem extends AshenArmorItem implements IInflictorGemHolder {
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

	public Supplier<Integer> gemSlots;

	public AshenArmorGemItem(Properties properties, Supplier<Integer> gemSlots) {
		super(properties);
		this.gemSlots = gemSlots;
	}

	@Override
	public int getGemSlots(ItemStack holder) {
		return gemSlots.get();
	}

	@Override
	public boolean canAttachGem(ItemStack holder, ItemStack gem) {
		return gem.getItem() instanceof IInflictorGem;
	}

	@Override
	public void attachGem(ItemStack holder, ItemStack gem, int slot) {
		net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(holder);
		tag.put("gem" + slot, com.rekindled.embers.util.NbtBridge.saveOptional(gem, com.rekindled.embers.util.NbtBridge.registries()));
		_setTag(holder, tag);
	}

	@Override
	public ItemStack detachGem(ItemStack holder, int slot) {
		if (_getOrCreateTag(holder).contains("gem" + slot)) {
			ItemStack gem = com.rekindled.embers.util.NbtBridge.parse(com.rekindled.embers.util.NbtBridge.registries(), _getOrCreateTag(holder).getCompound("gem" + slot)).orElse(net.minecraft.world.item.ItemStack.EMPTY);
			net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(holder);
			tag.remove("gem" + slot);
			_setTag(holder, tag);
			return gem;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void clearGems(ItemStack holder) {
		CompoundTag tagCompound = _getOrCreateTag(holder);
		if(tagCompound == null)
			return;
		for (int i = 0; i < getGemSlots(holder); i++) {
			if (tagCompound.contains("gem" + i)) {
				tagCompound.remove("gem" + i);
			}
		}
	}

	@Override
	public ItemStack[] getAttachedGems(ItemStack holder) {
		ItemStack[] stacks = new ItemStack[getGemSlots(holder)];
		for (int i = 0; i < stacks.length; i++) {
			if(_getOrCreateTag(holder).contains("gem" + i))
				stacks[i] = com.rekindled.embers.util.NbtBridge.parse(com.rekindled.embers.util.NbtBridge.registries(), _getOrCreateTag(holder).getCompound("gem" + i)).orElse(ItemStack.EMPTY);
			else
				stacks[i] = ItemStack.EMPTY;
		}
		return stacks;
	}

	@Override
	public float getTotalDamageResistance(LivingEntity entity, DamageSource source, ItemStack holder) {
		float reduction = 0;

		if (!isBroken(holder)) {
			for (ItemStack stack : getAttachedGems(holder)) {
				Item item = stack.getItem();
				if (item instanceof IInflictorGem gem && gem.getAttunedSource(stack).equals(source.type().msgId())) {
					reduction += gem.getDamageResistance(stack, reduction);
				}
			}
		}
		return reduction;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag isAdvanced) {
		List<Component> tooltip = new com.rekindled.embers.util.TooltipList(tooltipAdder);
		super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, isAdvanced);
		ItemStack[] attached = getAttachedGems(stack);
		int filledSlots = 0;

		for (ItemStack stacks : attached) {
			if (!stacks.isEmpty()) {
				filledSlots++;
			}
		}
		if (getGemSlots(stack) > filledSlots)
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor.slots", getGemSlots(stack) - filledSlots).withStyle(ChatFormatting.GRAY));

		for (ItemStack stacks : attached) {
			if (!stacks.isEmpty()) {
				if (_getOrCreateTag(stacks).contains("type")) {
					tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor", _getOrCreateTag(stacks).getStringOr("type", "")).withStyle(ChatFormatting.GRAY));
				} else {
					tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor.none").withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
