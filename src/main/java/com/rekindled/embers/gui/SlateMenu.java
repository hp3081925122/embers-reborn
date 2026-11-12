package com.rekindled.embers.gui;

import java.util.ArrayList;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.item.AlchemyHintItem;
import com.rekindled.embers.util.ItemNbtHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SlateMenu extends AbstractContainerMenu {
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



	public static int slateHeight = 158;
	public static int slateWidth = 192;
	public static int heightMargin = 12;
	public static int widthMargin = 15;
	public static int invHeight = 100;
	public static int invWidth = 176;
	public static int slotIndent = -28;
	public static int slotWidth = 26;
	public static int slotHeight = 22;
	public static int slotCount = 5;
	public static int layerHeight = (slateHeight - slotHeight - (2 * heightMargin)) / slotCount;
	public static int layerWidth = slateWidth - slotWidth - (2 * (widthMargin));

	public ItemStackHandler inventory;
	public ItemStack slate;

	public SlateMenu(int id, Inventory inv, ItemStack slate) {
		super(RegistryManager.SLATE_MENU.get(), id);
		this.slate = slate;
		net.minecraft.core.HolderLookup.Provider registries = inv.player.registryAccess();
		boolean[] loading = { true };
		inventory = new ItemStackHandler(slotCount) {
			public void onContentsChanged(int slot) {
				if (loading[0])
					return;
				ItemNbtHelper.setTagElement(SlateMenu.this.slate, "inventory", this.serializeNBT(registries));
			}
		};
		CompoundTag nbt = ItemNbtHelper.getTagElement(slate, "inventory");
		if (!nbt.isEmpty())
			inventory.deserializeNBT(registries, nbt);
		loading[0] = false;

		//slate inventory
		Slot master = this.addSlot(new SlotItemHandler(inventory, 0, slotIndent, heightMargin + layerHeight / 2 + 16));
		for (int j = 1; j < slotCount; ++j) {
			this.addSlot(new SlateSlot(inventory, j, slotIndent, heightMargin + j * layerHeight + layerHeight / 2 + 16, master));
		}
		//player inventory
		for (int l = 0; l < 3; ++l) {
			for (int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(inv, k + l * 9 + 9, 8 + k * 18, l * 18 + (slateHeight + 18)));
			}
		}
		//hotbar
		for (int i = 0; i < 9; ++i) {
			if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(inv.getItem(i), slate)) {
				this.addSlot(new LockedSlot(inv, i, 8 + i * 18, slateHeight + 76));
			} else {
				this.addSlot(new Slot(inv, i, 8 + i * 18, slateHeight + 76));
			}
		}
	}

	public boolean stillValid(Player player) {
		return player.getMainHandItem() == slate || player.getOffhandItem() == slate;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < inventory.getSlots()) {
				if (!this.moveItemStackTo(itemstack1, inventory.getSlots(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, inventory.getSlots(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	public void removed(Player p_39649_) {
		super.removed(p_39649_);
	}

	public static SlateMenu fromBuffer(int id, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
		ItemStack slate = buf != null ? net.minecraft.world.item.ItemStack.STREAM_CODEC.decode(buf) : inv.player.getMainHandItem();
		if (slate.isEmpty() || !(slate.getItem() instanceof com.rekindled.embers.item.CodebreakingSlateItem)) {
			slate = inv.player.getOffhandItem();
		}
		return new SlateMenu(id, inv, slate);
	}

	public static class SlateSlot extends SlotItemHandler {

		public Slot master;

		public SlateSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Slot master) {
			super(itemHandler, index, xPosition, yPosition);
			this.master = master;
		}

		public boolean mayPlace(ItemStack stack) {
			ItemStack masterItem = master.getItem();
			if (masterItem.isEmpty())
				return false;
			ArrayList<ItemStack> masterInputs = AlchemyHintItem.getInputs(masterItem);
			ArrayList<ItemStack> inputs = AlchemyHintItem.getInputs(stack);
			if (masterInputs.size() != inputs.size())
				return false;
			for (int i = 0; i < masterInputs.size(); ++i) {
				if (!ItemStack.isSameItem(masterInputs.get(i), inputs.get(i)))
					return false;
			}
			return true;
		}
	}

	public static class LockedSlot extends Slot {

		public LockedSlot(Container container, int id, int x, int y) {
			super(container, id, x, y);
		}

		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		public boolean mayPickup(Player player) {
			return false;
		}

		public boolean allowModification(Player player) {
			return false;
		}
	}
}
