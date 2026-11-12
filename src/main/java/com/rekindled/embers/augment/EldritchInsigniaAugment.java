package com.rekindled.embers.augment;

import com.rekindled.embers.api.augment.AugmentUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EldritchInsigniaAugment extends AugmentBase {

	public EldritchInsigniaAugment(ResourceLocation id) {
		super(id, 0.0);
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onEntityTarget(LivingChangeTargetEvent event) {
		if (event.getNewAboutToBeSetTarget() instanceof Player player) {
			int level = AugmentUtil.getArmorAugmentLevel(player, this);
			if ((event.getEntity().getLastDamageSource() == null
					|| event.getEntity().getLastDamageSource().getEntity() == null
					|| event.getEntity().getLastDamageSource().getEntity().getUUID().compareTo(event.getNewAboutToBeSetTarget().getUUID()) != 0)
					&& event.getEntity().getId() % (3+level) >= 2) {
				if (level > 0 && !(event.getEntity() instanceof Player)) {
					event.setCanceled(true);
				}
			}
		}
	}
}
