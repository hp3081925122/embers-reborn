package com.rekindled.embers.item;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberItemCapability;
import com.rekindled.embers.util.DecimalFormats;

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
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class CopperCellBlockItem extends BlockItem {

	public CopperCellBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null) != null;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap != null) {
			return Math.round(13.0F - (float) (cap.getEmberCapacity()-cap.getEmber()) * 13.0F / (float) cap.getEmberCapacity());
		}
		return 0;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFF6600;
	}

	/* initCapabilities removed - use RegisterCapabilitiesEvent */

	public static ItemStack getCharged() {
		ItemStack chargedCell = new ItemStack(RegistryManager.COPPER_CELL_ITEM.get());
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(chargedCell, null);
		if (cap != null) cap.setEmber(cap.getEmberCapacity());
		return chargedCell;
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(pStack, null);
		if (cap != null) {
			com.rekindled.embers.util.ItemNbtHelper.updateTagElement(pStack, "BlockEntityTag", cap::writeToNBT);
		}
		return BlockItem.updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		IEmberCapability cap = com.rekindled.embers.api.capabilities.EmbersCapabilities.EMBER_ITEM_CAPABILITY.getCapability(stack, null);
		if (cap != null) {
			DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item.ember", emberFormat.format(cap.getEmber()),  emberFormat.format(cap.getEmberCapacity())).withStyle(ChatFormatting.GRAY));
		}
	}
}
