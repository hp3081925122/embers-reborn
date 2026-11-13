package com.rekindled.embers.compat.curios;

import java.util.Map;
import java.util.function.Predicate;
import java.util.Optional;
import net.neoforged.neoforge.registries.DeferredHolder;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.block.ExplosionPedestalBlock;
import com.rekindled.embers.blockentity.ExplosionPedestalBlockEntity;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.item.DawnstoneMailItem;
import com.rekindled.embers.item.EmberBulbItem;
import com.rekindled.embers.item.EmberDiscountBaubleItem;
import com.rekindled.embers.item.EmberStorageItem;
import com.rekindled.embers.item.ExplosionCharmItem;
import com.rekindled.embers.item.GenericCurioItemItem;
import com.rekindled.embers.item.NonbeleiverAmuletItem;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.subtypes.ResearchShowItem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class CuriosCompat {

	public static final DeferredHolder<Block, Block> EXPLOSION_PEDESTAL = com.rekindled.embers.util.RegCtx.block(RegistryManager.BLOCKS, "explosion_pedestal", () -> new ExplosionPedestalBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(EmbersSounds.CAMINITE).requiresCorrectToolForDrops().strength(1.6f).noOcclusion(), EmbersSounds.MULTIBLOCK_EXTRA));

	public static final DeferredHolder<Item, Item> EMBER_RING = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "ember_ring", () -> new EmberDiscountBaubleItem(new Item.Properties().stacksTo(1), 0.15));
	public static final DeferredHolder<Item, Item> EMBER_BELT = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "ember_belt", () -> new EmberDiscountBaubleItem(new Item.Properties().stacksTo(1), 0.25));
	public static final DeferredHolder<Item, Item> EMBER_AMULET = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "ember_amulet", () -> new EmberDiscountBaubleItem(new Item.Properties().stacksTo(1), 0.2));
	public static final DeferredHolder<Item, Item> EMBER_BULB = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "ember_bulb", () -> new EmberBulbItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> DAWNSTONE_MAIL = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "dawnstone_mail", () -> new DawnstoneMailItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> ASHEN_AMULET = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "ashen_amulet", () -> new GenericCurioItemItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> NONBELEIVER_AMULET = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "nonbeliever_amulet", () -> new NonbeleiverAmuletItem(new Item.Properties().stacksTo(1)));
	public static final DeferredHolder<Item, Item> EXPLOSION_CHARM = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "explosion_charm", () -> new ExplosionCharmItem(new Item.Properties().stacksTo(1)));

	public static final DeferredHolder<Item, Item> EXPLOSION_PEDESTAL_ITEM = com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, "explosion_pedestal", () -> new BlockItem(EXPLOSION_PEDESTAL.get(), new Item.Properties()));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExplosionPedestalBlockEntity>> EXPLOSION_PEDESTAL_ENTITY = RegistryManager.BLOCK_ENTITY_TYPES.register("explosion_pedestal", () -> new BlockEntityType<>(ExplosionPedestalBlockEntity::new, EXPLOSION_PEDESTAL.get()));

	public static void init() {}

	@OnlyIn(Dist.CLIENT)
	public static boolean checkForCurios(LivingEntity living, Predicate<ItemStack> predicate) {
		Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(living);
		if (inv.isPresent()) {
			Map<String, ICurioStacksHandler> curios = inv.orElseThrow().getCurios();
			for (ICurioStacksHandler curio : curios.values()) {
				for (int i = 0; i < curio.getStacks().getSlots(); i++) {
					if (predicate.test(curio.getStacks().getStackInSlot(i))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static double getEmberCapacityTotal(LivingEntity living) {
		double amount = 0;
		Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(living);
		if (inv.isPresent()) {
			Map<String, ICurioStacksHandler> curios = inv.orElseThrow().getCurios();
			for (ICurioStacksHandler curio : curios.values()) {
				for (int i = 0; i < curio.getStacks().getSlots(); i++) {
					IEmberCapability capability = EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(curio.getStacks().getStackInSlot(i), null);
					if (capability != null) {
						amount += capability.getEmberCapacity();
					}
				}
			}
		}
		return amount;
	}

	public static double getEmberTotal(LivingEntity living) {
		double amount = 0;
		Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(living);
		if (inv.isPresent()) {
			Map<String, ICurioStacksHandler> curios = inv.orElseThrow().getCurios();
			for (ICurioStacksHandler curio : curios.values()) {
				for (int i = 0; i < curio.getStacks().getSlots(); i++) {
					IEmberCapability capability = EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(curio.getStacks().getStackInSlot(i), null);
					if (capability != null) {
						amount += capability.getEmber();
					}
				}
			}
		}
		return amount;
	}

	public static double removeEmber(LivingEntity living, double amount) {
		Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(living);
		if (inv.isPresent()) {
			Map<String, ICurioStacksHandler> curios = inv.orElseThrow().getCurios();
			for (ICurioStacksHandler curio : curios.values()) {
				for (int i = 0; i < curio.getStacks().getSlots(); i++) {
					IEmberCapability capability = EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(curio.getStacks().getStackInSlot(i), null);
					if (capability != null) {
						amount -= capability.removeAmount(amount, true);
						if (amount <= 0)
							return amount;
					}
				}
			}
		}
		return amount;
	}

	public static void initCuriosCategory() {
		ItemStack fullBulb = EmberStorageItem.withFill(EMBER_BULB.get(), ((EmberBulbItem)EMBER_BULB.get()).getCapacity());

		ResearchManager.cost_reduction = new ResearchShowItem(loc("cost_reduction"), new ItemStack(EMBER_AMULET.get()), 5, 5).addItem(new ResearchShowItem.DisplayItem(new ItemStack(EMBER_AMULET.get()), new ItemStack(EMBER_BELT.get()), new ItemStack(EMBER_RING.get()))).setLookupIngredient(Ingredient.of(EMBER_AMULET.get(), EMBER_BELT.get(), EMBER_RING.get()));
		ResearchManager.mantle_bulb = new ResearchBase(loc("mantle_bulb"), fullBulb, 7, 3);
		ResearchManager.dawnstone_mail = new ResearchBase(loc("dawnstone_mail"), new ItemStack(DAWNSTONE_MAIL.get()), 3, 7);
		ResearchManager.ashen_amulet = new ResearchBase(loc("ashen_amulet"), new ItemStack(ASHEN_AMULET.get()), 4, 3);
		ResearchManager.nonbeliever_amulet = new ResearchBase(loc("nonbeliever_amulet"), new ItemStack(NONBELEIVER_AMULET.get()), 1, 3);
		ResearchManager.explosion_charm = new ResearchBase(loc("explosion_charm"), new ItemStack(EXPLOSION_CHARM.get()), 9, 2);
		ResearchManager.explosion_pedestal = new ResearchBase(loc("explosion_pedestal"), new ItemStack(EXPLOSION_PEDESTAL_ITEM.get()), 11, 1).addAncestor(ResearchManager.explosion_charm);

		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.cost_reduction);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.mantle_bulb);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.dawnstone_mail);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.ashen_amulet);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.nonbeliever_amulet);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.explosion_charm);
		ResearchManager.subCategoryBaubles.addResearch(ResearchManager.explosion_pedestal);
	}

	static Identifier loc(String name) {
		return Identifier.fromNamespaceAndPath(Embers.MODID, name);
	}
}
