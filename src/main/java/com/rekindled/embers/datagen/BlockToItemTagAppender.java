package com.rekindled.embers.datagen;

import java.util.Objects;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Writes block-tag declarations into an item tag (block -> its item, block tag -> item tag with the same id). Same as vanilla's private VanillaItemTagsProvider.BlockToItemConverter. */
public class BlockToItemTagAppender implements TagAppender<Block, Block> {
	private final TagAppender<Item, Item> itemAppender;

	public BlockToItemTagAppender(TagAppender<Item, Item> itemAppender) {
		this.itemAppender = itemAppender;
	}

	private static TagKey<Item> itemTag(TagKey<Block> blockTag) {
		return TagKey.create(Registries.ITEM, blockTag.location());
	}

	@Override
	public TagAppender<Block, Block> add(Block element) {
		itemAppender.add(Objects.requireNonNull(element.asItem()));
		return this;
	}

	@Override
	public TagAppender<Block, Block> addOptional(Block element) {
		itemAppender.addOptional(Objects.requireNonNull(element.asItem()));
		return this;
	}

	@Override
	public TagAppender<Block, Block> addTag(TagKey<Block> tag) {
		itemAppender.addTag(itemTag(tag));
		return this;
	}

	@Override
	public TagAppender<Block, Block> addOptionalTag(TagKey<Block> tag) {
		itemAppender.addOptionalTag(itemTag(tag));
		return this;
	}

	@Override
	public TagAppender<Block, Block> add(TagEntry entry) {
		itemAppender.add(entry);
		return this;
	}

	@Override
	public TagAppender<Block, Block> replace(boolean value) {
		itemAppender.replace(value);
		return this;
	}

	@Override
	public TagAppender<Block, Block> remove(Block block) {
		itemAppender.remove(Objects.requireNonNull(block.asItem()));
		return this;
	}

	@Override
	public TagAppender<Block, Block> remove(TagKey<Block> tag) {
		itemAppender.remove(itemTag(tag));
		return this;
	}
}
