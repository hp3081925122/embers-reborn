package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Exposes the mod's existing IItemHandler / IFluidHandler implementations as 26.1 ResourceHandlers.
 * Modifiable handlers (ItemStackHandler, FluidTank) change immediately and are reverted from a snapshot on abort.
 * Other handlers queue the operation and run it when the outermost transaction commits.
 */
@SuppressWarnings("removal")
public final class LegacyHandlers {
	private LegacyHandlers() {}

	public static ResourceHandler<ItemResource> items(IItemHandler handler) {
		return handler == null ? null : new Items(handler);
	}

	public static ResourceHandler<FluidResource> fluids(IFluidHandler handler) {
		return handler == null ? null : new Fluids(handler);
	}

	private static final class Items extends SnapshotJournal<Object> implements ResourceHandler<ItemResource> {
		private final IItemHandler handler;
		private final List<Runnable> pending = new ArrayList<>();

		Items(IItemHandler handler) {
			this.handler = handler;
		}

		@Override
		public int size() {
			return handler.getSlots();
		}

		@Override
		public ItemResource getResource(int index) {
			return ItemResource.of(handler.getStackInSlot(index));
		}

		@Override
		public long getAmountAsLong(int index) {
			return handler.getStackInSlot(index).getCount();
		}

		@Override
		public long getCapacityAsLong(int index, ItemResource resource) {
			return resource.isEmpty() ? handler.getSlotLimit(index) : Math.min(handler.getSlotLimit(index), resource.toStack().getMaxStackSize());
		}

		@Override
		public boolean isValid(int index, ItemResource resource) {
			return handler.isItemValid(index, resource.toStack());
		}

		@Override
		public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if (resource.isEmpty() || amount <= 0)
				return 0;
			ItemStack stack = resource.toStack(amount);
			int accepted = amount - handler.insertItem(index, stack, true).getCount();
			if (accepted <= 0)
				return 0;
			updateSnapshots(transaction);
			if (handler instanceof IItemHandlerModifiable) {
				handler.insertItem(index, resource.toStack(accepted), false);
			} else {
				pending.add(() -> handler.insertItem(index, resource.toStack(accepted), false));
			}
			return accepted;
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			if (resource.isEmpty() || amount <= 0 || !resource.matches(handler.getStackInSlot(index)))
				return 0;
			int extracted = handler.extractItem(index, amount, true).getCount();
			if (extracted <= 0)
				return 0;
			updateSnapshots(transaction);
			if (handler instanceof IItemHandlerModifiable) {
				handler.extractItem(index, extracted, false);
			} else {
				pending.add(() -> handler.extractItem(index, extracted, false));
			}
			return extracted;
		}

		@Override
		protected Object createSnapshot() {
			if (handler instanceof IItemHandlerModifiable) {
				ItemStack[] copy = new ItemStack[handler.getSlots()];
				for (int i = 0; i < copy.length; i++)
					copy[i] = handler.getStackInSlot(i).copy();
				return copy;
			}
			return pending.size();
		}

		@Override
		protected void revertToSnapshot(Object snapshot) {
			if (snapshot instanceof ItemStack[] copy && handler instanceof IItemHandlerModifiable modifiable) {
				for (int i = 0; i < copy.length && i < modifiable.getSlots(); i++)
					modifiable.setStackInSlot(i, copy[i]);
			} else if (snapshot instanceof Integer size) {
				while (pending.size() > size)
					pending.remove(pending.size() - 1);
			}
		}

		@Override
		protected void onRootCommit(Object originalState) {
			List<Runnable> run = new ArrayList<>(pending);
			pending.clear();
			run.forEach(Runnable::run);
		}
	}

	private static final class Fluids extends SnapshotJournal<Object> implements ResourceHandler<FluidResource> {
		private final IFluidHandler handler;
		private final List<Runnable> pending = new ArrayList<>();

		Fluids(IFluidHandler handler) {
			this.handler = handler;
		}

		@Override
		public int size() {
			return handler.getTanks();
		}

		@Override
		public FluidResource getResource(int index) {
			return FluidResource.of(handler.getFluidInTank(index));
		}

		@Override
		public long getAmountAsLong(int index) {
			return handler.getFluidInTank(index).getAmount();
		}

		@Override
		public long getCapacityAsLong(int index, FluidResource resource) {
			return handler.getTankCapacity(index);
		}

		@Override
		public boolean isValid(int index, FluidResource resource) {
			return handler.isFluidValid(index, resource.toStack(1));
		}

		@Override
		public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if (resource.isEmpty() || amount <= 0)
				return 0;
			int accepted = handler.fill(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
			if (accepted <= 0)
				return 0;
			updateSnapshots(transaction);
			if (handler instanceof FluidTank) {
				handler.fill(resource.toStack(accepted), IFluidHandler.FluidAction.EXECUTE);
			} else {
				pending.add(() -> handler.fill(resource.toStack(accepted), IFluidHandler.FluidAction.EXECUTE));
			}
			return accepted;
		}

		@Override
		public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
			if (resource.isEmpty() || amount <= 0)
				return 0;
			int extracted = handler.drain(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE).getAmount();
			if (extracted <= 0)
				return 0;
			updateSnapshots(transaction);
			if (handler instanceof FluidTank) {
				handler.drain(resource.toStack(extracted), IFluidHandler.FluidAction.EXECUTE);
			} else {
				pending.add(() -> handler.drain(resource.toStack(extracted), IFluidHandler.FluidAction.EXECUTE));
			}
			return extracted;
		}

		@Override
		protected Object createSnapshot() {
			if (handler instanceof FluidTank tank)
				return tank.getFluid().copy();
			return pending.size();
		}

		@Override
		protected void revertToSnapshot(Object snapshot) {
			if (snapshot instanceof FluidStack fluid && handler instanceof FluidTank tank) {
				tank.setFluid(fluid);
			} else if (snapshot instanceof Integer size) {
				while (pending.size() > size)
					pending.remove(pending.size() - 1);
			}
		}

		@Override
		protected void onRootCommit(Object originalState) {
			List<Runnable> run = new ArrayList<>(pending);
			pending.clear();
			run.forEach(Runnable::run);
		}
	}
}
