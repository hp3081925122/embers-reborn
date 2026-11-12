package com.rekindled.embers.blockentity;

import com.rekindled.embers.util.LegacyCaps;

import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
// FluidTank removed; use FluidTank directly
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MechanicalPumpTopBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements net.neoforged.neoforge.fluids.capability.IFluidHandler, IExtraCapabilityInformation {

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

	public int getCapacity(){
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

	// IFluidHandler delegation
	@Override public int getTanks() { return tank.getTanks(); }
	@Override public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int t) { return tank.getFluidInTank(t); }
	@Override public int getTankCapacity(int t) { return tank.getTankCapacity(t); }
	@Override public boolean isFluidValid(int t, net.neoforged.neoforge.fluids.FluidStack s) { return tank.isFluidValid(t, s); }
	@Override public int fill(net.neoforged.neoforge.fluids.FluidStack r, IFluidHandler.FluidAction a) { return tank.fill(r, a); }
	@Override public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack r, IFluidHandler.FluidAction a) { return tank.drain(r, a); }
	@Override public net.neoforged.neoforge.fluids.FluidStack drain(int m, IFluidHandler.FluidAction a) { return tank.drain(m, a); }

}
