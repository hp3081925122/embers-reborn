package com.rekindled.embers.augment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

/**
 * Blasting core block-break explosion. Breaks a fixed list of blocks and drops them as if mined with {@link #tool}.
 * 26.1: Explosion is an interface; this extends ServerExplosion and replaces explode() (1.20.1 overrode finalizeExplosion).
 */
public class ToolExplosion extends ServerExplosion {

	public ItemStack tool = ItemStack.EMPTY;
	private final List<BlockPos> positions;

	public ToolExplosion(ItemStack tool, ServerLevel level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction, List<BlockPos> positions) {
		super(level, source, null, null, new Vec3(x, y, z), radius, fire, blockInteraction);
		this.tool = tool;
		this.positions = new ArrayList<>(positions);
	}

	@Override
	public int explode() {
		ServerLevel level = level();
		level.gameEvent(getDirectSourceEntity(), GameEvent.EXPLODE, center());
		if (getBlockInteraction() == Explosion.BlockInteraction.KEEP)
			return 0;

		List<Pair<ItemStack, BlockPos>> drops = new ArrayList<>();
		boolean dropExperience = getIndirectSourceEntity() instanceof Player;
		net.minecraft.util.Util.shuffle(positions, level.getRandom());

		for (BlockPos pos : positions) {
			BlockState state = level.getBlockState(pos);
			if (state.isAir())
				continue;
			BlockPos immutable = pos.immutable();
			if (state.canDropFromExplosion(level, pos, this)) {
				BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
				LootParams.Builder params = new LootParams.Builder(level)
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
						.withParameter(LootContextParams.TOOL, tool)
						.withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
						.withOptionalParameter(LootContextParams.THIS_ENTITY, getDirectSourceEntity());
				if (getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY)
					params.withParameter(LootContextParams.EXPLOSION_RADIUS, radius());
				state.spawnAfterBreak(level, pos, ItemStack.EMPTY, dropExperience);
				state.getDrops(params).forEach(stack -> drops.add(Pair.of(stack, immutable)));
			}
			state.onBlockExploded(level, pos, this);
		}

		for (Pair<ItemStack, BlockPos> pair : drops)
			Block.popResource(level, pair.getSecond(), pair.getFirst());
		return positions.size();
	}
}
