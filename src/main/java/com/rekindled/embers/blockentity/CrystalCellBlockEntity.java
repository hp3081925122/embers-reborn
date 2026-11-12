package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageCrystalCellGrowFX;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.recipe.IEmberActivationRecipe;
import com.rekindled.embers.recipe.SingleRecipeInput;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class CrystalCellBlockEntity extends BlockEntity implements ISoundController, IExtraDialInformation, IExtraCapabilityInformation, IUpgradeable {
	public static final int MAX_CAPACITY = 1440000;
	Random random = new Random();
	public long ticksExisted = 0;
	public float angle = 0;
	public long seed = 0;
	public double renderCapacity;
	public double renderCapacityLast;
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CrystalCellBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return true;
		}
	};
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CrystalCellBlockEntity.this.setChanged();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (Misc.getRecipe(cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), new SingleRecipeInput(stack), level) != null) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)
	public IEmberActivationRecipe cachedRecipe = null;
	protected List<UpgradeContext> upgrades;

	public static final int SOUND_AMBIENT = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_AMBIENT};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public CrystalCellBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CRYSTAL_CELL_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(64000);
		seed = random.nextLong();
	}

	public AABB getRenderBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(-1, 1, -1)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(2, 5, 2)));
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		seed = nbt.getLongOr("seed", 0L);
		if (nbt.contains("inventory"))
			inventory.deserializeNBT(provider, nbt.getCompoundOrEmpty("inventory"));
		capability.deserializeNBT(provider, nbt.getCompoundOrEmpty("ember"));
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
		saveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));
		com.rekindled.embers.util.NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		nbt.putLong("seed", seed);
		nbt.put("inventory", inventory.serializeNBT(provider));
		nbt.put("ember", capability.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putLong("seed", seed);
		nbt.put("ember", capability.serializeNBT(provider));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void commonTick(Level level, BlockPos pos, BlockState state, CrystalCellBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{Direction.DOWN});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;
		if (level.isClientSide())
			blockEntity.handleSound();
		blockEntity.ticksExisted++;
		blockEntity.renderCapacityLast = blockEntity.renderCapacity;
		if (blockEntity.renderCapacity < blockEntity.capability.getEmberCapacity())
			blockEntity.renderCapacity += Math.min(10000, blockEntity.capability.getEmberCapacity() - blockEntity.renderCapacity);
		else
			blockEntity.renderCapacity -= Math.min(10000, blockEntity.renderCapacity - blockEntity.capability.getEmberCapacity());
		if (!blockEntity.inventory.getStackInSlot(0).isEmpty() && blockEntity.ticksExisted % 4 == 0) {
			boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
			if (!cancel) {
				com.rekindled.embers.recipe.SingleRecipeInput wrapper = new com.rekindled.embers.recipe.SingleRecipeInput(blockEntity.inventory.getStackInSlot(0));
				blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
				if (blockEntity.cachedRecipe != null) {
					double emberValue = blockEntity.cachedRecipe.process(wrapper);
					if (!level.isClientSide()) {
						blockEntity.inventory.extractItem(0, 1, false);
						int maxCapacity = UpgradeUtil.getOtherParameter(blockEntity, "max_capacity", MAX_CAPACITY, blockEntity.upgrades);
						if (blockEntity.capability.getEmberCapacity() < maxCapacity) {
							blockEntity.capability.setEmberCapacity(Math.min(maxCapacity, blockEntity.capability.getEmberCapacity() + emberValue * 10));
							blockEntity.setChanged();
						}
						net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingChunk((net.minecraft.server.level.ServerLevel) level, level.getChunkAt(pos).getPos(), new MessageCrystalCellGrowFX(pos, blockEntity.capability.getEmberCapacity()));
					}
				}
			}
		}
		float numLayers = 2 + (float) Math.floor(blockEntity.capability.getEmberCapacity() / 120000.0f);
		if (level.isClientSide()) {
			for (int i = 0; i < numLayers / 2; i++) {
				float layerHeight = 0.25f;
				float height = layerHeight * numLayers;
				float xDest = pos.getX() + 0.5f;
				float yDest = pos.getY() + height / 2.0f + 1.5f;
				float zDest = pos.getZ() + 0.5f;
				float x = pos.getX() + 0.5f + 2.0f * (blockEntity.random.nextFloat() - 0.5f);
				float z = pos.getZ() + 0.5f + 2.0f * (blockEntity.random.nextFloat() - 0.5f);
				float y = pos.getY() + 1.0f;
				level.addParticle(new GlowParticleOptions(EmbersColors.EMBER_ID, new Vec3((xDest - x) / 1.0f * blockEntity.random.nextFloat(), (yDest - y) / 1.0f * blockEntity.random.nextFloat(), (zDest - z) / 1.0f * blockEntity.random.nextFloat()), 2.0F), x, y, z, 0, 0, 0);
			}
		}
	}

	@Deprecated @SuppressWarnings("deprecation")
	public Object getCapabilityLEGACY(net.neoforged.neoforge.capabilities.BlockCapability<?, ?> cap, net.minecraft.core.Direction side) {
		// In NeoForge 1.21.1, capabilities are registered via RegisterCapabilitiesEvent
		return null;
	}
	@Override
	public void invalidateCapabilities() {
		super.invalidateCapabilities();
		capability.invalidate();
		// holder.invalidate() removed
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_AMBIENT:
			EmbersSounds.playMachineSound(this, SOUND_AMBIENT, EmbersSounds.CRYSTAL_CELL_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, worldPosition.getX() + 0.5f, worldPosition.getY() - 0.5f, worldPosition.getZ() + 0.5f);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		return id == SOUND_AMBIENT;
	}

	@Override
	public void addDialInformation(Direction facing, List<Component> information, String dialType) {
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.ITEM_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.ITEM_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.ember")));
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return face == Direction.DOWN;
	}
}
