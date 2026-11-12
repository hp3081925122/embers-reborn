package com.rekindled.embers.apiimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.augment.IAugment;
import com.rekindled.embers.api.augment.IAugmentUtil;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * AugmentUtilImpl — migrated to DataComponents API for 1.21.1.
 *
 * All NBT access now goes through CustomData component.
 * Key helpers:
 *   getItemTag(stack)          → read-only copy (may be null)
 *   getOrCreateItemTag(stack)  → read-write copy (never null)
 *   setItemTag(stack, tag)     → write back after modification
 *   hasItemTag(stack)          → stack has non-empty custom data
 */
public class AugmentUtilImpl implements IAugmentUtil {

	// ---- DataComponents helpers ----

	/** Returns a mutable copy of the stack's CustomData compound, or null if absent. */
	private static CompoundTag getItemTag(ItemStack stack) {
		CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
		if (cd == null || cd.isEmpty()) return null;
		return cd.copyTag();
	}

	/** Returns a mutable copy of the stack's CustomData compound (creates empty if absent). */
	private static CompoundTag getOrCreateItemTag(ItemStack stack) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		return cd.copyTag();
	}

	/** Writes a modified CompoundTag back to the stack's CustomData component. */
	private static void setItemTag(ItemStack stack, CompoundTag tag) {
		if (tag == null || tag.isEmpty()) {
			stack.remove(DataComponents.CUSTOM_DATA);
		} else {
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
		}
	}

	/** Returns true if the stack has a non-empty CustomData compound. */
	private static boolean hasItemTag(ItemStack stack) {
		if (stack.isEmpty()) return false;
		CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}

	// ---- IAugmentUtil implementation ----

	@Override
	public Collection<IAugment> getAllAugments() {
		return RegistryManager.augmentRegistry.values();
	}

	@Override
	public IAugment getAugment(Identifier name) {
		return RegistryManager.augmentRegistry.get(name);
	}

	@Override
	public List<IAugment> getAugments(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root == null) return Lists.newArrayList();
			ListTag tagAugments = root.getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			if (tagAugments.size() > 0) {
				List<IAugment> results = new ArrayList<>();
				for (int i = 0; i < tagAugments.size(); i++) {
					CompoundTag tagAugment = tagAugments.getCompound(i);
					IAugment augment = getAugment(Identifier.parse(tagAugment.getString("name")));
					if (augment != null)
						results.add(augment);
				}
				return results;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public int getTotalAugmentLevel(ItemStack stack) {
		int total = 0;
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root == null) return 0;
			ListTag list = root.getCompound(HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag compound = list.getCompound(i);
				IAugment augment = getAugment(Identifier.parse(compound.getString("name")));
				if (augment != null && augment.countTowardsTotalLevel()) {
					total += compound.getInt("level");
				}
			}
		}
		return total;
	}

	@Override
	public boolean hasAugment(ItemStack stack, IAugment augment) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root == null) return false;
			ListTag list = root.getCompound(HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag compound = list.getCompound(i);
				if (compound.contains("name")) {
					if (compound.getString("name").equals(augment.getName().toString())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void addAugment(ItemStack stack, ItemStack augmentStack, IAugment augment) {
		checkForTag(stack);
		CompoundTag root = getOrCreateItemTag(stack);
		CompoundTag heatTag = root.getCompound(HEAT_TAG);
		ListTag list = heatTag.getList("augments", Tag.TAG_COMPOUND);
		int level = getAugmentLevel(stack, augment);
		if (level == 0) {
			CompoundTag augmentCompound = new CompoundTag();
			augmentCompound.putString("name", augment.getName().toString());
			ListTag items = new ListTag();
			augmentCompound.put("items", items);
			if (!augmentStack.isEmpty()) {
				items.add(augmentStack.save(net.minecraft.core.RegistryAccess.EMPTY));
			}
			augmentCompound.putInt("level", 1);
			list.add(augmentCompound);
		} else {
			for (int i = 0; i < list.size(); i++) {
				CompoundTag augmentCompound = list.getCompound(i);
				if (augmentCompound.contains("name")) {
					if (augmentCompound.getString("name").equals(augment.getName().toString())) {
						ListTag items = augmentCompound.getList("items", Tag.TAG_COMPOUND);
						if (!augmentStack.isEmpty()) {
							items.add(augmentStack.save(net.minecraft.core.RegistryAccess.EMPTY));
						}
						augmentCompound.putInt("level", level + 1);
					}
				}
			}
		}
		heatTag.put("augments", list);
		root.put(HEAT_TAG, heatTag);
		setItemTag(stack, root);
		augment.onApply(stack);
	}

	@Override
	public List<ItemStack> removeAllAugments(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag root = getOrCreateItemTag(stack);
			CompoundTag heatCompound = root.getCompound(IAugmentUtil.HEAT_TAG);
			ListTag tagAugments = heatCompound.getList("augments", Tag.TAG_COMPOUND);
			if (tagAugments.size() > 0) {
				List<ItemStack> results = new ArrayList<>();
				ListTag remainingAugments = new ListTag();
				List<IAugment> removedAugments = new ArrayList<>();
				for (int i = 0; i < tagAugments.size(); i++) {
					CompoundTag tagAugment = tagAugments.getCompound(i);
					IAugment augment = getAugment(Identifier.parse(tagAugment.getString("name")));
					if (augment != null) {
						if (augment.canRemove()) {
							for (int j = 0; j < tagAugment.getInt("level"); j++) {
								removedAugments.add(augment);
							}
							if (tagAugment.contains("items")) {
								ListTag items = tagAugment.getList("items", Tag.TAG_COMPOUND);
								for (int j = 0; j < items.size(); j++) {
									ItemStack.parse(net.minecraft.core.RegistryAccess.EMPTY, items.getCompound(j))
											.ifPresent(results::add);
								}
							}
						} else {
							remainingAugments.add(tagAugment);
						}
					}
				}
				heatCompound.put("augments", remainingAugments);
				root.put(IAugmentUtil.HEAT_TAG, heatCompound);
				setItemTag(stack, root);
				for (IAugment augment : removedAugments) {
					augment.onRemove(stack);
				}
				return results;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public void addAugmentLevel(ItemStack stack, IAugment augment, int levels) {
		setAugmentLevel(stack, augment, getAugmentLevel(stack, augment) + levels);
	}

	@Override
	public void setAugmentLevel(ItemStack stack, IAugment augment, int level) {
		checkForTag(stack);
		CompoundTag root = getOrCreateItemTag(stack);
		CompoundTag heatTag = root.getCompound(IAugmentUtil.HEAT_TAG);
		ListTag list = heatTag.getList("augments", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag compound = list.getCompound(i);
			if (compound.contains("name")) {
				if (compound.getString("name").equals(augment.getName().toString())) {
					compound.putInt("level", level);
				}
			}
		}
		heatTag.put("augments", list);
		root.put(IAugmentUtil.HEAT_TAG, heatTag);
		setItemTag(stack, root);
	}

	@Override
	public int getAugmentLevel(ItemStack stack, IAugment augment) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root == null) return 0;
			ListTag list = root.getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag compound = list.getCompound(i);
				if (compound.contains("name")) {
					if (compound.getString("name").equals(augment.getName().toString())) {
						return compound.getInt("level");
					}
				}
			}
		}
		return 0;
	}

	@Override
	public boolean hasHeat(ItemStack stack) {
		if (!stack.isEmpty() && hasItemTag(stack)) {
			CompoundTag tag = getItemTag(stack);
			return tag != null && tag.contains(HEAT_TAG);
		}
		return false;
	}

	@Override
	public void addHeat(ItemStack stack, float heat) {
		checkForTag(stack);
		CompoundTag root = getOrCreateItemTag(stack);
		CompoundTag heatTag = root.getCompound(HEAT_TAG);
		heatTag.putFloat("heat", Math.min(getMaxHeat(stack), getHeat(stack) + heat));
		root.put(HEAT_TAG, heatTag);
		setItemTag(stack, root);
	}

	@Override
	public void setHeat(ItemStack stack, float heat) {
		checkForTag(stack);
		CompoundTag root = getOrCreateItemTag(stack);
		CompoundTag heatTag = root.getCompound(HEAT_TAG);
		heatTag.putFloat("heat", heat);
		root.put(HEAT_TAG, heatTag);
		setItemTag(stack, root);
	}

	@Override
	public float getHeat(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root != null) return root.getCompound(HEAT_TAG).getFloat("heat");
		}
		return 0.0f;
	}

	@Override
	public float getMaxHeat(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root != null) return 500f + 250f * root.getCompound(HEAT_TAG).getFloat("heat_level");
		}
		return 0.0f;
	}

	@Override
	public int getLevel(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag root = getItemTag(stack);
			if (root != null) return root.getCompound(HEAT_TAG).getInt("heat_level");
		}
		return 0;
	}

	@Override
	public void setLevel(ItemStack stack, int level) {
		checkForTag(stack);
		CompoundTag root = getOrCreateItemTag(stack);
		CompoundTag heatTag = root.getCompound(HEAT_TAG);
		heatTag.putInt("heat_level", level);
		root.put(HEAT_TAG, heatTag);
		setItemTag(stack, root);
	}

	@Override
	public int getArmorAugmentLevel(LivingEntity entity, IAugment augment) {
		int maxLevel = 0;
		for (EquipmentSlot slot : new EquipmentSlot[]{
				EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			ItemStack item = entity.getItemBySlot(slot);
			if (hasHeat(item)) {
				int l = getAugmentLevel(item, augment);
				if (l > maxLevel) maxLevel = l;
			}
		}
		return maxLevel;
	}

	@Override
	public IAugment registerAugment(IAugment augment) {
		RegistryManager.augmentRegistry.put(augment.getName(), augment);
		return augment;
	}

	/** Ensures the heat compound exists in the stack's custom data. */
	public static void checkForTag(ItemStack stack) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = cd.copyTag();
		if (!root.contains(HEAT_TAG)) {
			CompoundTag heatTag = new CompoundTag();
			heatTag.putInt("heat_level", 0);
			heatTag.putFloat("heat", 0);
			heatTag.put("augments", new ListTag());
			root.put(HEAT_TAG, heatTag);
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(root));
		}
	}
}
