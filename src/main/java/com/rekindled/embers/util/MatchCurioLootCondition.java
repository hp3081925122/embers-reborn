package com.rekindled.embers.util;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.compat.curios.CuriosCompat;

import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.fml.ModList;

/**
 * A LootItemCondition that checks worn curios against an {@link ItemPredicate}.
 */
public record MatchCurioLootCondition(ItemPredicate predicate) implements LootItemCondition {

	public static final MapCodec<MatchCurioLootCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ItemPredicate.CODEC.fieldOf("predicate").forGetter(MatchCurioLootCondition::predicate)
	).apply(inst, MatchCurioLootCondition::new));

	@Override
	public MapCodec<MatchCurioLootCondition> codec() {
		return CODEC;
	}

	@Override
	public Set<ContextKey<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY, LootContextParams.THIS_ENTITY);
	}

	@Override
	public boolean test(LootContext context) {
		if (!ModList.get().isLoaded("curios"))
			return false;

		Entity user = null;
		if (context.hasParameter(LootContextParams.ATTACKING_ENTITY))
			user = context.getParameter(LootContextParams.ATTACKING_ENTITY);
		else if (context.hasParameter(LootContextParams.THIS_ENTITY))
			user = context.getParameter(LootContextParams.THIS_ENTITY);

		if (user instanceof LivingEntity living)
			return CuriosCompat.checkForCurios(living, predicate::test);
		return false;
	}

	public static LootItemCondition.Builder curioMatches(ItemPredicate.Builder pToolPredicateBuilder) {
		return () -> new MatchCurioLootCondition(pToolPredicateBuilder.build());
	}
}
