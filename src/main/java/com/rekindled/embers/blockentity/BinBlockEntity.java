package com.rekindled.embers.blockentity;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IBin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BinBlockEntity extends BlockEntity implements IBin {

	int ticksExisted = 0;

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			BinBlockEntity.this.setChanged();
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public BinBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.BIN_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, BinBlockEntity blockEntity) {
		blockEntity.ticksExisted ++;
		if (blockEntity.ticksExisted % 10 == 0){
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX(),pos.getY(),pos.getZ(),pos.getX()+1,pos.getY()+1.25,pos.getZ()+1));
			for (int i = 0; i < items.size(); i ++){
				ItemStack stack = blockEntity.inventory.insertItem(0, items.get(i).getItem(), false);
				if (!stack.isEmpty()){
					items.get(i).setItem(stack);
				} else {
					items.get(i).remove(RemovalReason.DISCARDED);
				}
			}
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
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public IItemHandler getInventory() {
		return inventory;
	}
}
