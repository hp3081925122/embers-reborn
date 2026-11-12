package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReservoirEdgeBlock extends MechEdgeBlockBase {

	public ReservoirEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.block.Block> codec() {
		return simpleCodec(ReservoirEdgeBlock::new);
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.RESERVOIR.get();
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}
}
