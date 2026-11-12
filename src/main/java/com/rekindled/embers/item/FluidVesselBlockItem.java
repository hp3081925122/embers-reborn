package com.rekindled.embers.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.block.FluidDialBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidVesselBlockItem extends BlockItem {

	public FluidVesselBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		net.neoforged.neoforge.fluids.SimpleFluidContent content = stack.get(com.rekindled.embers.RegistryManager.FLUID_CONTENT);
		if (content != null && !content.isEmpty()) {
			return 1;
		}
		return super.getMaxStackSize(stack);
	}

	/* initCapabilities removed - use RegisterCapabilitiesEvent */

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
		boolean changed = BlockItem.updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
		// copy the item's fluid into the placed vessel
		net.neoforged.neoforge.fluids.SimpleFluidContent content = pStack.get(com.rekindled.embers.RegistryManager.FLUID_CONTENT);
		if (content != null && !content.isEmpty() && !pLevel.isClientSide()
				&& pLevel.getBlockEntity(pPos) instanceof com.rekindled.embers.blockentity.FluidVesselBlockEntity vessel) {
			vessel.getTank().setFluid(content.copy());
			vessel.setChanged();
			changed = true;
		}
		return changed;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		IFluidHandler cap = net.neoforged.neoforge.fluids.FluidUtil.getFluidHandler(stack).orElse(null);
		if (cap != null) {
			tooltip.add(FluidDialBlock.formatFluidStack(cap.getFluidInTank(0), cap.getTankCapacity(0)).withStyle(ChatFormatting.GRAY));
		}
	}
}
