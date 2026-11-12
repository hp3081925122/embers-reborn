package com.rekindled.embers.compat.jade;

import com.rekindled.embers.Embers;
import com.rekindled.embers.block.FluidExtractorBlock;
import com.rekindled.embers.blockentity.FluidExtractorBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(Embers.MODID)
public class FluidExtractorJadePlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "fluid_extractor_stuck");
	private static final String STUCK = "Stuck";

	@Override
	public void register(snownee.jade.api.IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(this, FluidExtractorBlock.class);
	}

	@Override
	public void registerClient(snownee.jade.api.IWailaClientRegistration registration) {
		registration.registerBlockComponent(this, FluidExtractorBlock.class);
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		if (accessor.getBlockEntity() instanceof FluidExtractorBlockEntity extractor && stuck(extractor))
			data.putBoolean(STUCK, true);
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (accessor.getServerData().getBoolean(STUCK))
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.fluid_extractor.stuck").withStyle(ChatFormatting.GRAY));
	}

	private static boolean stuck(FluidExtractorBlockEntity extractor) {
		if (extractor.getLevel() == null || extractor.tank.getFluid().isEmpty() || !extractor.getLevel().hasNeighborSignal(extractor.getBlockPos()))
			return false;
		for (Direction facing : Direction.values()) {
			if (extractor.getConnection(facing).transfer && !extractor.from[facing.get3DDataValue()])
				return false;
		}
		return true;
	}
}
