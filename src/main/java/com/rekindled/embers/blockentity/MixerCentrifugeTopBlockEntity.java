package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
// FluidTank removed; use FluidTank directly
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MixerCentrifugeTopBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements net.neoforged.neoforge.fluids.capability.IFluidHandler, IExtraCapabilityInformation, IUpgradeable {

	public static int capacity = FluidType.BUCKET_VOLUME * 8;
	protected FluidTank tank;
	public boolean loaded = false;
	public float renderOffset;
	int previousFluid;

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			MixerCentrifugeTopBlockEntity.this.setChanged();
		}
	};

	public MixerCentrifugeTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			public void onContentsChanged() {
				MixerCentrifugeTopBlockEntity.this.setChanged();
			}
		};
		capability.setEmberCapacity(8000);
		capability.setEmber(0);
	}

	@Override
	protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		com.rekindled.embers.util.NbtBridge.load(tank, provider, nbt.getCompoundOrEmpty("tank"));
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
		nbt.put("ember", capability.serializeNBT(provider));
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
        nbt.put("tank", com.rekindled.embers.util.NbtBridge.writeTank(tank, provider, new net.minecraft.nbt.CompoundTag()));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public int getCapacity(){
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MixerCentrifugeTopBlockEntity blockEntity) {
		//I know I'm supposed to use onLoad for stuff on the first tick but the tank isn't synced to the client yet when that happens
		if (!blockEntity.loaded) {
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
			blockEntity.loaded = true;
		}
		if (blockEntity.tank.getFluidAmount() != blockEntity.previousFluid) {
			blockEntity.renderOffset = blockEntity.renderOffset + blockEntity.tank.getFluidAmount() - blockEntity.previousFluid;
			blockEntity.previousFluid = blockEntity.tank.getFluidAmount();
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
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.FLUID_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.FLUID_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.fluid", Component.translatable(Embers.MODID + ".tooltip.goggles.fluid.metal")));
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return true;
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
