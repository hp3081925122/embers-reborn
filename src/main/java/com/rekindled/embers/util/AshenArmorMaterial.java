package com.rekindled.embers.util;

import java.util.Map;

import com.rekindled.embers.Embers;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/**
 * Ashen armor material, same values as 1.20.1: durability factor 19, defense 3/7/5/3, enchantability 18,
 * toughness 1, no knockback resistance, repaired with ashen fabric (item tag embers_reborn:repairs_ashen_armor).
 */
public final class AshenArmorMaterial {
	public static final TagKey<Item> REPAIR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Embers.MODID, "repairs_ashen_armor"));
	public static final ResourceKey<EquipmentAsset> ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Embers.MODID, "ashen"));

	public static final ArmorMaterial INSTANCE = new ArmorMaterial(
			19,
			Map.of(ArmorType.HELMET, 3, ArmorType.CHESTPLATE, 7, ArmorType.LEGGINGS, 5, ArmorType.BOOTS, 3, ArmorType.BODY, 7),
			18,
			SoundEvents.ARMOR_EQUIP_GENERIC,
			1.0f,
			0.0f,
			REPAIR,
			ASSET);

	public static Item.Properties properties(ArmorType type) {
		return new Item.Properties().humanoidArmor(INSTANCE, type);
	}

	private AshenArmorMaterial() {}
}
