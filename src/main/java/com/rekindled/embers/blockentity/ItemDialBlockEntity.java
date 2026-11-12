package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IDialEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemDialBlockEntity extends BlockEntity implements IDialEntity {

	public ItemStack[] itemStacks = new ItemStack[0];
	public int extraLines = 0;
	public boolean display = false;

	public ItemDialBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_DIAL_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		ListTag items = nbt.getList("items", Tag.TAG_COMPOUND);
		itemStacks = new ItemStack[items.size()];
		if (items.size() > 0) {
			for (int i = 0; i < items.size(); i++) {
				itemStacks[i] = ItemStack.parse(provider, items.getCompound(i)).orElse(net.minecraft.world.item.ItemStack.EMPTY);
			}
		}
		if (nbt.contains("more_lines"))
			extraLines = nbt.getInt("more_lines");
		if (nbt.contains("display"))
			display = nbt.getBoolean("display");
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		return getUpdateTag(100, provider);
	}

	public CompoundTag getUpdateTag(int maxLines, net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		BlockState state = level.getBlockState(worldPosition);
		boolean display = false;
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(facing, -1));
			if (blockEntity != null) {
				IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), facing.getOpposite());
				if (cap != null) {
					ListTag items = new ListTag();
					for (int i = 0; i < cap.getSlots() && i < maxLines; i++) {
						ItemStack stack = cap.getStackInSlot(i);
						items.add(stack.save(provider));
					}
					nbt.put("items", items);

					if (cap.getSlots() > maxLines) {
						nbt.putInt("more_lines", cap.getSlots() - maxLines);
					} else {
						nbt.putInt("more_lines", 0);
					}
					display = true;
				}
			}
		}
		nbt.putBoolean("display", display);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket(int maxLines) {
		return ClientboundBlockEntityDataPacket.create(this, (BE, prov) -> getUpdateTag(maxLines, prov));
	}
}
