package com.rekindled.embers.item;

import com.rekindled.embers.gui.SlateMenu;
import com.rekindled.embers.util.ItemNbtHelper;
import com.rekindled.embers.util.Misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CodebreakingSlateItem extends Item implements MenuProvider {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public CodebreakingSlateItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (!level.isClientSide) {
			ItemStack slate = player.getItemInHand(hand);
			((ServerPlayer) player).openMenu(this);

			//give players their waste back if they have a legacy slate
			net.minecraft.core.HolderLookup.Provider registries = player.registryAccess();
			ItemStackHandler inventory = new ItemStackHandler(7) {
				public void onContentsChanged(int slot) {
					ItemNbtHelper.setTagElement(slate, "inventory", this.serializeNBT(registries));
				}
			};
			net.minecraft.nbt.CompoundTag nbt = ItemNbtHelper.getTagElement(slate, "inventory");
			if (!nbt.isEmpty()) inventory.deserializeNBT(registries, nbt);
			if (inventory.getSlots() > 6) {
				Misc.giveItemToPlayer(inventory.getStackInSlot(5), player);
				inventory.setStackInSlot(5, ItemStack.EMPTY);
				Misc.giveItemToPlayer(inventory.getStackInSlot(6), player);
				inventory.setStackInSlot(6, ItemStack.EMPTY);
			}
		}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
		ItemStack heldItem = player.getMainHandItem();
		if (!(heldItem.getItem() instanceof CodebreakingSlateItem))
			heldItem = player.getOffhandItem();
		return new SlateMenu(id, inv, heldItem);
	}

	@Override
	public Component getDisplayName() {
		return getDescription();
	}
}
