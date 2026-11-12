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
		setEmberCapacity(capacity);
		// NeoForge 1.21.1: LazyOptional removed — no-op

		net.minecraft.nbt.CompoundTag BEnbt = _getCustomSubTag(stack, "BlockEntityTag");
		if (BEnbt != null) {
			setEmber(BEnbt.getDouble(EMBER));
			setEmberCapacity(BEnbt.getDouble(EMBER_CAPACITY));
		}
	}

	public void invalidate() {
		// NeoForge 1.21.1: LazyOptional.invalidate() removed
	}

		// getCapability removed — handled by RegisterCapabilitiesEvent

	@Override
	public double getEmber() {
		if (stack.isEmpty())
			return 0;
		return _getOrCreateCustomTag(stack, "ForgeCaps").getDouble(EMBER);
	}

	@Override
	public double getEmberCapacity() {
		if (stack.isEmpty())
			return 0;
		return _getOrCreateCustomTag(stack, "ForgeCaps").getDouble(EMBER_CAPACITY);
	}

	@Override
	public void setEmber(double value) {
		ember = value;
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
			ember = nbt.getDouble(EMBER);
		if (nbt.contains(EMBER_CAPACITY))
			capacity = nbt.getDouble(EMBER_CAPACITY);
	}

	@Override
	public void onContentsChanged() {

	}

	// NBT helpers for CustomData in 1.21.1
	private static CompoundTag _getOrCreateCustomTag(ItemStack stack, String key) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = cd.copyTag();
		if (!root.contains(key)) root.put(key, new CompoundTag());
		return root.getCompound(key);
	}
	private static void _putCustomTag(ItemStack stack, String key, String field, double value) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			if (!tag.contains(key)) tag.put(key, new CompoundTag());
			tag.getCompound(key).putDouble(field, value);
		});
	}
	private static CompoundTag _getCustomSubTag(ItemStack stack, String key) {
		CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CompoundTag root = cd.copyTag();
		return root.contains(key) ? root.getCompound(key) : null;
	}
}