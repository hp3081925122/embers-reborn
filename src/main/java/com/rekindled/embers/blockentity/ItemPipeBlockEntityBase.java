package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.util.PipePriorityMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class ItemPipeBlockEntityBase extends PipeBlockEntityBase implements IItemPipePriority {

	public ItemStackHandler inventory;
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public ItemPipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		initInventory();
	}

	protected void initInventory() {
		inventory = new ItemStackHandler(1) {
			@Override
			public int getSlotLimit(int slot) {
				return ItemPipeBlockEntityBase.this.getCapacity();
			}

			@Override
			protected void onContentsChanged(int slot) {
				ItemPipeBlockEntityBase.this.setChanged();
			}
		};
	}

	public abstract int getCapacity();

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_PIPE;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntityBase blockEntity) {
		if (!blockEntity.loaded)
			blockEntity.initConnections();
		blockEntity.ticksExisted++;
		boolean itemsMoved = false;
		ItemStack passStack = blockEntity.inventory.extractItem(0, 1, true);
		if (!passStack.isEmpty()) {
			PipePriorityMap<Integer, Direction> possibleDirections = new PipePriorityMap<>();
			IItemHandler[] itemHandlers = new IItemHandler[Direction.values().length];

			for (Direction facing : Direction.values()) {
				if (!blockEntity.getConnection(facing).transfer)
					continue;
				if (blockEntity.isFrom(facing))
					continue;
				BlockEntity tile = level.getBlockEntity(pos.relative(facing));
				if (tile != null) {
					IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(facing), facing.getOpposite());
					if (handler != null) {
						int priority = PRIORITY_BLOCK;
						if (tile instanceof IItemPipePriority)
							priority = ((IItemPipePriority) tile).getPriority(facing.getOpposite());
						if (blockEntity.isFrom(facing.getOpposite()))
							priority -= 5; //aka always try opposite first
						possibleDirections.put(priority, facing);
						itemHandlers[facing.get3DDataValue()] = handler;
					}
				}
			}

			for (int key : possibleDirections.keySet()) {
				ArrayList<Direction> list = possibleDirections.get(key);
				for (int i = 0; i < list.size(); i++) {
					Direction facing = list.get((i+blockEntity.lastRobin) % list.size());
					IItemHandler handler = itemHandlers[facing.get3DDataValue()];
					itemsMoved = blockEntity.pushStack(passStack, facing, handler);
					if(blockEntity.lastTransfer != facing) {
						blockEntity.lastTransfer = facing;
						blockEntity.syncTransfer = true;
						blockEntity.setChanged();
					}
					if (itemsMoved) {
						blockEntity.lastRobin++;
						break;
					}
				}
				if (itemsMoved)
					break;
			}
		}

		if (blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			if (blockEntity.lastTransfer != null && !itemsMoved) {
				blockEntity.lastTransfer = null;
				blockEntity.syncTransfer = true;
				blockEntity.setChanged();
			}
			itemsMoved = true;
			blockEntity.resetFrom();
		}
		if (blockEntity.clogged == itemsMoved) {
			blockEntity.clogged = !itemsMoved;
			blockEntity.syncCloggedFlag = true;
			blockEntity.setChanged();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientTick(Level level, BlockPos pos, BlockState state, ItemPipeBlockEntityBase blockEntity) {
		PipeBlockEntityBase.clientTick(level, pos, state, blockEntity);
	}

	private boolean pushStack(ItemStack passStack, Direction facing, IItemHandler handler) {
		int slot = -1;
		for (int j = 0; j < handler.getSlots() && slot == -1; j++) {
			if (handler.insertItem(j, passStack, true).isEmpty()) {
				slot = j;
			}
		}

		if (slot != -1) {
			ItemStack added = handler.insertItem(slot, passStack, false);
			if (added.isEmpty()) {
				this.inventory.extractItem(0, 1, false);
				return true;
			}
		}

		if (isFrom(facing))
			setFrom(facing, false);
		return false;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("inventory"))
			inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		writeInventory(nbt);
	}

	public void writeInventory(CompoundTag nbt) /* no provider */  {
		nbt.put("inventory", inventory.serializeNBT(com.rekindled.embers.util.RegistryHelper.registries()));
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		// holder.invalidate() removed
	}
}
