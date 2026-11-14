package com.rekindled.embers.compat.jade;

import com.rekindled.embers.Embers;
import com.rekindled.embers.block.ItemExtractorBlock;
import com.rekindled.embers.blockentity.ItemExtractorBlockEntity;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin(Embers.MODID)
public class ItemExtractorJadePlugin implements IWailaPlugin {

	private static final Identifier UID = Identifier.fromNamespaceAndPath(Embers.MODID, "item_extractor_stuck");
	private static final String STUCK = "Stuck";

	@Override
	public void register(snownee.jade.api.IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(Server.INSTANCE, ItemExtractorBlock.class);
	}

	@Override
	public void registerClient(snownee.jade.api.IWailaClientRegistration registration) {
		registration.registerBlockComponent(Client.INSTANCE, ItemExtractorBlock.class);
	}

	public enum Server implements IServerDataProvider<BlockAccessor> {
		INSTANCE;

		@Override
		public Identifier getUid() {
			return UID;
		}

		@Override
		public void appendServerData(CompoundTag data, BlockAccessor accessor) {
			if (accessor.getBlockEntity() instanceof ItemExtractorBlockEntity extractor && stuck(extractor))
				data.putBoolean(STUCK, true);
		}
	}

	public enum Client implements IBlockComponentProvider {
		INSTANCE;

		@Override
		public Identifier getUid() {
			return UID;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().getBooleanOr(STUCK, false))
				tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item_extractor.stuck").withStyle(ChatFormatting.GRAY));
		}
	}

	private static boolean stuck(ItemExtractorBlockEntity extractor) {
		if (extractor.getLevel() == null || extractor.inventory.getStackInSlot(0).isEmpty() || !extractor.getLevel().hasNeighborSignal(extractor.getBlockPos()))
			return false;
		for (Direction facing : Direction.values()) {
			if (extractor.getConnection(facing).transfer && !extractor.from[facing.get3DDataValue()])
				return false;
		}
		return true;
	}
}
