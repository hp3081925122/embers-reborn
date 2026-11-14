package com.rekindled.embers.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class AugmentPredicate implements ItemSubPredicate {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "augment");

	public static final Codec<AugmentPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			ResourceLocation.CODEC.fieldOf("augment").forGetter(p -> p.augment.getName()),
			Codec.INT.optionalFieldOf("level", 1).forGetter(p -> p.level)
	).apply(inst, (augmentId, level) -> new AugmentPredicate(AugmentUtil.getAugment(augmentId), level)));

	public static final ItemSubPredicate.Type<AugmentPredicate> TYPE = new ItemSubPredicate.Type<>(CODEC);

	protected final IAugment augment;
	protected final int level;

	public AugmentPredicate(IAugment augment, int level) {
		this.augment = augment;
		this.level = level;
	}

	@Override
	public boolean matches(ItemStack stack) {
		if (stack == null || stack.isEmpty() || augment == null)
			return false;
		return AugmentUtil.hasHeat(stack) && AugmentUtil.getAugmentLevel(stack, augment) >= level;
	}
}
