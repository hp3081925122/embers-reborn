package com.rekindled.embers.power;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class DefaultEmberItemCapability implements IEmberCapability {
	@Nonnull
	public ItemStack stack;
	public final Optional<?> capOptional = Optional.empty(); // NeoForge 1.21.1: LazyOptional removed
	double ember = 0;
	double capacity = 0;

	public DefaultEmberItemCapability(@Nonnull ItemStack stack, double capacity) {
		this.stack = stack;
		// Do not write to the stack here: capabilities are queried every frame (tooltips, bars),
		// and writing would mutate the stack on every query. The default capacity is used until
		// something actually stores a value.
		this.capacity = capacity;
	}

	public void invalidate() {
		// NeoForge 1.21.1: LazyOptional.invalidate() removed
	}

		// getCapability removed — handled by RegisterCapabilitiesEvent

	@Override
	public double getEmber() {
		if (stack.isEmpty())
			return 0;
		return _getOrCreateCustomTag(stack, "ForgeCaps").getDoubleOr(EMBER, 0.0D);
	}

	@Override
	public double getEmberCapacity() {
		if (stack.isEmpty())
			return 0;
		CompoundTag tag = _getOrCreateCustomTag(stack, "ForgeCaps");
		return tag.contains(EMBER_CAPACITY) ? tag.getDoubleOr(EMBER_CAPACITY, 0.0D) : capacity;
	}

	@Override
	public void setEmber(double value) {
		ember = value;
		if (!_hasCustomField(stack, "ForgeCaps", EMBER_CAPACITY))
			_putCustomTag(stack, "ForgeCaps", EMBER_CAPACITY, capacity);
		_putCustomTag(stack, "ForgeCaps", EMBER, value);
	}

	@Override
	public void setEmberCapacity(double value) {
		capacity = value;
		_putCustomTag(stack, "ForgeCaps", EMBER_CAPACITY, value);
	}

	@Override
	public double addAmount(double value, boolean doAdd) {
		double ember = getEmber();
		double capacity = getEmberCapacity();
		double added = Math.min(capacity - ember, value);
		double newEmber = ember + added;
		if (doAdd) {
			if (newEmber != ember)
				onContentsChanged();
			setEmber(ember + added);
		}
		return added;
	}

	@Override
	public double removeAmount(double value, boolean doRemove) {
		double ember = getEmber();
		double removed = Math.min(ember, value);
		double newEmber = ember - removed;
		if (doRemove) {
			if (newEmber != ember)
				onContentsChanged();
			setEmber(ember - removed);
		}
		return removed;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putDouble(EMBER, ember);
		nbt.putDouble(EMBER_CAPACITY, capacity);
	}

	@Override
	public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
		if (nbt.contains(EMBER))
			ember = nbt.getDoubleOr(EMBER, 0.0D);
		if (nbt.contains(EMBER_CAPACITY))
			capacity = nbt.getDoubleOr(EMBER_CAPACITY, 0.0D);
	}

	@Override
	public void onContentsChanged() {

	}

	// NBT helpers for CustomData in 1.21.1
	private static CompoundTag _getOrCreateCustomTag(ItemStack stack, String key) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = cd.copyTag();
		if (!root.contains(key)) root.put(key, new CompoundTag());
		return root.getCompoundOrEmpty(key);
	}
	private static void _putCustomTag(ItemStack stack, String key, String field, double value) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			if (!tag.contains(key)) tag.put(key, new CompoundTag());
			tag.getCompoundOrEmpty(key).putDouble(field, value);
		});
	}
	private static boolean _hasCustomField(ItemStack stack, String key, String field) {
		CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		return root.contains(key) && root.getCompoundOrEmpty(key).contains(field);
	}
	private static CompoundTag _getCustomSubTag(ItemStack stack, String key) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = cd.copyTag();
		return root.contains(key) ? root.getCompoundOrEmpty(key) : null;
	}
}