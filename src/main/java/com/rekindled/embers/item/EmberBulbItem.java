package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class EmberBulbItem extends EmberStorageItem implements IEmbersCurioItem {

	public static final double CAPACITY = 1000.0;

	public EmberBulbItem(Properties properties) {
		super(properties);
	}

	@Override
	public double getCapacity() {
		return CAPACITY;
	}

	/* initCapabilities removed - use RegisterCapabilitiesEvent */
}
