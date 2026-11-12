package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemDropperBlockEntity extends BlockEntity implements IItemPipePriority {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			ItemDropperBlockEntity.this.setChanged();
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public ItemDropperBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_DROPPER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		com.rekindled.embers.util.NbtBridge.load(inventory, provider, nbt.getCompoundOrEmpty("inventory"));
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
		saveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));
		com.rekindled.embers.util.NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		nbt.put("inventory", com.rekindled.embers.util.NbtBridge.save(inventory, provider));
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemDropperBlockEntity blockEntity) {
		if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			level.addFreshEntity(new ItemEntity(level, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, blockEntity.inventory.extractItem(0, 1, false), 0, -0.1, 0));
		}
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

	@Override
	public int getPriority(Direction facing) {
		return 50;
	}
}
