package com.rekindled.embers.blockentity;

import java.util.ArrayList;

import com.rekindled.embers.api.tile.IFluidPipePriority;
import com.rekindled.embers.util.PipePriorityMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public abstract class FluidPipeBlockEntityBase extends PipeBlockEntityBase implements IFluidPipePriority {

	public static final int MAX_PUSH = 120;

	public FluidTank tank;
	public IFluidHandler holder = tank;

	public FluidPipeBlockEntityBase(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		initFluidTank();
	}

	protected void initFluidTank() {
		tank = new FluidTank(getCapacity()) {
			@Override
			protected void onContentsChanged() {
				FluidPipeBlockEntityBase.this.setChanged();
			}
		};
	}

	public abstract int getCapacity();

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_PIPE;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntityBase blockEntity) {
		if (!blockEntity.loaded)
			blockEntity.initConnections();
		blockEntity.ticksExisted++;
		boolean fluidMoved = false;
		FluidStack passStack = blockEntity.tank.drain(MAX_PUSH, IFluidHandler.FluidAction.SIMULATE);
		if (!passStack.isEmpty()) {
			PipePriorityMap<Integer, Direction> possibleDirections = new PipePriorityMap<>();
			IFluidHandler[] fluidHandlers = new IFluidHandler[Direction.values().length];

			for (Direction facing : Direction.values()) {
				if (!blockEntity.getConnection(facing).transfer)
					continue;
				if (blockEntity.isFrom(facing))
					continue;
				BlockEntity tile = level.getBlockEntity(pos.relative(facing));
				if (tile != null) {
					IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, tile.getBlockPos(), facing.getOpposite());
					if (handler != null) {
						int priority = PRIORITY_BLOCK;
						if (tile instanceof IFluidPipePriority)
							priority = ((IFluidPipePriority) tile).getPriority(facing.getOpposite());
						if (blockEntity.isFrom(facing.getOpposite()))
							priority -= 5; //aka always try opposite first
						possibleDirections.put(priority, facing);
						fluidHandlers[facing.get3DDataValue()] = handler;
					}
				}
			}

			for (int key : possibleDirections.keySet()) {
				ArrayList<Direction> list = possibleDirections.get(key);
				for (int i = 0; i < list.size(); i++) {
					Direction facing = list.get((i + blockEntity.lastRobin) % list.size());
					IFluidHandler handler = fluidHandlers[facing.get3DDataValue()];
					fluidMoved = blockEntity.pushStack(passStack, facing, handler);
					if (blockEntity.lastTransfer != facing) {
						blockEntity.lastTransfer = facing;
						blockEntity.syncTransfer = true;
						blockEntity.setChanged();
					}
					if (fluidMoved) {
						blockEntity.lastRobin++;
						break;
					}
				}
				if (fluidMoved)
					break;
			}
		}

		//if (fluidMoved)
		//    resetFrom();
		if (blockEntity.tank.getFluidAmount() <= 0) {
			if (blockEntity.lastTransfer != null && !fluidMoved) {
				blockEntity.lastTransfer = null;
				blockEntity.syncTransfer = true;
				blockEntity.setChanged();
			}
			fluidMoved = true;
			blockEntity.resetFrom();
		}
		if (blockEntity.clogged == fluidMoved) {
			blockEntity.clogged = !fluidMoved;
			blockEntity.syncCloggedFlag = true;
			blockEntity.setChanged();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientTick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntityBase blockEntity) {
		PipeBlockEntityBase.clientTick(level, pos, state, blockEntity);
	}

	private boolean pushStack(FluidStack passStack, Direction facing, IFluidHandler handler) {
		int added = handler.fill(passStack, IFluidHandler.FluidAction.SIMULATE);
		if (added > 0) {
			handler.fill(passStack, IFluidHandler.FluidAction.EXECUTE);
			this.tank.drain(added, IFluidHandler.FluidAction.EXECUTE);
			passStack.setAmount(passStack.getAmount() - added);
			return passStack.getAmount() <= 0;
		}

		if (isFrom(facing))
			setFrom(facing, false);
		return false;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		if (nbt.contains("tank"))
			tank.readFromNBT(provider, nbt.getCompound("tank"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		writeTank(nbt);
	}

	public void writeTank(CompoundTag nbt) {
		nbt.put("tank", tank.writeToNBT(net.minecraft.core.RegistryAccess.EMPTY, new CompoundTag()));
	}

	/* getCapabilityLEGACY removed */

	@Override
	public void invalidateCapabilities() { super.invalidateCapabilities(); }
}
