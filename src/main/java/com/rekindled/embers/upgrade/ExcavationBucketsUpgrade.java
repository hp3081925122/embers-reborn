package com.rekindled.embers.upgrade;

import java.util.List;

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

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ExcavationBucketsUpgrade extends DefaultUpgradeProvider {

	@OnlyIn(Dist.CLIENT)
	public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart buckets;

	public ExcavationBucketsUpgrade(BlockEntity tile) {
		super(Identifier.fromNamespaceAndPath(Embers.MODID, "excavation_buckets"), tile);
	}

	@Override
	public int getPriority() {
		return -90; //after the clockwork attenuator
	}

	@Override
	public int getLimit(BlockEntity tile) {
		if (tile instanceof EmberBoreBlockEntity)
			return Integer.MAX_VALUE;
		return 0;
	}

	@Override
	public boolean doTick(BlockEntity tile, List<UpgradeContext> upgrades, int distance, int count) {
		((ExcavationBucketsBlockEntity) this.tile).lastAngle = ((ExcavationBucketsBlockEntity) this.tile).angle;
		if (tile instanceof EmberBoreBlockEntity bore && bore.isRunning)
			((ExcavationBucketsBlockEntity) this.tile).angle += 14.0f * bore.speedMod;
		return false;
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (tile instanceof EmberBoreBlockEntity bore && event instanceof MachineRecipeEvent<?> recipeEvent && recipeEvent.getRecipe() instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<IBoringRecipe> recipes = (List<IBoringRecipe>) recipeEvent.getRecipe();

			ResourceKey<Biome> biome = tile.getLevel().getBiome(tile.getBlockPos()).unwrapKey().get();
			BoringContext context = new BoringContext(tile.getLevel().dimension().identifier(), biome.identifier(), tile.getBlockPos().getY(), tile.getLevel().getBlockStatesIfLoaded(bore.getBladeBoundingBox()).collect(java.util.stream.Collectors.toList()));
			List<IBoringRecipe> newRecipes = tile.getLevel().getRecipeManager().getRecipesFor(RegistryManager.EXCAVATION.get(), context, tile.getLevel()).stream().map(net.minecraft.world.item.crafting.RecipeHolder::value).collect(java.util.stream.Collectors.toList());

			recipes.addAll(newRecipes);
		}
		if (tile.getLevel().isClientSide()) {
			CLientStuff.throwEvent(tile, upgrades, event, distance, count);
		}
	}

	public static class CLientStuff {

		public static void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
			if (buckets != null && event instanceof EmberBoreBladeRenderEvent renderEvent) {
				com.rekindled.embers.render.LegacyBlockEntityRenderer.model(renderEvent.getPose(), buckets, renderEvent.getLight(), renderEvent.getOverlay());
			}
		}
	}
}
