package com.rekindled.embers.util;

import com.rekindled.embers.gui.GuiCodex;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class GlowingTextTooltip implements TooltipComponent {

	public Component normalText;
	public Component glowingText;
	public float intensity = -2.0F;

	public GlowingTextTooltip(Component normalText, Component glowingText, float intensity) {
		this.normalText = normalText;
		this.glowingText = glowingText;
		this.intensity = intensity;
	}

	public GlowingTextTooltip(Component glowingText, float intensity) {
		this(Component.empty(), glowingText, intensity);
	}

	public GlowingTextTooltip(Component normalText, Component glowingText) {
		this(normalText, glowingText, -2.0F);
	}

	public GlowingTextTooltip(Component glowingText) {
		this(Component.empty(), glowingText, -2.0F);
	}

	public static class GlowingTextClientTooltip implements ClientTooltipComponent {

		GlowingTextTooltip tooltip;

		public GlowingTextClientTooltip(GlowingTextTooltip tooltip) {
			this.tooltip = tooltip;
		}

		@Override
		public int getHeight(Font font) {
			return 10;
		}

		@Override
		public int getWidth(Font font) {
			return font.width(tooltip.normalText) + font.width(tooltip.glowingText);
		}

		@Override
		public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
			graphics.text(font, tooltip.normalText, x, y, -1, true);
		}

		@Override
		public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
			if (tooltip.intensity < -1.0f) {
				GuiCodex.drawTextGlowingAura(font, graphics, tooltip.glowingText.getVisualOrderText(), font.width(tooltip.normalText) + x, y);
			} else {
				graphics.text(font, tooltip.glowingText, x, y, -1, true);
				// 1.20.1 pushed the aura 0.06 forward in z; 26.1 GUI draws in submission order, so drawing it after the text is enough
				GuiCodex.drawTextGlowingAura(font, graphics, tooltip.glowingText.plainCopy().getVisualOrderText(), font.width(tooltip.normalText) + x, y, tooltip.intensity);
			}
		}
	}
}
