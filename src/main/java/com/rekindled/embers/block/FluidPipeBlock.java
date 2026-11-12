package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.FluidPipeBlockEntity;
import com.rekindled.embers.blockentity.FluidPipeBlockEntityBase;
import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidPipeBlock extends PipeBlockBase {

	public FluidPipeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean connected(Direction direction, BlockState state) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.FLUID_PIPE_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide() ? createTickerHelper(pBlockEntityType, RegistryManager.FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::serverTick);
	}

	@Override
	public TagKey<Block> getConnectionTag() {
		return EmbersBlockTags.FLUID_PIPE_CONNECTION;
	}

	@Override
	public TagKey<Block> getToggleConnectionTag() {
		return EmbersBlockTags.FLUID_PIPE_CONNECTION_TOGGLEABLE;
	}

	@Override
	public boolean connectToTile(BlockEntity blockEntity, Direction direction) {
		return blockEntity != null && blockEntity.getLevel() != null && blockEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), direction.getOpposite()) != null;
	}

	@Override
	public boolean unclog(BlockEntity blockEntity, Level level, BlockPos pos) {
		if (blockEntity instanceof FluidPipeBlockEntityBase pipeEntity && pipeEntity.clogged) {
			pipeEntity.resetFrom();
			pipeEntity.lastTransfer = null;
			pipeEntity.syncTransfer = true;
			IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), null);
			handler.drain(handler.getTankCapacity(0), IFluidHandler.FluidAction.EXECUTE);
			level.updateNeighbourForOutputSignal(pos, this);
			return true;
		}
		return false;
	}
}
