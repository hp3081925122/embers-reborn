package com.rekindled.embers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import com.rekindled.embers.fluidtypes.EmbersFluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;
import com.rekindled.embers.augment.BlastingCoreAugment;
import com.rekindled.embers.augment.CasterOrbAugment;
import com.rekindled.embers.augment.CinderJetAugment;
import com.rekindled.embers.augment.CoreAugment;
import com.rekindled.embers.augment.DiffractionBarrelAugment;
import com.rekindled.embers.augment.EldritchInsigniaAugment;
import com.rekindled.embers.augment.FlameBarrierAugment;
import com.rekindled.embers.augment.FocalLensAugment;
import com.rekindled.embers.augment.IntelligentApparatusAugment;
import com.rekindled.embers.augment.ResonatingBellAugment;
import com.rekindled.embers.augment.ShiftingScalesAugment;
import com.rekindled.embers.augment.SuperheaterAugment;
import com.rekindled.embers.augment.TinkerLensAugment;
import com.rekindled.embers.augment.WindingGearsAugment;
import com.rekindled.embers.block.AlchemyPedestalBlock;
import com.rekindled.embers.block.AlchemyTabletBlock;
import com.rekindled.embers.block.ArchaicLightBlock;
import com.rekindled.embers.block.AtmosphericBellowsBlock;
import com.rekindled.embers.block.AtmosphericGaugeBlock;
import com.rekindled.embers.block.AutomaticHammerBlock;
import com.rekindled.embers.block.BeamCannonBlock;
import com.rekindled.embers.block.BeamSplitterBlock;
import com.rekindled.embers.block.BinBlock;
import com.rekindled.embers.block.CaminiteGaugeBlock;
import com.rekindled.embers.block.CaminiteGaugeEdgeBlock;
import com.rekindled.embers.block.CaminiteRingBlock;
import com.rekindled.embers.block.CaminiteRingEdgeBlock;
import com.rekindled.embers.block.CaminiteValveBlock;
import com.rekindled.embers.block.CaminiteValveEdgeBlock;
import com.rekindled.embers.block.CatalysisChamberBlock;
import com.rekindled.embers.block.CatalyticPlugBlock;
import com.rekindled.embers.block.CharInstillerBlock;
import com.rekindled.embers.block.CinderPlinthBlock;
import com.rekindled.embers.block.ClockworkAttenuatorBlock;
import com.rekindled.embers.block.CombustionChamberBlock;
import com.rekindled.embers.block.CopperCellBlock;
import com.rekindled.embers.block.CopperChargerBlock;
import com.rekindled.embers.block.CreativeEmberBlock;
import com.rekindled.embers.block.CrystalCellBlock;
import com.rekindled.embers.block.CrystalCellEdgeBlock;
import com.rekindled.embers.block.CrystalSeedBlock;
import com.rekindled.embers.block.DawnstoneAnvilBlock;
import com.rekindled.embers.block.EmberActivatorBlock;
import com.rekindled.embers.block.EmberBoreBlock;
import com.rekindled.embers.block.EmberBoreEdgeBlock;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.block.EmberEjectorBlock;
import com.rekindled.embers.block.EmberEmitterBlock;
import com.rekindled.embers.block.EmberFunnelBlock;
import com.rekindled.embers.block.EmberInjectorBlock;
import com.rekindled.embers.block.EmberLanternBlock;
import com.rekindled.embers.block.EmberReceiverBlock;
import com.rekindled.embers.block.EmberRelayBlock;
import com.rekindled.embers.block.EmberSiphonBlock;
import com.rekindled.embers.block.EntropicEnumeratorBlock;
import com.rekindled.embers.block.ExcavationBucketsBlock;
import com.rekindled.embers.block.FieldChartBlock;
import com.rekindled.embers.block.FieldChartEdgeBlock;
import com.rekindled.embers.block.FluidDialBlock;
import com.rekindled.embers.block.FluidExtractorBlock;
import com.rekindled.embers.block.FluidPipeBlock;
import com.rekindled.embers.block.FluidTransferBlock;
import com.rekindled.embers.block.FluidVesselBlock;
import com.rekindled.embers.block.GeologicSeparatorBlock;
import com.rekindled.embers.block.GlimmerBlock;
import com.rekindled.embers.block.HearthCoilBlock;
import com.rekindled.embers.block.HearthCoilEdgeBlock;
import com.rekindled.embers.block.HeatExchangerBlock;
import com.rekindled.embers.block.HeatInsulationBlock;
import com.rekindled.embers.block.IgnemReactorBlock;
import com.rekindled.embers.block.InfernoForgeBlock;
import com.rekindled.embers.block.InfernoForgeEdgeBlock;
import com.rekindled.embers.block.ItemDialBlock;
import com.rekindled.embers.block.ItemDropperBlock;
import com.rekindled.embers.block.ItemExtractorBlock;
import com.rekindled.embers.block.ItemPipeBlock;
import com.rekindled.embers.block.ItemTransferBlock;
import com.rekindled.embers.block.ItemVacuumBlock;
import com.rekindled.embers.block.MechanicalCoreBlock;
import com.rekindled.embers.block.MechanicalPumpBlock;
import com.rekindled.embers.block.MelterBlock;
import com.rekindled.embers.block.MiniBoilerBlock;
import com.rekindled.embers.block.MirrorRelayBlock;
import com.rekindled.embers.block.MithrilBlock;
import com.rekindled.embers.block.MixerCentrifugeBlock;
import com.rekindled.embers.block.MnemonicInscriberBlock;
import com.rekindled.embers.block.PressureRefineryBlock;
import com.rekindled.embers.block.ReservoirBlock;
import com.rekindled.embers.block.ReservoirEdgeBlock;
import com.rekindled.embers.block.StampBaseBlock;
import com.rekindled.embers.block.StamperBlock;
import com.rekindled.embers.block.WaterloggableButtonBlock;
import com.rekindled.embers.block.WaterloggableLeverBlock;
import com.rekindled.embers.block.WildfireStirlingBlock;
import com.rekindled.embers.blockentity.AlchemyPedestalBlockEntity;
import com.rekindled.embers.blockentity.AlchemyPedestalTopBlockEntity;
import com.rekindled.embers.blockentity.AlchemyTabletBlockEntity;
import com.rekindled.embers.blockentity.AtmosphericBellowsBlockEntity;
import com.rekindled.embers.blockentity.AtmosphericGaugeBlockEntity;
import com.rekindled.embers.blockentity.AutomaticHammerBlockEntity;
import com.rekindled.embers.blockentity.BeamCannonBlockEntity;
import com.rekindled.embers.blockentity.BeamSplitterBlockEntity;
import com.rekindled.embers.blockentity.BinBlockEntity;
import com.rekindled.embers.blockentity.CaminiteValveBlockEntity;
import com.rekindled.embers.blockentity.CatalysisChamberBlockEntity;
import com.rekindled.embers.blockentity.CatalyticPlugBlockEntity;
import com.rekindled.embers.blockentity.CharInstillerBlockEntity;
import com.rekindled.embers.blockentity.CinderPlinthBlockEntity;
import com.rekindled.embers.blockentity.ClockworkAttenuatorBlockEntity;
import com.rekindled.embers.blockentity.CombustionChamberBlockEntity;
import com.rekindled.embers.blockentity.CopperCellBlockEntity;
import com.rekindled.embers.blockentity.CopperChargerBlockEntity;
import com.rekindled.embers.blockentity.CreativeEmberBlockEntity;
import com.rekindled.embers.blockentity.CrystalCellBlockEntity;
import com.rekindled.embers.blockentity.CrystalSeedBlockEntity;
import com.rekindled.embers.blockentity.DawnstoneAnvilBlockEntity;
import com.rekindled.embers.blockentity.EmberActivatorBottomBlockEntity;
import com.rekindled.embers.blockentity.EmberActivatorTopBlockEntity;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;
import com.rekindled.embers.blockentity.EmberDialBlockEntity;
import com.rekindled.embers.blockentity.EmberEjectorBlockEntity;
import com.rekindled.embers.blockentity.EmberEmitterBlockEntity;
import com.rekindled.embers.blockentity.EmberFunnelBlockEntity;
import com.rekindled.embers.blockentity.EmberInjectorBlockEntity;
import com.rekindled.embers.blockentity.EmberReceiverBlockEntity;
import com.rekindled.embers.blockentity.EmberRelayBlockEntity;
import com.rekindled.embers.blockentity.EmberSiphonBlockEntity;
import com.rekindled.embers.blockentity.EntropicEnumeratorBlockEntity;
import com.rekindled.embers.blockentity.ExcavationBucketsBlockEntity;
import com.rekindled.embers.blockentity.FieldChartBlockEntity;
import com.rekindled.embers.blockentity.FluidDialBlockEntity;
import com.rekindled.embers.blockentity.FluidExtractorBlockEntity;
import com.rekindled.embers.blockentity.FluidPipeBlockEntity;
import com.rekindled.embers.blockentity.FluidTransferBlockEntity;
import com.rekindled.embers.blockentity.FluidVesselBlockEntity;
import com.rekindled.embers.blockentity.GeologicSeparatorBlockEntity;
import com.rekindled.embers.blockentity.HearthCoilBlockEntity;
import com.rekindled.embers.blockentity.HeatExchangerBlockEntity;
import com.rekindled.embers.blockentity.HeatInsulationBlockEntity;
import com.rekindled.embers.blockentity.IgnemReactorBlockEntity;
import com.rekindled.embers.blockentity.InfernoForgeBottomBlockEntity;
import com.rekindled.embers.blockentity.InfernoForgeTopBlockEntity;
import com.rekindled.embers.blockentity.ItemDialBlockEntity;
import com.rekindled.embers.blockentity.ItemDropperBlockEntity;
import com.rekindled.embers.blockentity.ItemExtractorBlockEntity;
import com.rekindled.embers.blockentity.ItemPipeBlockEntity;
import com.rekindled.embers.blockentity.ItemTransferBlockEntity;
import com.rekindled.embers.blockentity.ItemVacuumBlockEntity;
import com.rekindled.embers.blockentity.MechanicalCoreBlockEntity;
import com.rekindled.embers.blockentity.MechanicalPumpBottomBlockEntity;
import com.rekindled.embers.blockentity.MechanicalPumpTopBlockEntity;
import com.rekindled.embers.blockentity.MelterBottomBlockEntity;
import com.rekindled.embers.blockentity.MelterTopBlockEntity;
import com.rekindled.embers.blockentity.MiniBoilerBlockEntity;
import com.rekindled.embers.blockentity.MirrorRelayBlockEntity;
import com.rekindled.embers.blockentity.MithrilBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeTopBlockEntity;
import com.rekindled.embers.blockentity.MnemonicInscriberBlockEntity;
import com.rekindled.embers.blockentity.PressureRefineryBottomBlockEntity;
import com.rekindled.embers.blockentity.PressureRefineryTopBlockEntity;
import com.rekindled.embers.blockentity.ReservoirBlockEntity;
import com.rekindled.embers.blockentity.StampBaseBlockEntity;
import com.rekindled.embers.blockentity.StamperBlockEntity;
import com.rekindled.embers.blockentity.WildfireStirlingBlockEntity;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.entity.EmberProjectileEntity;
import com.rekindled.embers.entity.GlimmerProjectileEntity;
import com.rekindled.embers.fluidtypes.EmbersFluidType.FluidInfo;
import com.rekindled.embers.fluidtypes.MoltenMetalFluidType;
import com.rekindled.embers.fluidtypes.SteamFluidType;
import com.rekindled.embers.fluidtypes.ViscousFluidType;
import com.rekindled.embers.gui.SlateMenu;
import com.rekindled.embers.item.AlchemicalNoteItem;
import com.rekindled.embers.item.AlchemyHintItem;
import com.rekindled.embers.item.AncientCodexItem;
import com.rekindled.embers.item.AshenArmorGemItem;
import com.rekindled.embers.item.BlazingRayItem;
import com.rekindled.embers.item.CaminiteRingBlockItem;
import com.rekindled.embers.item.CinderStaffItem;
import com.rekindled.embers.item.ClockworkAxeItem;
import com.rekindled.embers.item.ClockworkHammerItem;
import com.rekindled.embers.item.ClockworkPickaxeItem;
import com.rekindled.embers.item.CodebreakingSlateItem;
import com.rekindled.embers.item.CopperCellBlockItem;
import com.rekindled.embers.item.EmberCartridgeItem;
import com.rekindled.embers.item.EmberJarItem;
import com.rekindled.embers.item.EmberRecordItem;
import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.item.FluidVesselBlockItem;
import com.rekindled.embers.item.FuelItem;
import com.rekindled.embers.item.GlimmerCrystalItem;
import com.rekindled.embers.item.GlimmerLampItem;
import com.rekindled.embers.item.InflictorGemItem;
import com.rekindled.embers.item.TinkerHammerItem;
import com.rekindled.embers.item.TyrfingItem;
import com.rekindled.embers.item.UpgradeItem;
import com.rekindled.embers.particle.AlchemyCircleParticleOptions;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.particle.TyrfingParticleOptions;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.particle.XRayGlowParticleOptions;
import com.rekindled.embers.recipe.AlchemyRecipe;
import com.rekindled.embers.recipe.AlchemyRecipeForBabies;
import com.rekindled.embers.recipe.AnvilAugmentRecipe;
import com.rekindled.embers.recipe.AnvilAugmentRemoveRecipe;
import com.rekindled.embers.recipe.AnvilBreakdownRecipe;
import com.rekindled.embers.recipe.AnvilRepairMateriaRecipe;
import com.rekindled.embers.recipe.AnvilRepairRecipe;
import com.rekindled.embers.recipe.BoilingRecipe;
import com.rekindled.embers.recipe.BoringRecipe;
import com.rekindled.embers.recipe.CatalysisCombustionRecipe;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.ExcavationRecipe;
import com.rekindled.embers.recipe.GaseousFuelRecipe;
import com.rekindled.embers.recipe.GemSocketRecipe;
import com.rekindled.embers.recipe.GemUnsocketRecipe;
import com.rekindled.embers.recipe.IAlchemyRecipe;
import com.rekindled.embers.recipe.IBoilingRecipe;
import com.rekindled.embers.recipe.IBoringRecipe;
import com.rekindled.embers.recipe.ICatalysisCombustionRecipe;
import com.rekindled.embers.recipe.IDawnstoneAnvilRecipe;
import com.rekindled.embers.recipe.IEmberActivationRecipe;
import com.rekindled.embers.recipe.IGaseousFuelRecipe;
import com.rekindled.embers.recipe.IMeltingRecipe;
import com.rekindled.embers.recipe.IMetalCoefficientRecipe;
import com.rekindled.embers.recipe.IMixingRecipe;
import com.rekindled.embers.recipe.IStampingRecipe;
import com.rekindled.embers.recipe.MeltingRecipe;
import com.rekindled.embers.recipe.MetalCoefficientRecipe;
import com.rekindled.embers.recipe.MixingRecipe;
import com.rekindled.embers.recipe.StampingRecipe;
import com.rekindled.embers.util.AshenAmuletLootModifier;
import com.rekindled.embers.util.AshenArmorMaterial;
import com.rekindled.embers.util.EmbersTiers;
import com.rekindled.embers.util.GrandhammerLootModifier;
import com.rekindled.embers.util.LegacyDeferredRegister;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.SuperHeaterLootModifier;
import com.rekindled.embers.worldgen.CaveStructure;
import com.rekindled.embers.worldgen.CrystalSeedStructureProcessor;
import com.rekindled.embers.worldgen.EntityMobilizerStructureProcessor;

