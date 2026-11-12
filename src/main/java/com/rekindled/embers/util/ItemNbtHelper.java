package com.rekindled.embers.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * NBT helper for ItemStack in NeoForge 1.21.1.
 * Replaces the removed ItemStack.getTag(), getOrCreateTag(), getOrCreateTagElement(),
 * hasTag(), and getTagElement() methods.
 */
public class ItemNbtHelper {

    public static boolean hasTag(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        return cd != null && !cd.copyTag().isEmpty();
    }

    public static CompoundTag getTag(ItemStack stack) {
        if (stack.isEmpty()) return new CompoundTag();
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        return cd != null ? cd.copyTag() : new CompoundTag();
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        if (stack.isEmpty()) return new CompoundTag();
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return cd.copyTag();
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        if (stack.isEmpty()) return;
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static boolean hasTagElement(ItemStack stack, String key) {
        return getTag(stack).contains(key);
    }

    public static CompoundTag getTagElement(ItemStack stack, String key) {
        return getTag(stack).getCompound(key);
    }

    public static CompoundTag getOrCreateTagElement(ItemStack stack, String key) {
        CompoundTag tag = getOrCreateTag(stack);
        if (!tag.contains(key)) tag.put(key, new CompoundTag());
        return tag.getCompound(key);
    }

    public static void setTagElement(ItemStack stack, String key, CompoundTag value) {
        CompoundTag tag = getOrCreateTag(stack);
        tag.put(key, value);
        setTag(stack, tag);
    }

    public static void updateTagElement(ItemStack stack, String key, java.util.function.Consumer<CompoundTag> updater) {
        CompoundTag tag = getOrCreateTag(stack);
        CompoundTag sub = tag.contains(key) ? tag.getCompound(key) : new CompoundTag();
        updater.accept(sub);
        tag.put(key, sub);
        setTag(stack, tag);
    }
}
