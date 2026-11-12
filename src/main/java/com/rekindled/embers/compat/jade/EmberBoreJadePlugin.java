package com.rekindled.embers.compat.jade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.block.EmberBoreEdgeBlock;
import com.rekindled.embers.block.MechEdgeBlockBase;
import com.rekindled.embers.blockentity.EmberBoreBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;

/**
 * The bore's inventory lives on the center block. Edges have no capability, so Jade
 * only listed items when looking at the middle. This shows that same inventory, and the
 * dig cycle, on every block of the structure.
 */
@WailaPlugin
public class EmberBoreJadePlugin implements IWailaPlugin {

	private static final ResourceLocation ITEMS = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "bore_items");
	private static final ResourceLocation PROGRESS = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "bore_progress");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerItemStorage(Items.INSTANCE, com.rekindled.embers.block.EmberBoreBlock.class);
		registration.registerItemStorage(Items.INSTANCE, EmberBoreEdgeBlock.class);
		registration.registerProgress(Progress.INSTANCE, com.rekindled.embers.block.EmberBoreBlock.class);
		registration.registerProgress(Progress.INSTANCE, EmberBoreEdgeBlock.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerItemStorageClient(Items.INSTANCE);
		registration.registerProgressClient(Progress.INSTANCE);
	}

	static EmberBoreBlockEntity bore(Accessor<?> accessor) {
		if (!(accessor instanceof BlockAccessor block) || block.getLevel() == null)
			return null;
		BlockPos pos = block.getPosition();
		BlockState state = block.getBlockState();
		if (state.getBlock() instanceof EmberBoreEdgeBlock)
			pos = pos.offset(state.getValue(MechEdgeBlockBase.EDGE).centerPos);
		BlockEntity entity = block.getLevel().getBlockEntity(pos);
		return entity instanceof EmberBoreBlockEntity bore ? bore : null;
	}

	public enum Items implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
		INSTANCE;

		@Override
		public ResourceLocation getUid() {
			return ITEMS;
		}

		@Override
		public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
			EmberBoreBlockEntity bore = bore(accessor);
			if (bore == null)
				return null;
			return ItemView.groupOf(bore.inventory, accessor);
		}

		@Override
		public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
			return ClientViewGroup.map(groups, ItemView::new, null);
		}
	}

	public enum Progress implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, ProgressView> {
		INSTANCE;

		@Override
		public ResourceLocation getUid() {
			return PROGRESS;
		}

		@Override
		public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
			EmberBoreBlockEntity bore = bore(accessor);
			if (bore == null)
				return null;
			float progress = bore.digProgress();
			if (progress < 0)
				return null;
			return List.of(new ViewGroup<>(List.of(ProgressView.create(progress))));
		}

		@Override
		public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
			return ClientViewGroup.map(groups, tag -> {
				ProgressView view = ProgressView.read(tag);
				view.text = Component.translatable(Embers.MODID + ".jade.bore_progress", Math.round(view.progress * 100f));
				view.style.color(0xFFCC6A28);
				return view;
			}, null);
		}
	}
}
