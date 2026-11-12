package com.rekindled.embers.util;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.rekindled.embers.compat.curios.CuriosCompat;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.fml.ModList;

/**
 * A LootItemCondition that checks worn curios against an {@link ItemPredicate}.
 */
public class MatchCurioLootCondition implements LootItemCondition {

		public static LootItemConditionType LOOT_CONDITION_TYPE = null;

	final ItemPredicate predicate;

	public MatchCurioLootCondition(ItemPredicate pToolPredicate) {
		this.predicate = pToolPredicate;
	}

	public LootItemConditionType getType() {
		return LOOT_CONDITION_TYPE;
	}

	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(net.minecraft.world.level.storage.loot.parameters.LootContextParams.DIRECT_ATTACKING_ENTITY, LootContextParams.THIS_ENTITY);
	}

	public boolean test(LootContext context) {
		if (!ModList.get().isLoaded("curios"))
			return false;

		Entity user = null;
		if (context.hasParam(net.minecraft.world.level.storage.loot.parameters.LootContextParams.DIRECT_ATTACKING_ENTITY))
			user = context.getParam(net.minecraft.world.level.storage.loot.parameters.LootContextParams.DIRECT_ATTACKING_ENTITY);
		else if (context.hasParam(LootContextParams.THIS_ENTITY))
			user = context.getParam(LootContextParams.THIS_ENTITY);

		if (user instanceof LivingEntity)
			return CuriosCompat.checkForCurios((LivingEntity) user, stack -> true);
		return false;
	}

	public static LootItemCondition.Builder curioMatches(ItemPredicate.Builder pToolPredicateBuilder) {
		return () -> {
			return new MatchCurioLootCondition(pToolPredicateBuilder.build());
		};
	}
	}