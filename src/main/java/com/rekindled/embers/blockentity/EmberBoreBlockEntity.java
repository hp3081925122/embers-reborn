package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.MachineRecipeEvent;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.recipe.BoringContext;
import com.rekindled.embers.recipe.IBoringRecipe;
import com.rekindled.embers.util.EmberGenUtil;
import com.rekindled.embers.util.WeightedItemStack;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
// ForgeHooks removed in NeoForge 1.21.1; use EventHooks
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class EmberBoreBlockEntity extends BlockEntity implements ISoundController, IMechanicallyPowered, IExtraDialInformation, IExtraCapabilityInformation, IUpgradeable {

	public static final int SLOT_FUEL = 8;

	public static final double SUPERSPEED_THRESHOLD = 2.5;
	public static final int SOUND_ON = 1;
	public static final int SOUND_ON_DRILL = 2;
	public static final int SOUND_ON_SUPERSPEED = 3;
	public static final int SOUND_ON_DRILL_SUPERSPEED = 4;
	public static final int[] SOUND_IDS = new int[]{SOUND_ON, SOUND_ON_DRILL, SOUND_ON_SUPERSPEED, SOUND_ON_DRILL_SUPERSPEED};

	Random random = new Random();
	public long ticksExisted = 0;
	public float angle = 0;
	public double ticksFueled = 0;
	public float lastAngle;
	public boolean isRunning;

	HashSet<Integer> soundsPlaying = new HashSet<>();
	public List<UpgradeContext> upgrades = new ArrayList<>();
	public double speedMod;

	public EmberBoreInventory inventory = new EmberBoreInventory(9);
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public EmberBoreBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_BORE_ENTITY.get(), pPos, pBlockState);
	}

	public AABB getRenderBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(-1, -2, -1)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(2, 0, 2)));
	}

	public AABB getBladeBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(-1, -2, -1)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(1, -1, 1)));
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		if (nbt.contains("inventory"))
			com.rekindled.embers.util.NbtBridge.load(inventory, provider, nbt.getCompoundOrEmpty("inventory"));
		if (nbt.contains("fueled"))
			ticksFueled = nbt.getDoubleOr("fueled", 0.0D);
		isRunning = nbt.getBooleanOr("isRunning", false);
	}

	@Override
	protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
		super.saveAdditional(output);
		net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
		saveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));
		com.rekindled.embers.util.NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		nbt.put("inventory", com.rekindled.embers.util.NbtBridge.save(inventory, provider));
		nbt.putDouble("fueled", ticksFueled);
		nbt.putBoolean("isRunning", isRunning);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putBoolean("isRunning", isRunning);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	Boolean canMine = null;

	public boolean canMine() {
		if (canMine == null) {
			ResourceKey<Biome> biome = level.getBiome(worldPosition).unwrapKey().get();
			if (biome != null) {
				BoringContext context = new BoringContext(level.dimension().identifier(), biome.identifier(), worldPosition.getY(), level.getBlockStatesIfLoaded(getBladeBoundingBox()).collect(java.util.stream.Collectors.toList()));
				List<IBoringRecipe> recipes = level.getRecipeManager().getRecipesFor(RegistryManager.BORING.get(), context, level).stream().map(net.minecraft.world.item.crafting.RecipeHolder::value).collect(java.util.stream.Collectors.toList());
				UpgradeUtil.throwEvent(this, new MachineRecipeEvent<List<IBoringRecipe>>(this, recipes), this.upgrades);
				canMine = !recipes.isEmpty();
			} else {
				canMine = false;
			}
		}
		return canMine;
	}

	public boolean isSuperSpeed() {
		return speedMod >= SUPERSPEED_THRESHOLD;
	}

	public boolean canInsert(ArrayList<ItemStack> returns) {
		for (ItemStack stack : returns) {
			ItemStack returned = stack;
			for (int slot = 0; slot < inventory.getSlots() - 1; slot++) {
				returned = inventory.insertItemInternal(slot, returned, true);
			}
			if (!returned.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void insert(ArrayList<ItemStack> returns) {
		for (ItemStack stack : returns) {
			ItemStack returned = stack;
			for (int slot = 0; slot < inventory.getSlots() - 1; slot++) {
				returned = inventory.insertItemInternal(slot, returned, false);
			}
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberBoreBlockEntity blockEntity) {
		commonTick(level, pos, state, blockEntity);
		boolean previousRunning = blockEntity.isRunning;
		blockEntity.isRunning = false;
		blockEntity.ticksExisted++;

		double fuelConsumption = UpgradeUtil.getOtherParameter(blockEntity, "fuel_consumption", (double) ConfigManager.EMBER_BORE_FUEL_CONSUMPTION.get(), blockEntity.upgrades);
		boolean cancel = false;
		if (blockEntity.ticksFueled >= fuelConsumption) {
			blockEntity.isRunning = true;
			blockEntity.ticksFueled -= fuelConsumption;
			cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
		} else {
			blockEntity.ticksFueled = 0;
		}

		if (!cancel) {
			if (blockEntity.ticksFueled < fuelConsumption) {
				ItemStack fuel = blockEntity.inventory.getStackInSlot(SLOT_FUEL);
				if (!fuel.isEmpty()) {
					ItemStack fuelCopy = fuel.copy();
					int burnTime = fuelCopy.getBurnTime(RegistryManager.BORING.get());
					if (burnTime > 0) {
						blockEntity.ticksFueled = burnTime;
						fuel.shrink(1);
						if (fuel.isEmpty())
							blockEntity.inventory.setStackInSlot(SLOT_FUEL, fuelCopy.getItem().getCraftingRemainingItem(fuelCopy));
						blockEntity.setChanged();
					}
				}
			} else {
				//multiply with the config value here so the bore isn't visually or audibly different at higher speeds
				int boreTime = (int) Math.ceil(ConfigManager.EMBER_BORE_TIME.get() / (blockEntity.speedMod * ConfigManager.EMBER_BORE_SPEED_MOD.get()));
				if (blockEntity.ticksExisted % boreTime == 0) {
					ResourceKey<Biome> biome = level.getBiome(pos).unwrapKey().get();
					if (biome != null) {
						BoringContext context = new BoringContext(level.dimension().identifier(), biome.identifier(), pos.getY(), level.getBlockStatesIfLoaded(blockEntity.getBladeBoundingBox()).collect(java.util.stream.Collectors.toList()));
						List<IBoringRecipe> recipes = level.getRecipeManager().getRecipesFor(RegistryManager.BORING.get(), context, level).stream().map(net.minecraft.world.item.crafting.RecipeHolder::value).collect(java.util.stream.Collectors.toList());
						UpgradeUtil.throwEvent(blockEntity, new MachineRecipeEvent<List<IBoringRecipe>>(blockEntity, recipes), blockEntity.upgrades);
						ArrayList<WeightedItemStack> stacks = new ArrayList<>();
						float rand = blockEntity.random.nextFloat();
						double chance = EmberGenUtil.getEmberDensity(((ServerLevel) level).getSeed(), pos.getX(), pos.getZ());
						for (IBoringRecipe recipe : recipes) {
							if (rand < (recipe.getChance() == -1.0 ? chance : recipe.getChance())) {
								stacks.add(recipe.getOutput(context));
							}
						}

						ArrayList<ItemStack> returns = new ArrayList<>();
						if (!stacks.isEmpty()) {
							Optional<WeightedItemStack> picked = WeightedRandom.getRandomItem(level.getRandom(), stacks, WeightedItemStack::weight);
							returns.add(picked.get().getStack().copy());
						}
						UpgradeUtil.transformOutput(blockEntity, returns, blockEntity.upgrades);
						if (blockEntity.canInsert(returns)) {
							blockEntity.insert(returns);
						}
					}
				}
			}
		} else {
			blockEntity.isRunning = false;
		}

		if (blockEntity.isRunning != previousRunning) {
			blockEntity.setChanged();
		}
	}

	/** Fraction of the current dig cycle, or -1 when the bore is idle. */
	public float digProgress() {
		if (!isRunning)
			return -1f;
		double speed = speedMod > 0 ? speedMod : 1;
		int boreTime = (int) Math.ceil(ConfigManager.EMBER_BORE_TIME.get() / (speed * ConfigManager.EMBER_BORE_SPEED_MOD.get()));
		if (boreTime <= 0)
			return -1f;
		return (ticksExisted % boreTime) / (float) boreTime;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, EmberBoreBlockEntity blockEntity) {
		commonTick(level, pos, state, blockEntity);
		blockEntity.handleSound();
	}

	public static void commonTick(Level level, BlockPos pos, BlockState state, EmberBoreBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{Direction.UP});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		blockEntity.speedMod = UpgradeUtil.getTotalSpeedModifier(blockEntity, blockEntity.upgrades);
		blockEntity.lastAngle = blockEntity.angle;
		if (blockEntity.isRunning) {
			blockEntity.angle += 12.0f * blockEntity.speedMod;
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
		float soundX = (float) worldPosition.getX() + 0.5f;
		float soundY = (float) worldPosition.getY() - 0.5f;
		float soundZ = (float) worldPosition.getZ() + 0.5f;
		switch (id) {
		case SOUND_ON:
			EmbersSounds.playMachineSound(this, SOUND_ON, EmbersSounds.BORE_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_ON_DRILL:
			EmbersSounds.playMachineSound(this, SOUND_ON_DRILL, EmbersSounds.BORE_LOOP_MINE.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_ON_SUPERSPEED:
			EmbersSounds.playMachineSound(this, SOUND_ON_SUPERSPEED, EmbersSounds.BORE_LOOP_SUPERSPEED.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		case SOUND_ON_DRILL_SUPERSPEED:
			EmbersSounds.playMachineSound(this, SOUND_ON_DRILL_SUPERSPEED, EmbersSounds.BORE_LOOP_MINE_SUPERSPEED.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, soundX, soundY, soundZ);
			break;
		}
		level.playLocalSound(soundX, soundY, soundZ, EmbersSounds.BORE_START.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		level.playLocalSound((float) worldPosition.getX() + 0.5f, (float) worldPosition.getY() - 0.5f, (float) worldPosition.getZ() + 0.5f, EmbersSounds.BORE_STOP.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
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
		switch (id) {
		case SOUND_ON:
			return isRunning && !canMine() && !isSuperSpeed();
		case SOUND_ON_DRILL:
			return isRunning && canMine() && !isSuperSpeed();
		case SOUND_ON_SUPERSPEED:
			return isRunning && !canMine() && isSuperSpeed();
		case SOUND_ON_DRILL_SUPERSPEED:
			return isRunning && canMine() && isSuperSpeed();
		default:
			return false;
		}
	}

	@Override
	public float getCurrentVolume(int id, float volume) {
		switch (id) {
		case SOUND_ON:
			return !canMine() && !isSuperSpeed() ? 1.0f : 0.0f;
		case SOUND_ON_DRILL:
			return canMine() && !isSuperSpeed() ? 1.0f : 0.0f;
		case SOUND_ON_SUPERSPEED:
			return !canMine() && isSuperSpeed() ? 1.0f : 0.0f;
		case SOUND_ON_DRILL_SUPERSPEED:
			return canMine() && isSuperSpeed() ? 1.0f : 0.0f;
		default:
			return 0f;
		}
	}

	@Override
	public float getCurrentPitch(int id, float pitch) {
		if (isSuperSpeed())
			return (float) (speedMod + 1.0f - SUPERSPEED_THRESHOLD);
		return (float) speedMod;
	}

	@Override
	public double getMechanicalSpeed(double power) {
		return power > 0 ? Math.log10(power / 15) * 3 : 0;
	}

	@Override
	public double getMinimumPower() {
		return 15;
	}

	@Override
	public double getNominalSpeed() {
		return 1;
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
		if (capability == LegacyCaps.ITEM_BLOCK) {
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.fuel")));
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.ember")));
		}
	}

	public class EmberBoreInventory extends ItemStackHandler {
		public EmberBoreInventory() {
		}

		public EmberBoreInventory(int size) {
			super(size);
		}

		public EmberBoreInventory(NonNullList<ItemStack> stacks) {
			super(stacks);
		}

		@Override
		protected void onContentsChanged(int slot) {
			EmberBoreBlockEntity.this.setChanged();
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			ItemStack currentFuel = this.getStackInSlot(SLOT_FUEL);
			if (currentFuel.isEmpty()) {
				int burnTime = stack.getBurnTime(RegistryManager.BORING.get());
				if (burnTime != 0)
					return super.insertItem(SLOT_FUEL, stack, simulate);
				else
					return stack;
			}

			//if the item stacks then it has the same burn time, therefore we don't need to check it
			return super.insertItem(SLOT_FUEL, stack, simulate);
		}

		public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			//disallow extraction from fuel slot if it's a stacked item or it doesn't have burn time
			if (slot == SLOT_FUEL) {
				ItemStack fuelStack = getStackInSlot(SLOT_FUEL);
				if (fuelStack.getCount() > 1 || fuelStack.getBurnTime(RegistryManager.BORING.get()) != 0)
					return ItemStack.EMPTY;
			}
			return super.extractItem(slot, amount, simulate);
		}
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return face == Direction.UP;
	}
}
