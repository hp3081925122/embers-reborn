package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.util.LegacyCaps;
import com.rekindled.embers.util.NbtBridge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class MechanicalPumpTopBlockEntity extends BlockEntity implements IFluidHandler, IExtraCapabilityInformation {

	public static int capacity = FluidType.BUCKET_VOLUME * 8;
	protected FluidTank tank;

	public MechanicalPumpTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.MECHANICAL_PUMP_TOP_ENTITY.get(), pPos, pBlockState);
		tank = new FluidTank(capacity) {
			@Override
			public void onContentsChanged() {
				MechanicalPumpTopBlockEntity.this.setChanged();
			}
		};
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		loadAdditionalNbt(NbtBridge.read(input), input.lookup());
	}

	public void loadAdditionalNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		NbtBridge.load(tank, provider, nbt.getCompoundOrEmpty("tank"));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		CompoundTag nbt = new CompoundTag();
		saveAdditionalNbt(nbt, NbtBridge.provider(this));
		NbtBridge.write(output, nbt);
	}

	public void saveAdditionalNbt(CompoundTag nbt, HolderLookup.Provider provider) {
		nbt.put("tank", NbtBridge.writeTank(tank, provider, new CompoundTag()));
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.put("tank", NbtBridge.writeTank(tank, provider, new CompoundTag()));
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.getChunkSource().blockChanged(worldPosition);
		}
	}

	public int getCapacity() {
		return tank.getCapacity();
	}

	public FluidStack getFluidStack() {
		return tank.getFluid();
	}

	public FluidTank getTank() {
		return tank;
	}

	@Override
	public boolean hasCapabilityDescription(BlockCapability<?, ?> capability) {
		return capability == LegacyCaps.FLUID_BLOCK;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, BlockCapability<?, ?> capability, Direction facing) {
		if (capability == LegacyCaps.FLUID_BLOCK)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.OUTPUT, Embers.MODID + ".tooltip.goggles.fluid", Component.translatable(Embers.MODID + ".tooltip.goggles.fluid.water")));
	}

	@Override
	public int getTanks() {
		return tank.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int t) {
		return tank.getFluidInTank(t);
	}

	@Override
	public int getTankCapacity(int t) {
		return tank.getTankCapacity(t);
	}

	@Override
	public boolean isFluidValid(int t, FluidStack s) {
		return tank.isFluidValid(t, s);
	}

	@Override
	public int fill(FluidStack r, IFluidHandler.FluidAction a) {
		return tank.fill(r, a);
	}

	@Override
	public FluidStack drain(FluidStack r, IFluidHandler.FluidAction a) {
		return tank.drain(r, a);
	}

	@Override
	public FluidStack drain(int m, IFluidHandler.FluidAction a) {
		return tank.drain(m, a);
	}
}
