package com.rekindled.embers.api.item;

import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public interface ITyrfingWeapon {
	public void attack(LivingIncomingDamageEvent event, double armor);
}
