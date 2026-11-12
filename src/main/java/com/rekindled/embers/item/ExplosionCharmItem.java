package com.rekindled.embers.item;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.compat.curios.CuriosCompat;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ExplosionCharmItem extends Item implements IEmbersCurioItem {
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



	public static final int COOLDOWN = 100;
	public static final int MERCY_TIME = 5;

	public ExplosionCharmItem(Properties properties) {
		super(properties);
		NeoForge.EVENT_BUS.register(this);
	}

	public static String ITEM_COOLDOWN_KEY = Embers.MODID + ":cooldown_time";

	public static boolean hasItemCooledDown(ItemStack stack, long time, int cooldown) {
		if (_hasTag(stack)) {
			CompoundTag nbt = _getTag(stack);
			if (nbt.contains(ITEM_COOLDOWN_KEY)) {
				return (nbt.getLong(ITEM_COOLDOWN_KEY) + cooldown) < time;
			}
		}
		return true;
	}

	public static boolean hasItemMercy(ItemStack stack, long time, int mercy) {
		if (_hasTag(stack)) {
			CompoundTag nbt = _getTag(stack);
			if (nbt.contains(ITEM_COOLDOWN_KEY)) {
				return (nbt.getLong(ITEM_COOLDOWN_KEY) + mercy) > time;
			}
		}
		return true;
	}

	public static ItemStack setItemCooldown(ItemStack stack, long time) {
		_getOrCreateTag(stack).putLong(ITEM_COOLDOWN_KEY, time);
		return stack;
	}

	@SubscribeEvent
	public void onExplosion(ExplosionEvent.Start event) {
		if (event.isCanceled())
			return;
		Explosion explosion = event.getExplosion();
		float f3 = 4.0F;
		Vec3 explosionPos = new net.minecraft.world.phys.Vec3(explosion.center().x, explosion.center().y, explosion.center().z);
		int k1 = Mth.floor(explosionPos.x - (double) f3 - 1.0D);
		int l1 = Mth.floor(explosionPos.x + (double) f3 + 1.0D);
		int i2 = Mth.floor(explosionPos.y - (double) f3 - 1.0D);
		int i1 = Mth.floor(explosionPos.y + (double) f3 + 1.0D);
		int j2 = Mth.floor(explosionPos.z - (double) f3 - 1.0D);
		int j1 = Mth.floor(explosionPos.z + (double) f3 + 1.0D);
		List<Entity> entities = event.getLevel().getEntities((Entity) null, new AABB(k1, i2, j2, l1, i1, j1), EntitySelector.NO_SPECTATORS);

		for (Entity entity : entities) {
			if (event.isCanceled())
				return;

			if (entity instanceof LivingEntity) {
				CuriosCompat.checkForCurios((LivingEntity) entity, stack -> {
					if (stack.getItem() == this) {
						long time = event.getLevel().getGameTime();
						if (hasItemCooledDown(stack, time, COOLDOWN) || hasItemMercy(stack, time, MERCY_TIME)) {
							event.getLevel().playSound(null, explosionPos.x, explosionPos.y, explosionPos.z, EmbersSounds.EXPLOSION_CHARM_ABSORB.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

							if (event.getLevel() instanceof ServerLevel server) {
								server.sendParticles(GlowParticleOptions.EMBER, explosionPos.x, explosionPos.y, explosionPos.z, 3, 0.0, 0.0, 0.0, 0.1);
								server.sendParticles(SmokeParticleOptions.BIG_SMOKE, explosionPos.x, explosionPos.y, explosionPos.z, 20, 0.25, 0.25, 0.25, 1.0);

							}
							event.setCanceled(true);
							if (hasItemCooledDown(stack, time, COOLDOWN)) {
								event.getLevel().playSound(null, explosionPos.x, explosionPos.y, explosionPos.z, EmbersSounds.EXPLOSION_CHARM_RECHARGE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
								setItemCooldown(stack, time);
							}
						}
						return true;
					}
					return false;
				});
			}
		}
	}
}
