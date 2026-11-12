package com.rekindled.embers.power;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class DefaultEmberCapability implements IEmberCapability {
	public static boolean allAcceptVolatile = false;
	private double ember = 0;
	private double capacity = 0;
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public DefaultEmberCapability() {
		// holder init removed
	}

	public DefaultEmberCapability(IEmberCapability capability) {
		// holder init removed
	}

	@Override
	public double getEmber() {
		return ember;
	}

	@Override
	public double getEmberCapacity() {
		return capacity;
	}

	@Override
	public void setEmber(double value) {
		ember = value;
	}

	@Override
	public void setEmberCapacity(double value) {
		capacity = value;
	}

	@Override
	public double addAmount(double value, boolean doAdd) {
		double added = Math.min(capacity - ember,value);
		double newEmber = ember + added;
		if (doAdd){
			if(newEmber != ember)
				onContentsChanged();
			ember += added;
		}
		return added;
	}

	@Override
	public double removeAmount(double value, boolean doRemove) {
		double removed = Math.min(ember,value);
		double newEmber = ember - removed;
		if (doRemove){
			if(newEmber != ember)
				onContentsChanged();
			ember -= removed;
		}
		return removed;
	}

	@Override
	public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putDouble(EMBER, ember);
		nbt.putDouble(EMBER_CAPACITY, capacity);
	}

	@Override
	public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
		if (nbt.contains(EMBER)){
			ember = nbt.getDouble(EMBER);
		}
		if (nbt.contains(EMBER_CAPACITY)){
			capacity = nbt.getDouble(EMBER_CAPACITY);
		}
	}

	@Override
	public void onContentsChanged() {

	}

	@Override
	public boolean acceptsVolatile() {
		return allAcceptVolatile;
	}

	@Override
	public void invalidate() {
		// holder.invalidate() removed
	}

}
