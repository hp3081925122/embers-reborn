package com.rekindled.embers.item;

import com.rekindled.embers.api.item.IEmberChargedTool;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkToolItem extends DiggerItem implements IEmberChargedTool {
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



	public ClockworkToolItem(Tier tier, TagKey<Block> blocks, Properties properties) {
		super(tier, blocks, properties);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		if (hasEmber(stack)) {
			entity.igniteForSeconds(2);
			return false;
		}
		return true;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (hasEmber(stack)) {
			return super.getDestroySpeed(stack, state);
		}
		return 0;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		_getOrCreateTag(stack).putBoolean("didUse", true);
		if (target.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(GlowParticleOptions.EMBER, target.getX(), target.getY() + target.getEyeHeight() / 1.5, target.getZ(), 70, 0.15, 0.15, 0.15, 0.6);
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		_getOrCreateTag(stack).putBoolean("didUse", true);
		return super.mineBlock(stack, level, state, pos, entityLiving);
	}

	@Override
	public boolean isEnchantable(ItemStack pStack) {
		return true;
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant){
		return true;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (_hasTag(oldStack) && _hasTag(newStack)) {
			return slotChanged || _getTag(oldStack).getBoolean("poweredOn") != _getTag(newStack).getBoolean("poweredOn") || newStack.getItem() != oldStack.getItem();
		}
		return slotChanged || newStack.getItem() != oldStack.getItem();
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		if (_hasTag(oldStack) && _hasTag(newStack)) {
			return _getTag(oldStack).getBoolean("poweredOn") != _getTag(newStack).getBoolean("poweredOn");
		}
		return false;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!selected || world.isClientSide())
			return;
		if (!_hasTag(stack)) {
			_getOrCreateTag(stack);
			_getTag(stack).putBoolean("poweredOn", false);
			_getTag(stack).putBoolean("didUse", false);
		} else {
			if (entity instanceof Player player) {
				if (world.getGameTime() % 5 == 0) {
					if (EmberInventoryUtil.getEmberTotal(player) > 5.0) {
						if (!_getTag(stack).getBoolean("poweredOn")) {
							_getTag(stack).putBoolean("poweredOn", true);
						}
					} else {
						if (_getTag(stack).getBoolean("poweredOn")) {
							_getTag(stack).putBoolean("poweredOn", false);
						}
					}
				}
				if (_getTag(stack).getBoolean("didUse")) {
					_getTag(stack).putBoolean("didUse", false);
					EmberInventoryUtil.removeEmber(player, 5.0);
					if (EmberInventoryUtil.getEmberTotal(player) < 5.0) {
						_getTag(stack).putBoolean("poweredOn", false);
					}
				}
			}
		}
	}

	@Override
	public boolean hasEmber(ItemStack stack) {
		return _hasTag(stack) && _getTag(stack).getBoolean("poweredOn");
	}
}
