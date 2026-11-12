package com.rekindled.embers.augment;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class ToolExplosion extends Explosion {

	public ItemStack tool = ItemStack.EMPTY;

	// Own copies of private Explosion fields (1.21.1 made them all private)
	private final Level myLevel;
	private final double myX, myY, myZ;
	private final float myRadius;
	private final boolean myFire;

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, List<BlockPos> pPositions) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, false, Explosion.BlockInteraction.DESTROY, pPositions);
		this.tool = tool;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myFire = false;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, pFire, pBlockInteraction, pPositions);
		this.tool = tool;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myFire = pFire;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pX, double pY, double pZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pX, pY, pZ, pRadius, pFire, pBlockInteraction);
		this.tool = tool;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myFire = pFire;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, Entity pSource, DamageSource pDamageSource, ExplosionDamageCalculator pDamageCalculator, double pX, double pY, double pZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pDamageSource, pDamageCalculator, pX, pY, pZ, pRadius, pFire, pBlockInteraction,
			net.minecraft.core.particles.ParticleTypes.EXPLOSION,
			net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
			net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE);
		this.tool = tool;
		this.myLevel = pLevel;
		this.myX = pX;
		this.myY = pY;
		this.myZ = pZ;
		this.myRadius = pRadius;
		this.myFire = pFire;
	}

	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	@Override
	public void finalizeExplosion(boolean pSpawnParticles) {
		if (myLevel.isClientSide) {
			myLevel.playLocalSound(myX, myY, myZ, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + (myLevel.random.nextFloat() - myLevel.random.nextFloat()) * 0.2F) * 0.7F, false);
		}

		boolean flag = this.interactsWithBlocks();
		if (pSpawnParticles) {
			if (!(myRadius < 2.0F) && flag) {
				myLevel.addParticle(ParticleTypes.EXPLOSION_EMITTER, myX, myY, myZ, 1.0D, 0.0D, 0.0D);
			} else {
				myLevel.addParticle(ParticleTypes.EXPLOSION, myX, myY, myZ, 1.0D, 0.0D, 0.0D);
			}
		}

		if (flag) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
			boolean flag1 = this.getIndirectSourceEntity() instanceof Player;
			Util.shuffle(this.getToBlow(), myLevel.random);

			for(BlockPos blockpos : this.getToBlow()) {
				BlockState blockstate = myLevel.getBlockState(blockpos);
				if (!blockstate.isAir()) {
					BlockPos blockpos1 = blockpos.immutable();
					myLevel.getProfiler().push("explosion_blocks");
					if (blockstate.canDropFromExplosion(myLevel, blockpos, this)) {
						Level $$9 = myLevel;
						if ($$9 instanceof ServerLevel) {
							ServerLevel serverlevel = (ServerLevel)$$9;
							BlockEntity blockentity = blockstate.hasBlockEntity() ? myLevel.getBlockEntity(blockpos) : null;
							LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel))
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
								.withParameter(LootContextParams.TOOL, tool)
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
								.withOptionalParameter(LootContextParams.THIS_ENTITY, this.getDirectSourceEntity());
							if (this.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
								lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, myRadius);
							}

							blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
							blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
								// addBlockDrops equivalent: collect drops
								objectarraylist.add(Pair.of(p_46074_, blockpos1));
							});
						}
					}

					blockstate.onBlockExploded(myLevel, blockpos, this);
					myLevel.getProfiler().pop();
				}
			}

			for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
				Block.popResource(myLevel, pair.getSecond(), pair.getFirst());
			}
		}

		if (myFire) {
			for(BlockPos blockpos2 : this.getToBlow()) {
				if (myLevel.random.nextInt(3) == 0 && myLevel.getBlockState(blockpos2).isAir() && myLevel.getBlockState(blockpos2.below()).isSolidRender(myLevel, blockpos2.below())) {
					myLevel.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(myLevel, blockpos2));
				}
			}
		}
	}
}
