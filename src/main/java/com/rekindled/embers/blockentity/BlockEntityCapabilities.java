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

	private static final BlockCapability<IItemHandler, Direction> ITEM = Capabilities.ItemHandler.BLOCK;
	private static final BlockCapability<IFluidHandler, Direction> FLUID = Capabilities.FluidHandler.BLOCK;

	public static void register(RegisterCapabilitiesEvent event) {
		// --- item handlers, all sides ---
		event.registerBlockEntity(ITEM, RegistryManager.ALCHEMY_PEDESTAL_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.BIN_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.CATALYSIS_CHAMBER_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.COMBUSTION_CHAMBER_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.CINDER_PLINTH_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.COPPER_CHARGER_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.DAWNSTONE_ANVIL_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.IGNEM_REACTOR_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.MELTER_TOP_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.STAMP_BASE_ENTITY.get(), (be, side) -> be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.STAMPER_ENTITY.get(), (be, side) -> be.stamp);

		// --- item handlers, sided ---
		event.registerBlockEntity(ITEM, RegistryManager.ALCHEMY_TABLET_ENTITY.get(),
				(be, side) -> side == Direction.DOWN ? be.outputHandler : be.inventory);
		event.registerBlockEntity(ITEM, RegistryManager.CRYSTAL_CELL_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.HEARTH_COIL_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(),
				(be, side) -> side != Direction.DOWN ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(),
				(be, side) -> side != Direction.DOWN ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.EMBER_BORE_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.UP) ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.ITEM_DROPPER_ENTITY.get(),
				(be, side) -> side == Direction.UP ? be.inventory : null);
		event.registerBlockEntity(ITEM, RegistryManager.ITEM_VACUUM_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && side == facing.getOpposite() ? be.inventory : null;
		});
		event.registerBlockEntity(ITEM, RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get(), (be, side) -> {
			if (side == null)
				return be.inventory;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && side.getOpposite() != facing ? be.inventory : null;
		});

		// --- item pipes ---
		event.registerBlockEntity(ITEM, RegistryManager.ITEM_PIPE_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.inventory;
		});
		event.registerBlockEntity(ITEM, RegistryManager.ITEM_EXTRACTOR_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.inventory;
		});
		event.registerBlockEntity(ITEM, RegistryManager.ITEM_TRANSFER_ENTITY.get(), (be, side) -> {
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
		event.registerBlockEntity(FLUID, RegistryManager.FLUID_PIPE_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.tank;
		});
		event.registerBlockEntity(FLUID, RegistryManager.FLUID_EXTRACTOR_ENTITY.get(), (be, side) -> {
			if (side != null && be.getConnection(side).transfer)
				return be.sideHandlers[side.get3DDataValue()];
			return be.tank;
		});
		event.registerBlockEntity(FLUID, RegistryManager.FLUID_TRANSFER_ENTITY.get(), (be, side) -> {
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
		event.registerBlockEntity(FLUID, RegistryManager.FLUID_VESSEL_ENTITY.get(), (be, side) -> be);
		event.registerBlockEntity(FLUID, RegistryManager.MELTER_TOP_ENTITY.get(), (be, side) -> be);
		event.registerBlockEntity(FLUID, RegistryManager.STAMP_BASE_ENTITY.get(), (be, side) -> be);
		event.registerBlockEntity(FLUID, RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), (be, side) -> be);
		event.registerBlockEntity(FLUID, RegistryManager.PRESSURE_REFINERY_BOTTOM_ENTITY.get(), (be, side) -> be);
		event.registerBlockEntity(FLUID, RegistryManager.RESERVOIR_ENTITY.get(),
				(be, side) -> (side == null || side == Direction.DOWN) ? be : null);
		event.registerBlockEntity(FLUID, RegistryManager.GEOLOGIC_SEPARATOR_ENTITY.get(),
				(be, side) -> side != Direction.UP ? be : null);

		// --- fluid handlers, sided ---
		event.registerBlockEntity(FLUID, RegistryManager.CAMINITE_VALVE_ENTITY.get(),
				(be, side) -> (side == null || side.getAxis() != Direction.Axis.Y) ? be.fluidHandler : null);
		event.registerBlockEntity(FLUID, RegistryManager.CATALYTIC_PLUG_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && (side == null || side == facing) ? be.tank : null;
		});
		event.registerBlockEntity(FLUID, RegistryManager.WILDFIRE_STIRLING_ENTITY.get(), (be, side) -> {
			Direction facing = facing(be.getBlockState(), BlockStateProperties.FACING);
			return facing != null && (side == null || side == facing) ? be.tank : null;
		});
		event.registerBlockEntity(FLUID, RegistryManager.MINI_BOILER_ENTITY.get(), (be, side) -> {
			if (side == Direction.UP)
				return be.gasTank;
			Direction facing = facing(be.getBlockState(), BlockStateProperties.HORIZONTAL_FACING);
			return (side == Direction.DOWN || side != facing) ? be.fluidTank : null;
		});
		event.registerBlockEntity(FLUID, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), (be, side) -> {
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
		proxy(event, ITEM);
		proxy(event, FLUID);
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

	private static Direction facing(BlockState state, net.minecraft.world.level.block.state.properties.DirectionProperty property) {
		return state.hasProperty(property) ? state.getValue(property) : null;
	}
}
