package com.rekindled.embers.item;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.datagen.EmbersDamageTypeTags;
import com.rekindled.embers.datagen.EmbersSounds;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class InflictorGemItem extends Item implements IInflictorGem {
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



	public InflictorGemItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && player.isSecondaryUseActive() && _getOrCreateTag(stack).contains("type")) {
			net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(stack);
			tag.remove("type");
			_setTag(stack, tag);
			if (player.getHealth() > 1f)
				player.setHealth(Math.max(player.getHealth() - 10.0f, 1f));
		}
		return player.isSecondaryUseActive() ? InteractionResultHolder.consume(stack) : InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, context, tooltip, isAdvanced);
		if (_getOrCreateTag(stack).contains("type")) {
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor", _getOrCreateTag(stack).getString("type")).withStyle(ChatFormatting.GRAY));
		} else {
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor.none").withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public void attuneSource(ItemStack stack, LivingEntity entity, DamageSource source) {
		String damageType = source.type().msgId();
		if (!source.is(EmbersDamageTypeTags.INFLICTOR_GEM_BLACKLIST)) {
			net.minecraft.nbt.CompoundTag tag = _getOrCreateTag(stack);
			tag.putString("type", damageType);
			_setTag(stack, tag);
			if (entity != null)
				entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), EmbersSounds.INFLICTOR_GEM.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
		}
	}

	@Override
	public String getAttunedSource(ItemStack stack) {
		if (!stack.isEmpty() && _getOrCreateTag(stack).contains("type"))
			return _getOrCreateTag(stack).getString("type");
		return null;
	}
	@Override
	public float getDamageResistance(ItemStack stack, float modifier) {
		return 0.35f;
	}
}
