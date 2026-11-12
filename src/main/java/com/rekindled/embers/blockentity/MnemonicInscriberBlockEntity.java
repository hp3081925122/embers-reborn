package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.upgrade.MnemonicInscriberUpgrade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class MnemonicInscriberBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.is(EmbersItemTags.INSCRIBABLE_PAPER);
		}

		@Override
		protected void onContentsChanged(int slot) {
			MnemonicInscriberBlockEntity.this.setChanged();
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public MnemonicInscriberUpgrade upgrade;

	public MnemonicInscriberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), pPos, pBlockState);
		upgrade = new MnemonicInscriberUpgrade(this);
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
		saveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));
		com.rekindled.embers.util.NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
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

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		// holder.invalidate() removed
		upgrade.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.ITEM_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.ITEM_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.paper")));
	}
}
