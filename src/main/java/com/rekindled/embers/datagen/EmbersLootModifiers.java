package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.curios.CuriosCompat;
import com.rekindled.embers.util.AshenAmuletLootModifier;
import com.rekindled.embers.util.GrandhammerLootModifier;
import com.rekindled.embers.util.MatchCurioLootCondition;
import com.rekindled.embers.util.SuperHeaterLootModifier;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

public class EmbersLootModifiers extends GlobalLootModifierProvider {

	public EmbersLootModifiers(PackOutput output, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookup) {
		super(output, lookup, com.rekindled.embers.Embers.MODID);
	}

	@Override
	protected void start() {
		add("grandhammer", new GrandhammerLootModifier(new LootItemCondition[]{MatchTool.toolMatches(ItemPredicate.Builder.item()).build()}));
		add("superheater", new SuperHeaterLootModifier(new LootItemCondition[]{MatchTool.toolMatches(ItemPredicate.Builder.item()).build()}));
		add("ashenamulet", new AshenAmuletLootModifier(new LootItemCondition[]{MatchCurioLootCondition.curioMatches(ItemPredicate.Builder.item()).build()}));
	}
}