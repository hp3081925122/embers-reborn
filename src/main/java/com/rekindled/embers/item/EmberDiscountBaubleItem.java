package com.rekindled.embers.item;

import com.rekindled.embers.api.event.EmberRemoveEvent;
import com.rekindled.embers.compat.curios.CuriosCompat;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;

public class EmberDiscountBaubleItem extends Item implements IEmbersCurioItem {

	public double reduction;

	public EmberDiscountBaubleItem(Properties properties, double reduction) {
		super(properties);
		this.reduction = reduction;
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onTake(EmberRemoveEvent event) {
		CuriosCompat.checkForCurios(event.getPlayer(), stack -> {
			if (stack.getItem() == this) {
				event.addReduction(reduction);
			}
			return false;
		});
	}
}
