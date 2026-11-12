package com.rekindled.embers.compat.jade;

import com.rekindled.embers.Embers;
import com.rekindled.embers.block.MixerCentrifugeBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/** The mixer's lower half has four input tanks Jade cannot list well; point players at the fluid dial instead. */
@WailaPlugin(Embers.MODID)
public class MixerCentrifugeJadePlugin implements IWailaPlugin, IBlockComponentProvider {

	private static final Identifier UID = Identifier.fromNamespaceAndPath(Embers.MODID, "mixer_fluid_dial_hint");

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(this, MixerCentrifugeBlock.class);
	}

	@Override
	public Identifier getUid() {
		return UID;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (accessor.getBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
				&& accessor.getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.mixer.fluid_dial").withStyle(ChatFormatting.GRAY));
	}
}
