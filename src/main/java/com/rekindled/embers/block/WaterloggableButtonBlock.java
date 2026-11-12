package com.rekindled.embers.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class WaterloggableButtonBlock extends ButtonBlock {

	public WaterloggableButtonBlock(BlockSetType type, int ticksToStayPressed, Properties properties) {
		super(type, ticksToStayPressed, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
		return state == null ? null : state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	protected BlockState updateShape(BlockState pState, net.minecraft.world.level.LevelReader pLevel, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos pCurrentPos, Direction pFacing, BlockPos pFacingPos, BlockState pFacingState, net.minecraft.util.RandomSource random) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			ticks.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pLevel, ticks, pCurrentPos, pFacing, pFacingPos, pFacingState, random);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
