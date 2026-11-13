package com.rekindled.embers.item;

import com.rekindled.embers.api.item.IEmberChargedTool;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClockworkToolItem extends Item implements IEmberChargedTool {
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



	public ClockworkToolItem(net.minecraft.world.item.ToolMaterial tier, TagKey<Block> blocks, float attackDamage, float attackSpeed, Properties properties) {
		// 等级耐久是 -1，表示不损坏。1.21 会把它写成最大耐久 -1，第一下就会碎
		super(properties.tool(tier, blocks, attackDamage, attackSpeed, 0.0F).component(DataComponents.UNBREAKABLE, new Unbreakable(false)));
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
		putBoolean(stack, "didUse", true);
		if (target.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(GlowParticleOptions.EMBER, target.getX(), target.getY() + target.getEyeHeight() / 1.5, target.getZ(), 70, 0.15, 0.15, 0.15, 0.6);
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		putBoolean(stack, "didUse", true);
		return super.mineBlock(stack, level, state, pos, entityLiving);
	}

	@Override
	public boolean isEnchantable(ItemStack pStack) {
		return true;
	}

	// 1.20.1 canApplyAtEnchantingTable: only WEAPON/DIGGER enchantments, never BREAKABLE (unbreaking/mending) or sweeping edge.
	// 1.21 enchantments are data-driven, so the old categories are matched by their supported_items tag.
	private static final java.util.Set<TagKey<net.minecraft.world.item.Item>> WEAPON_TAGS = java.util.Set.of(
			net.minecraft.tags.ItemTags.SWORD_ENCHANTABLE, net.minecraft.tags.ItemTags.SHARP_WEAPON_ENCHANTABLE,
			net.minecraft.tags.ItemTags.WEAPON_ENCHANTABLE, net.minecraft.tags.ItemTags.FIRE_ASPECT_ENCHANTABLE);
	private static final java.util.Set<TagKey<net.minecraft.world.item.Item>> DIGGER_TAGS = java.util.Set.of(
			net.minecraft.tags.ItemTags.MINING_ENCHANTABLE, net.minecraft.tags.ItemTags.MINING_LOOT_ENCHANTABLE);

	protected boolean allowsEnchantment(net.minecraft.core.Holder<Enchantment> enchant) {
		if (enchant.is(Enchantments.SWEEPING_EDGE))
			return false;
		return enchant.value().getSupportedItems().unwrapKey()
				.map(tag -> WEAPON_TAGS.contains(tag) || DIGGER_TAGS.contains(tag))
				.orElse(false);
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, net.minecraft.core.Holder<Enchantment> enchant) {
		return allowsEnchantment(enchant);
	}

	@Override
	public boolean isPrimaryItemFor(ItemStack stack, net.minecraft.core.Holder<Enchantment> enchant) {
		return allowsEnchantment(enchant);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		if (_hasTag(oldStack) && _hasTag(newStack)) {
			return slotChanged || _getTag(oldStack).getBooleanOr("poweredOn", false) != _getTag(newStack).getBooleanOr("poweredOn", false) || newStack.getItem() != oldStack.getItem();
		}
		return slotChanged || newStack.getItem() != oldStack.getItem();
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		if (_hasTag(oldStack) && _hasTag(newStack)) {
			return _getTag(oldStack).getBooleanOr("poweredOn", false) != _getTag(newStack).getBooleanOr("poweredOn", false);
		}
		return false;
	}

	private static void putBoolean(ItemStack stack, String key, boolean value) {
		net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(stack);
		tag.putBoolean(key, value);
		_setTag(stack, tag);
	}

	@Override
	public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel world, Entity entity, @org.jspecify.annotations.Nullable net.minecraft.world.entity.EquipmentSlot slot) {
		if (!stack.has(DataComponents.UNBREAKABLE)) {
			stack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
			stack.set(DataComponents.DAMAGE, 0);
		}
		// 26.1 only ticks inventories on the server; 'selected' became the main-hand slot
		if (slot != net.minecraft.world.entity.EquipmentSlot.MAINHAND)
			return;
		net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(stack);
		if (!_hasTag(stack)) {
			tag.putBoolean("poweredOn", false);
			tag.putBoolean("didUse", false);
			_setTag(stack, tag);
			return;
		}
		if (entity instanceof Player player) {
			boolean changed = false;
			if (world.getGameTime() % 5 == 0) {
				boolean powered = EmberInventoryUtil.getEmberTotal(player) > 5.0;
				if (tag.getBooleanOr("poweredOn", false) != powered) {
					tag.putBoolean("poweredOn", powered);
					changed = true;
				}
			}
			if (tag.getBooleanOr("didUse", false)) {
				tag.putBoolean("didUse", false);
				changed = true;
				EmberInventoryUtil.removeEmber(player, 5.0);
				if (EmberInventoryUtil.getEmberTotal(player) < 5.0)
					tag.putBoolean("poweredOn", false);
			}
			if (changed)
				_setTag(stack, tag);
		}
	}

	@Override
	public boolean hasEmber(ItemStack stack) {
		return _hasTag(stack) && _getTag(stack).getBooleanOr("poweredOn", false);
	}
}