import net.minecraft.core.BlockPos;
// BlockSource removed in 1.21.1; dispenser API changed
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry.InteractionInformation;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class RegistryManager {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, Embers.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, Embers.MODID);
	public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Embers.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Embers.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Embers.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES_NEW = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Embers.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES_OLD = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Embers.MODID_OLD);
	public static final LegacyDeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = new LegacyDeferredRegister<BlockEntityType<?>>(BLOCK_ENTITY_TYPES_NEW, BLOCK_ENTITY_TYPES_OLD);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Embers.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Embers.MODID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Embers.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Embers.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Embers.MODID);
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Embers.MODID);
	public static final net.neoforged.neoforge.registries.DeferredRegister<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENT_TYPES =
		net.neoforged.neoforge.registries.DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES, Embers.MODID);
	public static final net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.common.crafting.IngredientType<?>, net.neoforged.neoforge.common.crafting.IngredientType<com.rekindled.embers.recipe.HeatIngredient>> HEAT_INGREDIENT_TYPE =
		INGREDIENT_TYPES.register("heat_ingredient", () -> new net.neoforged.neoforge.common.crafting.IngredientType<>(com.rekindled.embers.recipe.HeatIngredient.CODEC, com.rekindled.embers.recipe.HeatIngredient.STREAM_CODEC));
	public static final net.neoforged.neoforge.registries.DeferredHolder<net.neoforged.neoforge.common.crafting.IngredientType<?>, net.neoforged.neoforge.common.crafting.IngredientType<com.rekindled.embers.recipe.AugmentIngredient>> AUGMENT_INGREDIENT_TYPE =
		INGREDIENT_TYPES.register("augment_ingredient", () -> new net.neoforged.neoforge.common.crafting.IngredientType<>(com.rekindled.embers.recipe.AugmentIngredient.CODEC, com.rekindled.embers.recipe.AugmentIngredient.STREAM_CODEC));
	// Item data components
	public static final DeferredRegister<net.minecraft.core.component.DataComponentType<?>> DATA_COMPONENTS =
		DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Embers.MODID);
	/** Fluid stored in a Fluid Vessel item (was the "Fluid" NBT tag in 1.20.1) */
	public static final java.util.function.Supplier<net.minecraft.core.component.DataComponentType<net.neoforged.neoforge.fluids.SimpleFluidContent>> FLUID_CONTENT =
		DATA_COMPONENTS.register("fluid_content", () -> net.minecraft.core.component.DataComponentType.<net.neoforged.neoforge.fluids.SimpleFluidContent>builder()
			.persistent(net.neoforged.neoforge.fluids.SimpleFluidContent.CODEC)
			.networkSynchronized(net.neoforged.neoforge.fluids.SimpleFluidContent.STREAM_CODEC).build());

	// Persistent per-entity data (replaces the 1.20.1 attached capabilities)
	public static final DeferredRegister<net.neoforged.neoforge.attachment.AttachmentType<?>> ATTACHMENT_TYPES =
		DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Embers.MODID);
	public static final java.util.function.Supplier<net.neoforged.neoforge.attachment.AttachmentType<com.rekindled.embers.research.capability.DefaultResearchCapability>> RESEARCH_ATTACHMENT =
		ATTACHMENT_TYPES.register("research", () -> com.rekindled.embers.util.NbtBridge.attachment(com.rekindled.embers.research.capability.DefaultResearchCapability::new).copyOnDeath().build());
	public static final java.util.function.Supplier<net.neoforged.neoforge.attachment.AttachmentType<com.rekindled.embers.augment.ShiftingScalesAugment.DefaultScalesCapability>> SCALES_ATTACHMENT =
		ATTACHMENT_TYPES.register("scales", () -> com.rekindled.embers.util.NbtBridge.attachment(com.rekindled.embers.augment.ShiftingScalesAugment.DefaultScalesCapability::new).build());
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Embers.MODID);
	public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Embers.MODID);
	public static final DeferredRegister<net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, Embers.MODID);
	public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Embers.MODID);

	public static Map<Identifier, IAugment> augmentRegistry = new HashMap<Identifier, IAugment>();

	public static List<FluidStuff> fluidList = new ArrayList<FluidStuff>();

	public static FluidStuff addFluid(String localizedName, FluidInfo info, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Source> source, Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Flowing> flowing, @Nullable Consumer<BaseFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		FluidStuff fluid = new FluidStuff(info.name, localizedName, info.color, type.apply(prop, info), block, fluidProperties, source, flowing);
		fluidList.add(fluid);
		return fluid;
	}

	public static FluidStuff addFluid(String localizedName, FluidInfo info, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<BaseFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		return addFluid(localizedName, info, type, block, EmbersFluidType.Source::new, EmbersFluidType.Flowing::new, fluidProperties, prop);
	}

	public static FluidType.Properties moltenMetalProps() {
		return FluidType.Properties.create()
				.canSwim(false)
				.canDrown(false)
				.pathType(PathType.LAVA)
				.adjacentPathType(null)
				.motionScale(0.0023333333333333335D)
				.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
				.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
				.lightLevel(12)
				.density(3000)
				.viscosity(6000)
				.temperature(1100);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
		return ENTITY_TYPES.register(name, () -> builder.build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Embers.MODID, name))));
	}

		@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T extends ParticleOptions> DeferredHolder<ParticleType<T>, ParticleType<T>> registerParticle(String name, boolean overrideLimiter, Object deserializerOrCodec, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.FriendlyByteBuf, T> streamCodec) {
		return (DeferredHolder<ParticleType<T>, ParticleType<T>>) (Object) PARTICLE_TYPES.register(name, () -> new ParticleType<T>(overrideLimiter) {
			@Override
			public com.mojang.serialization.MapCodec<T> codec() {
				if (deserializerOrCodec instanceof com.mojang.serialization.Codec) {
					return ((com.mojang.serialization.Codec<T>) deserializerOrCodec).fieldOf("options");
				} else if (deserializerOrCodec instanceof com.mojang.serialization.MapCodec) {
					return (com.mojang.serialization.MapCodec<T>) deserializerOrCodec;
				}
				return com.mojang.serialization.MapCodec.unit(() -> { throw new UnsupportedOperationException(); });
			}
			@Override
			public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, T> streamCodec() {
				return (net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, T>) (net.minecraft.network.codec.StreamCodec<?, T>) streamCodec;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T extends Recipe<?>> DeferredHolder<RecipeType<T>, RecipeType<T>> registerRecipeType(final String identifier) {
		return (DeferredHolder<RecipeType<T>, RecipeType<T>>) (Object) RECIPE_TYPES.register(identifier, () -> new RecipeType<T>() {
			public String toString() {
				return Embers.MODID + ":" + identifier;
			}
		});
	}

	//blocks
	public static final DeferredHolder<Block, Block> LEAD_ORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "lead_ore", () -> new DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(0, 0), Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.0f)));
	public static final DeferredHolder<Block, Block> DEEPSLATE_LEAD_ORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "deepslate_lead_ore", () -> new DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(0, 0), Properties.ofFullCopy(LEAD_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredHolder<Block, Block> RAW_LEAD_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "raw_lead_block", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F)));
	public static final DeferredHolder<Block, Block> LEAD_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "lead_block", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));

	public static final DeferredHolder<Block, Block> SILVER_ORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "silver_ore", () -> new DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(0, 0), Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.0f)));
	public static final DeferredHolder<Block, Block> DEEPSLATE_SILVER_ORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "deepslate_silver_ore", () -> new DropExperienceBlock(net.minecraft.util.valueproviders.UniformInt.of(0, 0), Properties.ofFullCopy(SILVER_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredHolder<Block, Block> RAW_SILVER_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "raw_silver_block", () -> new Block(Properties.of().mapColor(MapColor.ICE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 6.0F)));
	public static final DeferredHolder<Block, Block> SILVER_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "silver_block", () -> new Block(Properties.of().mapColor(MapColor.ICE).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));

	public static final DeferredHolder<Block, Block> DAWNSTONE_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "dawnstone_block", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredHolder<Block, Block> MITHRIL_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mithril_block", () -> new MithrilBlock(Properties.of().mapColor(MapColor.GLOW_LICHEN).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(5.0F, 6.0F).noOcclusion()));

	public static final DeferredHolder<Block, Block> CAMINITE_BRICKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_bricks", () -> new Block(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks CAMINITE_BRICKS_DECO = new StoneDecoBlocks("caminite_bricks", CAMINITE_BRICKS, () -> Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f));
	public static final DeferredHolder<Block, Block> CAMINITE_LARGE_BRICKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_large_bricks", () -> new Block(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks CAMINITE_LARGE_BRICKS_DECO = new StoneDecoBlocks("caminite_large_bricks", CAMINITE_LARGE_BRICKS, () -> Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> RAW_CAMINITE_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "raw_caminite_block", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.GRAVEL).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CAMINITE_LARGE_TILE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_large_tile", () -> new Block(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks CAMINITE_LARGE_TILE_DECO = new StoneDecoBlocks("caminite_large_tile", CAMINITE_LARGE_TILE, () -> Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> CAMINITE_TILES = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_tiles", () -> new Block(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks CAMINITE_TILES_DECO = new StoneDecoBlocks("caminite_tiles", CAMINITE_TILES, () -> Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> ARCHAIC_BRICKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "archaic_bricks", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ARCHAIC_BRICKS_DECO = new StoneDecoBlocks("archaic_bricks", ARCHAIC_BRICKS, () -> Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f));
	public static final DeferredHolder<Block, Block> ARCHAIC_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "archaic_edge", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> ARCHAIC_TILE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "archaic_tile", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ARCHAIC_TILE_DECO = new StoneDecoBlocks("archaic_tile", ARCHAIC_TILE, () -> Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> ARCHAIC_LARGE_BRICKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "archaic_large_bricks", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ARCHAIC_LARGE_BRICKS_DECO = new StoneDecoBlocks("archaic_large_bricks", ARCHAIC_LARGE_BRICKS, () -> Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> ARCHAIC_LIGHT = com.rekindled.embers.util.RegCtx.block(BLOCKS, "archaic_light", () -> new ArchaicLightBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f).lightLevel(state -> 15)));
	public static final DeferredHolder<Block, Block> ASHEN_STONE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ashen_stone", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ASHEN_STONE_DECO = new StoneDecoBlocks("ashen_stone", ASHEN_STONE, () -> Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f));
	public static final DeferredHolder<Block, Block> ASHEN_BRICK = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ashen_brick", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ASHEN_BRICK_DECO = new StoneDecoBlocks("ashen_brick", ASHEN_BRICK, () -> Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f));
	public static final DeferredHolder<Block, Block> ASHEN_TILE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ashen_tile", () -> new Block(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks ASHEN_TILE_DECO = new StoneDecoBlocks("ashen_tile", ASHEN_TILE, () -> Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f));
	public static final DeferredHolder<Block, Block> SEALED_PLANKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "sealed_planks", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f)));
	public static final StoneDecoBlocks SEALED_PLANKS_DECO = new StoneDecoBlocks("sealed_planks", SEALED_PLANKS, () -> Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, Block> REINFORCED_SEALED_PLANKS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "reinforced_sealed_planks", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(2.6f)));
	public static final DeferredHolder<Block, Block> SEALED_WOOD_TILE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "sealed_wood_tile", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f)));
	public static final StoneDecoBlocks SEALED_WOOD_TILE_DECO = new StoneDecoBlocks("sealed_wood_tile", SEALED_WOOD_TILE, () -> Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f), true, true, false);
	public static final DeferredHolder<Block, RotatedPillarBlock> SEALED_WOOD_PILLAR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "sealed_wood_pillar", () -> new RotatedPillarBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f)));
	public static final DeferredHolder<Block, RotatedPillarBlock> SEALED_WOOD_KEG = com.rekindled.embers.util.RegCtx.block(BLOCKS, "sealed_wood_keg", () -> new RotatedPillarBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).strength(1.6f)));
	public static final DeferredHolder<Block, Block> SOLIDIFIED_METAL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "solidified_metal", () -> new Block(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> METAL_PLATFORM = com.rekindled.embers.util.RegCtx.block(BLOCKS, "metal_platform", () -> new Block(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f)));
	public static final StoneDecoBlocks METAL_PLATFORM_DECO = new StoneDecoBlocks("metal_platform", METAL_PLATFORM, () -> Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f), false, true, false);
	public static final DeferredHolder<Block, Block> EMBER_LANTERN = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_lantern", () -> new EmberLanternBlock(Properties.of().mapColor(MapColor.NONE).sound(SoundType.LANTERN).requiresCorrectToolForDrops().strength(1.6f).lightLevel(state -> 15)));

	public static final DeferredHolder<Block, Block> COPPER_CELL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "copper_cell", () -> new CopperCellBlock(Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.4f).noOcclusion().isRedstoneConductor((a, b, c) -> true)));
	public static final DeferredHolder<Block, Block> CREATIVE_EMBER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "creative_ember_source", () -> new CreativeEmberBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> EMBER_DIAL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_dial", () -> new EmberDialBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> ITEM_DIAL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_dial", () -> new ItemDialBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> FLUID_DIAL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "fluid_dial", () -> new FluidDialBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> ATMOSPHERIC_GAUGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "atmospheric_gauge", () -> new AtmosphericGaugeBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> EMBER_EMITTER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_emitter", () -> new EmberEmitterBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_MACHINE).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> EMBER_RECEIVER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_receiver", () -> new EmberReceiverBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_MACHINE).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> CAMINITE_LEVER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_lever", () -> new WaterloggableLeverBlock(Properties.of().mapColor(MapColor.NONE).noCollision().sound(EmbersSounds.CAMINITE).strength(0.75f)));
	public static final DeferredHolder<Block, ButtonBlock> CAMINITE_BUTTON = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_button", () -> new WaterloggableButtonBlock(BlockSetType.STONE, 20, Properties.of().mapColor(MapColor.NONE).noCollision().strength(0.5f)));
	public static final DeferredHolder<Block, Block> ITEM_PIPE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_pipe", () -> new ItemPipeBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).dynamicShape().noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> ITEM_EXTRACTOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_extractor", () -> new ItemExtractorBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).dynamicShape().noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> EMBER_BORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_bore", () -> new EmberBoreBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MULTIBLOCK_CENTER).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> EMBER_BORE_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_bore_edge", () -> new EmberBoreEdgeBlock(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> MECHANICAL_CORE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mechanical_core", () -> new MechanicalCoreBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().isRedstoneConductor((a, b, c) -> true)));
	public static final DeferredHolder<Block, Block> EMBER_ACTIVATOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_activator", () -> new EmberActivatorBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> MELTER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "melter", () -> new MelterBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> FLUID_PIPE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "fluid_pipe", () -> new FluidPipeBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).dynamicShape().noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> FLUID_EXTRACTOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "fluid_extractor", () -> new FluidExtractorBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).dynamicShape().noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> FLUID_VESSEL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "fluid_vessel", () -> new FluidVesselBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> STAMPER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "stamper", () -> new StamperBlock(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> STAMP_BASE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "stamp_base", () -> new StampBaseBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> BIN = com.rekindled.embers.util.RegCtx.block(BLOCKS, "bin", () -> new BinBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> MIXER_CENTRIFUGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mixer_centrifuge", () -> new MixerCentrifugeBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> ITEM_DROPPER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_dropper", () -> new ItemDropperBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> PRESSURE_REFINERY = com.rekindled.embers.util.RegCtx.block(BLOCKS, "pressure_refinery", () -> new PressureRefineryBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> EMBER_EJECTOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_ejector", () -> new EmberEjectorBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_MACHINE).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> EMBER_FUNNEL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_funnel", () -> new EmberFunnelBlock(Properties.of().mapColor(MapColor.NONE).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> EMBER_RELAY = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_relay", () -> new EmberRelayBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> MIRROR_RELAY = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mirror_relay", () -> new MirrorRelayBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> BEAM_SPLITTER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "beam_splitter", () -> new BeamSplitterBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(0.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> ITEM_VACUUM = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_vacuum", () -> new ItemVacuumBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> HEARTH_COIL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "hearth_coil", () -> new HearthCoilBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.MULTIBLOCK_CENTER).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> HEARTH_COIL_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "hearth_coil_edge", () -> new HearthCoilEdgeBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> RESERVOIR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "reservoir", () -> new ReservoirBlock(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.CAMINITE_MULTIBLOCK_CENTER).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> RESERVOIR_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "reservoir_edge", () -> new ReservoirEdgeBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CAMINITE_RING = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_ring", () -> new CaminiteRingBlock(Properties.of().mapColor(MapColor.NONE).pushReaction(PushReaction.BLOCK).noCollision().sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> CAMINITE_RING_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_ring_edge", () -> new CaminiteRingEdgeBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.CAMINITE_MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CAMINITE_GAUGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_gauge", () -> new CaminiteGaugeBlock(Properties.of().mapColor(MapColor.NONE).pushReaction(PushReaction.BLOCK).noCollision().sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> CAMINITE_GAUGE_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_gauge_edge", () -> new CaminiteGaugeEdgeBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.CAMINITE_MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CAMINITE_VALVE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_valve", () -> new CaminiteValveBlock(Properties.of().mapColor(MapColor.NONE).pushReaction(PushReaction.BLOCK).noCollision().sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> CAMINITE_VALVE_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "caminite_valve_edge", () -> new CaminiteValveEdgeBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.CAMINITE_MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CRYSTAL_CELL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "crystal_cell", () -> new CrystalCellBlock(Properties.of().mapColor(MapColor.WOOD).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.MULTIBLOCK_CENTER).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> CRYSTAL_CELL_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "crystal_cell_edge", () -> new CrystalCellEdgeBlock(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> CLOCKWORK_ATTENUATOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "clockwork_attenuator", () -> new ClockworkAttenuatorBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.LIGHT_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> GEOLOGIC_SEPARATOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "geologic_separator", () -> new GeologicSeparatorBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> COPPER_CHARGER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "copper_charger", () -> new CopperChargerBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> EMBER_SIPHON = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_siphon", () -> new EmberSiphonBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> ITEM_TRANSFER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "item_transfer", () -> new ItemTransferBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> FLUID_TRANSFER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "fluid_transfer", () -> new FluidTransferBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> ALCHEMY_PEDESTAL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "alchemy_pedestal", () -> new AlchemyPedestalBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> ALCHEMY_TABLET = com.rekindled.embers.util.RegCtx.block(BLOCKS, "alchemy_tablet", () -> new AlchemyTabletBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> BEAM_CANNON = com.rekindled.embers.util.RegCtx.block(BLOCKS, "beam_cannon", () -> new BeamCannonBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> MECHANICAL_PUMP = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mechanical_pump", () -> new MechanicalPumpBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> MINI_BOILER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mini_boiler", () -> new MiniBoilerBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> CATALYTIC_PLUG = com.rekindled.embers.util.RegCtx.block(BLOCKS, "catalytic_plug", () -> new CatalyticPlugBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> WILDFIRE_STIRLING = com.rekindled.embers.util.RegCtx.block(BLOCKS, "wildfire_stirling", () -> new WildfireStirlingBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> EMBER_INJECTOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ember_injector", () -> new EmberInjectorBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final MetalCrystalSeed COPPER_CRYSTAL_SEED = new MetalCrystalSeed("copper");
	public static final MetalCrystalSeed IRON_CRYSTAL_SEED = new MetalCrystalSeed("iron");
	public static final MetalCrystalSeed GOLD_CRYSTAL_SEED = new MetalCrystalSeed("gold");
	public static final MetalCrystalSeed LEAD_CRYSTAL_SEED = new MetalCrystalSeed("lead");
	public static final MetalCrystalSeed SILVER_CRYSTAL_SEED = new MetalCrystalSeed("silver");
	public static final MetalCrystalSeed NICKEL_CRYSTAL_SEED = new MetalCrystalSeed("nickel");
	public static final MetalCrystalSeed TIN_CRYSTAL_SEED = new MetalCrystalSeed("tin");
	public static final MetalCrystalSeed ALUMINUM_CRYSTAL_SEED = new MetalCrystalSeed("aluminum");
	public static final MetalCrystalSeed ZINC_CRYSTAL_SEED = new MetalCrystalSeed("zinc");
	public static final MetalCrystalSeed PLATINUM_CRYSTAL_SEED = new MetalCrystalSeed("platinum");
	public static final MetalCrystalSeed URANIUM_CRYSTAL_SEED = new MetalCrystalSeed("uranium");
	public static final MetalCrystalSeed DAWNSTONE_CRYSTAL_SEED = new MetalCrystalSeed("dawnstone");
	public static final MetalCrystalSeed MITHRIL_CRYSTAL_SEED = new MetalCrystalSeed("dwarven_mithril");
	public static final DeferredHolder<Block, Block> FIELD_CHART = com.rekindled.embers.util.RegCtx.block(BLOCKS, "field_chart", () -> new FieldChartBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.NETHER_BRICKS).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> FIELD_CHART_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "field_chart_edge", () -> new FieldChartEdgeBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).pushReaction(PushReaction.BLOCK).sound(EmbersSounds.ARCHAIC_MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> IGNEM_REACTOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "ignem_reactor", () -> new IgnemReactorBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> CATALYSIS_CHAMBER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "catalysis_chamber", () -> new CatalysisChamberBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> COMBUSTION_CHAMBER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "combustion_chamber", () -> new CombustionChamberBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> GLIMMER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "glimmer", () -> new GlimmerBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.STONE).strength(0f).lightLevel(state -> 15).noCollision().noOcclusion()));
	public static final DeferredHolder<Block, Block> CINDER_PLINTH = com.rekindled.embers.util.RegCtx.block(BLOCKS, "cinder_plinth", () -> new CinderPlinthBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> DAWNSTONE_ANVIL = com.rekindled.embers.util.RegCtx.block(BLOCKS, "dawnstone_anvil", () -> new DawnstoneAnvilBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(SoundType.ANVIL).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> AUTOMATIC_HAMMER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "automatic_hammer", () -> new AutomaticHammerBlock(Properties.of().mapColor(MapColor.WOOD).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion()));
	public static final DeferredHolder<Block, Block> INFERNO_FORGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "inferno_forge", () -> new InfernoForgeBlock(Properties.of().mapColor(MapColor.COLOR_GRAY).sound(EmbersSounds.MULTIBLOCK_CENTER).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));
	public static final DeferredHolder<Block, Block> INFERNO_FORGE_EDGE = com.rekindled.embers.util.RegCtx.block(BLOCKS, "inferno_forge_edge", () -> new InfernoForgeEdgeBlock(Properties.of().mapColor(MapColor.TERRACOTTA_YELLOW).sound(EmbersSounds.MULTIBLOCK_EXTRA).requiresCorrectToolForDrops().strength(1.6f)));
	public static final DeferredHolder<Block, Block> MNEMONIC_INSCRIBER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "mnemonic_inscriber", () -> new MnemonicInscriberBlock(Properties.of().mapColor(MapColor.NONE).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn()));
	public static final DeferredHolder<Block, Block> CHAR_INSTILLER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "char_instiller", () -> new CharInstillerBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> ATMOSPHERIC_BELLOWS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "atmospheric_bellows", () -> new AtmosphericBellowsBlock(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).sound(SoundType.WOOD).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> ENTROPIC_ENUMERATOR = com.rekindled.embers.util.RegCtx.block(BLOCKS, "entropic_enumerator", () -> new EntropicEnumeratorBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.ASHEN_STONE).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> HEAT_EXCHANGER = com.rekindled.embers.util.RegCtx.block(BLOCKS, "heat_exchanger", () -> new HeatExchangerBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> HEAT_INSULATION = com.rekindled.embers.util.RegCtx.block(BLOCKS, "heat_insulation", () -> new HeatInsulationBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).sound(EmbersSounds.MACHINE).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));
	public static final DeferredHolder<Block, Block> EXCAVATION_BUCKETS = com.rekindled.embers.util.RegCtx.block(BLOCKS, "excavation_buckets", () -> new ExcavationBucketsBlock(Properties.of().mapColor(MapColor.TERRACOTTA_PURPLE).sound(EmbersSounds.SOLID_METAL).requiresCorrectToolForDrops().strength(1.6f).forceSolidOn()));

	//items
	public static final DeferredHolder<Item, Item> TINKER_HAMMER = com.rekindled.embers.util.RegCtx.item(ITEMS, "tinker_hammer", () -> new TinkerHammerItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> TINKER_LENS = com.rekindled.embers.util.RegCtx.item(ITEMS, "tinker_lens", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> SMOKY_TINKER_LENS = com.rekindled.embers.util.RegCtx.item(ITEMS, "smoky_tinker_lens", () -> new Item(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ANCIENT_CODEX = com.rekindled.embers.util.RegCtx.item(ITEMS, "ancient_codex", () -> new AncientCodexItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ATMOSPHERIC_GAUGE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "atmospheric_gauge", () -> new BlockItem(ATMOSPHERIC_GAUGE.get(), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_JAR = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_jar", () -> new EmberJarItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> EMBER_CARTRIDGE = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_cartridge", () -> new EmberCartridgeItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> CLOCKWORK_PICKAXE = com.rekindled.embers.util.RegCtx.item(ITEMS, "clockwork_pickaxe", () -> new ClockworkPickaxeItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> CLOCKWORK_AXE = com.rekindled.embers.util.RegCtx.item(ITEMS, "clockwork_axe", () -> new ClockworkAxeItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> GRANDHAMMER = com.rekindled.embers.util.RegCtx.item(ITEMS, "grandhammer", () -> new ClockworkHammerItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> BLAZING_RAY = com.rekindled.embers.util.RegCtx.item(ITEMS, "blazing_ray", () -> new BlazingRayItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> CINDER_STAFF = com.rekindled.embers.util.RegCtx.item(ITEMS, "cinder_staff", () -> new CinderStaffItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ALCHEMICAL_WASTE = com.rekindled.embers.util.RegCtx.item(ITEMS, "alchemical_waste", () -> new AlchemyHintItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ALCHEMICAL_NOTE = com.rekindled.embers.util.RegCtx.item(ITEMS, "alchemical_note", () -> new AlchemicalNoteItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> CODEBREAKING_SLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "codebreaking_slate", () -> new CodebreakingSlateItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> TYRFING = com.rekindled.embers.util.RegCtx.item(ITEMS, "tyrfing", () -> new TyrfingItem(new Item.Properties().sword(EmbersTiers.TYRFING, 3, -2.4F)));
	public static final DeferredHolder<Item, Item> INFLICTOR_GEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "inflictor_gem", () -> new InflictorGemItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ASHEN_GOGGLES = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_goggles", () -> new AshenArmorGemItem(AshenArmorMaterial.properties(net.minecraft.world.item.equipment.ArmorType.HELMET), ConfigManager.ASHEN_GOGGLES_SLOTS));
	public static final DeferredHolder<Item, Item> ASHEN_CLOAK = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_cloak", () -> new AshenArmorGemItem(AshenArmorMaterial.properties(net.minecraft.world.item.equipment.ArmorType.CHESTPLATE), ConfigManager.ASHEN_CLOAK_SLOTS));
	public static final DeferredHolder<Item, Item> ASHEN_LEGGINGS = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_leggings", () -> new AshenArmorGemItem(AshenArmorMaterial.properties(net.minecraft.world.item.equipment.ArmorType.LEGGINGS), ConfigManager.ASHEN_LEGGINGS_SLOTS));
	public static final DeferredHolder<Item, Item> ASHEN_BOOTS = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_boots", () -> new AshenArmorGemItem(AshenArmorMaterial.properties(net.minecraft.world.item.equipment.ArmorType.BOOTS), ConfigManager.ASHEN_BOOTS_SLOTS));
	public static final DeferredHolder<Item, Item> GLIMMER_CRYSTAL = com.rekindled.embers.util.RegCtx.item(ITEMS, "glimmer_crystal", () -> new GlimmerCrystalItem(new Item.Properties().durability(800)));
	public static final DeferredHolder<Item, Item> GLIMMER_LAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "glimmer_lamp", () -> new GlimmerLampItem(new Item.Properties().durability(1200)));

	public static final DeferredHolder<Item, Item> COPPER_CELL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "copper_cell", () -> new CopperCellBlockItem(COPPER_CELL.get(), new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> CREATIVE_EMBER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "creative_ember_source", () -> new BlockItem(CREATIVE_EMBER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_DIAL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_dial", () -> new BlockItem(EMBER_DIAL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ITEM_DIAL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_dial", () -> new BlockItem(ITEM_DIAL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FLUID_DIAL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "fluid_dial", () -> new BlockItem(FLUID_DIAL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_EMITTER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_emitter", () -> new BlockItem(EMBER_EMITTER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_RECEIVER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_receiver", () -> new BlockItem(EMBER_RECEIVER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CAMINITE_LEVER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_lever", () -> new BlockItem(CAMINITE_LEVER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CAMINITE_BUTTON_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_button", () -> new BlockItem(CAMINITE_BUTTON.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ITEM_PIPE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_pipe", () -> new BlockItem(ITEM_PIPE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ITEM_EXTRACTOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_extractor", () -> new BlockItem(ITEM_EXTRACTOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_BORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_bore", () -> new BlockItem(EMBER_BORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MECHANICAL_CORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mechanical_core", () -> new BlockItem(MECHANICAL_CORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_ACTIVATOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_activator", () -> new BlockItem(EMBER_ACTIVATOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MELTER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "melter", () -> new BlockItem(MELTER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FLUID_PIPE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "fluid_pipe", () -> new BlockItem(FLUID_PIPE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FLUID_EXTRACTOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "fluid_extractor", () -> new BlockItem(FLUID_EXTRACTOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FLUID_VESSEL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "fluid_vessel", () -> new FluidVesselBlockItem(FLUID_VESSEL.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> STAMPER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "stamper", () -> new BlockItem(STAMPER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> STAMP_BASE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "stamp_base", () -> new BlockItem(STAMP_BASE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> BIN_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "bin", () -> new BlockItem(BIN.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MIXER_CENTRIFUGE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mixer_centrifuge", () -> new BlockItem(MIXER_CENTRIFUGE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ITEM_DROPPER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_dropper", () -> new BlockItem(ITEM_DROPPER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> PRESSURE_REFINERY_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "pressure_refinery", () -> new BlockItem(PRESSURE_REFINERY.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_EJECTOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_ejector", () -> new BlockItem(EMBER_EJECTOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_FUNNEL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_funnel", () -> new BlockItem(EMBER_FUNNEL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_RELAY_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_relay", () -> new BlockItem(EMBER_RELAY.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MIRROR_RELAY_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mirror_relay", () -> new BlockItem(MIRROR_RELAY.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> BEAM_SPLITTER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "beam_splitter", () -> new BlockItem(BEAM_SPLITTER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ITEM_VACUUM_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_vacuum", () -> new BlockItem(ITEM_VACUUM.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> HEARTH_COIL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "hearth_coil", () -> new BlockItem(HEARTH_COIL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> RESERVOIR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "reservoir", () -> new BlockItem(RESERVOIR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CAMINITE_RING_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_ring", () -> new CaminiteRingBlockItem(CAMINITE_RING.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> CAMINITE_GAUGE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_gauge", () -> new CaminiteRingBlockItem(CAMINITE_GAUGE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> CAMINITE_VALVE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_valve", () -> new CaminiteRingBlockItem(CAMINITE_VALVE.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> CRYSTAL_CELL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "crystal_cell", () -> new BlockItem(CRYSTAL_CELL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CLOCKWORK_ATTENUATOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "clockwork_attenuator", () -> new UpgradeItem(CLOCKWORK_ATTENUATOR.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> GEOLOGIC_SEPARATOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "geologic_separator", () -> new UpgradeItem(GEOLOGIC_SEPARATOR.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> COPPER_CHARGER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "copper_charger", () -> new BlockItem(COPPER_CHARGER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> EMBER_SIPHON_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_siphon", () -> new UpgradeItem(EMBER_SIPHON.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> ITEM_TRANSFER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "item_transfer", () -> new BlockItem(ITEM_TRANSFER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FLUID_TRANSFER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "fluid_transfer", () -> new BlockItem(FLUID_TRANSFER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ALCHEMY_PEDESTAL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "alchemy_pedestal", () -> new BlockItem(ALCHEMY_PEDESTAL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ALCHEMY_TABLET_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "alchemy_tablet", () -> new BlockItem(ALCHEMY_TABLET.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> BEAM_CANNON_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "beam_cannon", () -> new BlockItem(BEAM_CANNON.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MECHANICAL_PUMP_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mechanical_pump", () -> new BlockItem(MECHANICAL_PUMP.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MINI_BOILER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mini_boiler", () -> new UpgradeItem(MINI_BOILER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> CATALYTIC_PLUG_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "catalytic_plug", () -> new UpgradeItem(CATALYTIC_PLUG.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> WILDFIRE_STIRLING_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "wildfire_stirling", () -> new UpgradeItem(WILDFIRE_STIRLING.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> EMBER_INJECTOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_injector", () -> new BlockItem(EMBER_INJECTOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> FIELD_CHART_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "field_chart", () -> new BlockItem(FIELD_CHART.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> IGNEM_REACTOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ignem_reactor", () -> new BlockItem(IGNEM_REACTOR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CATALYSIS_CHAMBER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "catalysis_chamber", () -> new BlockItem(CATALYSIS_CHAMBER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> COMBUSTION_CHAMBER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "combustion_chamber", () -> new BlockItem(COMBUSTION_CHAMBER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CINDER_PLINTH_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "cinder_plinth", () -> new BlockItem(CINDER_PLINTH.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> DAWNSTONE_ANVIL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_anvil", () -> new BlockItem(DAWNSTONE_ANVIL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> AUTOMATIC_HAMMER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "automatic_hammer", () -> new BlockItem(AUTOMATIC_HAMMER.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> INFERNO_FORGE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "inferno_forge", () -> new BlockItem(INFERNO_FORGE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MNEMONIC_INSCRIBER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mnemonic_inscriber", () -> new UpgradeItem(MNEMONIC_INSCRIBER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> CHAR_INSTILLER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "char_instiller", () -> new UpgradeItem(CHAR_INSTILLER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> ATMOSPHERIC_BELLOWS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "atmospheric_bellows", () -> new UpgradeItem(ATMOSPHERIC_BELLOWS.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> ENTROPIC_ENUMERATOR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "entropic_enumerator", () -> new UpgradeItem(ENTROPIC_ENUMERATOR.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> HEAT_EXCHANGER_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "heat_exchanger", () -> new UpgradeItem(HEAT_EXCHANGER.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> HEAT_INSULATION_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "heat_insulation", () -> new UpgradeItem(HEAT_INSULATION.get(), new Item.Properties()));
	public static final DeferredHolder<Item, Item> EXCAVATION_BUCKETS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "excavation_buckets", () -> new UpgradeItem(EXCAVATION_BUCKETS.get(), new Item.Properties()));

	public static final DeferredHolder<Item, Item> EMBER_CRYSTAL = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_crystal", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> EMBER_SHARD = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_shard", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> EMBER_GRIT = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_grit", () -> new FuelItem(new Item.Properties(), 1600));
	public static final DeferredHolder<Item, Item> CAMINITE_BLEND = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_blend", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> CAMINITE_BRICK = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_brick", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ARCHAIC_BRICK = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_brick", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ANCIENT_MOTIVE_CORE = com.rekindled.embers.util.RegCtx.item(ITEMS, "ancient_motive_core", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ASH = com.rekindled.embers.util.RegCtx.item(ITEMS, "ash", () -> new FuelItem(new Item.Properties(), 200));
	public static final DeferredHolder<Item, Item> ASHEN_FABRIC = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_fabric", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> EMBER_CRYSTAL_CLUSTER = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_crystal_cluster", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> WILDFIRE_CORE = com.rekindled.embers.util.RegCtx.item(ITEMS, "wildfire_core", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ISOLATED_MATERIA = com.rekindled.embers.util.RegCtx.item(ITEMS, "isolated_materia", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ADHESIVE = com.rekindled.embers.util.RegCtx.item(ITEMS, "adhesive", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ARCHAIC_CIRCUIT = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_circuit", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> SUPERHEATER = com.rekindled.embers.util.RegCtx.item(ITEMS, "superheater", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> CINDER_JET = com.rekindled.embers.util.RegCtx.item(ITEMS, "cinder_jet", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> MUSIC_DISC_7F_PATTERNS = com.rekindled.embers.util.RegCtx.item(ITEMS, "music_disc_7f_patterns", () -> new EmberRecordItem(7, EmbersSounds.ULTRASYD_7F_PATTERNS, new Item.Properties().rarity(Rarity.RARE).stacksTo(1), 4444));
	public static final DeferredHolder<Item, Item> BLASTING_CORE = com.rekindled.embers.util.RegCtx.item(ITEMS, "blasting_core", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> CASTER_ORB = com.rekindled.embers.util.RegCtx.item(ITEMS, "caster_orb", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RESONATING_BELL = com.rekindled.embers.util.RegCtx.item(ITEMS, "resonating_bell", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> FLAME_BARRIER = com.rekindled.embers.util.RegCtx.item(ITEMS, "flame_barrier", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> ELDRITCH_INSIGNIA = com.rekindled.embers.util.RegCtx.item(ITEMS, "eldritch_insignia", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> INTELLIGENT_APPARATUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "intelligent_apparatus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> DIFFRACTION_BARREL = com.rekindled.embers.util.RegCtx.item(ITEMS, "diffraction_barrel", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> FOCAL_LENS = com.rekindled.embers.util.RegCtx.item(ITEMS, "focal_lens", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> SHIFTING_SCALES = com.rekindled.embers.util.RegCtx.item(ITEMS, "shifting_scales", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> WINDING_GEARS = com.rekindled.embers.util.RegCtx.item(ITEMS, "winding_gears", () -> new Item(new Item.Properties()));

	static { COPPER_CRYSTAL_SEED.makeItem(); }
	static { IRON_CRYSTAL_SEED.makeItem(); }
	static { GOLD_CRYSTAL_SEED.makeItem(); }
	static { LEAD_CRYSTAL_SEED.makeItem(); }
	static { SILVER_CRYSTAL_SEED.makeItem(); }
	static { NICKEL_CRYSTAL_SEED.makeItem(); }
	static { TIN_CRYSTAL_SEED.makeItem(); }
	static { ALUMINUM_CRYSTAL_SEED.makeItem(); }
	static { ZINC_CRYSTAL_SEED.makeItem(); }
	static { PLATINUM_CRYSTAL_SEED.makeItem(); }
	static { URANIUM_CRYSTAL_SEED.makeItem(); }
	static { DAWNSTONE_CRYSTAL_SEED.makeItem(); }
	static { MITHRIL_CRYSTAL_SEED.makeItem(); }

	public static final DeferredHolder<Item, Item> RAW_CAMINITE_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_caminite_plate", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RAW_FLAT_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_flat_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RAW_INGOT_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_ingot_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RAW_NUGGET_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_nugget_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RAW_PLATE_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_plate_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> RAW_GEAR_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_gear_stamp", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> CAMINITE_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_plate", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> FLAT_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "flat_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> INGOT_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "ingot_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> NUGGET_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "nugget_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> PLATE_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "plate_stamp", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> GEAR_STAMP = com.rekindled.embers.util.RegCtx.item(ITEMS, "gear_stamp", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> IRON_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "iron_aspectus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> COPPER_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "copper_aspectus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> LEAD_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_aspectus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> SILVER_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_aspectus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> DAWNSTONE_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_aspectus", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> MITHRIL_ASPECTUS = com.rekindled.embers.util.RegCtx.item(ITEMS, "mithril_aspectus", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> IRON_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "iron_plate", () -> new Item(new Item.Properties()));
	//public static final DeferredHolder<Item, Item> GOLD_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "gold_plate", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> COPPER_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "copper_plate", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> COPPER_NUGGET = com.rekindled.embers.util.RegCtx.item(ITEMS, "copper_nugget", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> RAW_LEAD = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_lead", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> LEAD_INGOT = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_ingot", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> LEAD_NUGGET = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_nugget", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> LEAD_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_plate", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> LEAD_ORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_ore", () -> new BlockItem(LEAD_ORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> DEEPSLATE_LEAD_ORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "deepslate_lead_ore", () -> new BlockItem(DEEPSLATE_LEAD_ORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> RAW_LEAD_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_lead_block", () -> new BlockItem(RAW_LEAD_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> LEAD_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "lead_block", () -> new BlockItem(LEAD_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));

	public static final DeferredHolder<Item, Item> RAW_SILVER = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_silver", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> SILVER_INGOT = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_ingot", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> SILVER_NUGGET = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_nugget", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> SILVER_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_plate", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> SILVER_ORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_ore", () -> new BlockItem(SILVER_ORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> DEEPSLATE_SILVER_ORE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "deepslate_silver_ore", () -> new BlockItem(DEEPSLATE_SILVER_ORE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> RAW_SILVER_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_silver_block", () -> new BlockItem(RAW_SILVER_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> SILVER_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "silver_block", () -> new BlockItem(SILVER_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));

	public static final DeferredHolder<Item, Item> DAWNSTONE_INGOT = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_ingot", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> DAWNSTONE_NUGGET = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_nugget", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> DAWNSTONE_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_plate", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> MITHRIL_INGOT = com.rekindled.embers.util.RegCtx.item(ITEMS, "mithril_ingot", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> MITHRIL_NUGGET = com.rekindled.embers.util.RegCtx.item(ITEMS, "mithril_nugget", () -> new Item(new Item.Properties()));
	public static final DeferredHolder<Item, Item> MITHRIL_PLATE = com.rekindled.embers.util.RegCtx.item(ITEMS, "mithril_plate", () -> new Item(new Item.Properties()));

	public static final DeferredHolder<Item, Item> DAWNSTONE_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "dawnstone_block", () -> new BlockItem(DAWNSTONE_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> MITHRIL_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "mithril_block", () -> new BlockItem(MITHRIL_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));

	public static final ToolSet LEAD_TOOLS = new ToolSet("lead", EmbersTiers.LEAD);
	public static final ToolSet SILVER_TOOLS = new ToolSet("silver", EmbersTiers.SILVER);
	public static final ToolSet DAWNSTONE_TOOLS = new ToolSet("dawnstone", EmbersTiers.DAWNSTONE);

	public static final DeferredHolder<Item, Item> CAMINITE_BRICKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_bricks", () -> new BlockItem(CAMINITE_BRICKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { CAMINITE_BRICKS_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> CAMINITE_LARGE_BRICKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_large_bricks", () -> new BlockItem(CAMINITE_LARGE_BRICKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { CAMINITE_LARGE_BRICKS_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> RAW_CAMINITE_BLOCK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "raw_caminite_block", () -> new BlockItem(RAW_CAMINITE_BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> CAMINITE_LARGE_TILE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_large_tile", () -> new BlockItem(CAMINITE_LARGE_TILE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { CAMINITE_LARGE_TILE_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> CAMINITE_TILES_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "caminite_tiles", () -> new BlockItem(CAMINITE_TILES.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { CAMINITE_TILES_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ARCHAIC_BRICKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_bricks", () -> new BlockItem(ARCHAIC_BRICKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ARCHAIC_BRICKS_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ARCHAIC_EDGE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_edge", () -> new BlockItem(ARCHAIC_EDGE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ARCHAIC_TILE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_tile", () -> new BlockItem(ARCHAIC_TILE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ARCHAIC_TILE_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ARCHAIC_LARGE_BRICKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_large_bricks", () -> new BlockItem(ARCHAIC_LARGE_BRICKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ARCHAIC_LARGE_BRICKS_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ARCHAIC_LIGHT_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "archaic_light", () -> new BlockItem(ARCHAIC_LIGHT.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> ASHEN_STONE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_stone", () -> new BlockItem(ASHEN_STONE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ASHEN_STONE_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ASHEN_BRICK_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_brick", () -> new BlockItem(ASHEN_BRICK.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ASHEN_BRICK_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> ASHEN_TILE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ashen_tile", () -> new BlockItem(ASHEN_TILE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { ASHEN_TILE_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> SEALED_PLANKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "sealed_planks", () -> new BlockItem(SEALED_PLANKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { SEALED_PLANKS_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> REINFORCED_SEALED_PLANKS_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "reinforced_sealed_planks", () -> new BlockItem(REINFORCED_SEALED_PLANKS.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> SEALED_WOOD_TILE_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "sealed_wood_tile", () -> new BlockItem(SEALED_WOOD_TILE.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { SEALED_WOOD_TILE_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> SEALED_WOOD_PILLAR_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "sealed_wood_pillar", () -> new BlockItem(SEALED_WOOD_PILLAR.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> SEALED_WOOD_KEG_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "sealed_wood_keg", () -> new BlockItem(SEALED_WOOD_KEG.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> SOLIDIFIED_METAL_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "solidified_metal", () -> new BlockItem(SOLIDIFIED_METAL.get(), new Item.Properties().useBlockDescriptionPrefix()));
	public static final DeferredHolder<Item, Item> METAL_PLATFORM_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "metal_platform", () -> new BlockItem(METAL_PLATFORM.get(), new Item.Properties().useBlockDescriptionPrefix()));
	static { METAL_PLATFORM_DECO.makeItems(); }
	public static final DeferredHolder<Item, Item> EMBER_LANTERN_ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, "ember_lantern", () -> new BlockItem(EMBER_LANTERN.get(), new Item.Properties().useBlockDescriptionPrefix()));

	//fluids
	public static final FluidStuff MOLTEN_IRON = addFluid("Molten Iron", new FluidInfo("molten_iron", 0xC72913, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_GOLD = addFluid("Molten Gold", new FluidInfo("molten_gold", 0xF9C026, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_COPPER = addFluid("Molten Copper", new FluidInfo("molten_copper", 0xEA7E38, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_LEAD = addFluid("Molten Lead", new FluidInfo("molten_lead", 0x665975, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_SILVER = addFluid("Molten Silver", new FluidInfo("molten_silver", 0xBCEAF7, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_DAWNSTONE = addFluid("Molten Dawnstone", new FluidInfo("molten_dawnstone", 0xFF9C36, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_MITHRIL = addFluid("Molten Mithril", new FluidInfo("molten_mithril", 0xC8EBA2, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_NICKEL = addFluid("Molten Nickel", new FluidInfo("molten_nickel", 0xDDEBC0, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_TIN = addFluid("Molten Tin", new FluidInfo("molten_tin", 0xDCEDE5, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_ALUMINUM = addFluid("Molten Aluminum", new FluidInfo("molten_aluminum", 0xFFAE9C, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_ZINC = addFluid("Molten Zinc", new FluidInfo("molten_zinc", 0x8C8D7B, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_PLATINUM = addFluid("Molten Platinum", new FluidInfo("molten_platinum", 0xA6DEE8, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_URANIUM = addFluid("Molten Uranium", new FluidInfo("molten_uranium", 0x314630, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_BRONZE = addFluid("Molten Bronze", new FluidInfo("molten_bronze", 0xEDAE66, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_ELECTRUM = addFluid("Molten Electrum", new FluidInfo("molten_electrum", 0xFAE176, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_BRASS = addFluid("Molten Brass", new FluidInfo("molten_brass", 0xD3A756, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_CONSTANTAN = addFluid("Molten Constantan", new FluidInfo("molten_constantan", 0xB55C46, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff MOLTEN_INVAR = addFluid("Molten Invar", new FluidInfo("molten_invar", 0xB1D0D1, 0.1F, 1.5F), MoltenMetalFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2), moltenMetalProps());

	public static final FluidStuff STEAM = addFluid("Steam", new FluidInfo("steam", 0xFFFCFC, 0.1F, 1.5F), SteamFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(3),
			FluidType.Properties.create()
			.canSwim(false)
			.canDrown(false)
			.pathType(PathType.LAVA)
			.adjacentPathType(null)
			.motionScale(0.0005D)
			.canPushEntity(false)
			.canHydrate(true)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
			.density(-1000)
			.viscosity(100)
			.temperature(400));

	public static final FluidStuff SOUL_CRUDE = addFluid("Soul Crude", new FluidInfo("soul_crude", 0x35261D, 0.1F, 1.5F), ViscousFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(30).slopeFindDistance(2).levelDecreasePerBlock(2),
			FluidType.Properties.create()
			.canSwim(false)
			.canDrown(true)
			.motionScale(0.0023333333333333335D)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
			.density(3000)
			.viscosity(4000)
			.temperature(330));

	public static final FluidStuff DWARVEN_OIL = addFluid("Dwarven Oil", new FluidInfo("dwarven_oil", 0xffDB18, 0.1F, 1.5F), ViscousFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(8).slopeFindDistance(2).levelDecreasePerBlock(2),
			FluidType.Properties.create()
			.canSwim(false)
			.canDrown(true)
			.motionScale(0.0023333333333333335D)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
			.density(2000)
			.viscosity(2000)
			.temperature(330));

	public static final FluidStuff DWARVEN_GAS = addFluid("Dwarven Gas", new FluidInfo("dwarven_gas", 0x99DC4D, 0.1F, 1.5F), SteamFluidType::new, (supplier, props) -> new net.minecraft.world.level.block.LiquidBlock(supplier.get(), props),
			prop -> prop.explosionResistance(1000F).tickRate(3).slopeFindDistance(2).levelDecreasePerBlock(2),
			FluidType.Properties.create()
			.canSwim(false)
			.canDrown(false)
			.pathType(PathType.LAVA)
			.adjacentPathType(null)
			.motionScale(0.0005D)
			.canPushEntity(false)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
			.density(-100)
			.viscosity(100)
			.temperature(400));


	// ===== Block Entity Type helper =====
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <T extends net.minecraft.world.level.block.entity.BlockEntity>
		DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> rBE(
			String id,
			BlockEntityType.BlockEntitySupplier<T> factory,
			java.util.function.Supplier<? extends Block> block) {
		java.util.function.Supplier sup = () -> new BlockEntityType<>(factory, block.get());
		Object holder = BLOCK_ENTITY_TYPES_NEW.register(id, sup);
		return (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>) holder;
	}
	// ===== End helper =====

	//block entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperCellBlockEntity>> COPPER_CELL_ENTITY = rBE("copper_cell", CopperCellBlockEntity::new, COPPER_CELL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEmberBlockEntity>> CREATIVE_EMBER_ENTITY = rBE("creative_ember_source", CreativeEmberBlockEntity::new, CREATIVE_EMBER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberDialBlockEntity>> EMBER_DIAL_ENTITY = rBE("ember_dial", EmberDialBlockEntity::new, EMBER_DIAL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDialBlockEntity>> ITEM_DIAL_ENTITY = rBE("item_dial", ItemDialBlockEntity::new, ITEM_DIAL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidDialBlockEntity>> FLUID_DIAL_ENTITY = rBE("fluid_dial", FluidDialBlockEntity::new, FLUID_DIAL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtmosphericGaugeBlockEntity>> ATMOSPHERIC_GAUGE_ENTITY = rBE("atmospheric_gauge", AtmosphericGaugeBlockEntity::new, ATMOSPHERIC_GAUGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberEmitterBlockEntity>> EMBER_EMITTER_ENTITY = rBE("ember_emitter", EmberEmitterBlockEntity::new, EMBER_EMITTER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberReceiverBlockEntity>> EMBER_RECEIVER_ENTITY = rBE("ember_receiver", EmberReceiverBlockEntity::new, EMBER_RECEIVER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemPipeBlockEntity>> ITEM_PIPE_ENTITY = rBE("item_pipe", ItemPipeBlockEntity::new, ITEM_PIPE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemExtractorBlockEntity>> ITEM_EXTRACTOR_ENTITY = rBE("item_extractor", ItemExtractorBlockEntity::new, ITEM_EXTRACTOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberBoreBlockEntity>> EMBER_BORE_ENTITY = rBE("ember_bore", EmberBoreBlockEntity::new, EMBER_BORE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MechanicalCoreBlockEntity>> MECHANICAL_CORE_ENTITY = rBE("mechanical_core", MechanicalCoreBlockEntity::new, MECHANICAL_CORE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberActivatorBottomBlockEntity>> EMBER_ACTIVATOR_BOTTOM_ENTITY = rBE("ember_activator_bottom", EmberActivatorBottomBlockEntity::new, EMBER_ACTIVATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberActivatorTopBlockEntity>> EMBER_ACTIVATOR_TOP_ENTITY = rBE("ember_activator_top", EmberActivatorTopBlockEntity::new, EMBER_ACTIVATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MelterBottomBlockEntity>> MELTER_BOTTOM_ENTITY = rBE("melter_bottom", MelterBottomBlockEntity::new, MELTER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MelterTopBlockEntity>> MELTER_TOP_ENTITY = rBE("melter_top", MelterTopBlockEntity::new, MELTER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidPipeBlockEntity>> FLUID_PIPE_ENTITY = rBE("fluid_pipe", FluidPipeBlockEntity::new, FLUID_PIPE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidExtractorBlockEntity>> FLUID_EXTRACTOR_ENTITY = rBE("fluid_extractor", FluidExtractorBlockEntity::new, FLUID_EXTRACTOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidVesselBlockEntity>> FLUID_VESSEL_ENTITY = rBE("fluid_vesel", FluidVesselBlockEntity::new, FLUID_VESSEL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StamperBlockEntity>> STAMPER_ENTITY = rBE("stamper", StamperBlockEntity::new, STAMPER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StampBaseBlockEntity>> STAMP_BASE_ENTITY = rBE("stamp_base", StampBaseBlockEntity::new, STAMP_BASE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BinBlockEntity>> BIN_ENTITY = rBE("bin", BinBlockEntity::new, BIN);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MixerCentrifugeBottomBlockEntity>> MIXER_CENTRIFUGE_BOTTOM_ENTITY = rBE("mixer_centrifuge_bottom", MixerCentrifugeBottomBlockEntity::new, MIXER_CENTRIFUGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MixerCentrifugeTopBlockEntity>> MIXER_CENTRIFUGE_TOP_ENTITY = rBE("mixer_centrifuge_top", MixerCentrifugeTopBlockEntity::new, MIXER_CENTRIFUGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemDropperBlockEntity>> ITEM_DROPPER_ENTITY = rBE("item_dropper", ItemDropperBlockEntity::new, ITEM_DROPPER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PressureRefineryBottomBlockEntity>> PRESSURE_REFINERY_BOTTOM_ENTITY = rBE("pressure_refinery_bottom", PressureRefineryBottomBlockEntity::new, PRESSURE_REFINERY);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PressureRefineryTopBlockEntity>> PRESSURE_REFINERY_TOP_ENTITY = rBE("pressure_refinery_top", PressureRefineryTopBlockEntity::new, PRESSURE_REFINERY);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberEjectorBlockEntity>> EMBER_EJECTOR_ENTITY = rBE("ember_ejector", EmberEjectorBlockEntity::new, EMBER_EJECTOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberFunnelBlockEntity>> EMBER_FUNNEL_ENTITY = rBE("ember_funnel", EmberFunnelBlockEntity::new, EMBER_FUNNEL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberRelayBlockEntity>> EMBER_RELAY_ENTITY = rBE("ember_relay", EmberRelayBlockEntity::new, EMBER_RELAY);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MirrorRelayBlockEntity>> MIRROR_RELAY_ENTITY = rBE("mirror_relay", MirrorRelayBlockEntity::new, MIRROR_RELAY);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeamSplitterBlockEntity>> BEAM_SPLITTER_ENTITY = rBE("beam_splitter", BeamSplitterBlockEntity::new, BEAM_SPLITTER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemVacuumBlockEntity>> ITEM_VACUUM_ENTITY = rBE("item_vacuum", ItemVacuumBlockEntity::new, ITEM_VACUUM);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HearthCoilBlockEntity>> HEARTH_COIL_ENTITY = rBE("hearth_coil", HearthCoilBlockEntity::new, HEARTH_COIL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReservoirBlockEntity>> RESERVOIR_ENTITY = rBE("reservoir", ReservoirBlockEntity::new, RESERVOIR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CaminiteValveBlockEntity>> CAMINITE_VALVE_ENTITY = rBE("caminite_valve", CaminiteValveBlockEntity::new, CAMINITE_VALVE_EDGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalCellBlockEntity>> CRYSTAL_CELL_ENTITY = rBE("crystal_cell", CrystalCellBlockEntity::new, CRYSTAL_CELL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ClockworkAttenuatorBlockEntity>> CLOCKWORK_ATTENUATOR_ENTITY = rBE("clockwork_attenuator", ClockworkAttenuatorBlockEntity::new, CLOCKWORK_ATTENUATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeologicSeparatorBlockEntity>> GEOLOGIC_SEPARATOR_ENTITY = rBE("geologic_separator", GeologicSeparatorBlockEntity::new, GEOLOGIC_SEPARATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CopperChargerBlockEntity>> COPPER_CHARGER_ENTITY = rBE("copper_charger", CopperChargerBlockEntity::new, COPPER_CHARGER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberSiphonBlockEntity>> EMBER_SIPHON_ENTITY = rBE("ember_siphon", EmberSiphonBlockEntity::new, EMBER_SIPHON);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ItemTransferBlockEntity>> ITEM_TRANSFER_ENTITY = rBE("item_transfer", ItemTransferBlockEntity::new, ITEM_TRANSFER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTransferBlockEntity>> FLUID_TRANSFER_ENTITY = rBE("fluid_transfer", FluidTransferBlockEntity::new, FLUID_TRANSFER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyPedestalBlockEntity>> ALCHEMY_PEDESTAL_ENTITY = rBE("alchemy_pedestal", AlchemyPedestalBlockEntity::new, ALCHEMY_PEDESTAL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyPedestalTopBlockEntity>> ALCHEMY_PEDESTAL_TOP_ENTITY = rBE("alchemy_pedestal_top", AlchemyPedestalTopBlockEntity::new, ALCHEMY_PEDESTAL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTabletBlockEntity>> ALCHEMY_TABLET_ENTITY = rBE("alchemy_tablet", AlchemyTabletBlockEntity::new, ALCHEMY_TABLET);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeamCannonBlockEntity>> BEAM_CANNON_ENTITY = rBE("beam_cannon", BeamCannonBlockEntity::new, BEAM_CANNON);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MechanicalPumpBottomBlockEntity>> MECHANICAL_PUMP_BOTTOM_ENTITY = rBE("mechanical_pump_bottom", MechanicalPumpBottomBlockEntity::new, MECHANICAL_PUMP);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MechanicalPumpTopBlockEntity>> MECHANICAL_PUMP_TOP_ENTITY = rBE("mechanical_pump_top", MechanicalPumpTopBlockEntity::new, MECHANICAL_PUMP);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MiniBoilerBlockEntity>> MINI_BOILER_ENTITY = rBE("mini_boiler", MiniBoilerBlockEntity::new, MINI_BOILER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CatalyticPlugBlockEntity>> CATALYTIC_PLUG_ENTITY = rBE("catalytic_plug", CatalyticPlugBlockEntity::new, CATALYTIC_PLUG);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WildfireStirlingBlockEntity>> WILDFIRE_STIRLING_ENTITY = rBE("wildfire_stirling", WildfireStirlingBlockEntity::new, WILDFIRE_STIRLING);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmberInjectorBlockEntity>> EMBER_INJECTOR_ENTITY = rBE("ember_injector", EmberInjectorBlockEntity::new, EMBER_INJECTOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FieldChartBlockEntity>> FIELD_CHART_ENTITY = rBE("field_chart", FieldChartBlockEntity::new, FIELD_CHART);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IgnemReactorBlockEntity>> IGNEM_REACTOR_ENTITY = rBE("ignem_reactor", IgnemReactorBlockEntity::new, IGNEM_REACTOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CatalysisChamberBlockEntity>> CATALYSIS_CHAMBER_ENTITY = rBE("catalysis_chamber", CatalysisChamberBlockEntity::new, CATALYSIS_CHAMBER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombustionChamberBlockEntity>> COMBUSTION_CHAMBER_ENTITY = rBE("combustion_chamber", CombustionChamberBlockEntity::new, COMBUSTION_CHAMBER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CinderPlinthBlockEntity>> CINDER_PLINTH_ENTITY = rBE("cinder_plinth", CinderPlinthBlockEntity::new, CINDER_PLINTH);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DawnstoneAnvilBlockEntity>> DAWNSTONE_ANVIL_ENTITY = rBE("dawnstone_anvil", DawnstoneAnvilBlockEntity::new, DAWNSTONE_ANVIL);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutomaticHammerBlockEntity>> AUTOMATIC_HAMMER_ENTITY = rBE("automatic_hammer", AutomaticHammerBlockEntity::new, AUTOMATIC_HAMMER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfernoForgeBottomBlockEntity>> INFERNO_FORGE_BOTTOM_ENTITY = rBE("inferno_forge_bottom", InfernoForgeBottomBlockEntity::new, INFERNO_FORGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InfernoForgeTopBlockEntity>> INFERNO_FORGE_TOP_ENTITY = rBE("inferno_forge_top", InfernoForgeTopBlockEntity::new, INFERNO_FORGE);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MnemonicInscriberBlockEntity>> MNEMONIC_INSCRIBER_ENTITY = rBE("mnemonic_inscriber", MnemonicInscriberBlockEntity::new, MNEMONIC_INSCRIBER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CharInstillerBlockEntity>> CHAR_INSTILLER_ENTITY = rBE("char_instiller", CharInstillerBlockEntity::new, CHAR_INSTILLER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AtmosphericBellowsBlockEntity>> ATMOSPHERIC_BELLOWS_ENTITY = rBE("atmospheric_bellows", AtmosphericBellowsBlockEntity::new, ATMOSPHERIC_BELLOWS);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EntropicEnumeratorBlockEntity>> ENTROPIC_ENUMERATOR_ENTITY = rBE("entropic_enumerator", EntropicEnumeratorBlockEntity::new, ENTROPIC_ENUMERATOR);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MithrilBlockEntity>> MITHRIL_BLOCK_ENTITY = rBE("mithril_block", MithrilBlockEntity::new, MITHRIL_BLOCK);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatExchangerBlockEntity>> HEAT_EXCHANGER_ENTITY = rBE("heat_exchanger", HeatExchangerBlockEntity::new, HEAT_EXCHANGER);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatInsulationBlockEntity>> HEAT_INSULATION_ENTITY = rBE("heat_insulation", HeatInsulationBlockEntity::new, HEAT_INSULATION);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExcavationBucketsBlockEntity>> EXCAVATION_BUCKETS_ENTITY = rBE("excavation_buckets", ExcavationBucketsBlockEntity::new, EXCAVATION_BUCKETS);

	//creative tabs
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EMBERS_TAB = CREATIVE_TABS.register("main_tab", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup." + Embers.MODID))
			.icon(() -> new ItemStack(EMBER_CRYSTAL.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.displayItems((params, output) -> {
				for (DeferredHolder<Item, ? extends Item> item : ITEMS.getEntries()) {
					if (item == MITHRIL_BLOCK_ITEM || item == MITHRIL_CRYSTAL_SEED.ITEM || item == MITHRIL_ASPECTUS || item == MITHRIL_INGOT || item == MITHRIL_NUGGET || item == MITHRIL_PLATE || item.get() == MOLTEN_MITHRIL.FLUID_BUCKET.get())
						continue;
					output.accept(item.get());
					if (item == COPPER_CELL_ITEM)
						output.accept(CopperCellBlockItem.getCharged());
					if (item.get() instanceof EmberStorageItem)
						output.accept(EmberStorageItem.withFill(item.get(), ((EmberStorageItem) item.get()).getCapacity()));
				}
			})
			.build());

	//entities
	public static final DeferredHolder<EntityType<?>, EntityType<EmberPacketEntity>> EMBER_PACKET = registerEntity("ember_packet", EntityType.Builder.<EmberPacketEntity>of(EmberPacketEntity::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).fireImmune().clientTrackingRange(3).updateInterval(1));
	public static final DeferredHolder<EntityType<?>, EntityType<EmberProjectileEntity>> EMBER_PROJECTILE = registerEntity("ember_projectile", EntityType.Builder.<EmberProjectileEntity>of(EmberProjectileEntity::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).fireImmune().clientTrackingRange(3).updateInterval(1));
	public static final DeferredHolder<EntityType<?>, EntityType<GlimmerProjectileEntity>> GLIMMER_PROJECTILE = registerEntity("glimmer_projectile", EntityType.Builder.<GlimmerProjectileEntity>of(GlimmerProjectileEntity::new, MobCategory.MISC).noLootTable().sized(0.5F, 0.5F).fireImmune().clientTrackingRange(3).updateInterval(1));
	public static final DeferredHolder<EntityType<?>, EntityType<AncientGolemEntity>> ANCIENT_GOLEM = registerEntity("ancient_golem", EntityType.Builder.<AncientGolemEntity>of(AncientGolemEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).fireImmune().clientTrackingRange(8));

	//spawn eggs
	public static final DeferredHolder<Item, Item> ANCIENT_GOLEM_SPAWN_EGG = com.rekindled.embers.util.RegCtx.item(ITEMS, "ancient_golem_spawn_egg", () -> new net.minecraft.world.item.SpawnEggItem(new Item.Properties().spawnEgg(ANCIENT_GOLEM.get())));

	//augments
	public static final IAugment CORE_AUGMENT = AugmentUtil.registerAugment(new CoreAugment());
	public static final IAugment TINKER_LENS_AUGMENT = AugmentUtil.registerAugment(new TinkerLensAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "tinker_lens"), false));
	public static final IAugment SMOKY_LENS_AUGMENT = AugmentUtil.registerAugment(new TinkerLensAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "smoky_tinker_lens"), true));
	public static final IAugment SUPERHEATER_AUGMENT = AugmentUtil.registerAugment(new SuperheaterAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "superheater")));
	public static final IAugment CINDER_JET_AUGMENT = AugmentUtil.registerAugment(new CinderJetAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "cinder_jet")));
	public static final IAugment BLASTING_CORE_AUGMENT = AugmentUtil.registerAugment(new BlastingCoreAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "blasting_core")));
	public static final IAugment CASTER_ORB_AUGMENT = AugmentUtil.registerAugment(new CasterOrbAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "caster_orb")));
	public static final IAugment RESONATING_BELL_AUGMENT = AugmentUtil.registerAugment(new ResonatingBellAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "resonating_bell")));
	public static final IAugment FLAME_BARRIER_AUGMENT = AugmentUtil.registerAugment(new FlameBarrierAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "flame_barrier")));
	public static final IAugment ELDRITCH_INSIGNIA_AUGMENT = AugmentUtil.registerAugment(new EldritchInsigniaAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "eldritch_insignia")));
	public static final IAugment INTELLIGENT_APPARATUS_AUGMENT = AugmentUtil.registerAugment(new IntelligentApparatusAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "intelligent_apparatus")));
	public static final IAugment DIFFRACTION_BARREL_AUGMENT = AugmentUtil.registerAugment(new DiffractionBarrelAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "diffraction_barrel")));
	public static final IAugment FOCAL_LENS_AUGMENT = AugmentUtil.registerAugment(new FocalLensAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "focal_lens")));
	public static final IAugment SHIFTING_SCALES_AUGMENT = AugmentUtil.registerAugment(new ShiftingScalesAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "shifting_scales")));
	public static final IAugment WINDING_GEARS_AUGMENT = AugmentUtil.registerAugment(new WindingGearsAugment(Identifier.fromNamespaceAndPath(Embers.MODID, "winding_gears")));

	//particle types
	public static final DeferredHolder<ParticleType<GlowParticleOptions>, ParticleType<GlowParticleOptions>> GLOW_PARTICLE = registerParticle("glow", false, GlowParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(GlowParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<StarParticleOptions>, ParticleType<StarParticleOptions>> STAR_PARTICLE = registerParticle("star", false, StarParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(StarParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<SparkParticleOptions>, ParticleType<SparkParticleOptions>> SPARK_PARTICLE = registerParticle("spark", false, SparkParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(SparkParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<SmokeParticleOptions>, ParticleType<SmokeParticleOptions>> SMOKE_PARTICLE = registerParticle("smoke", false, SmokeParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(SmokeParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<VaporParticleOptions>, ParticleType<VaporParticleOptions>> VAPOR_PARTICLE = registerParticle("vapor", false, VaporParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(VaporParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<AlchemyCircleParticleOptions>, ParticleType<AlchemyCircleParticleOptions>> ALCHEMY_CIRCLE_PARTICLE = registerParticle("alchemy_circle", false, AlchemyCircleParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(AlchemyCircleParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<TyrfingParticleOptions>, ParticleType<TyrfingParticleOptions>> TYRFING_PARTICLE = registerParticle("tyrfing", false, TyrfingParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(TyrfingParticleOptions.CODEC));
	public static final DeferredHolder<ParticleType<XRayGlowParticleOptions>, ParticleType<XRayGlowParticleOptions>> XRAY_GLOW_PARTICLE = registerParticle("xray_glow", false, XRayGlowParticleOptions.CODEC, net.minecraft.network.codec.ByteBufCodecs.fromCodec(XRayGlowParticleOptions.CODEC));

	//recipe types
	public static final DeferredHolder<RecipeType<IBoringRecipe>, RecipeType<IBoringRecipe>> BORING = registerRecipeType("boring");
	public static final DeferredHolder<RecipeType<IBoringRecipe>, RecipeType<IBoringRecipe>> EXCAVATION = registerRecipeType("excavation");
	public static final DeferredHolder<RecipeType<IEmberActivationRecipe>, RecipeType<IEmberActivationRecipe>> EMBER_ACTIVATION = registerRecipeType("ember_activation");
	public static final DeferredHolder<RecipeType<IMeltingRecipe>, RecipeType<IMeltingRecipe>> MELTING = registerRecipeType("melting");
	public static final DeferredHolder<RecipeType<IStampingRecipe>, RecipeType<IStampingRecipe>> STAMPING = registerRecipeType("stamping");
	public static final DeferredHolder<RecipeType<IMixingRecipe>, RecipeType<IMixingRecipe>> MIXING = registerRecipeType("mixing");
	public static final DeferredHolder<RecipeType<IMetalCoefficientRecipe>, RecipeType<IMetalCoefficientRecipe>> METAL_COEFFICIENT = registerRecipeType("metal_coefficient");
	public static final DeferredHolder<RecipeType<IAlchemyRecipe>, RecipeType<IAlchemyRecipe>> ALCHEMY = registerRecipeType("alchemy");
	public static final DeferredHolder<RecipeType<IBoilingRecipe>, RecipeType<IBoilingRecipe>> BOILING = registerRecipeType("boiling");
	public static final DeferredHolder<RecipeType<IGaseousFuelRecipe>, RecipeType<IGaseousFuelRecipe>> GASEOUS_FUEL = registerRecipeType("gaseous_fuel");
	public static final DeferredHolder<RecipeType<ICatalysisCombustionRecipe>, RecipeType<ICatalysisCombustionRecipe>> CATALYSIS_COMBUSTION = registerRecipeType("catalysis_combustion");
	public static final DeferredHolder<RecipeType<IDawnstoneAnvilRecipe>, RecipeType<IDawnstoneAnvilRecipe>> DAWNSTONE_ANVIL_RECIPE = registerRecipeType("dawnstone_anvil");

	//recipe serializers
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BoringRecipe>> BORING_SERIALIZER = RECIPE_SERIALIZERS.register("boring", () -> BoringRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ExcavationRecipe>> EXCAVATION_SERIALIZER = RECIPE_SERIALIZERS.register("excavation", () -> ExcavationRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EmberActivationRecipe>> EMBER_ACTIVATION_SERIALIZER = RECIPE_SERIALIZERS.register("ember_activation", () -> EmberActivationRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MeltingRecipe>> MELTING_SERIALIZER = RECIPE_SERIALIZERS.register("melting", () -> MeltingRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StampingRecipe>> STAMPING_SERIALIZER = RECIPE_SERIALIZERS.register("stamping", () -> StampingRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MixingRecipe>> MIXING_SERIALIZER = RECIPE_SERIALIZERS.register("mixing", () -> MixingRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MetalCoefficientRecipe>> METAL_COEFFICIENT_SERIALIZER = RECIPE_SERIALIZERS.register("metal_coefficient", () -> MetalCoefficientRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyRecipe>> ALCHEMY_SERIALIZER = RECIPE_SERIALIZERS.register("alchemy", () -> AlchemyRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemyRecipeForBabies>> ALCHEMY_FOR_BABIES_SERIALIZER = RECIPE_SERIALIZERS.register("alchemy_for_babies", () -> AlchemyRecipeForBabies.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BoilingRecipe>> BOILING_SERIALIZER = RECIPE_SERIALIZERS.register("boiling", () -> BoilingRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GaseousFuelRecipe>> GASEOUS_FUEL_SERIALIZER = RECIPE_SERIALIZERS.register("gaseous_fuel", () -> GaseousFuelRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CatalysisCombustionRecipe>> CATALYSIS_COMBUSTION_SERIALIZER = RECIPE_SERIALIZERS.register("catalysis_combustion", () -> CatalysisCombustionRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GemSocketRecipe>> GEM_SOCKET_SERIALIZER = RECIPE_SERIALIZERS.register("gem_socket", () -> GemSocketRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GemUnsocketRecipe>> GEM_UNSOCKET_SERIALIZER = RECIPE_SERIALIZERS.register("gem_unsocket", () -> GemUnsocketRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilRepairRecipe>> TOOL_REPAIR = RECIPE_SERIALIZERS.register("tool_repair", () -> AnvilRepairRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilRepairMateriaRecipe>> MATERIA_REPAIR = RECIPE_SERIALIZERS.register("tool_materia_repair", () -> AnvilRepairMateriaRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilBreakdownRecipe>> TOOL_BREAKDOWN = RECIPE_SERIALIZERS.register("tool_breakdown", () -> AnvilBreakdownRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilAugmentRecipe>> TOOL_AUGMENT = RECIPE_SERIALIZERS.register("tool_augment", () -> AnvilAugmentRecipe.SERIALIZER);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnvilAugmentRemoveRecipe>> TOOL_AUGMENT_REMOVE = RECIPE_SERIALIZERS.register("tool_augment_remove", () -> AnvilAugmentRemoveRecipe.SERIALIZER);

	//loot modifiers
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<GrandhammerLootModifier>> GRANDHAMMER_MODIFIER = LOOT_MODIFIERS.register("grandhammer", () -> GrandhammerLootModifier.CODEC);
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SuperHeaterLootModifier>> SUPERHEATER_MODIFIER = LOOT_MODIFIERS.register("superheater", () -> SuperHeaterLootModifier.CODEC);
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AshenAmuletLootModifier>> ASHENAMULET_MODIFIER = LOOT_MODIFIERS.register("ashenamulet", () -> AshenAmuletLootModifier.CODEC);

	//menu types
	public static final DeferredHolder<MenuType<?>, MenuType<SlateMenu>> SLATE_MENU = MENU_TYPES.register("codebreaking_slate", () -> new net.minecraft.world.inventory.MenuType<>(new net.neoforged.neoforge.network.IContainerFactory<SlateMenu>() {
		@Override
		public SlateMenu create(int containerId, net.minecraft.world.entity.player.Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
			return SlateMenu.fromBuffer(containerId, inv, buf);
		}
	}, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));

	//structure types
	public static final DeferredHolder<StructureType<?>, StructureType<CaveStructure>> CAVE_STRUCTURE = STRUCTURE_TYPES.<StructureType<CaveStructure>>register("cave_structure", () -> new StructureType<CaveStructure>() { @Override public com.mojang.serialization.MapCodec<CaveStructure> codec() { return CaveStructure.CODEC; } });
	public static final DeferredHolder<net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType, net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType> CAVE_STRUCTURE_PIECE = STRUCTURE_PIECE_TYPES.register("cave_structure_piece", () -> com.rekindled.embers.worldgen.CaveStructurePiece::new);

	//structure processor types
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<CrystalSeedStructureProcessor>> CRYSTAL_SEED_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.<StructureProcessorType<CrystalSeedStructureProcessor>>register("crystal_seed_processor", () -> new StructureProcessorType<CrystalSeedStructureProcessor>() { @Override public com.mojang.serialization.MapCodec<CrystalSeedStructureProcessor> codec() { return CrystalSeedStructureProcessor.CODEC; } });
	public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<EntityMobilizerStructureProcessor>> ENTITY_MOBILIZER_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.<StructureProcessorType<EntityMobilizerStructureProcessor>>register("entity_mobilizer", () -> new StructureProcessorType<EntityMobilizerStructureProcessor>() { @Override public com.mojang.serialization.MapCodec<EntityMobilizerStructureProcessor> codec() { return EntityMobilizerStructureProcessor.CODEC; } });

	public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			net.minecraft.core.dispenser.DispenseItemBehavior dispenseBucket = new net.minecraft.core.dispenser.DefaultDispenseItemBehavior() {
				private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

				@Override
				public ItemStack execute(net.minecraft.core.dispenser.BlockSource source, ItemStack stack) {
					DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
					BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
					Level level = source.level();
					if (container.emptyContents(null, level, blockpos, null)) {
						container.checkExtraContent(null, level, stack, blockpos);
						return new ItemStack(Items.BUCKET);
					} else {
						return this.defaultDispenseItemBehavior.dispense(source, stack);
					}
				}
			};
			for (FluidStuff fluid : fluidList) {
				DispenserBlock.registerBehavior(fluid.FLUID_BUCKET.get(), dispenseBucket);
			}

			EmbersAPI.registerEmberResonance(Ingredient.of(DAWNSTONE_TOOLS.SWORD.get(), DAWNSTONE_TOOLS.SHOVEL.get(), DAWNSTONE_TOOLS.PICKAXE.get(), DAWNSTONE_TOOLS.AXE.get(), DAWNSTONE_TOOLS.HOE.get()), 2.0);
			EmbersAPI.registerEmberResonance(Ingredient.of(CLOCKWORK_PICKAXE.get(), CLOCKWORK_AXE.get(), GRANDHAMMER.get(), BLAZING_RAY.get(), CINDER_STAFF.get()), 2.0);
			EmbersAPI.registerEmberResonance(Ingredient.of(ASHEN_GOGGLES.get(), ASHEN_CLOAK.get(), ASHEN_LEGGINGS.get(), ASHEN_BOOTS.get()), 2.0);

			moltenMetalFluidInteractions(MOLTEN_IRON.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_GOLD.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_COPPER.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_LEAD.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_SILVER.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_DAWNSTONE.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_MITHRIL.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_TIN.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_ALUMINUM.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_ZINC.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_PLATINUM.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_URANIUM.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_BRONZE.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_ELECTRUM.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_BRASS.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_CONSTANTAN.TYPE.get());
			moltenMetalFluidInteractions(MOLTEN_INVAR.TYPE.get());

			EmbersAPI.registerLens(Ingredient.of(TINKER_LENS.get()));
			EmbersAPI.registerWearableLens(Ingredient.of(ASHEN_GOGGLES.get()));
		});
	}

	public static void moltenMetalFluidInteractions(FluidType source) {
		FluidInteractionRegistry.addInteraction(source, new InteractionInformation(
				(level, currentPos, relativePos, currentState) -> level.getFluidState(relativePos).is(EmbersFluidTags.WATERY),
				SOLIDIFIED_METAL.get().defaultBlockState()));
	}

	public static class FluidStuff {

		public final BaseFlowingFluid.Properties PROPERTIES;

		public final DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Source> FLUID;
		public final DeferredHolder<net.minecraft.world.level.material.Fluid, BaseFlowingFluid.Flowing> FLUID_FLOW;
		public final DeferredHolder<FluidType, FluidType> TYPE;

		public final DeferredHolder<Block, LiquidBlock> FLUID_BLOCK;

		public final DeferredHolder<Item, BucketItem> FLUID_BUCKET;

		public final String name;
		public final String localizedName;
		public final int color;

		public FluidStuff(String name, String localizedName, int color, FluidType type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<BaseFlowingFluid.Properties> fluidProperties, Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Source> source, Function<BaseFlowingFluid.Properties, BaseFlowingFluid.Flowing> flowing) {
			this.name = name;
			this.localizedName = localizedName;
			this.color = color;

			FLUID = FLUIDS.register(name, () -> source.apply(getFluidProperties()));
			FLUID_FLOW = FLUIDS.register("flowing_" + name, () -> flowing.apply(getFluidProperties()));
			TYPE = FLUIDTYPES.register(name, () -> type);

			PROPERTIES = new BaseFlowingFluid.Properties(TYPE, FLUID, FLUID_FLOW);
			if (fluidProperties != null)
				fluidProperties.accept(PROPERTIES);

			FLUID_BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, name + "_block", () -> block.apply(FLUID, Block.Properties.of().liquid().pushReaction(PushReaction.DESTROY).lightLevel((state) -> { return type.getLightLevel(); }).randomTicks().replaceable().strength(100.0F).noLootTable()));
			FLUID_BUCKET = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_bucket", () -> new BucketItem(FLUID.get(), new BucketItem.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

			PROPERTIES.bucket(FLUID_BUCKET).block(FLUID_BLOCK);
		}

		public BaseFlowingFluid.Properties getFluidProperties() {
			return PROPERTIES;
		}
	}

	public static class StoneDecoBlocks {

		public String name;

		public DeferredHolder<Block, Block> block;
		public DeferredHolder<Block, StairBlock> stairs = null;
		public DeferredHolder<Item, Item> stairsItem = null;
		public DeferredHolder<Block, SlabBlock> slab = null;
		public DeferredHolder<Item, Item> slabItem = null;
		public DeferredHolder<Block, WallBlock> wall = null;
		public DeferredHolder<Item, Item> wallItem = null;

		public StoneDecoBlocks(String name, DeferredHolder<Block, Block> block, java.util.function.Supplier<Properties> properties, boolean stairs, boolean slab, boolean wall) {
			this.name = name;
			this.block = block;
			if (stairs) {
				this.stairs = com.rekindled.embers.util.RegCtx.block(BLOCKS, name + "_stairs", () -> new StairBlock(block.get().defaultBlockState(), properties.get()));
			}
			if (slab) {
				this.slab = com.rekindled.embers.util.RegCtx.block(BLOCKS, name + "_slab", () -> new SlabBlock(properties.get()));
			}
			if (wall) {
				this.wall = com.rekindled.embers.util.RegCtx.block(BLOCKS, name + "_wall", () -> new WallBlock(properties.get()));
			}
		}

		public void makeItems() {
			if (stairs != null) {
				this.stairsItem = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_stairs", () -> new BlockItem(this.stairs.get(), new Item.Properties().useBlockDescriptionPrefix()));
			}
			if (slab != null) {
				this.slabItem = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_slab", () -> new BlockItem(this.slab.get(), new Item.Properties().useBlockDescriptionPrefix()));
			}
			if (wall != null) {
				this.wallItem = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_wall", () -> new BlockItem(this.wall.get(), new Item.Properties().useBlockDescriptionPrefix()));
			}
		}

		public StoneDecoBlocks(String name, DeferredHolder<Block, Block> block, java.util.function.Supplier<Properties> properties) {
			this(name, block, properties, true, true, true);
		}
	}

	public static class ToolSet {

		public String name;

		public final DeferredHolder<Item, Item> SWORD;
		public final DeferredHolder<Item, Item> SHOVEL;
		public final DeferredHolder<Item, Item> PICKAXE;
		public final DeferredHolder<Item, Item> AXE;
		public final DeferredHolder<Item, Item> HOE;

		public ToolSet(String name, net.minecraft.world.item.ToolMaterial tier) {
			this.name = name;

			SWORD = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_sword", () -> new Item(new Item.Properties().sword(tier, 3, -2.4F)));
			SHOVEL = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_shovel", () -> new ShovelItem(tier, 1.5F, -3.0F, new Item.Properties()));
			PICKAXE = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_pickaxe", () -> new Item(new Item.Properties().pickaxe(tier, 1.0F, -2.8F)));
			AXE = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_axe", () -> new AxeItem(tier, 6.0F, -3.0F, new Item.Properties()));
			HOE = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_hoe", () -> new HoeItem(tier, -tier.attackDamageBonus(), Math.min(0.0F, tier.attackDamageBonus() - 3.0F), new Item.Properties()));
		}
	}

	public static class MetalCrystalSeed {

		public static HashMap<String, MetalCrystalSeed> seeds = new HashMap<String, MetalCrystalSeed>();

		public String name;

		public final DeferredHolder<Block, Block> BLOCK;
		public DeferredHolder<Item, Item> ITEM;
		public final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystalSeedBlockEntity>> BLOCKENTITY;

		public MetalCrystalSeed(String type) {
			this.name = type;

			BLOCK = com.rekindled.embers.util.RegCtx.block(BLOCKS, type + "_crystal_seed", () -> new CrystalSeedBlock(Properties.of().mapColor(MapColor.NONE).sound(SoundType.AMETHYST).requiresCorrectToolForDrops().strength(1.6f).noOcclusion().forceSolidOn(), type));
			BLOCKENTITY = BLOCK_ENTITY_TYPES.register(type + "_crystal_seed", () -> new BlockEntityType<>((pos, state) -> new CrystalSeedBlockEntity(pos, state, type), BLOCK.get()));
			seeds.put(type, this);
		}

		public void makeItem() {
			ITEM = com.rekindled.embers.util.RegCtx.item(ITEMS, name + "_crystal_seed", () -> new BlockItem(BLOCK.get(), new Item.Properties().useBlockDescriptionPrefix()));
		}
	}
}
