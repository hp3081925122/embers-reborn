package com.rekindled.embers.item;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.rekindled.embers.Embers;
import com.rekindled.embers.util.GlowingTextTooltip;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.TooltipFlag;

public class EmberRecordItem extends Item {

	public static final ResourceKey<JukeboxSong> SONG_7F_PATTERNS = ResourceKey.create(
			Registries.JUKEBOX_SONG,
			ResourceLocation.fromNamespaceAndPath(Embers.MODID, "7f_patterns"));

	public EmberRecordItem(int comparatorValue, Supplier<SoundEvent> soundSupplier, Item.Properties builder, int lengthInTicks) {
		super(builder.stacksTo(1).component(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder<>(SONG_7F_PATTERNS), false)));
	}

	@Override
	public void appendHoverText(ItemStack pStack, Item.TooltipContext context, List<Component> pTooltip, TooltipFlag pFlag) {
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new GlowingTextTooltip(Component.translatable(this.getDescriptionId() + ".desc")));
	}
}
