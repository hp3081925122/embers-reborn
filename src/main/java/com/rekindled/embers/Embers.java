package com.rekindled.embers;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.apiimpl.EmbersAPIImpl;
import com.rekindled.embers.augment.ShiftingScalesAugment;
import com.rekindled.embers.augment.ShiftingScalesAugment.IScalesCapability;
import com.rekindled.embers.augment.WindingGearsAugment;
import com.rekindled.embers.blockentity.render.AlchemyPedestalBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AlchemyPedestalTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AlchemyTabletBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AtmosphericBellowsBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.AutomaticHammerBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.BinBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CatalyticPlugBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CinderPlinthBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CopperChargerBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CrystalCellBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.CrystalSeedBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.DawnstoneAnvilBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.EmberBoreBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.EntropicEnumeratorBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ExcavationBucketsBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FieldChartBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FluidTransferBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.FluidVesselBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.GeologicSeparatorBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.InfernoForgeTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ItemTransferBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MechanicalPumpBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MelterTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MithrilBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeBottomBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MixerCentrifugeTopBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.MnemonicInscriberBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.ReservoirBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StampBaseBlockEntityRenderer;
import com.rekindled.embers.blockentity.render.StamperBlockEntityRenderer;
import com.rekindled.embers.compat.curios.CuriosCompat;
import com.rekindled.embers.datagen.EmbersBiomeModifiers;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersConfiguredFeatures;
import com.rekindled.embers.datagen.EmbersDamageTypeTags;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersLang;
import com.rekindled.embers.datagen.EmbersLootModifiers;
import com.rekindled.embers.datagen.EmbersLootTables;
import com.rekindled.embers.datagen.EmbersPlacedFeatures;
import com.rekindled.embers.datagen.EmbersRecipes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.datagen.EmbersStructures;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.entity.render.AncientGolemRenderer;
import com.rekindled.embers.entity.render.EmberPacketRenderer;
import com.rekindled.embers.entity.render.EmberProjectileRenderer;
import com.rekindled.embers.entity.render.GlimmerProjectileRenderer;
import com.rekindled.embers.gui.SlateScreen;
import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.item.TyrfingItem;
import com.rekindled.embers.model.AncientGolemModel;
import com.rekindled.embers.model.AshenArmorModel;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.particle.AlchemyCircleParticle;
import com.rekindled.embers.particle.GlowParticle;
import com.rekindled.embers.particle.SmokeParticle;
import com.rekindled.embers.particle.SparkParticle;
import com.rekindled.embers.particle.StarParticle;
import com.rekindled.embers.particle.TyrfingParticle;
import com.rekindled.embers.particle.VaporParticle;
import com.rekindled.embers.particle.XRayGlowParticle;
import com.rekindled.embers.recipe.AugmentIngredient;
import com.rekindled.embers.recipe.HeatIngredient;
import com.rekindled.embers.render.EmbersRenderTypes;
import com.rekindled.embers.render.PipeModel;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.capability.IResearchCapability;
import com.rekindled.embers.util.AugmentPredicate;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.GlowingTextTooltip;
import com.rekindled.embers.util.GlowingTextTooltip.GlowingTextClientTooltip;
import com.rekindled.embers.util.HeatBarTooltip;
import com.rekindled.embers.util.HeatBarTooltip.HeatBarClientTooltip;
import com.rekindled.embers.util.MatchCurioLootCondition;
import com.rekindled.embers.util.Misc;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Embers.MODID)
public class Embers {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public static final String MODID_OLD = "embers"; // legacy (pre-port) ID
	public static final String MODID = "embers_reborn"; // ported ID

	public static final Logger LOGGER = LogUtils.getLogger();

	public Embers(IEventBus modEventBus) {


		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::gatherData);
		modEventBus.addListener(this::registerCaps);
		modEventBus.addListener(this::entityAttributes);
		modEventBus.addListener(this::spawnPlacements);
		modEventBus.addListener(this::registerRecipeSerializers);
		modEventBus.addListener(com.rekindled.embers.network.PacketHandler::register);

