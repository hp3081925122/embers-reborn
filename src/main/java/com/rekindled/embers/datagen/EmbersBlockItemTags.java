package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.StoneDecoBlocks;
import com.rekindled.embers.compat.curios.CuriosCompat;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

/** Tags that exist as both a block tag and an item tag with the same contents (was ItemTagsProvider#copy). */
public abstract class EmbersBlockItemTags extends net.minecraft.data.tags.BlockItemTagsProvider {

	@Override
	public void run() {
		tag(EmbersBlockTags.WORLD_BOTTOM, EmbersItemTags.WORLD_BOTTOM).add(Blocks.BEDROCK);
		tag(EmbersBlockTags.SNOW, EmbersItemTags.SNOW).add(Blocks.SNOW_BLOCK);
		tag(EmbersBlockTags.PRISTINE_COPPER, EmbersItemTags.PRISTINE_COPPER).addTag(Tags.Blocks.STORAGE_BLOCKS_COPPER).add(Blocks.CUT_COPPER);
		tag(EmbersBlockTags.EXPOSED_COPPER, EmbersItemTags.EXPOSED_COPPER).add(Blocks.EXPOSED_COPPER).add(Blocks.EXPOSED_CUT_COPPER);
		tag(EmbersBlockTags.WEATHERED_COPPER, EmbersItemTags.WEATHERED_COPPER).add(Blocks.WEATHERED_COPPER).add(Blocks.WEATHERED_CUT_COPPER);
		tag(EmbersBlockTags.OXIDIZED_COPPER, EmbersItemTags.OXIDIZED_COPPER).add(Blocks.OXIDIZED_COPPER).add(Blocks.OXIDIZED_CUT_COPPER);
		tag(EmbersBlockTags.CRYSTAL_SEEDS, EmbersItemTags.CRYSTAL_SEEDS).addTags(EmbersBlockTags.COPPER_SEED, EmbersBlockTags.IRON_SEED, EmbersBlockTags.GOLD_SEED, EmbersBlockTags.LEAD_SEED, EmbersBlockTags.SILVER_SEED, EmbersBlockTags.NICKEL_SEED, EmbersBlockTags.TIN_SEED, EmbersBlockTags.ALUMINUM_SEED, EmbersBlockTags.ZINC_SEED, EmbersBlockTags.PLATINUM_SEED, EmbersBlockTags.URANIUM_SEED, EmbersBlockTags.DAWNSTONE_SEED, EmbersBlockTags.MITHRIL_SEED);
		tag(EmbersBlockTags.COPPER_SEED, EmbersItemTags.COPPER_SEED).add(RegistryManager.COPPER_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.IRON_SEED, EmbersItemTags.IRON_SEED).add(RegistryManager.IRON_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.GOLD_SEED, EmbersItemTags.GOLD_SEED).add(RegistryManager.GOLD_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.LEAD_SEED, EmbersItemTags.LEAD_SEED).add(RegistryManager.LEAD_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.SILVER_SEED, EmbersItemTags.SILVER_SEED).add(RegistryManager.SILVER_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.NICKEL_SEED, EmbersItemTags.NICKEL_SEED).add(RegistryManager.NICKEL_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.TIN_SEED, EmbersItemTags.TIN_SEED).add(RegistryManager.TIN_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.ALUMINUM_SEED, EmbersItemTags.ALUMINUM_SEED).add(RegistryManager.ALUMINUM_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.ZINC_SEED, EmbersItemTags.ZINC_SEED).add(RegistryManager.ZINC_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.PLATINUM_SEED, EmbersItemTags.PLATINUM_SEED).add(RegistryManager.PLATINUM_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.URANIUM_SEED, EmbersItemTags.URANIUM_SEED).add(RegistryManager.URANIUM_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.DAWNSTONE_SEED, EmbersItemTags.DAWNSTONE_SEED).add(RegistryManager.DAWNSTONE_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.MITHRIL_SEED, EmbersItemTags.MITHRIL_SEED).add(RegistryManager.MITHRIL_CRYSTAL_SEED.BLOCK.get());
		tag(EmbersBlockTags.ANVIL_METAL, EmbersItemTags.ANVIL_METAL).addTags(EmbersBlockTags.DAWNSTONE_BLOCK, EmbersBlockTags.MITHRIL_BLOCK);
	}
}
