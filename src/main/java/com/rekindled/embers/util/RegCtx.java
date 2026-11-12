package com.rekindled.embers.util;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 26.1 requires Item.Properties / BlockBehaviour.Properties to carry the registry id before the object is built.
 * The mod's hundreds of registrations create their Properties inside the supplier, so instead of rewriting each one
 * we expose the id being registered here and the Properties mixins stamp it onto every Properties created meanwhile.
 */
public final class RegCtx {
	private static final ThreadLocal<ResourceKey<Item>> ITEM = new ThreadLocal<>();
	private static final ThreadLocal<ResourceKey<Block>> BLOCK = new ThreadLocal<>();

	private RegCtx() {}

	@Nullable
	public static ResourceKey<Item> currentItem() {
		return ITEM.get();
	}

	@Nullable
	public static ResourceKey<Block> currentBlock() {
		return BLOCK.get();
	}

	public static <T extends Item> DeferredHolder<Item, T> item(DeferredRegister<Item> register, String name, Supplier<T> supplier) {
		return register.register(name, id -> {
			ResourceKey<Item> previous = ITEM.get();
			ITEM.set(ResourceKey.create(Registries.ITEM, id));
			try {
				return supplier.get();
			} finally {
				ITEM.set(previous);
			}
		});
	}

	public static <T extends Block> DeferredHolder<Block, T> block(DeferredRegister<Block> register, String name, Supplier<T> supplier) {
		return register.register(name, id -> {
			ResourceKey<Block> previous = BLOCK.get();
			BLOCK.set(ResourceKey.create(Registries.BLOCK, id));
			try {
				return supplier.get();
			} finally {
				BLOCK.set(previous);
			}
		});
	}
}
