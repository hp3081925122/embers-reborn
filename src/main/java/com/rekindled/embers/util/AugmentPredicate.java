package com.rekindled.embers.util;

import java.util.function.Predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;
import com.rekindled.embers.api.augment.IAugmentUtil;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class AugmentPredicate implements DataComponentPredicate, Predicate<ItemStack> {

	public static final Identifier ID = Identifier.fromNamespaceAndPath(Embers.MODID, "augment");

	public static final Codec<AugmentPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
			Identifier.CODEC.fieldOf("augment").forGetter(AugmentPredicate::getAugmentId),
			Codec.INT.optionalFieldOf("level", 1).forGetter(AugmentPredicate::getLevel)
	).apply(inst, AugmentPredicate::new));

	public static final DataComponentPredicate.ConcreteType<AugmentPredicate> TYPE = new DataComponentPredicate.ConcreteType<>(CODEC);

	protected final Identifier augmentId;
	protected final int level;

	public AugmentPredicate(IAugment augment, int level) {
		this(augment.getName(), level);
	}

	public AugmentPredicate(Identifier augmentId, int level) {
		this.augmentId = augmentId;
		this.level = level;
	}

	public Identifier getAugmentId() {
		return augmentId;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public boolean matches(DataComponentGetter components) {
		if (components instanceof ItemStack stack) {
			return test(stack);
		}
		CustomData customData = components.get(DataComponents.CUSTOM_DATA);
		if (customData == null || customData.isEmpty()) {
			return false;
		}
		CompoundTag root = customData.copyTag();
		if (!root.contains(IAugmentUtil.HEAT_TAG)) {
			return false;
		}
		ListTag list = root.getCompoundOrEmpty(IAugmentUtil.HEAT_TAG).getListOrEmpty("augments");
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.getCompoundOrEmpty(i);
			if (augmentId.toString().equals(entry.getStringOr("name", "")) && entry.getIntOr("level", 0) >= level) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		IAugment augment = AugmentUtil.getAugment(augmentId);
		if (augment == null)
			return false;
		return AugmentUtil.hasHeat(stack) && AugmentUtil.getAugmentLevel(stack, augment) >= level;
	}

	public JsonElement serializeToJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", ID.toString());
		json.addProperty("augment", augmentId.toString());
		if (level != 1)
			json.addProperty("level", level);
		return json;
	}

	public static AugmentPredicate deserialize(JsonObject json) {
		Identifier id = Identifier.parse(GsonHelper.getAsString(json, "augment"));
		int level = 1;
		if (json.has("level"))
			level = GsonHelper.getAsInt(json, "level");
		return new AugmentPredicate(id, level);
	}
}
