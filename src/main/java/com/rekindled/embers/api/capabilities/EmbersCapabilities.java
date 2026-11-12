package com.rekindled.embers.api.capabilities;

import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.augment.ShiftingScalesAugment.IScalesCapability;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

/**
 * Embers Reborn capability definitions (NeoForge 1.21.1).
 *
 * Registration of providers happens in RegisterCapabilitiesEvent
 * (see EmbersEvents.registerCapabilities).
 *
 * Migration note: Forge Capability<T>/CapabilityManager/CapabilityToken removed.
 * BlockCapability.createSided() replaces block/BE capabilities.
 * EntityCapability.createSimple() replaces entity capabilities.
 */
public final class EmbersCapabilities {

    // Block/BlockEntity capabilities (sided — context = Direction)
    public static final BlockCapability<IEmberCapability, Direction> EMBER_CAPABILITY =
            BlockCapability.createSided(Identifier.fromNamespaceAndPath("embers_reborn", "ember"), IEmberCapability.class);

    public static final BlockCapability<IUpgradeProvider, Direction> UPGRADE_PROVIDER_CAPABILITY =
            BlockCapability.createSided(Identifier.fromNamespaceAndPath("embers_reborn", "upgrade_provider"), IUpgradeProvider.class);

    // Entity capabilities (no context — use createVoid)
    public static final EntityCapability<IResearchCapability, Void> RESEARCH_CAPABILITY =
            EntityCapability.createVoid(Identifier.fromNamespaceAndPath("embers_reborn", "research"), IResearchCapability.class);

    public static final EntityCapability<IScalesCapability, Void> SCALES_CAPABILITY =
            EntityCapability.createVoid(Identifier.fromNamespaceAndPath("embers_reborn", "scales"), IScalesCapability.class);

    // Item capabilities (no context — use createVoid)
    public static final ItemCapability<IEmberCapability, Void> EMBER_ITEM_CAPABILITY =
            ItemCapability.createVoid(Identifier.fromNamespaceAndPath("embers_reborn", "ember_item"), IEmberCapability.class);

    private EmbersCapabilities() {}
}