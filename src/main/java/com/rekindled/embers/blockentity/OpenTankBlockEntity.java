package com.rekindled.embers.blockentity;

import org.jetbrains.annotations.NotNull;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
// FluidTank removed; use FluidTank directly

public abstract class OpenTankBlockEntity extends net.minecraft.world.level.block.entity.BlockEntity implements net.neoforged.neoforge.fluids.capability.IFluidHandler {

	protected net.neoforged.neoforge.fluids.capability.templates.FluidTank tank = new net.neoforged.neoforge.fluids.capability.templates.FluidTank(Integer.MAX_VALUE);


	FluidStack lastEscaped = null;
	long lastEscapedTickServer;
	long lastEscapedTickClient;

	public OpenTankBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}
	// IFluidHandler delegation to tank
	@Override public int getTanks() { return tank.getTanks(); }
	@Override public @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int t) { return tank.getFluidInTank(t); }
	@Override public int getTankCapacity(int t) { return tank.getTankCapacity(t); }
	@Override public boolean isFluidValid(int t, @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack s) { return tank.isFluidValid(t, s); }
	@Override public int fill(@org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack r, @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction a) { return tank.fill(r, a); }
	@Override public @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack drain(@org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack r, @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction a) { return tank.drain(r, a); }
	@Override public @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.FluidStack drain(int m, @org.jetbrains.annotations.NotNull net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction a) { return tank.drain(m, a); }


	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		tank.readFromNBT(provider, nbt);
		if (nbt.contains("lastEscaped")) {
			lastEscaped = FluidStack.parseOptional(provider, nbt.getCompound("lastEscaped"));
			lastEscapedTickServer = nbt.getLong("lastEscapedTick");
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		tank.writeToNBT(provider, nbt);
		if (lastEscaped != null) {
			nbt.put("lastEscaped", (net.minecraft.nbt.CompoundTag) lastEscaped.save(provider, new net.minecraft.nbt.CompoundTag()));
			nbt.putLong("lastEscapedTick", lastEscapedTickServer);
		}
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		tank.writeToNBT(provider, nbt);
		if (lastEscaped != null) {
			nbt.put("lastEscaped", (net.minecraft.nbt.CompoundTag) lastEscaped.save(provider, new net.minecraft.nbt.CompoundTag()));
			nbt.putLong("lastEscapedTick", lastEscapedTickServer);
		}
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	public void setEscapedFluid(FluidStack stack) {
		if (stack != null && !stack.isEmpty()) {
			lastEscaped = stack.copy();
			lastEscapedTickServer = level.getLevelData().getGameTime();

			this.setChanged();
		}
	}

	protected boolean shouldEmitParticles() {
		if (lastEscaped == null || lastEscaped.isEmpty())
			return false;
		if (lastEscapedTickClient < lastEscapedTickServer) {
			lastEscapedTickClient = lastEscapedTickServer;
			return true;
		}
		long dTime = level.getLevelData().getGameTime() - lastEscapedTickClient;
		if (dTime < lastEscaped.getAmount() + 5)
			return true;
		return false;
	}

	protected abstract void updateEscapeParticles();
}
