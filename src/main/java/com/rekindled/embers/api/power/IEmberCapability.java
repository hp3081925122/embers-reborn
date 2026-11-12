package com.rekindled.embers.api.power;

import com.rekindled.embers.Embers;

import net.minecraft.nbt.CompoundTag;
import com.rekindled.embers.util.INBTSerializable;

/**
 * Ember energy storage interface.
 * In NeoForge 1.21.1 ICapabilitySerializable is gone; we use INBTSerializable for NBT
 * serialization and register the provider via RegisterCapabilitiesEvent.
 */
public interface IEmberCapability extends INBTSerializable<CompoundTag> {

    String EMBER_CAPACITY = Embers.MODID + ":ember_capacity";
    String EMBER           = Embers.MODID + ":ember";

    double getEmber();
    double getEmberCapacity();
    void   setEmber(double value);
    void   setEmberCapacity(double value);
    double addAmount(double value, boolean doAdd);
    double removeAmount(double value, boolean doRemove);
    void   writeToNBT(CompoundTag nbt);
    void   onContentsChanged();
    void   invalidate();

    default boolean acceptsVolatile() {
        return false;
    }
}