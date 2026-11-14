package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.curios.CuriosCompat;
import com.rekindled.embers.util.AshenAmuletLootModifier;
import com.rekindled.embers.util.AugmentPredicate;
import com.rekindled.embers.util.GrandhammerLootModifier;
import com.rekindled.embers.util.MatchCurioLootCondition;
import com.rekindled.embers.util.SuperHeaterLootModifier;

import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.concurrent.CompletableFuture;

public class EmbersLootModifiers extends GlobalLootModifierProvider {

	public EmbersLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup, Embers.MODID);
	}

	@Override
	protected void start() {
		HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);
		add("grandhammer", new GrandhammerLootModifier(new LootItemCondition[]{
				MatchTool.toolMatches(ItemPredicate.Builder.item().of(items, RegistryManager.GRANDHAMMER.get())).build()
		}, 20));
		add("superheater", new SuperHeaterLootModifier(new LootItemCondition[]{
				MatchTool.toolMatches(ItemPredicate.Builder.item().withComponents(
						DataComponentMatchers.Builder.components().partial(AugmentPredicate.TYPE, new AugmentPredicate(RegistryManager.SUPERHEATER_AUGMENT, 1)).build()
				)).build()
		}, 10));
		add("ashenamulet", new AshenAmuletLootModifier(new LootItemCondition[]{
				MatchCurioLootCondition.curioMatches(ItemPredicate.Builder.item().of(items, CuriosCompat.ASHEN_AMULET.get())).build()
		}, IGlobalLootModifier.DEFAULT_PRIORITY));
	}
}
