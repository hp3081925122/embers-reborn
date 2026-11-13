package com.rekindled.embers.blockentity;

import java.util.Random;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.filter.FilterItem;
import com.rekindled.embers.api.filter.IFilter;
import com.rekindled.embers.api.item.IFilterItem;
import com.rekindled.embers.particle.VaporParticleOptions;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.FilterUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class ItemTransferBlockEntity extends ItemPipeBlockEntityBase {

	public static final int PRIORITY_TRANSFER = -10;
	public ItemStack filterItem = ItemStack.EMPTY;
	Random random = new Random();
	public boolean syncFilter = true;
	IItemHandler outputSide;
	public IItemHandler outputHolder = outputSide;

	IFilter filter = FilterUtil.FILTER_ANY;

	public ItemTransferBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ITEM_TRANSFER_ENTITY.get(), pPos, pBlockState);
		syncConnections = false;
		saveConnections = false;
	}

	@Override
	protected void initInventory() {
		inventory = new ItemStackHandler(1) {
			@Override
			public int getSlotLimit(int slot) {
				return ItemTransferBlockEntity.this.getCapacity();
			}

			@Override
			protected void onContentsChanged(int slot) {
				ItemTransferBlockEntity.this.setChanged();
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (ItemTransferBlockEntity.this.acceptsItem(stack))
					return super.insertItem(slot, stack, simulate);
				else
					return stack;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return ItemTransferBlockEntity.this.acceptsItem(stack);
			}
		};
		outputSide = Misc.makeRestrictedItemHandler(inventory, false, true);
	}

	public boolean acceptsItem(ItemStack stack) {
		return filter.acceptsItem(stack);
	}

	@Override
	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditionalNbt(nbt, provider);
		if (nbt.contains("filter")) {
			filterItem = com.rekindled.embers.util.NbtBridge.parse(provider, nbt.getCompoundOrEmpty("filter")).orElse(net.minecraft.world.item.ItemStack.EMPTY);
		}
		setupFilter();
	}

	@Override
	protected boolean requiresSync() {
		return syncFilter || super.requiresSync();
	}

	@Override
	protected void resetSync() {
		super.resetSync();
		syncFilter = false;
	}

	@Override
	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditionalNbt(nbt, provider);
		writeFilter(nbt, provider);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		if (syncFilter)
			writeFilter(nbt, provider);
		return nbt;
	}

	private void writeFilter(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		// ItemStack.save throws on an empty stack (no filter set); saveOptional writes an empty tag instead
		nbt.put("filter", com.rekindled.embers.util.NbtBridge.saveOptional(filterItem, provider));
	}

	public void setupFilter() {
		Item item = this.filterItem.getItem();
		if (item instanceof IFilterItem)
			filter = ((IFilterItem) item).getFilter(this.filterItem);
		else if(!this.filterItem.isEmpty())
			filter = new FilterItem(this.filterItem);
		else
			filter = FilterUtil.FILTER_ANY;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ItemTransferBlockEntity blockEntity) {
		if (level instanceof ServerLevel && blockEntity.clogged && blockEntity.isAnySideUnclogged()) {
			Random posRand = new Random(pos.asLong());
			double angleA = posRand.nextDouble() * Math.PI * 2;
			double angleB = posRand.nextDouble() * Math.PI * 2;
			float xOffset = (float) (Math.cos(angleA) * Math.cos(angleB));
			float yOffset = (float) (Math.sin(angleA) * Math.cos(angleB));
			float zOffset = (float) Math.sin(angleB);
			float speed = 0.1f;
			float vx = xOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vy = yOffset * speed + posRand.nextFloat() * speed * 0.3f;
			float vz = zOffset * speed + posRand.nextFloat() * speed * 0.3f;
			((ServerLevel) level).sendParticles(new VaporParticleOptions(EmbersColors.VAPOR_ID, new Vec3(vx, vy, vz), 1.0f), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4, 0, 0, 0, 1.0);
		}
		ItemPipeBlockEntityBase.serverTick(level, pos, state, blockEntity);
	}

	/* getCapabilityLEGACY removed */

	@Override
	public int getCapacity() {
		return 4;
	}

	@Override
	public int getPriority(Direction facing) {
		return PRIORITY_TRANSFER;
	}

	@Override
	public PipeConnection getConnection(Direction facing) {
		BlockState state = level.getBlockState(this.getBlockPos());
		return state.hasProperty(BlockStateProperties.FACING) && state.getValue(BlockStateProperties.FACING).getAxis() == facing.getAxis() ? PipeConnection.PIPE : PipeConnection.NONE;
	}

	@Override
	protected boolean isFrom(Direction facing) {
		return level.getBlockState(this.getBlockPos()).getValue(BlockStateProperties.FACING) == facing;
	}

	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		// outputHolder.invalidate() removed
	}
}
