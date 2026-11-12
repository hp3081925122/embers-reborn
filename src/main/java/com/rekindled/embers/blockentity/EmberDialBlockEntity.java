package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IDialEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EmberDialBlockEntity extends BlockEntity implements IDialEntity {

	public double ember = 0;
	public double capacity = 0;
	public boolean display = false;

	public EmberDialBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_DIAL_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		if (nbt.contains(IEmberCapability.EMBER))
			ember = nbt.getDouble(IEmberCapability.EMBER);
		if (nbt.contains(IEmberCapability.EMBER_CAPACITY))
			capacity = nbt.getDouble(IEmberCapability.EMBER_CAPACITY);
		if (nbt.contains("display"))
			display = nbt.getBoolean("display");
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		BlockState state = level.getBlockState(worldPosition);
		boolean display = false;
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(facing, -1));
			if (blockEntity != null) {
				IEmberCapability cap = (blockEntity.getLevel() != null ? blockEntity.getLevel().getCapability(EmbersCapabilities.EMBER_CAPABILITY, blockEntity.getBlockPos(), facing.getOpposite()) : null);
				if (cap != null) {
					nbt.putDouble(IEmberCapability.EMBER, cap.getEmber());
					nbt.putDouble(IEmberCapability.EMBER_CAPACITY, cap.getEmberCapacity());
					display = true;
				}
			}
		}
		nbt.putBoolean("display", display);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket(int maxLines) {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
