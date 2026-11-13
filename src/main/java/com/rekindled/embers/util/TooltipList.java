package com.rekindled.embers.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.network.chat.Component;

/**
 * 26.1 appendHoverText hands a Consumer instead of a List. This List forwards every add to that consumer,
 * so the old tooltip code (tooltip.add / addAll) keeps working unchanged.
 */
public class TooltipList extends AbstractList<Component> {

	private final Consumer<Component> adder;
	private final List<Component> seen = new ArrayList<>();

	public TooltipList(Consumer<Component> adder) {
		this.adder = adder;
	}

	@Override
	public void add(int index, Component element) {
		seen.add(element);
		adder.accept(element);
	}

	@Override
	public Component get(int index) {
		return seen.get(index);
	}

	@Override
	public int size() {
		return seen.size();
	}
}
