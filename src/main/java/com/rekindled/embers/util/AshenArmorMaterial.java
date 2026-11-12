package com.rekindled.embers.util;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;

/**
 * In 1.21.1 ArmorMaterial is a record (cannot be extended).
 * Provide a static INSTANCE of the record and a Holder for use with ArmorItem.
 */
public class AshenArmorMaterial {

	// Durability multipliers from vanilla: Helmet=11, Chestplate=16, Leggings=15, Boots=13
	public static final ArmorMaterial INSTANCE = new ArmorMaterial(
		Map.of(
			ArmorItem.Type.HELMET,     3,
			ArmorItem.Type.CHESTPLATE, 7,
			ArmorItem.Type.LEGGINGS,   5,
			ArmorItem.Type.BOOTS,      3
		),
		18, // enchantmentValue
		SoundEvents.ARMOR_EQUIP_GENERIC, // Holder<SoundEvent> in 1.21.1
		() -> Ingredient.of(RegistryManager.ASHEN_FABRIC.get()),
		List.of(new ArmorMaterial.Layer(ResourceLocation.parse(Embers.MODID + ":empty"))),
		1.0f, // toughness
		0.0f  // knockbackResistance
	);

	/** Wrap in a plain Holder for use with ArmorItem constructor. */
	public static final net.minecraft.core.Holder<ArmorMaterial> HOLDER =
		net.minecraft.core.Holder.direct(INSTANCE);

	private AshenArmorMaterial() {}
}
