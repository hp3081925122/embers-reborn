package com.rekindled.embers.compat.jade;

import com.rekindled.embers.Embers;
import com.rekindled.embers.blockentity.MachineProgress;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.ui.MessageType;

/**
 * Colours the Jade tooltip border by machine work progress, like the block breaking progress bar.
 * Progress is not synced to the client, so the server sends it as Jade server data.
 */
@WailaPlugin
public class EmbersJadePlugin implements IWailaPlugin {

	private static final String KEY = "embers_progress";
	private static final Identifier UID = Identifier.fromNamespaceAndPath(Embers.MODID, "machine_progress");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(new IServerDataProvider<BlockAccessor>() {
			@Override
			public void appendServerData(CompoundTag data, BlockAccessor accessor) {
				float progress = MachineProgress.get(accessor.getBlockEntity());
				if (progress >= 0)
					data.putFloat(KEY, progress);
			}

			@Override
			public Identifier getUid() {
				return UID;
			}
		}, BlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.addTooltipCollectedCallback((box, accessor) -> {
			if (accessor instanceof BlockAccessor blockAccessor && blockAccessor.getServerData().contains(KEY))
				box.setBoxProgress(MessageType.SUCCESS, blockAccessor.getServerData().getFloatOr(KEY, 0.0F));
		});
	}
}
