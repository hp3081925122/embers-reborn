package com.rekindled.embers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * EmbersReborn -- 1.21.1 NeoForge main entry (Batch-1 stub)
 *
 * Original mod: Embers Rekindled (c) RCXcrafter, BordListian, Elucent, TechnoMysterio, KomradeSpectre
 * Source: https://github.com/RCXcrafter/EmbersRekindled (MIT License)
 *
 * This class is the minimal skeleton for the first migration batch.
 * Actual registration logic will be migrated from Embers.java in Batches 2-10.
 *
 * See docs/migration.md for progress.
 */
@Mod(EmbersReborn.MODID)
public class EmbersReborn {

    /** New Mod ID -- must match mod_id in gradle.properties */
    public static final String MODID = "embers_reborn";

    /**
     * Legacy ID for save-compatibility (implemented in Batch 3).
     * embers:xxx entries in world saves need remapping to embers_reborn:xxx.
     */
    public static final String MODID_LEGACY = "embers";

    /**
     * NeoForge 1.21.1 requires IEventBus via constructor injection;
     * FMLJavaModLoadingContext.get().getModEventBus() is gone.
     *
     * @param modEventBus injected by FML
     */
    public EmbersReborn(IEventBus modEventBus) {
        // TODO Batch 3: com.rekindled.embers.util.RegCtx.block(RegistryManager.BLOCKS, modEventBus) etc.
        // TODO Batch 3: EmbersAPIImpl.init(), ConfigManager.register(), CuriosCompat.init()
        // TODO Batch 4: Capability / Data Attachment registration
        // TODO Batch 5: PacketHandler.init()
    }
}