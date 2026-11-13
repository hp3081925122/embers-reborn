package com.rekindled.embers.util;

import java.util.Collection;
import java.util.stream.Stream;

import com.rekindled.embers.RegistryManager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

/**
 * 26.1 removed Level.getRecipeManager(). The server reads its RecipeManager; the client only has the
 * recipe types the server asked NeoForge to sync, so every Embers type is sent on datapack sync.
 * Client side lookups are used by the ember bore animation/sound state and JEI.
 */
public final class EmbersRecipes {
	private static RecipeMap clientRecipes = RecipeMap.EMPTY;

	private EmbersRecipes() {}

	public static RecipeMap recipeMap(Level level) {
		if (level instanceof ServerLevel serverLevel)
			return serverLevel.recipeAccess().recipeMap();
		return clientRecipes;
	}

	public static <I extends RecipeInput, T extends Recipe<I>> Stream<RecipeHolder<T>> getRecipesFor(RecipeType<T> type, I input, Level level) {
		return recipeMap(level).getRecipesFor(type, input, level);
	}

	public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> byType(RecipeType<T> type, Level level) {
		return recipeMap(level).byType(type);
	}

	/** Game bus: ask NeoForge to send all Embers recipe types to clients. */
	public static void onDatapackSync(OnDatapackSyncEvent event) {
		event.sendRecipes(RegistryManager.RECIPE_TYPES.getEntries().stream().<RecipeType<?>>map(h -> h.get()).toList());
	}

	/** Client only; kept in a nested class so the dedicated server never links the client event. */
	public static final class Client {
		private Client() {}

		public static void onRecipesReceived(net.neoforged.neoforge.client.event.RecipesReceivedEvent event) {
			clientRecipes = event.getRecipeMap();
		}

		public static void onLogout(net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
			clientRecipes = RecipeMap.EMPTY;
		}
	}
}
