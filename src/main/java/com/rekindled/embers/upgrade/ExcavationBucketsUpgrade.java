package com.rekindled.embers.upgrade;

import java.util.List;
import java.util.stream.Collectors;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.EmberBoreBladeRenderEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;
import com.rekindled.embers.blockentity.ExcavationBucketsBlockEntity;
import com.rekindled.embers.recipe.BoringContext;
import com.rekindled.embers.recipe.IBoringRecipe;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ExcavationBucketsUpgrade extends DefaultUpgradeProvider {

	public ExcavationBucketsUpgrade(BlockEntity tile) {
		super(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "excavation_buckets"), tile);
	}

	@Override
	public int getPriority() {
		return -90;
	}

	@Override
	public int getLimit(BlockEntity tile) {
		if (tile instanceof EmberBoreBlockEntity)
			return Integer.MAX_VALUE;
		return 0;
	}

	@Override
	public boolean doTick(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		if (this.tile instanceof ExcavationBucketsBlockEntity bucketsEntity) {
			bucketsEntity.lastAngle = bucketsEntity.angle;
			if (tile instanceof EmberBoreBlockEntity bore && bore.isRunning)
				bucketsEntity.angle += 14.0f * bore.speedMod;
		}
		return false;
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (tile instanceof EmberBoreBlockEntity bore && event instanceof MachineRecipeEvent<?> recipeEvent && recipeEvent.getRecipe() instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<IBoringRecipe> recipes = (List<IBoringRecipe>) recipeEvent.getRecipe();

			ResourceKey<Biome> biome = tile.getLevel().getBiome(tile.getBlockPos()).unwrapKey().get();
			BoringContext context = new BoringContext(tile.getLevel().dimension().location(), biome.location(), tile.getBlockPos().getY(), tile.getLevel().getBlockStatesIfLoaded(bore.getBladeBoundingBox()).collect(Collectors.toList()));
			List<IBoringRecipe> newRecipes = tile.getLevel().getRecipeManager().getRecipesFor(RegistryManager.EXCAVATION.get(), context, tile.getLevel()).stream().map(RecipeHolder::value).collect(Collectors.toList());

			recipes.addAll(newRecipes);
		}
		if (tile.getLevel().isClientSide()) {
			CLientStuff.throwEvent(tile, upgrades, event, distance, count);
		}
	}

	public static class CLientStuff {

		public static net.minecraft.client.resources.model.BakedModel buckets;

		public static void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
			if (buckets != null && event instanceof EmberBoreBladeRenderEvent renderEvent) {
				net.minecraft.client.Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(renderEvent.getPose().last(), renderEvent.getBuffer().getBuffer(net.minecraft.client.renderer.Sheets.solidBlockSheet()), renderEvent.getBlockState(), buckets, 0.0f, 0.0f, 0.0f, renderEvent.getLight(), renderEvent.getOverlay(), ModelData.EMPTY, net.minecraft.client.renderer.Sheets.solidBlockSheet());
			}
		}
	}
}
