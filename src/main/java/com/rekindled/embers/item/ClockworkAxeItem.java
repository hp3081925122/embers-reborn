package com.rekindled.embers.item;

import java.util.Optional;

import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class ClockworkAxeItem extends ClockworkToolItem {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public ClockworkAxeItem(Properties properties) {
		super(EmbersTiers.CLOCKWORK_AXE, BlockTags.MINEABLE_WITH_AXE, properties.attributes(DiggerItem.createAttributes(EmbersTiers.CLOCKWORK_AXE, 2.0F, -3.0F)));
	}

	public InteractionResult useOn(UseOnContext pContext) {
		ItemStack itemstack = pContext.getItemInHand();
		if (!hasEmber(itemstack)) {
			return InteractionResult.PASS;
		}
		Level level = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		Player player = pContext.getPlayer();
		BlockState blockstate = level.getBlockState(blockpos);
		Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(pContext, net.neoforged.neoforge.common.ItemAbilities.AXE_STRIP, false));
		Optional<BlockState> optional1 = optional.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(pContext, net.neoforged.neoforge.common.ItemAbilities.AXE_SCRAPE, false));
		Optional<BlockState> optional2 = optional.isPresent() || optional1.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(pContext, net.neoforged.neoforge.common.ItemAbilities.AXE_WAX_OFF, false));
		Optional<BlockState> optional3 = Optional.empty();
		if (optional.isPresent()) {
			level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			optional3 = optional;
		} else if (optional1.isPresent()) {
			level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3005, blockpos, 0);
			optional3 = optional1;
		} else if (optional2.isPresent()) {
			level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3004, blockpos, 0);
			optional3 = optional2;
		}

		if (optional3.isPresent()) {
			if (player instanceof ServerPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
			}

			level.setBlock(blockpos, optional3.get(), 11);
			level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional3.get()));
			if (player != null) {
				itemstack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
			}
			net.minecraft.nbt.CompoundTag useTag = _getOrCreateTag(itemstack);
			useTag.putBoolean("didUse", true);
			_setTag(itemstack, useTag);
			return InteractionResult.SUCCESS;
		} else {
			return InteractionResult.PASS;
		}
	}

	@Override
	public boolean canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility ability) {
		return hasEmber(stack) && net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability);
	}
}
