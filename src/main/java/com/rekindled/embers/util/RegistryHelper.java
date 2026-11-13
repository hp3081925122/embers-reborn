package com.rekindled.embers.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Registry lookup for ItemStack/FluidStack NBT written outside a provider-carrying method.
 * RegistryAccess.EMPTY cannot encode data-driven components (enchantments, trims, ...), so
 * saving an enchanted item with it throws.
 */
public final class RegistryHelper {
	private RegistryHelper() {}

	public static HolderLookup.Provider registries() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null)
			return server.registryAccess();
		if (FMLEnvironment.dist.isClient())
			return Client.get();
		return RegistryAccess.EMPTY;
	}

	private static final class Client {
		static HolderLookup.Provider get() {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level != null)
				return mc.level.registryAccess();
			if (mc.getConnection() != null)
				return mc.getConnection().registryAccess();
			return RegistryAccess.EMPTY;
		}
	}
}
