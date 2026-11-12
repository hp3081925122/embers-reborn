package com.rekindled.embers.util;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Bridges 26.1's ValueInput/ValueOutput save API to the CompoundTag based code kept from 1.21.1.
 * The whole input is read as one CompoundTag and the written tag is merged back into the output,
 * so the on-disk layout stays the same as before.
 */
@SuppressWarnings("removal")
public final class NbtBridge {
	public static final MapCodec<CompoundTag> WHOLE = MapCodec.assumeMapUnsafe(CompoundTag.CODEC);

	private NbtBridge() {}

	public static CompoundTag read(ValueInput input) {
		return input.read(WHOLE).orElseGet(CompoundTag::new);
	}

	public static void write(ValueOutput output, CompoundTag tag) {
		if (!tag.isEmpty())
			output.store(WHOLE, tag);
	}

	public static HolderLookup.Provider provider(BlockEntity blockEntity) {
		if (blockEntity.getLevel() != null)
			return blockEntity.getLevel().registryAccess();
		if (ServerLifecycleHooks.getCurrentServer() != null)
			return ServerLifecycleHooks.getCurrentServer().registryAccess();
		return RegistryAccess.EMPTY;
	}

	public static ValueInput input(HolderLookup.Provider provider, CompoundTag tag) {
		return TagValueInput.create(ProblemReporter.DISCARDING, provider, tag);
	}

	public static CompoundTag save(ItemStackHandler handler, HolderLookup.Provider provider) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
		handler.serialize(output);
		return output.buildResult();
	}

	public static void load(ItemStackHandler handler, HolderLookup.Provider provider, CompoundTag tag) {
		handler.deserialize(input(provider, tag));
	}

	public static CompoundTag save(FluidTank tank, HolderLookup.Provider provider) {
		TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
		tank.serialize(output);
		return output.buildResult();
	}

	public static void load(FluidTank tank, HolderLookup.Provider provider, CompoundTag tag) {
		tank.deserialize(input(provider, tag));
	}
}
