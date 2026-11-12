package com.rekindled.embers.blockentity;

import java.util.ArrayList;
import java.util.List;
import net.neoforged.neoforge.capabilities.BlockCapability;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.DialInformationEvent;
import com.rekindled.embers.api.event.EmberEvent;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.api.tile.IExtraDialInformation;
import com.rekindled.embers.api.tile.IHammerable;
import com.rekindled.embers.api.tile.IMechanicallyPowered;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public class AutomaticHammerBlockEntity extends BlockEntity implements IMechanicallyPowered, IExtraDialInformation, IUpgradeable, IExtraCapabilityInformation {

	public static final double EMBER_COST = 40.0;
	public static final int PROCESS_TIME = 20;
	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			AutomaticHammerBlockEntity.this.setChanged();
		}
	};
	public long startTime = -1;
	public int processTime = -1;
	protected List<UpgradeContext> upgrades = new ArrayList<>();

	public AutomaticHammerBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.AUTOMATIC_HAMMER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(12000);
	}

	public AABB getRenderBoundingBox() {
		return new AABB(net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(-1, -1, -1)), net.minecraft.world.phys.Vec3.atLowerCornerOf(worldPosition.offset(2, 2, 2)));
	}

	@Override
	public void loadAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.loadAdditional(nbt, provider);
		capability.deserializeNBT(provider, nbt.getCompound("ember"));
		startTime = nbt.getLong("startTime");
		processTime = nbt.getInt("processTime");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider) {
		super.saveAdditional(nbt, provider);
		nbt.put("ember", capability.serializeNBT(provider));
		nbt.putLong("startTime", startTime);
		nbt.putInt("processTime", processTime);
	}

	@Override
	public CompoundTag getUpdateTag(net.minecraft.core.HolderLookup.Provider provider) {
		CompoundTag nbt = super.getUpdateTag(provider);
		nbt.putLong("startTime", startTime);
		nbt.putInt("processTime", processTime);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, AutomaticHammerBlockEntity blockEntity) {
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{facing.getOpposite()});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AutomaticHammerBlockEntity blockEntity) {
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		blockEntity.upgrades = UpgradeUtil.getUpgrades(level, pos, new Direction[]{facing.getOpposite()});
		UpgradeUtil.verifyUpgrades(blockEntity, blockEntity.upgrades);
		if (UpgradeUtil.doTick(blockEntity, blockEntity.upgrades))
			return;
		BlockEntity tile = level.getBlockEntity(pos.below().relative(facing));
		if (tile instanceof IHammerable) {
			double ember_cost = UpgradeUtil.getTotalEmberConsumption(blockEntity, EMBER_COST, blockEntity.upgrades);
			IHammerable hammerable = (IHammerable) tile;
			boolean redstoneEnabled = level.hasNeighborSignal(pos);
			if (hammerable.isValid() && redstoneEnabled && blockEntity.capability.getEmber() >= ember_cost) {
				boolean cancel = UpgradeUtil.doWork(blockEntity, blockEntity.upgrades);
				int processTime = UpgradeUtil.getWorkTime(blockEntity, PROCESS_TIME, blockEntity.upgrades);
				if (!cancel && blockEntity.startTime + processTime < level.getGameTime()) {
					blockEntity.startTime = level.getGameTime();
					blockEntity.processTime = processTime;
					blockEntity.setChanged();
				}
			}
			if (blockEntity.startTime + blockEntity.processTime / 2 == level.getGameTime() && blockEntity.capability.getEmber() >= ember_cost) {
				UpgradeUtil.throwEvent(blockEntity, new EmberEvent(blockEntity, EmberEvent.EnumType.CONSUME, ember_cost), blockEntity.upgrades);
				blockEntity.capability.removeAmount(ember_cost, true);
				hammerable.onHit(blockEntity);
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
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public double getMechanicalSpeed(double power) {
		return Misc.getDiminishedPower(power,20,1.5/20);
	}

	@Override
	public double getNominalSpeed() {
		return 1;
	}

	@Override
	public double getMinimumPower() {
		return 10;
	}

	@Override
	public void addDialInformation(Direction facing, List<Component> information, String dialType) {
		UpgradeUtil.throwEvent(this, new DialInformationEvent(this, information, dialType), upgrades);
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING) && getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite() == face;
	}

	@Override
	public void addOtherDescription(List<Component> strings, Direction facing) {
		strings.add(Component.translatable(Embers.MODID + ".tooltip.goggles.redstone_signal"));
	}
}
