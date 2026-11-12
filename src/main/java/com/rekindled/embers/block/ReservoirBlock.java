package com.rekindled.embers.block;

import com.rekindled.embers.util.LegacyCaps;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.MechEdgeBlockBase.MechEdge;
import com.rekindled.embers.blockentity.ReservoirBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class ReservoirBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
	@Override
	public com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.BaseEntityBlock> codec() {
		return simpleCodec(ReservoirBlock::new);
	}


	protected static final VoxelShape RESERVOIR_AABB = Shapes.or(Block.box(0,2,0,16,16,16),Block.box(4,1,4,12,2,12),Block.box(6,0,6,10,1,10));

	public ReservoirBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return RESERVOIR_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ReservoirBlockEntity reservoirEntity) {
		InteractionHand hand = InteractionHand.MAIN_HAND;
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty()) {
				IFluidHandler cap = LegacyCaps.fluid(level, reservoirEntity.getBlockPos(), hit.getDirection());
				if (cap != null) {
					boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, cap);

					if (didFill) {
						return InteractionResult.SUCCESS;
					}
				}
				//prevent buckets from placing their fluid in the world when clicking on the vessel
				if (net.neoforged.neoforge.fluids.FluidUtil.getFluidHandler(heldItem).isPresent()) {
					return InteractionResult.CONSUME_PARTIAL;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			for (MechEdge edge : MechEdge.values()) {
				BlockPos cornerPos = pos.subtract(edge.centerPos);
				if (level.getBlockState(cornerPos).getBlock() instanceof MechEdgeBlockBase) {
					level.destroyBlock(cornerPos, false);
				}
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		boolean clear = true;
		for (MechEdge edge : MechEdge.values()) {
			if (!pContext.getLevel().getBlockState(pContext.getClickedPos().subtract(edge.centerPos)).canBeReplaced(pContext))
				clear = false;
		}
		return clear ? super.getStateForPlacement(pContext).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER)) : null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		for (MechEdge edge : MechEdge.values()) {
			BlockState edgeState = RegistryManager.RESERVOIR_EDGE.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(level.getFluidState(pos.subtract(edge.centerPos)).getType() == Fluids.WATER));
			level.setBlock(pos.subtract(edge.centerPos), edgeState.setValue(MechEdgeBlockBase.EDGE, edge), UPDATE_ALL);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.RESERVOIR_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide() ? createTickerHelper(pBlockEntityType, RegistryManager.RESERVOIR_ENTITY.get(), ReservoirBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.RESERVOIR_ENTITY.get(), ReservoirBlockEntity::commonTick);
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
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
