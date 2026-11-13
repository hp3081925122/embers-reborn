package com.rekindled.embers.fluidtypes;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.FluidStuff;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * 26.1 moved fluid textures into fluid models (RegisterFluidModelsEvent) and client fluid extensions into
 * RegisterClientExtensionsEvent; this replaces the per-type initializeClient overrides of 1.21.1 with the same behaviour:
 * still/flow textures, fog colour and distances, steam fog alpha 0.7, and the overlay textures that only plain
 * EmbersFluidType fluids had (the molten metal, steam and viscous subclasses never set them).
 */
public final class EmbersFluidClient {
	private EmbersFluidClient() {}

	public static void registerFluidModels(RegisterFluidModelsEvent event) {
		for (FluidStuff fluid : RegistryManager.fluidList) {
			if (fluid.TYPE.get() instanceof EmbersFluidType type) {
				Material overlay = type.getClass() == EmbersFluidType.class ? new Material(type.TEXTURE_OVERLAY) : null;
				event.register(new FluidModel.Unbaked(new Material(type.TEXTURE_STILL), new Material(type.TEXTURE_FLOW), overlay, null, null), fluid.FLUID, fluid.FLUID_FLOW);
			}
		}
	}

	public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
		for (FluidStuff fluid : RegistryManager.fluidList) {
			if (fluid.TYPE.get() instanceof EmbersFluidType type)
				event.registerFluidType(new Extensions(type), type);
		}
	}

	/** Fog colour a fluid tints its escape particles with; the 1.21.1 code asked the client extension with a white default. */
	public static Vector3f fogColor(FluidType type) {
		return type instanceof EmbersFluidType embers ? embers.FOG_COLOR : new Vector3f(1, 1, 1);
	}

	public static Vector3f fogColor(Fluid fluid) {
		return fogColor(fluid.getFluidType());
	}

	@Nullable
	public static FluidModel model(Fluid fluid) {
		return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid.defaultFluidState());
	}

	/** Tint for a fluid stack as the world fluid model applies it; -1 (no tint) when the model has none. */
	public static int tint(FluidStack stack) {
		FluidModel model = model(stack.getFluid());
		return model != null && model.fluidTintSource() != null ? model.fluidTintSource().colorAsStack(stack) : -1;
	}

	private record Extensions(EmbersFluidType type) implements IClientFluidTypeExtensions {
		@Override
		@Nullable
		public Identifier getRenderOverlayTexture(Minecraft mc) {
			return type.getClass() == EmbersFluidType.class ? type.RENDER_OVERLAY : null;
		}

		@Override
		public void modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
			fluidFogColor.set(type.FOG_COLOR.x(), type.FOG_COLOR.y(), type.FOG_COLOR.z(), type instanceof SteamFluidType ? 0.7F : fluidFogColor.w());
		}

		@Override
		public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
			fogData.environmentalStart = type.fogStart;
			fogData.environmentalEnd = type.fogEnd;
		}
	}
}
