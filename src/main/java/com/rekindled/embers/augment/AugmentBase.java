package com.rekindled.embers.augment;

import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.resources.Identifier;

public class AugmentBase implements IAugment {

	Identifier name;
	double cost;

	public AugmentBase(Identifier name, double cost) {
		this.name = name;
		this.cost = cost;
	}

	@Override
	public Identifier getName() {
		return name;
	}

	@Override
	public double getCost() {
		return cost;
	}

}
