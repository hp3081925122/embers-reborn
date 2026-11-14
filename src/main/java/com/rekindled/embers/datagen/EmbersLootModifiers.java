package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.curios.CuriosCompat;
import com.rekindled.embers.util.AshenAmuletLootModifier;
import com.rekindled.embers.util.AugmentPredicate;
import com.rekindled.embers.util.GrandhammerLootModifier;
import com.rekindled.embers.util.MatchCurioLootCondition;
import com.rekindled.embers.util.SuperHeaterLootModifier;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

public class EmbersLootModifiers extends GlobalLootModifierProvider {

	public EmbersLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup, Embers.MODID);
	}

	@Override
	protected void start() {
		add("grandhammer", new GrandhammerLootModifier(new LootItemCondition[]{
				MatchTool.toolMatches(ItemPredicate.Builder.item().of(RegistryManager.GRANDHAMMER.get())).build()
		}));
		add("superheater", new SuperHeaterLootModifier(new LootItemCondition[]{
				MatchTool.toolMatches(ItemPredicate.Builder.item().withSubPredicate(AugmentPredicate.TYPE, new AugmentPredicate(RegistryManager.SUPERHEATER_AUGMENT, 1))).build()
		}));
		add("ashenamulet", new AshenAmuletLootModifier(new LootItemCondition[]{
				MatchCurioLootCondition.curioMatches(ItemPredicate.Builder.item().of(CuriosCompat.ASHEN_AMULET.get())).build()
		}));
	}
}
