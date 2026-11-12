package com.rekindled.embers.blockentity;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.recipe.CatalysisCombustionContext;
import com.rekindled.embers.recipe.ICatalysisCombustionRecipe;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CatalysisChamberBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	public static ItemStack machine = new ItemStack(RegistryManager.CATALYSIS_CHAMBER_ITEM.get());
	int progress = 0;
	double multiplier = 0;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CatalysisChamberBlockEntity.this.setChanged();
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)
	public ICatalysisCombustionRecipe cachedRecipe = null;

	public CatalysisChamberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CATALYSIS_CHAMBER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("inventory"))
			inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
		if (nbt.contains("progress"))
			progress = nbt.getInt("progress");
		multiplier = nbt.getDouble("multiplier");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
		nbt.putInt("progress", progress);
		nbt.putDouble("multiplier", multiplier);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putDouble("multiplier", multiplier);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CatalysisChamberBlockEntity blockEntity) {
		if (blockEntity.progress > 0) {
			blockEntity.progress --;
			if (blockEntity.progress == 0) {
				blockEntity.multiplier = 0;
				((ServerLevel) level).getChunkSource().blockChanged(pos);
			}
			blockEntity.setChanged();
		}
		if (blockEntity.progress == 0 && !blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			CatalysisCombustionContext wrapper = new CatalysisCombustionContext(blockEntity.inventory, machine);
			blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.CATALYSIS_COMBUSTION.get(), wrapper, level);
			if (blockEntity.cachedRecipe != null) {
				blockEntity.multiplier = blockEntity.cachedRecipe.getmultiplier(wrapper);
				blockEntity.progress = blockEntity.cachedRecipe.getBurnTIme(wrapper);

				//the recipe is responsible for taking items from the inventory
				blockEntity.cachedRecipe.process(wrapper);
				((ServerLevel) level).getChunkSource().blockChanged(pos);
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
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == Capabilities.ItemHandler.BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == Capabilities.ItemHandler.BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.catalysis")));
	}
}
