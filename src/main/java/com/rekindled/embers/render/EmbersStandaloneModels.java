package com.rekindled.embers.render;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

/** Moving machine parts drawn by block entity renderers (models/block/<name>.json), loaded as standalone models. */
public final class EmbersStandaloneModels {
	public static final String[] NAMES = {
			"ember_bore_blades", "mechanical_pump_piston_bottom", "mechanical_pump_piston_top", "stamper_arm",
			"automatic_hammer_end", "inferno_forge_hatch", "atmospheric_bellows_top", "atmospheric_bellows_leather",
			"entropic_enumerator_drf", "entropic_enumerator_dlf", "entropic_enumerator_urf", "entropic_enumerator_ulf",
			"entropic_enumerator_drb", "entropic_enumerator_dlb", "entropic_enumerator_urb", "entropic_enumerator_ulb",
			"excavation_buckets_wheel", "ember_bore_excavation_buckets" };

	private static final Map<String, StandaloneModelKey<BlockStateModelPart>> KEYS = new LinkedHashMap<>();

	static {
		for (String name : NAMES) {
			Identifier id = Identifier.fromNamespaceAndPath(Embers.MODID, "block/" + name);
			ModelDebugName debugName = id::toString;
			KEYS.put(name, new StandaloneModelKey<>(debugName));
		}
	}

	private EmbersStandaloneModels() {}

	public static void register(ModelEvent.RegisterStandalone event) {
		KEYS.forEach((name, key) -> event.register(key, SimpleUnbakedStandaloneModel.simpleModelWrapper(Identifier.fromNamespaceAndPath(Embers.MODID, "block/" + name))));
	}

	@Nullable
	public static BlockStateModelPart get(String name) {
		StandaloneModelKey<BlockStateModelPart> key = KEYS.get(name);
		return key == null ? null : Minecraft.getInstance().getModelManager().getStandaloneModel(key);
	}
}
