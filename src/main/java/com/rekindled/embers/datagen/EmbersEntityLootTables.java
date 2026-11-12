package com.rekindled.embers.datagen;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EmbersEntityLootTables extends EntityLootSubProvider {
	private net.minecraft.core.HolderLookup.Provider lookupProvider;

	public EmbersEntityLootTables(net.minecraft.core.HolderLookup.Provider provider) {
		super(FeatureFlags.VANILLA_SET, provider);
		this.lookupProvider = provider;
	}

	@Nonnull
	@Override
	public Stream<EntityType<?>> getKnownEntityTypes() {
		return BuiltInRegistries.ENTITY_TYPE.stream()
				.filter((entity) -> Embers.MODID.equals(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(entity)).getNamespace()));
	}

	@Override
	public void generate() {
		add(RegistryManager.ANCIENT_GOLEM.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(RegistryManager.ARCHAIC_BRICK.get())
								.apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 11.0F)))
								.apply(net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction.lootingMultiplier(lookupProvider, UniformGenerator.between(0.0F, 1.0F)))))
				.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(RegistryManager.ANCIENT_MOTIVE_CORE.get()))));
	}
}
