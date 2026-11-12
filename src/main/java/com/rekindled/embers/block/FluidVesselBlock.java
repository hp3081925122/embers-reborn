package com.rekindled.embers.block;

import com.rekindled.embers.util.LegacyCaps;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.FluidVesselBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidVesselBlock extends AbstractCauldronBlock implements EntityBlock, SimpleWaterloggedBlock {
	@Override
	public com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.AbstractCauldronBlock> codec() {
		return simpleCodec(FluidVesselBlock::new);
	}


	protected static final VoxelShape VESSEL_AABB = Shapes.or(Block.box(0,0,0,4,16,4),Block.box(12,0,0,16,16,4),Block.box(12,0,12,16,16,16),Block.box(0,0,12,4,16,16),Block.box(4,0,12,12,16,14),Block.box(4,0,2,12,16,4),Block.box(2,0,4,4,16,12),Block.box(12,0,4,14,16,12),Block.box(4,0,4,12,2,12),Block.box(6,6,0,10,10,2),Block.box(6,6,14,10,10,16),Block.box(14,6,6,16,10,10),Block.box(0,6,6,2,10,10));

	public FluidVesselBlock(Properties properties) {
		super(properties, net.minecraft.core.cauldron.CauldronInteraction.EMPTY);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public net.minecraft.world.InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//handled here instead of falling through to AbstractCauldronBlock, which would turn the vessel into a vanilla cauldron
		if (level.getBlockEntity(pos) instanceof FluidVesselBlockEntity vesselEntity && !heldItem.isEmpty()) {
			IFluidHandler cap = LegacyCaps.fluid(level, vesselEntity.getBlockPos(), hit.getDirection());
			if (cap != null && FluidUtil.interactWithFluidHandler(player, hand, cap)) {
				return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide());
			}
			//prevent buckets from placing their fluid in the world when clicking on the vessel
			if (FluidUtil.getFluidHandler(heldItem).isPresent()) {
				return net.minecraft.world.InteractionResult.CONSUME;
			}
		}
		return net.minecraft.world.InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return VESSEL_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public boolean isEntityInsideContent(BlockState state, BlockPos pos, Entity entity) {
		return false;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return false;
	}

	@Override
	public boolean isFull(BlockState state) {
		return false;
	}

	@Override
	public boolean canReceiveStalactiteDrip(Fluid fluid) {
		return true;
	}

	@Override
	public void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
		if (level.getBlockEntity(pos) instanceof FluidVesselBlockEntity vesselEntity) {
			IFluidHandler cap = LegacyCaps.fluid(level, vesselEntity.getBlockPos(), Direction.UP);
			if (cap != null) {
				int amount = 333;
				if (fluid == Fluids.LAVA)
					amount = FluidType.BUCKET_VOLUME;

				cap.fill(new FluidStack(fluid, amount), net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);

				if (fluid.getFluidType().getTemperature() > 500) {
					level.levelEvent(LevelEvent.SOUND_DRIP_LAVA_INTO_CAULDRON, pos, 0);
				} else {
					level.levelEvent(LevelEvent.SOUND_DRIP_WATER_INTO_CAULDRON, pos, 0);
				}
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.FLUID_VESSEL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (!pLevel.isClientSide() || pBlockEntityType != RegistryManager.FLUID_VESSEL_ENTITY.get()) return null;
		@SuppressWarnings("unchecked") BlockEntityTicker<T> _t = (BlockEntityTicker<T>) (BlockEntityTicker<net.minecraft.world.level.block.entity.BlockEntity>) (level, pos, state, be) -> FluidVesselBlockEntity.clientTick(level, pos, state, (FluidVesselBlockEntity) be); return _t;
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
		List<ItemStack> items = super.getDrops(pState, pBuilder);
		BlockEntity blockentity = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof FluidVesselBlockEntity) {
			net.neoforged.neoforge.fluids.FluidStack fluid = ((FluidVesselBlockEntity) blockentity).getTank().getFluid();
			if (!fluid.isEmpty()) {
				for (ItemStack stack : items) {
					if (stack.getItem() == RegistryManager.FLUID_VESSEL_ITEM.get()) {
						// keep the vessel's fluid on the dropped item, as in 1.20.1
						stack.set(RegistryManager.FLUID_CONTENT, net.neoforged.neoforge.fluids.SimpleFluidContent.copyOf(fluid));
					}
				}
			}
		}
		return items;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
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
