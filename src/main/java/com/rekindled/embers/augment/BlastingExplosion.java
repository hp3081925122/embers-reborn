package com.rekindled.embers.augment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class BlastingExplosion extends Explosion {

	public List<Entity> entitiesToBlast;
	public float damage;
	public Entity source;

	// Own copies of private Explosion fields (1.21.1 made them all private)
	private final Level myLevel;
	private final double myX, myY, myZ;
	private final float myRadius;
	private final ExplosionDamageCalculator myDamageCalculator;

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, List<BlockPos> pPositions) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, false, Explosion.BlockInteraction.KEEP, pPositions);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myDamageCalculator = new ExplosionDamageCalculator();
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, pFire, pBlockInteraction, pPositions);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myDamageCalculator = new ExplosionDamageCalculator();
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, pFire, pBlockInteraction);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myDamageCalculator = new ExplosionDamageCalculator();
	}

	public BlastingExplosion(List<Entity> entitiesToBlast, float damage, Level pLevel, Entity pSource, DamageSource pDamageSource, ExplosionDamageCalculator pDamageCalculator, double pX, double pY, double pZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pDamageSource, pDamageCalculator, pX, pY, pZ, pRadius, pFire, pBlockInteraction,
			net.minecraft.core.particles.ParticleTypes.EXPLOSION,
			net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
			net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE);
		this.entitiesToBlast = entitiesToBlast;
		this.damage = damage;
		this.source = pSource;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myDamageCalculator = pDamageCalculator;
	}

	@Override
	public void explode() {
		myLevel.gameEvent(source, GameEvent.EXPLODE, new Vec3(myX, myY, myZ));
		Set<BlockPos> set = Sets.newHashSet();
		int i = 16;

		for (int j = 0; j < i; ++j) {
			for (int k = 0; k < i; ++k) {
				for (int l = 0; l < i; ++l) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float f = myRadius * (0.7F + myLevel.getRandom().nextFloat() * 0.6F);
						double d4 = myX;
						double d6 = myY;
						double d8 = myZ;

						for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
							BlockPos blockpos = BlockPos.containing(d4, d6, d8);
							BlockState blockstate = myLevel.getBlockState(blockpos);
							FluidState fluidstate = myLevel.getFluidState(blockpos);
							if (!myLevel.isInWorldBounds(blockpos)) {
								break;
							}

							Optional<Float> optional = myDamageCalculator.getBlockExplosionResistance(this, myLevel, blockpos, blockstate, fluidstate);
							if (optional.isPresent()) {
								f -= (optional.get() + f1) * f1;
							}

							if (f > 0.0F && myDamageCalculator.shouldBlockExplode(this, myLevel, blockpos, blockstate, f)) {
								set.add(blockpos);
							}

							d4 += d0 * f1;
							d6 += d1 * f1;
							d8 += d2 * f1;
						}
					}
				}
			}
		}

		this.getToBlow().addAll(set);
		float f2 = myRadius * 2.0F;
		EventHooks.onExplosionDetonate(myLevel, this, entitiesToBlast, f2);
		Vec3 vec3 = new Vec3(myX, myY, myZ);

		for (int k2 = 0; k2 < entitiesToBlast.size(); ++k2) {
			Entity entity = entitiesToBlast.get(k2);
			if (!entity.ignoreExplosion(this) && (source == null || !Objects.equals(entity.getUUID(), source.getUUID()))) {
				double d12 = Math.sqrt(entity.distanceToSqr(vec3)) / (double)f2;
				if (d12 <= 1.0D) {
					double d5 = entity.getX() - myX;
					double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - myY;
					double d9 = entity.getZ() - myZ;
					double d13 = Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
					if (d13 != 0.0D) {
						d5 /= d13;
						d7 /= d13;
						d9 /= d13;
						double d14 = (double)getSeenPercent(vec3, entity);
						double d10 = (1.0D - d12) * d14;
						entity.hurt(entity.level().damageSources().explosion(null, getIndirectSourceEntity()), damage);
						if (entity instanceof LivingEntity living)
							living.hurtTime = 0;
						double d11;
						if (entity instanceof LivingEntity) {
							LivingEntity livingentity = (LivingEntity)entity;
							d11 = 0.0f;
						} else {
							d11 = d10;
						}

						d5 *= d11;
						d7 *= d11;
						d9 *= d11;
						Vec3 vec31 = new Vec3(d5, d7, d9);
						entity.setDeltaMovement(entity.getDeltaMovement().add(vec31));
						if (entity instanceof Player) {
							Player player = (Player)entity;
							if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
								this.getHitPlayers().put(player, vec31);
							}
						}
					}
				}
			}
		}
	}
}
