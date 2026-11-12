package com.rekindled.embers.util;

import com.rekindled.embers.Embers;
import com.rekindled.embers.datagen.EmbersItemTags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

/**
 * Tool materials. Harvest levels follow 1.20.1: lead/tyrfing/silver/dawnstone = iron, clockwork pick/axe = diamond,
 * clockwork hammer = stone. Clockwork tools are unbreakable (UNBREAKABLE component); ToolMaterial needs a positive
 * durability, so they use 1 instead of the old -1. Clockwork tools have no repair material (empty tag).
 */
public class EmbersTiers {

	public static final TagKey<Item> NO_REPAIR = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Embers.MODID, "no_repair_materials"));

	public static final ToolMaterial LEAD = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 168, 6.0f, 2.0f, 4, EmbersItemTags.LEAD_INGOT);
	public static final ToolMaterial TYRFING = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 512, 7.5f, 0.0f, 24, EmbersItemTags.ASH_DUST);
	public static final ToolMaterial SILVER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 202, 7.6f, 2.0f, 20, EmbersItemTags.SILVER_INGOT);
	public static final ToolMaterial DAWNSTONE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 644, 7.5f, 2.5f, 18, EmbersItemTags.DAWNSTONE_INGOT);
	public static final ToolMaterial CLOCKWORK_PICK = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1, 16.0F, 4.0F, 18, NO_REPAIR);
	public static final ToolMaterial CLOCKWORK_AXE = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1, 16.0F, 5.0F, 18, NO_REPAIR);
	public static final ToolMaterial CLOCKWORK_HAMMER = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 1, 6.0F, 6.0F, 18, NO_REPAIR);
}
