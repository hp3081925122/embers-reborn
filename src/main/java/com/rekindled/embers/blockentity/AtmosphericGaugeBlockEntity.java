package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AtmosphericGaugeBlockEntity extends BlockEntity {

	int power = 0;

	public AtmosphericGaugeBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ATMOSPHERIC_GAUGE_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		power = nbt.getIntOr("power", 0);
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
		saveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));
		com.rekindled.embers.util.NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		nbt.putInt("power", power);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AtmosphericGaugeBlockEntity blockEntity) {
		if (blockEntity.power != state.getAnalogOutputSignal(level, pos, net.minecraft.core.Direction.UP)) {
			level.updateNeighbourForOutputSignal(pos, state.getBlock());
			blockEntity.power = state.getAnalogOutputSignal(level, pos, net.minecraft.core.Direction.UP);
			blockEntity.setChanged();
		}
	}
}
