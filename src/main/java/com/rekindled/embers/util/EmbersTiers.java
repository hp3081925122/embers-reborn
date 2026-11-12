package com.rekindled.embers.util;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersItemTags;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class EmbersTiers {

	public static final Tier LEAD = new SimpleTier(EmbersBlockTags.NEEDS_LEAD_TOOL, 168, 6.0f, 2.0f, 4, () -> Ingredient.of(EmbersItemTags.LEAD_INGOT));
	public static final Tier TYRFING = new SimpleTier(EmbersBlockTags.NEEDS_TYRFING, 512, 7.5f, 0.0f, 24, () -> Ingredient.of(EmbersItemTags.ASH_DUST));
	public static final Tier SILVER = new SimpleTier(EmbersBlockTags.NEEDS_SILVER_TOOL, 202, 7.6f, 2.0f, 20, () -> Ingredient.of(EmbersItemTags.SILVER_INGOT));
	public static final Tier DAWNSTONE = new SimpleTier(EmbersBlockTags.NEEDS_DAWNSTONE_TOOL, 644, 7.5f, 2.5f, 18, () -> Ingredient.of(EmbersItemTags.DAWNSTONE_INGOT));
	public static final Tier CLOCKWORK_PICK = new SimpleTier(EmbersBlockTags.NEEDS_CLOCKWORK_TOOL, -1, 16.0F, 4.0F, 18, () -> Ingredient.EMPTY);
	public static final Tier CLOCKWORK_AXE = new SimpleTier(EmbersBlockTags.NEEDS_CLOCKWORK_TOOL, -1, 16.0F, 5.0F, 18, () -> Ingredient.EMPTY);
	public static final Tier CLOCKWORK_HAMMER = new SimpleTier(EmbersBlockTags.NEEDS_CLOCKWORK_HAMMER, -1, 6.0F, 6.0F, 18, () -> Ingredient.EMPTY);

	// static block removed — TierSortingRegistry replaced by tag hierarchy in 1.21.1
}
