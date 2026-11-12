package com.rekindled.embers.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

/**
 * In 1.21.1 ItemPredicate is a final record (cannot be extended).
 * Implement Predicate<ItemStack> directly instead.
 */
public class AugmentPredicate implements java.util.function.Predicate<ItemStack> {

	public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "augment");

	protected final IAugment augment;
	protected final int level;

	public AugmentPredicate(IAugment augment, int level) {
		this.augment = augment;
		this.level = level;
	}

	@Override
	public boolean test(ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return AugmentUtil.hasHeat(stack) && AugmentUtil.getAugmentLevel(stack, augment) >= level;
	}

	public JsonElement serializeToJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", ID.toString());
		json.addProperty("augment", augment.getName().toString());
		if (level != 1)
			json.addProperty("level", level);
		return json;
	}

	public static AugmentPredicate deserialize(JsonObject json) {
		IAugment augment = AugmentUtil.getAugment(ResourceLocation.parse(GsonHelper.getAsString(json, "augment")));
		int level = 1;
		if (json.has("level"))
			level = GsonHelper.getAsInt(json, "level");
		return new AugmentPredicate(augment, level);
	}
}
