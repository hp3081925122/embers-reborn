package com.rekindled.embers.compat.jei;

import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.util.FluidAmounts;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Adds "= N ingots" under fluid slots. 1.20.1 inserted the line right after JEI's amount line;
 * the 26.1 rich tooltip builder only appends, so the line now goes after JEI's own lines.
 */
public class IngotTooltipCallback implements IRecipeSlotRichTooltipCallback {

	public static IngotTooltipCallback INSTANCE = new IngotTooltipCallback();

	@Override
	public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
		FluidStack fluid = recipeSlotView.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
		if (fluid.getFluid().is(EmbersFluidTags.INGOT_TOOLTIP) && fluid.getAmount() >= FluidAmounts.nuggetValue())
			tooltip.add(FluidAmounts.getIngotTooltip(fluid.getAmount()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
	}
}
