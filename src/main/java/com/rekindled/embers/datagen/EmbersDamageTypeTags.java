package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.Optional;

import com.rekindled.embers.Embers;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

public class EmbersDamageTypeTags extends net.minecraft.data.tags.KeyTagProvider<DamageType> {

	public static final TagKey<DamageType> INFLICTOR_GEM_BLACKLIST = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Embers.MODID, "inflictor_gem_blacklist"));
	public static final TagKey<DamageType> HOLY_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("consecration", "holy"));

	public EmbersDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, Registries.DAMAGE_TYPE, lookupProvider, Embers.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(EmbersDamageTypes.EMBER_KEY);
		//tag(DamageTypeTags.IS_FIRE).addOptional(EmbersDamageTypes.EMBER_KEY);
		tag(INFLICTOR_GEM_BLACKLIST)
		.add(DamageTypes.MOB_ATTACK, DamageTypes.MOB_ATTACK_NO_AGGRO, DamageTypes.MOB_PROJECTILE)
		.add(DamageTypes.GENERIC, DamageTypes.GENERIC_KILL)
		.add(DamageTypes.PLAYER_ATTACK)
		.add(DamageTypes.ARROW)
		.addTag(DamageTypeTags.BYPASSES_INVULNERABILITY);

		tag(HOLY_DAMAGE).addOptional(EmbersDamageTypes.EMBER_KEY);
		tag(DamageTypeTags.WITCH_RESISTANT_TO).addOptional(EmbersDamageTypes.EMBER_KEY);
	}
}
