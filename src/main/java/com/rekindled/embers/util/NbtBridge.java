package com.rekindled.embers.util;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
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

	/**
	 * Registries for ItemStack/FluidStack NBT written outside a provider-carrying method.
	 * RegistryAccess.EMPTY cannot encode data-driven components such as enchantments.
	 */
	public static HolderLookup.Provider registries() {
		if (ServerLifecycleHooks.getCurrentServer() != null)
			return ServerLifecycleHooks.getCurrentServer().registryAccess();
		if (net.neoforged.fml.loading.FMLEnvironment.getDist().isClient())
			return ClientRegistries.get();
		return RegistryAccess.EMPTY;
	}

	private static final class ClientRegistries {
		static HolderLookup.Provider get() {
			net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
			if (mc.level != null)
				return mc.level.registryAccess();
			if (mc.getConnection() != null)
				return mc.getConnection().registryAccess();
			return RegistryAccess.EMPTY;
		}
	}

	/** 1.21.1 ItemStack.saveOptional: empty stack -> empty compound. */
	public static Tag saveOptional(ItemStack stack, HolderLookup.Provider provider) {
		return stack.isEmpty() ? new CompoundTag() : save(stack, provider);
	}

	/** 1.21.1 ItemStack.save: stack must not be empty. */
	public static Tag save(ItemStack stack, HolderLookup.Provider provider) {
		if (stack.isEmpty())
			return new CompoundTag();
		return ItemStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	/** 1.21.1 ItemStack.parse. */
	public static Optional<ItemStack> parse(HolderLookup.Provider provider, Tag tag) {
		return ItemStack.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
	}

	/** 1.21.1 ItemStack.parseOptional: empty compound -> empty stack. */
	public static ItemStack parseOptional(HolderLookup.Provider provider, Tag tag) {
		if (tag instanceof CompoundTag compound && compound.isEmpty())
			return ItemStack.EMPTY;
		return parse(provider, tag).orElse(ItemStack.EMPTY);
	}

	/** 1.21.1 FluidStack.saveOptional. */
	public static Tag saveOptional(FluidStack stack, HolderLookup.Provider provider) {
		if (stack.isEmpty())
			return new CompoundTag();
		return FluidStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
	}

	/** 1.21.1 FluidStack.parseOptional. */
	public static FluidStack parseOptionalFluid(HolderLookup.Provider provider, Tag tag) {
		if (tag instanceof CompoundTag compound && compound.isEmpty())
			return FluidStack.EMPTY;
		return FluidStack.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(FluidStack.EMPTY);
	}

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

	public static HolderLookup.Provider serverProvider() {
		if (ServerLifecycleHooks.getCurrentServer() != null)
			return ServerLifecycleHooks.getCurrentServer().registryAccess();
		return RegistryAccess.EMPTY;
	}

	/** Data attachment builder for the mod's CompoundTag based capability data (research, shifting scales). */
	public static <T extends INBTSerializable<CompoundTag>> net.neoforged.neoforge.attachment.AttachmentType.Builder<T> attachment(java.util.function.Supplier<T> factory) {
		return net.neoforged.neoforge.attachment.AttachmentType.builder(factory).serialize(new net.neoforged.neoforge.attachment.IAttachmentSerializer<T>() {
			@Override
			public T read(net.neoforged.neoforge.attachment.IAttachmentHolder holder, ValueInput input) {
				T value = factory.get();
				value.deserializeNBT(input.lookup(), NbtBridge.read(input));
				return value;
			}

			@Override
			public boolean write(T attachment, ValueOutput output) {
				CompoundTag tag = attachment.serializeNBT(serverProvider());
				NbtBridge.write(output, tag);
				return true;
			}
		});
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

	/** 1.21.1 FluidTank#writeToNBT(provider, tag): writes the tank into tag and returns it. */
	public static CompoundTag writeTank(FluidTank tank, HolderLookup.Provider provider, CompoundTag tag) {
		tag.merge(save(tank, provider));
		return tag;
	}

	public static void load(FluidTank tank, HolderLookup.Provider provider, CompoundTag tag) {
		tank.deserialize(input(provider, tag));
	}
}
