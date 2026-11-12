package com.rekindled.embers.blockentity;

import java.util.List;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IBin;
import com.rekindled.embers.api.tile.IHammerable;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.recipe.IDawnstoneAnvilRecipe;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;


public class DawnstoneAnvilBlockEntity extends BlockEntity implements IHammerable {

	int progress = 0;
	public ItemStackHandler inventory = new ItemStackHandler(2) {
		@Override
		protected void onContentsChanged(int slot) {
			DawnstoneAnvilBlockEntity.this.setChanged();
		}

		@Override
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)
	static Random random = new Random();
	public IDawnstoneAnvilRecipe cachedRecipe = null;

	public DawnstoneAnvilBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.DAWNSTONE_ANVIL_ENTITY.get(), pPos, pBlockState);
	}

	public AABB getRenderBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(0, 0, 0)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(1, 2, 1)));
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		progress = nbt.getInt("progress");
		inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.putInt("progress", progress);
		nbt.put("inventory", inventory.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putInt("progress", progress);
		nbt.put("inventory", inventory.serializeNBT(provider));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		// holder.invalidate() removed
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	public boolean onHit() {
		net.minecraft.world.item.crafting.RecipeInput context = new net.minecraft.world.item.crafting.RecipeInput() {
		@Override public net.minecraft.world.item.ItemStack getItem(int slot) { return inventory.getStackInSlot(slot); }
		@Override public int size() { return inventory.getSlots(); }
		};
		cachedRecipe = Misc.getRecipe(cachedRecipe, RegistryManager.DAWNSTONE_ANVIL_RECIPE.get(), context, level);
		if (cachedRecipe != null) {
			progress += 1;
			level.playSound(null, worldPosition, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.25f, 2.0f+random.nextFloat());
			if (progress > ConfigManager.DAWNSTONE_ANVIL_MAX_HITS.get()) {
				progress = 0;
				List<ItemStack> results = cachedRecipe.getOutput(context);
				for (ItemStack result : results) {
					BlockEntity bin = level.getBlockEntity(worldPosition.below());
					if (bin instanceof IBin) {
						ItemStack remainder = ((IBin) bin).getInventory().insertItem(0, result.copy(), false);
						if (!remainder.isEmpty() && !level.isClientSide()) {
							level.addFreshEntity(new ItemEntity(level, worldPosition.getX()+0.5,worldPosition.getY()+1.0625f,worldPosition.getZ()+0.5, remainder));
						}
					} else if (!level.isClientSide()) {
						level.addFreshEntity(new ItemEntity(level, worldPosition.getX()+0.5,worldPosition.getY()+1.0625f,worldPosition.getZ()+0.5, result));
					}
				}
				//the recipe is not responsible for removing items from the inventory
				inventory.setStackInSlot(0, ItemStack.EMPTY);
				inventory.setStackInSlot(1, ItemStack.EMPTY);

				if (level instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(new SparkParticleOptions(EmbersColors.EMBER_ID, 1.0f), worldPosition.getX() + 0.5f, worldPosition.getY() + 1.0625f, worldPosition.getZ() + 0.5f, 10, 0.1, 0.0, 0.1, 1.0);
					serverLevel.sendParticles(new SmokeParticleOptions(EmbersColors.SMOKE_ID, 3.0f), worldPosition.getX() + 0.5f, worldPosition.getY() + 1.0625f, worldPosition.getZ() + 0.5f, 10, 0.1, 0.0, 0.1, 1.0);
				}
				level.playSound(null, worldPosition, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 0.95f+random.nextFloat()*0.1f);
			} else if (level instanceof ServerLevel serverLevel) {
				setChanged();
				serverLevel.sendParticles(new SparkParticleOptions(EmbersColors.EMBER_ID, 1.0f), worldPosition.getX() + 0.5f, worldPosition.getY() + 1.0625f, worldPosition.getZ() + 0.5f, 1, 0.02, 0.0, 0.02, 1.0);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onHit(BlockEntity hammer) {
		progress = ConfigManager.DAWNSTONE_ANVIL_MAX_HITS.get();
		onHit();
	}

	@Override
	public boolean isValid() {
		cachedRecipe = Misc.getRecipe(cachedRecipe, RegistryManager.DAWNSTONE_ANVIL_RECIPE.get(), new net.minecraft.world.item.crafting.RecipeInput() {
		@Override public net.minecraft.world.item.ItemStack getItem(int slot) { return inventory.getStackInSlot(slot); }
		@Override public int size() { return inventory.getSlots(); }
		}, level);
		return cachedRecipe != null;
	}
}
