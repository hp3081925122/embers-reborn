package com.rekindled.embers.research;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ResearchCategoryComingSoon extends ResearchCategory {

	public ResearchCategoryComingSoon(Identifier name, double v) {
		super(name, v);
	}

	public ResearchCategoryComingSoon(Identifier name, double u, double v) {
		super(name, u, v);
	}

	public ResearchCategoryComingSoon(Identifier name, Identifier loc, double u, double v) {
		super(name, loc, u, v);
	}

	@Override
	public boolean isChecked() {
		return false;
	}

	@Override
	public List<Component> getTooltip(boolean showTooltips) {
		ArrayList<Component> tooltip = new ArrayList<>();
		if (showTooltips) {
			tooltip.add(Component.translatable(Embers.MODID + ".research.coming_soon"));
		}
		return tooltip;
	}
}
