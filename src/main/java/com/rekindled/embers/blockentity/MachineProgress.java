package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Work progress of a machine as a 0..1 fraction, for tooltips such as Jade.
 * Lives in this package because the progress fields are package-private.
 */
public final class MachineProgress {

	private MachineProgress() {}

	/** @return progress in [0, 1], or -1 if the block is not a machine with progress or is idle */
	public static float get(BlockEntity be) {
		if (be == null || be.getLevel() == null)
			return -1;
		// upper halves forward to the lower block that does the work
		if (be instanceof MelterTopBlockEntity || be instanceof EmberActivatorTopBlockEntity
				|| be instanceof PressureRefineryTopBlockEntity || be instanceof MechanicalPumpTopBlockEntity)
			be = be.getLevel().getBlockEntity(be.getBlockPos().below());

		if (be instanceof MelterBottomBlockEntity m)
			return ratio(m.progress, time(m, ConfigManager.MELTER_PROCESS_TIME.get(), m.upgrades));
		if (be instanceof EmberActivatorBottomBlockEntity a)
			return ratio(a.progress, time(a, EmberActivatorBottomBlockEntity.PROCESS_TIME, a.upgrades) + 1);
		if (be instanceof PressureRefineryBottomBlockEntity r)
			return ratio(r.progress, time(r, PressureRefineryBottomBlockEntity.PROCESS_TIME, r.upgrades) + 1);
		if (be instanceof CinderPlinthBlockEntity c)
			return ratio(c.progress, time(c, CinderPlinthBlockEntity.PROCESS_TIME, c.upgrades) + 1);
		if (be instanceof IgnemReactorBlockEntity i)
			return ratio(i.progress, time(i, IgnemReactorBlockEntity.PROCESS_TIME, i.upgrades) + 1);
		if (be instanceof AlchemyTabletBlockEntity t)
			return ratio(t.progress, time(t, AlchemyTabletBlockEntity.PROCESSING_TIME, t.upgrades));
		if (be instanceof DawnstoneAnvilBlockEntity d)
			return ratio(d.progress, ConfigManager.DAWNSTONE_ANVIL_MAX_HITS.get());
		if (be instanceof MechanicalPumpBottomBlockEntity p)
			return ratio(p.progress, 400);
		return -1;
	}

	private static int time(BlockEntity be, int base, List<UpgradeContext> upgrades) {
		return upgrades == null ? base : UpgradeUtil.getWorkTime(be, base, upgrades);
	}

	private static float ratio(int progress, int total) {
		if (progress <= 0 || total <= 0)
			return -1;
		return Math.min(1f, (float) progress / total);
	}
}