		EmbersAPIImpl.init();
		com.rekindled.embers.util.RegCtx.block(RegistryManager.BLOCKS, modEventBus);
		com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, modEventBus);
		RegistryManager.FLUIDTYPES.register(modEventBus);
		RegistryManager.FLUIDS.register(modEventBus);
		RegistryManager.ENTITY_TYPES.register(modEventBus);
		RegistryManager.BLOCK_ENTITY_TYPES_NEW.register(modEventBus);
		RegistryManager.BLOCK_ENTITY_TYPES_OLD.register(modEventBus);
		RegistryManager.CREATIVE_TABS.register(modEventBus);
		RegistryManager.PARTICLE_TYPES.register(modEventBus);
		RegistryManager.SOUND_EVENTS.register(modEventBus);
		RegistryManager.RECIPE_TYPES.register(modEventBus);
		RegistryManager.RECIPE_SERIALIZERS.register(modEventBus);
		RegistryManager.LOOT_MODIFIERS.register(modEventBus);
		RegistryManager.INGREDIENT_TYPES.register(modEventBus);
		RegistryManager.ATTACHMENT_TYPES.register(modEventBus);
		RegistryManager.DATA_COMPONENTS.register(modEventBus);
		RegistryManager.MENU_TYPES.register(modEventBus);
		RegistryManager.STRUCTURE_TYPES.register(modEventBus);
		RegistryManager.STRUCTURE_PROCESSOR_TYPES.register(modEventBus);
		EmbersSounds.init();

		ConfigManager.register();

		if (ModList.get().isLoaded("curios"))
			CuriosCompat.init();
	}

	public void commonSetup(final FMLCommonSetupEvent event) {
		RegistryManager.init(event);
				NeoForge.EVENT_BUS.addListener(ResearchManager::onClone);
		ResearchManager.initResearches();
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onJoin);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, EmbersEvents::onEntityDamaged);
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onBlockBreak);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, EmbersEvents::onProjectileFired);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOW, EmbersEvents::onArrowLoose);
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onAnvilUpdate);
		NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TagsUpdatedEvent.class, e -> Misc.tagItems.clear());
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onLevelLoad);
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onServerTick);
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onExplosion);
		NeoForge.EVENT_BUS.addListener(EmbersEvents::onTagsReload);
	}

	public void registerCaps(RegisterCapabilitiesEvent event) {
		// Item / fluid handlers (ported from the 1.20.1 getCapability overrides)
		com.rekindled.embers.blockentity.BlockEntityCapabilities.register(event);
		// Fluid Vessel item keeps its fluid (1.20.1 initCapabilities -> FluidHandlerItemStack)
		event.registerItem(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM,
			(stack, ctx) -> new net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack(RegistryManager.FLUID_CONTENT, stack, ConfigManager.FLUID_VESSEL_CAPACITY.get()),
			RegistryManager.FLUID_VESSEL_ITEM.get());

		// --- Item ember capabilities (Batch4) ---
		// EmberJar (IInventoryEmberCell + IHeldEmberCell)
		event.registerItem(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY,
			(stack, ctx) -> new com.rekindled.embers.item.EmberJarItem.EmberJarCapability(stack, com.rekindled.embers.item.EmberJarItem.CAPACITY),
			RegistryManager.EMBER_JAR.get());
		// EmberCartridge (IHeldEmberCell)
		event.registerItem(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY,
			(stack, ctx) -> new com.rekindled.embers.item.EmberCartridgeItem.EmberCartridgeCapability(stack, com.rekindled.embers.item.EmberCartridgeItem.CAPACITY),
			RegistryManager.EMBER_CARTRIDGE.get());
		// EmberBulb (IInventoryEmberCell + IHeldEmberCell via EmberStorageItem) — registered in CuriosCompat when loaded
		if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
			event.registerItem(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY,
				(stack, ctx) -> new com.rekindled.embers.power.DefaultEmberItemCapability(stack, com.rekindled.embers.item.EmberBulbItem.CAPACITY),
				com.rekindled.embers.compat.curios.CuriosCompat.EMBER_BULB.get());
		}
		// CopperCell BlockItem
		event.registerItem(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY,
			(stack, ctx) -> new com.rekindled.embers.power.DefaultEmberItemCapability(stack, 24000.0),
			RegistryManager.COPPER_CELL_ITEM.get());

		// --- BlockEntity ember capabilities (Batch4 BE) ---
		// NeoForge 1.21.1: registerBlockEntity(cap, beType, provider)
		// provider: (be, side) -> T  — returns null to deny
		// EMBER_CAPABILITY is sided (Direction context)

		// All-sides ember (no direction filter)
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.COPPER_CELL_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.CREATIVE_EMBER_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.COPPER_CHARGER_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.EMBER_ACTIVATOR_TOP_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.CINDER_PLINTH_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.EMBER_INJECTOR_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.EMBER_RECEIVER_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.AUTOMATIC_HAMMER_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.IGNEM_REACTOR_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.STAMPER_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.MELTER_BOTTOM_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.PRESSURE_REFINERY_TOP_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), (be, side) -> be.capability);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.INFERNO_FORGE_BOTTOM_ENTITY.get(), (be, side) -> be.capability);

		// DOWN-only ember (CrystalCell, HearthCoil)
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.CRYSTAL_CELL_ENTITY.get(),
			(be, side) -> (side == null || side == net.minecraft.core.Direction.DOWN) ? be.capability : null);
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.HEARTH_COIL_ENTITY.get(),
			(be, side) -> (side == null || side == net.minecraft.core.Direction.DOWN) ? be.capability : null);

		// Directional ember — not from FACING direction (EmberEmitter, BeamCannon)
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.EMBER_EMITTER_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return be.capability;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) != side ? be.capability : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.BEAM_CANNON_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return be.capability;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) != side ? be.capability : null;
			});

		// EmberSiphon: ember on horizontal sides (delegates up), upgrade on UP
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY,
			RegistryManager.EMBER_SIPHON_ENTITY.get(), (be, side) -> {
				if (side != null && side.getAxis() == net.minecraft.core.Direction.Axis.Y) return null;
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.entity.BlockEntity above = be.getLevel().getBlockEntity(be.getBlockPos().above());
				if (above == null) return null;
				return be.getLevel().getCapability(com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_CAPABILITY, be.getBlockPos().above(), net.minecraft.core.Direction.DOWN);
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.EMBER_SIPHON_ENTITY.get(),
			(be, side) -> (side == null || side == net.minecraft.core.Direction.UP) ? be.upgrade : null);

		// UPGRADE_PROVIDER_CAPABILITY — facing-side only
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.ATMOSPHERIC_BELLOWS_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) return null;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) == side ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.CHAR_INSTILLER_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) return null;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) == side ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.HEAT_EXCHANGER_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) return null;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) == side ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.HEAT_INSULATION_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return null;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite() == side ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.EXCAVATION_BUCKETS_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return null;
				return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite() == side ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) return null;
				return (side == null || state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) == side) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.CLOCKWORK_ATTENUATOR_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return null;
				return (side == null || state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite() == side) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.ENTROPIC_ENUMERATOR_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) return null;
				return (side == null || state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING).getOpposite() == side) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.CATALYTIC_PLUG_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				net.minecraft.core.Direction facing = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)
					? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) : null;
				return (facing == null || side == null || side.getOpposite() == facing) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				net.minecraft.core.Direction facing = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)
					? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) : null;
				return (facing == null || side == null || side.getOpposite() == facing) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.WILDFIRE_STIRLING_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				net.minecraft.core.Direction facing = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)
					? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING) : null;
				return (facing == null || side == null || side.getOpposite() == facing) ? be.upgrade : null;
			});
		event.registerBlockEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY,
			RegistryManager.MINI_BOILER_ENTITY.get(), (be, side) -> {
				if (be.getLevel() == null) return null;
				net.minecraft.world.level.block.state.BlockState state = be.getLevel().getBlockState(be.getBlockPos());
				net.minecraft.core.Direction facing = state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)
					? state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) : null;
				return facing != null && facing == side ? be.upgrade : null;
			});

		// --- Player entity capabilities (Batch4) ---
		// Backed by data attachments so the data is the same object every query and is saved with the entity
		event.registerEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.RESEARCH_CAPABILITY,
			net.minecraft.world.entity.EntityType.PLAYER,
			(player, ctx) -> player.getData(RegistryManager.RESEARCH_ATTACHMENT));
		// 1.20.1 attached scales to every LivingEntity, not only players
		for (net.minecraft.world.entity.EntityType<?> type : net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE) {
			event.registerEntity(com.rekindled.embers.api.capabilities.EmbersCapabilities.SCALES_CAPABILITY, type,
				(entity, ctx) -> entity instanceof net.minecraft.world.entity.LivingEntity ? entity.getData(RegistryManager.SCALES_ATTACHMENT) : null);
		}
	}

	public void entityAttributes(EntityAttributeCreationEvent event) {
		event.put(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemEntity.createAttributes().build());
	}

	public void spawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(RegistryManager.ANCIENT_GOLEM.get(), net.minecraft.world.entity.SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
	}

	public void gatherData(GatherDataEvent.Client event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		// blockstates, block/item models and client item definitions are static resources since 26.1 (tools/convert_assets_2612.py)
		gen.addProvider(true, new EmbersLang(output));
		gen.addProvider(true, new EmbersSounds(output));
		gen.addProvider(true, new EmbersLootTables(output, lookupProvider));
		gen.addProvider(true, new EmbersRecipes.Runner(output, lookupProvider));
		EmbersBlockTags blockTags = gen.addProvider(true, new EmbersBlockTags(output, lookupProvider));
		gen.addProvider(true, new EmbersItemTags(output, lookupProvider, blockTags.contentsGetter()));
		gen.addProvider(true, new EmbersFluidTags(output, lookupProvider));
		event.createDatapackRegistryObjects(new RegistrySetBuilder()
				.add(Registries.CONFIGURED_FEATURE, EmbersConfiguredFeatures::generate)
				.add(Registries.PLACED_FEATURE, EmbersPlacedFeatures::generate)
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, EmbersBiomeModifiers::generate)
				.add(Registries.DAMAGE_TYPE, EmbersDamageTypes::generate)
				.add(Registries.PROCESSOR_LIST, EmbersStructures::generateProcessors)
				.add(Registries.TEMPLATE_POOL, EmbersStructures::generatePools)
				.add(Registries.STRUCTURE, EmbersStructures::generateStructures)
				.add(Registries.STRUCTURE_SET, EmbersStructures::generateSets),
				Set.of(MODID));
		gen.addProvider(true, new EmbersDamageTypeTags(output, lookupProvider));
		gen.addProvider(true, new EmbersLootModifiers(output, lookupProvider));
	}

	public void registerRecipeSerializers(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {

		}
		if (event.getRegistryKey().equals(Registries.LOOT_CONDITION_TYPE)) {
			event.register(Registries.LOOT_CONDITION_TYPE, Identifier.fromNamespaceAndPath(MODID, "match_curio"), () -> MatchCurioLootCondition.LOOT_CONDITION_TYPE);
		}
	}

	@net.neoforged.fml.common.EventBusSubscriber(modid = Embers.MODID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {

			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onLevelLoad);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onClientTick);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onMovementInput);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onBlockHighlight);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onLevelRender);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onTooltip);
			NeoForge.EVENT_BUS.addListener(EmbersClientEvents::onWorldRender);
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.STEAM.FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.STEAM.FLUID_FLOW.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.DWARVEN_OIL.FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.DWARVEN_OIL.FLUID_FLOW.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.DWARVEN_GAS.FLUID.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RegistryManager.DWARVEN_GAS.FLUID_FLOW.get(), RenderType.translucent());
			event.enqueueWork(() -> ItemProperties.register(RegistryManager.INFLICTOR_GEM.get(), Identifier.fromNamespaceAndPath(Embers.MODID, "charged"), (stack, level, entity, seed) -> {
				return _getOrCreateTag(stack).contains("type") ? 1.0F : 0.0F;
			}));
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
			event.register(RegistryManager.SLATE_MENU.get(), com.rekindled.embers.gui.SlateScreen::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void overlayRegister(RegisterGuiLayersEvent event) {
			event.registerAboveAll(Identifier.parse(Embers.MODID + ":embers_ingame_overlay"), EmbersClientEvents.INGAME_OVERLAY);
			event.registerAboveAll(Identifier.parse(Embers.MODID + ":shifting_scales_particles"), (graphics, deltaTracker) -> {
				net.minecraft.client.Minecraft mc2 = net.minecraft.client.Minecraft.getInstance();
				com.rekindled.embers.augment.ShiftingScalesAugment.renderIngameOverlay(graphics, deltaTracker.getGameTimeDeltaPartialTick(false), mc2.getWindow().getGuiScaledWidth(), mc2.getWindow().getGuiScaledHeight());
			});
			event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.PLAYER_HEALTH, Identifier.parse(Embers.MODID + ":shifting_scales_hearts"), (graphics, deltaTracker) -> {
				net.minecraft.client.Minecraft mc2 = net.minecraft.client.Minecraft.getInstance();
				com.rekindled.embers.augment.ShiftingScalesAugment.renderHeartsOverlay(graphics, deltaTracker.getGameTimeDeltaPartialTick(false), mc2.getWindow().getGuiScaledWidth(), mc2.getWindow().getGuiScaledHeight());
			});
			event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.JUMP_METER, Identifier.parse(Embers.MODID + ":winding_gears_spring_bottom"), (graphics, deltaTracker) -> {
				net.minecraft.client.Minecraft mc2 = net.minecraft.client.Minecraft.getInstance();
				com.rekindled.embers.augment.WindingGearsAugment.renderSpringUnderlay(graphics, deltaTracker.getGameTimeDeltaPartialTick(false), mc2.getWindow().getGuiScaledWidth(), mc2.getWindow().getGuiScaledHeight());
			});
			event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.EXPERIENCE_BAR, Identifier.parse(Embers.MODID + ":winding_gears_spring_top"), (graphics, deltaTracker) -> {
				net.minecraft.client.Minecraft mc2 = net.minecraft.client.Minecraft.getInstance();
				com.rekindled.embers.augment.WindingGearsAugment.renderSpringOverlay(graphics, deltaTracker.getGameTimeDeltaPartialTick(false), mc2.getWindow().getGuiScaledWidth(), mc2.getWindow().getGuiScaledHeight());
			});
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addResourceListener(RegisterClientReloadListenersEvent event) {
			event.registerReloadListener(new DecimalFormats());
			event.registerReloadListener(new EmbersColors());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerRenderPipelines(net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent event) {
			com.rekindled.embers.render.EmbersParticleLayers.registerPipelines(event);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addParticleProvider(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(RegistryManager.GLOW_PARTICLE.get(), GlowParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.STAR_PARTICLE.get(), StarParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.SPARK_PARTICLE.get(), SparkParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.SMOKE_PARTICLE.get(), SmokeParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.VAPOR_PARTICLE.get(), VaporParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.ALCHEMY_CIRCLE_PARTICLE.get(), AlchemyCircleParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.TYRFING_PARTICLE.get(), TyrfingParticle.Provider::new);
			event.registerSpriteSet(RegistryManager.XRAY_GLOW_PARTICLE.get(), XRayGlowParticle.Provider::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void onModelsBaked(net.neoforged.neoforge.client.event.ModelEvent.BakingCompleted event) {
			EmbersClientEvents.afterModelBake(event);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(RegistryManager.EMBER_PACKET.get(), EmberPacketRenderer::new);
			event.registerEntityRenderer(RegistryManager.EMBER_PROJECTILE.get(), EmberProjectileRenderer::new);
			event.registerEntityRenderer(RegistryManager.GLIMMER_PROJECTILE.get(), GlimmerProjectileRenderer::new);
			event.registerEntityRenderer(RegistryManager.ANCIENT_GOLEM.get(), AncientGolemRenderer::new);

			event.registerBlockEntityRenderer(RegistryManager.EMBER_BORE_ENTITY.get(), EmberBoreBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MELTER_TOP_ENTITY.get(), MelterTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FLUID_VESSEL_ENTITY.get(), FluidVesselBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMPER_ENTITY.get(), StamperBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.STAMP_BASE_ENTITY.get(), StampBaseBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.BIN_ENTITY.get(), BinBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), MixerCentrifugeTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.RESERVOIR_ENTITY.get(), ReservoirBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.CRYSTAL_CELL_ENTITY.get(), CrystalCellBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(), GeologicSeparatorBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.COPPER_CHARGER_ENTITY.get(), CopperChargerBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ITEM_TRANSFER_ENTITY.get(), ItemTransferBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FLUID_TRANSFER_ENTITY.get(), FluidTransferBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_PEDESTAL_TOP_ENTITY.get(), AlchemyPedestalTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_PEDESTAL_ENTITY.get(), AlchemyPedestalBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALCHEMY_TABLET_ENTITY.get(), AlchemyTabletBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), MechanicalPumpBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.CATALYTIC_PLUG_ENTITY.get(), CatalyticPlugBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.COPPER_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.IRON_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.GOLD_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.LEAD_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.SILVER_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.NICKEL_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.TIN_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ALUMINUM_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ZINC_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.PLATINUM_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.URANIUM_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.DAWNSTONE_CRYSTAL_SEED.BLOCKENTITY.get(), CrystalSeedBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.FIELD_CHART_ENTITY.get(), FieldChartBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.CINDER_PLINTH_ENTITY.get(), CinderPlinthBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.DAWNSTONE_ANVIL_ENTITY.get(), DawnstoneAnvilBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.AUTOMATIC_HAMMER_ENTITY.get(), AutomaticHammerBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.INFERNO_FORGE_TOP_ENTITY.get(), InfernoForgeTopBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), MnemonicInscriberBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ATMOSPHERIC_BELLOWS_ENTITY.get(), AtmosphericBellowsBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.ENTROPIC_ENUMERATOR_ENTITY.get(), EntropicEnumeratorBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.MITHRIL_BLOCK_ENTITY.get(), MithrilBlockEntityRenderer::new);
			event.registerBlockEntityRenderer(RegistryManager.EXCAVATION_BUCKETS_ENTITY.get(), ExcavationBucketsBlockEntityRenderer::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(AncientGolemRenderer.LAYER_LOCATION, AncientGolemModel::createLayer);
			event.registerLayerDefinition(AshenArmorModel.ASHEN_ARMOR_HEAD, () -> LayerDefinition.create(AshenArmorModel.createHeadMesh(), 64, 64));
			event.registerLayerDefinition(AshenArmorModel.ASHEN_ARMOR_CHEST, () -> LayerDefinition.create(AshenArmorModel.createChestMesh(), 64, 64));
			event.registerLayerDefinition(AshenArmorModel.ASHEN_ARMOR_LEGS, () -> LayerDefinition.create(AshenArmorModel.createLegsMesh(), 64, 64));
			event.registerLayerDefinition(AshenArmorModel.ASHEN_ARMOR_FEET, () -> LayerDefinition.create(AshenArmorModel.createFeetMesh(), 64, 64));
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
			ItemColor emberContainerColor = new EmberStorageItem.ColorHandler();
			if (ModList.get().isLoaded("curios"))
				CuriosCompat.registerColorHandler(event, emberContainerColor);
			event.register(emberContainerColor, RegistryManager.EMBER_JAR.get(), RegistryManager.EMBER_CARTRIDGE.get());
			event.register(new TyrfingItem.ColorHandler(), RegistryManager.TYRFING.get());
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
			event.register(Identifier.parse(Embers.MODID + ":pipe"), PipeModel.Loader.INSTANCE);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
			event.register(GlowingTextTooltip.class, GlowingTextClientTooltip::new);
			event.register(HeatBarTooltip.class, HeatBarClientTooltip::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Identifier.fromNamespaceAndPath(MODID, "position_tex_color_additive"), DefaultVertexFormat.POSITION_TEX_COLOR), shaderInstance -> {
				EmbersRenderTypes.additiveShader = shaderInstance;
			});
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Identifier.fromNamespaceAndPath(MODID, "particle_ember"), DefaultVertexFormat.PARTICLE), shaderInstance -> {
				EmbersRenderTypes.emberParticleShader = shaderInstance;
			});
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Identifier.fromNamespaceAndPath(MODID, "particle_ember_fab"), DefaultVertexFormat.PARTICLE), shaderInstance -> {
				EmbersRenderTypes.emberParticleFabShader = shaderInstance;
			});
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Identifier.fromNamespaceAndPath(MODID, "particle_translucent"), DefaultVertexFormat.PARTICLE), shaderInstance -> {
				EmbersRenderTypes.translucentParticleShader = shaderInstance;
			});
			event.registerShader(new ShaderInstance(event.getResourceProvider(), Identifier.fromNamespaceAndPath(MODID, "rendertype_entity_solid_mithril"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
				EmbersRenderTypes.mithrilShader = shaderInstance;
			});
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void renderTypeRegistry(RegisterNamedRenderTypesEvent event) {
			event.register(Identifier.parse(Embers.MODID + ":mithril"), RenderType.solid(), EmbersRenderTypes.MITHRIL);
		}
	}
}
