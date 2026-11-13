package com.rekindled.embers.item;

import com.rekindled.embers.block.MechEdgeBlockBase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CaminiteRingBlockItem extends BlockItem{

	public CaminiteRingBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult interactionresult = null;
		if (context.getClickedFace() == Direction.UP) {
			BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
			if (clickedState.hasProperty(MechEdgeBlockBase.EDGE)) {
				BlockPos pos = context.getClickedPos().offset(clickedState.getValue(MechEdgeBlockBase.EDGE).centerPos);
				interactionresult = this.place(new BlockPlaceContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos), context.getClickedFace(), pos, false)));
			}
		}
		if (interactionresult == null) {
			interactionresult = this.place(new BlockPlaceContext(context));
		}
		if (!interactionresult.consumesAction() && context.getItemInHand().has(net.minecraft.core.component.DataComponents.FOOD)) {
			InteractionResult interactionresult1 = this.use(context.getLevel(), context.getPlayer(), context.getHand());
			return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME : interactionresult1;
		} else {
			return interactionresult;
		}
	}
}
