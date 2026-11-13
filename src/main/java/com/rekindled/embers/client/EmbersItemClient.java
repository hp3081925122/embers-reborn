package com.rekindled.embers.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.util.Misc;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;

/**
 * 26.1 replaced ItemColor and ItemProperties with data-driven item definitions (assets/embers_reborn/items).
 * The colours and the charged check are the same code as the 1.21.1 ColorHandler / ItemProperties lambdas.
 */
public final class EmbersItemClient {
	private EmbersItemClient() {}

	public static void registerTints(RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Identifier.fromNamespaceAndPath(Embers.MODID, "ember_charge"), EmberCharge.CODEC);
		event.register(Identifier.fromNamespaceAndPath(Embers.MODID, "tyrfing_glow"), TyrfingGlow.CODEC);
	}

	public static void registerProperties(RegisterConditionalItemModelPropertyEvent event) {
		event.register(Identifier.fromNamespaceAndPath(Embers.MODID, "charged"), Charged.CODEC);
	}

	/** Ember jar, cartridge and bulb: white when empty, pulsing orange as they fill. */
	public record EmberCharge() implements ItemTintSource {
		public static final MapCodec<EmberCharge> CODEC = MapCodec.unit(new EmberCharge());

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
			IEmberCapability capability = EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
			if (capability == null)
				return 0xFFFFFFFF;
			float coeff = (float) (capability.getEmber() / capability.getEmberCapacity());
			float timerSine = ((float) Math.sin(8.0 * Math.toRadians(EmbersClientEvents.ticks % 360)) + 1.0f) / 2.0f;
			int r = 255;
			int g = (int) (255.0f * (1.0f - coeff) + (64.0f * timerSine + 64.0f) * coeff);
			int b = (int) (255.0f * (1.0f - coeff) + 16.0f * coeff);
			return 0xFF000000 | Misc.intColor(r, g, b);
		}

		@Override
		public MapCodec<EmberCharge> type() {
			return CODEC;
		}
	}

	/** Tyrfing's pulsing purple layer. */
	public record TyrfingGlow() implements ItemTintSource {
		public static final MapCodec<TyrfingGlow> CODEC = MapCodec.unit(new TyrfingGlow());

		@Override
		public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
			float timerSine = ((float) Math.sin(8.0 * Math.toRadians(EmbersClientEvents.ticks % 360)) + 1.0f) / 2.0f;
			int r = (int) (64.0f * timerSine);
			int g = 16;
			int b = (int) (32.0f + 32.0f * timerSine);
			return 0xFF000000 | Misc.intColor(r, g, b);
		}

		@Override
		public MapCodec<TyrfingGlow> type() {
			return CODEC;
		}
	}

	/** Inflictor gem: true once it has stored a damage type. */
	public record Charged() implements ConditionalItemModelProperty {
		public static final MapCodec<Charged> CODEC = MapCodec.unit(new Charged());

		@Override
		public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext displayContext) {
			return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("type");
		}

		@Override
		public MapCodec<Charged> type() {
			return CODEC;
		}
	}
}
