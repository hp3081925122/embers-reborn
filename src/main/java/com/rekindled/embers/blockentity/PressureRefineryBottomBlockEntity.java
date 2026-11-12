package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.recipe.BlockStateContext;
import com.rekindled.embers.recipe.IEmberActivationRecipe;
import com.rekindled.embers.recipe.IMetalCoefficientRecipe;
import com.rekindled.embers.recipe.SingleRecipeInput;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidType;
// FluidTank removed; use FluidTank directly
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class PressureRefineryBottomBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements net.neoforged.neoforge.fluids.capability.IFluidHandler, IExtraDialInformation, IExtraCapabilityInformation, IUpgradeable {

	public static final float BASE_MULTIPLIER = 1.25f;
	public static final int FLUID_CONSUMED = 25;
	public static final float PER_BLOCK_MULTIPLIER = 0.25f;
	public static final int PROCESS_TIME = 20;
	public static int capacity = FluidType.BUCKET_VOLUME * 8;
	protected FluidTank tank;
	int progress = -1;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			PressureRefineryBottomBlockEntity.this.setChanged();
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
	protected List<UpgradeContext> upgrades = new ArrayList<>();
	public IEmberActivationRecipe cachedRecipe = null;
	public IMetalCoefficientRecipe cachedCoefficient = null;

	public PressureRefineryBottomBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			protected void onContentsChanged() {
				PressureRefineryBottomBlockEntity.this.setChanged();
			}
		};
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
		progress = nbt.getInt("progress");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("inventory", inventory.serializeNBT(provider));
		nbt.putInt("progress", progress);
	}

	public double getMultiplier() {
		BlockState metalState = level.getBlockState(worldPosition.below());
		BlockStateContext context = new BlockStateContext(metalState);
		cachedCoefficient = Misc.getRecipe(cachedCoefficient, RegistryManager.METAL_COEFFICIENT.get(), context, level);

		double metalMultiplier = cachedCoefficient == null ? BASE_MULTIPLIER : cachedCoefficient.getCoefficient(context) - BASE_MULTIPLIER;
		double totalMult = BASE_MULTIPLIER;
		if (cachedCoefficient == null) {
			totalMult = metalMultiplier;
		} else {
			for (Direction facing : Misc.horizontals) {
				if (level.getBlockState(worldPosition.below().relative(facing)).is(EmbersBlockTags.HEAT_SOURCES)) {
					totalMult += PER_BLOCK_MULTIPLIER * metalMultiplier;
				}
			}
		}
		return totalMult;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, PressureRefineryBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PressureRefineryBottomBlockEntity blockEntity) {
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, Misc.horizontals);
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;

		if (!blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			BlockEntity tile = level.getBlockEntity(pos.above());
			if (blockEntity.tank.getFluid() != null && blockEntity.tank.getFluid().getFluid().is(FluidTags.WATER) && blockEntity.tank.getFluidAmount() >= FLUID_CONSUMED) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);

				if (!cancel && tile instanceof PressureRefineryTopBlockEntity) {
					PressureRefineryTopBlockEntity top = (PressureRefineryTopBlockEntity) tile;
					blockEntity.progress++;
					if (blockEntity.progress > UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades)) {
						blockEntity.progress = 0;
						if (blockEntity.inventory != null) {
							net.minecraft.world.item.crafting.RecipeInput wrapper = new net.minecraft.world.item.crafting.RecipeInput() {
					@Override public net.minecraft.world.item.ItemStack getItem(int slot) { return blockEntity.inventory.getStackInSlot(slot); }
					@Override public int size() { return blockEntity.inventory.getSlots(); }
				};
							blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.EMBER_ACTIVATION.get(), wrapper, level);
							if (blockEntity.cachedRecipe != null) {
								double emberValue = blockEntity.cachedRecipe.getOutput(wrapper) * blockEntity.getMultiplier();
								double ember = UpgradeUtil.getTotalEmberProduction(blockEntity, emberValue, blockEntity.upgrades);
								if ((ember > 0 || emberValue == 0) && top.capability.getEmber() + ember <= top.capability.getEmberCapacity()) {
									level.playSound(null, pos, EmbersSounds.PRESSURE_REFINERY.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

									if (level instanceof ServerLevel serverLevel) {
										serverLevel.sendParticles(new GlowParticleOptions(EmbersColors.EMBER_ID, new Vec3(0, 0.65f, 0), 4.7f), pos.getX() + 0.5f, pos.getY() + 1.5f, pos.getZ() + 0.5f, 80, 0.1, 0.1, 0.1, 1.0);
										serverLevel.sendParticles(new SmokeParticleOptions(EmbersColors.SMOKE_ID, 5.0f), pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 20, 0.1, 0.1, 0.1, 1.0);
									}
									UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.PRODUCE, ember), blockEntity.upgrades);
									top.capability.addAmount(ember, true);

									//the recipe is responsible for taking items from the inventory
									blockEntity.cachedRecipe.process(wrapper);
									blockEntity.tank.drain(FLUID_CONSUMED, IFluidHandler.FluidAction.EXECUTE);
								}
							}
						}
					}
					blockEntity.setChanged();
				}
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
		// holder.invalidate() removed
	}

	@Override
	public void addDialInformation(Direction facing, List<Component> information, String dialType) {
		if(EmberDialBlock.DIAL_TYPE.equals(dialType)) {
			DecimalFormat multiplierFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember_multiplier");
			double multiplier = getMultiplier();
			information.add(Component.translatable(Embers.MODID + ".tooltip.dial.ember_multiplier", multiplierFormat.format(multiplier)));
		}
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
		if(capability == LegacyCaps.FLUID_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.fluid", Component.translatable(Embers.MODID + ".tooltip.goggles.fluid.water")));
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return face.getAxis() != Axis.Y;
	}

	// IFluidHandler delegation to internal tank
	@Override public int getTanks() { return tank.getTanks(); }
	@Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int tank2) { return tank.getFluidInTank(tank2); }
	@Override public int getTankCapacity(int tank2) { return tank.getTankCapacity(tank2); }
	@Override public boolean isFluidValid(int tank2, net.neoforged.neoforge.fluids.FluidStack stack) { return tank.isFluidValid(tank2, stack); }
	@Override public int fill(net.neoforged.neoforge.fluids.FluidStack resource, IFluidHandler.FluidAction action) { return tank.fill(resource, action); }
	@Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack resource, IFluidHandler.FluidAction action) { return tank.drain(resource, action); }
	@Override public net.neoforged.neoforge.fluids.FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) { return tank.drain(maxDrain, action); }

}
