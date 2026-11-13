package com.rekindled.embers.augment;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Blasting core hit explosion: deals a fixed damage to a given list of entities, never breaks blocks,
 * and does not knock back living entities (same as 1.20.1).
 * 26.1: Explosion is an interface; this extends ServerExplosion and replaces explode().
 */
public class BlastingExplosion extends ServerExplosion {

	public List<Entity> entitiesToBlast;
	public float damage;
	public @Nullable Entity source;

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, ServerLevel level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction) {
		super(level, source, null, null, new Vec3(x, y, z), radius, fire, blockInteraction);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = source;
	}

	@Override
	public int explode() {
		ServerLevel level = level();
		Vec3 center = center();
		level.gameEvent(source, GameEvent.EXPLODE, center);
		float doubleRadius = radius() * 2.0F;
		EventHooks.onExplosionDetonate(level, this, entitiesToBlast, List.of());

		for (Entity entity : entitiesToBlast) {
			if (entity.ignoreExplosion(this) || (source != null && Objects.equals(entity.getUUID(), source.getUUID())))
				continue;
			double dist = Math.sqrt(entity.distanceToSqr(center)) / doubleRadius;
			if (dist > 1.0D)
				continue;
			double dx = entity.getX() - center.x;
			double dy = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - center.y;
			double dz = entity.getZ() - center.z;
			double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
			if (len == 0.0D)
				continue;
			dx /= len;
			dy /= len;
			dz /= len;
			double power = (1.0D - dist) * getSeenPercent(center, entity);
			entity.hurtServer(level, level.damageSources().explosion(null, getIndirectSourceEntity()), damage);
			double knockback = 0.0D;
			if (entity instanceof LivingEntity living)
				living.hurtTime = 0;
			else
				knockback = power;
			Vec3 push = new Vec3(dx * knockback, dy * knockback, dz * knockback);
			entity.setDeltaMovement(entity.getDeltaMovement().add(push));
			if (entity instanceof Player player && !player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying))
				getHitPlayers().put(player, push);
		}
		return 0;
	}
}
