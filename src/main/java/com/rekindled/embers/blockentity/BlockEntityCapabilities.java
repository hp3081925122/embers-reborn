package com.rekindled.embers.blockentity;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.blockentity.MechanicalCoreBlockEntity.BlockEntityDirection;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Item and fluid handler capabilities, ported 1:1 from the 1.20.1 getCapability overrides.
 * Lives in the blockentity package so it can reach package-private/protected handler fields.
 */
public class BlockEntityCapabilities {

	private static <T extends net.minecraft.world.level.block.entity.BlockEntity> void items(RegisterCapabilitiesEvent event, BlockEntityType<T> type, java.util.function.BiFunction<T, Direction, IItemHandler> provider) {
		event.registerBlockEntity(Capabilities.Item.BLOCK, type, (be, side) -> com.rekindled.embers.util.LegacyHandlers.items(provider.apply(be, side)));
	}

	private static <T extends net.minecraft.world.level.block.entity.BlockEntity> void fluids(RegisterCapabilitiesEvent event, BlockEntityType<T> type, java.util.function.BiFunction<T, Direction, IFluidHandler> provider) {
		event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (be, side) -> com.rekindled.embers.util.LegacyHandlers.fluids(provider.apply(be, side)));
	}

	public static void register(RegisterCapabilitiesEvent event) {
		// --- item handlers, all sides ---
		items(event, RegistryManager.ALCHEMY_PEDESTAL_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.BIN_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.CATALYSIS_CHAMBER_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.COMBUSTION_CHAMBER_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.CINDER_PLINTH_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.COPPER_CHARGER_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.DAWNSTONE_ANVIL_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.IGNEM_REACTOR_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.MELTER_TOP_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.STAMP_BASE_ENTITY.get(), (be, side) -> be.inventory);
		items(event, RegistryManager.STAMPER_ENTITY.get(), (be, side) -> be.stamp);

		// --- item handlers, sided ---
		items(event, RegistryManager.ALCHEMY_TABLET_ENTITY.get(),
				(be, side) -> side == Direction.DOWN ? be.outputHandler : be.inventory);
		items(event, RegistryManager.CRYSTAL_CELL_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be.inventory : null);
		items(event, RegistryManager.HEARTH_COIL_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be.inventory : null);
		items(event, RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(),
				(be, side) -> side != Direction.DOWN ? be.inventory : null);
		items(event, RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(),
				(be, side) -> side != Direction.DOWN ? be.inventory : null);
		items(event, RegistryManager.EMBER_BORE_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.UP) ? be.inventory : null);
		items(event, RegistryManager.ITEM_DROPPER_ENTITY.get(),
				(be, side) -> side == Direction.UP ? be.inventory : null);
		items(event, RegistryManager.ITEM_VACUUM_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && side == facing.getOpposite() ? be.inventory : null;
		});
		items(event, RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), (be, side) -> {
			if (side == null)
				return be.inventory;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && side.getOpposite() != facing ? be.inventory : null;
		});

		// --- item pipes ---
		items(event, RegistryManager.ITEM_PIPE_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.inventory;
		});
		items(event, RegistryManager.ITEM_EXTRACTOR_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.inventory;
		});
		items(event, RegistryManager.ITEM_TRANSFER_ENTITY.get(), (be, side) -> {
			if (side == null)
				return be.inventory;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			if (facing == null)
				return null;
			if (side.getOpposite() == facing)
				return be.outputSide;
			return side.getAxis() == facing.getAxis() ? be.inventory : null;
		});

		// --- fluid pipes ---
		fluids(event, RegistryManager.FLUID_PIPE_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.tank;
		});
		fluids(event, RegistryManager.FLUID_EXTRACTOR_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.tank;
		});
		fluids(event, RegistryManager.FLUID_TRANSFER_ENTITY.get(), (be, side) -> {
			if (side == null)
				return be.tank;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			if (facing == null)
				return null;
			if (side.getOpposite() == facing)
				return be.outputSide;
			return side.getAxis() == facing.getAxis() ? be.tank : null;
		});

		// --- fluid tanks (block entity is the handler) ---
		fluids(event, RegistryManager.FLUID_VESSEL_ENTITY.get(), (be, side) -> be);
		fluids(event, RegistryManager.MELTER_TOP_ENTITY.get(), (be, side) -> be);
		fluids(event, RegistryManager.STAMP_BASE_ENTITY.get(), (be, side) -> be);
		fluids(event, RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), (be, side) -> be);
		fluids(event, RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(), (be, side) -> be);
		fluids(event, RegistryManager.RESERVOIR_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be : null);
		fluids(event, RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(),
				(be, side) -> side != Direction.UP ? be : null);

		// --- fluid handlers, sided ---
		fluids(event, RegistryManager.CAMINITE_VALVE_ENTITY.get(),
				(be, side) -> (side == null || side.getAxis() != Direction.Axis.Y) ? be.fluidHandler : null);
		fluids(event, RegistryManager.CATALYTIC_PLUG_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && (side == null || side == facing) ? be.tank : null;
		});
		fluids(event, RegistryManager.WILDFIRE_STIRLING_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && (side == null || side == facing) ? be.tank : null;
		});
		fluids(event, RegistryManager.MINI_BOILER_ENTITY.get(), (be, side) -> {
			if (side == Direction.UP)
				return be.gasTank;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.HORIZONTAL_FACING);
			return (side == Direction.DOWN || side != facing) ? be.fluidTank : null;
		});
		fluids(event, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), (be, side) -> {
			if (side == null)
				return null;
			return switch (side) {
			case NORTH -> be.north;
			case SOUTH -> be.south;
			case EAST -> be.east;
			case WEST -> be.west;
			default -> null;
			};
		});

		// --- mechanical core proxies every capability to the attached multiblock ---
		proxy(event, Capabilities.Item.BLOCK);
		proxy(event, Capabilities.Fluid.BLOCK);
		proxy(event, EmbersCapabilities.EMBER_CAPABILITY);
		proxy(event, EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY);
	}

	private static <T> void proxy(RegisterCapabilitiesEvent event, BlockCapability<T, Direction> cap) {
		BlockEntityType<MechanicalCoreBlockEntity> type = RegistryManager.MECHANICAL_CORE_ENTITY.get();
		event.registerBlockEntity(cap, type, (be, side) -> {
			if (be.getLevel() == null)
				return null;
			BlockEntityDirection multiblock = be.getAttachedMultiblock(ConfigManager.MAX_PROXY_DISTANCE.get());
			if (multiblock == null)
				return null;
			return be.getLevel().getCapability(cap, multiblock.blockEntity.getBlockPos(), multiblock.direction);
		});
	}

	private static Direction facing(BlockState state, net.minecraft.world.level.block.state.properties.EnumProperty<Direction> property) {
		return state.hasProperty(property) ? state.getValue(property) : null;
	}
}
