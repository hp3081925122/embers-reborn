package com.rekindled.embers.gui;

import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.item.AlchemyHintItem;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SlateScreen extends AbstractContainerScreen<SlateMenu> {

	private static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int u, int v, int width, int height) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, 256, 256);
	}

	private static void blit(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int z, int u, int v, int width, int height) {
		graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, 256, 256);
	}

	Identifier inventory = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/gui/inventory_gui.png");
	Identifier slate = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/gui/codebreaking_slate_gui.png");

	public static int slotOffset = SlateMenu.slotIndent + 3;

	public SlateScreen(SlateMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle, SlateMenu.invWidth, SlateMenu.invHeight + SlateMenu.slateHeight);
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.extractRenderState(graphics, pMouseX, pMouseY, pPartialTick);
		
		renderAspectHint(graphics, pMouseX, pMouseY);
	}

	/** Hovering an aspect in a recorded guess shows whether that slot was right; needs a note made with alchemy_slot_hints on. */
	private void renderAspectHint(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		ItemStack baseStack = this.menu.getSlot(0).getItem();
		if (baseStack.isEmpty())
			return;
		int x = (this.width - SlateMenu.slateWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		int inputCount = AlchemyHintItem.getInputs(baseStack).size();
		if (inputCount == 0)
			return;
		for (int i = 0; i < SlateMenu.slotCount; ++i) {
			ItemStack stack = this.menu.getSlot(i).getItem();
			ArrayList<ItemStack> aspects = AlchemyHintItem.getAspects(stack);
			if (aspects.isEmpty())
				return;
			byte[] correct = AlchemyHintItem.getCorrect(stack);
			if (correct == null)
				continue;
			int height = y + SlateMenu.heightMargin + i * SlateMenu.layerHeight + SlateMenu.layerHeight / 2 + (SlateMenu.slotHeight - 9);
			for (int j = 0; j < inputCount && j < aspects.size() && j < correct.length; ++j) {
				int slotX = x + SlateMenu.widthMargin + j * SlateMenu.layerWidth / inputCount + SlateMenu.layerWidth / (2 * inputCount) - 10;
				if (mouseX >= slotX && mouseX < slotX + 20 && mouseY >= height && mouseY < height + 24) {
					boolean right = correct[j] != 0;
					graphics.setTooltipForNextFrame(this.font, java.util.List.of(
							aspects.get(j).getHoverName().getVisualOrderText(),
							Component.translatable(Embers.MODID + ".alchemy_hint.slot." + (right ? "correct" : "wrong"))
									.withStyle(right ? net.minecraft.ChatFormatting.GREEN : net.minecraft.ChatFormatting.RED).getVisualOrderText()),
						DefaultTooltipPositioner.INSTANCE, mouseX, mouseY, true);
					return;
				}
			}
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int invX = (this.width - this.imageWidth) / 2;
		int x = (this.width - SlateMenu.slateWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		blit(graphics, inventory, invX, y + SlateMenu.slateHeight, 0, 0, SlateMenu.invWidth, SlateMenu.invHeight);
		blit(graphics, slate, x, y, 0, 0, SlateMenu.slateWidth, SlateMenu.slateHeight);

		//render ingredients
		ItemStack baseStack = this.menu.getSlot(0).getItem();
		ArrayList<ItemStack> inputs = AlchemyHintItem.getInputs(baseStack);
		for (int j = 0; j < inputs.size(); ++j) {
			int slotX = x + SlateMenu.widthMargin + j * SlateMenu.layerWidth / inputs.size() + SlateMenu.layerWidth / (2 * inputs.size()) - 12;
			blit(graphics, slate, slotX, y + SlateMenu.heightMargin, 192, 0, 24, 24);
			graphics.item(inputs.get(j), slotX + 4, y + SlateMenu.heightMargin + 4);
		}

		//render guesses
		boolean cont = true;
		for (int i = 0; i < SlateMenu.slotCount; ++i) {
			int height = y + SlateMenu.heightMargin + i * SlateMenu.layerHeight + SlateMenu.layerHeight / 2 + (SlateMenu.slotHeight - 9);

			blit(graphics, slate, x + slotOffset, height, 192, 24, SlateMenu.slotWidth, SlateMenu.slotHeight);

			if (baseStack.isEmpty() || !cont)
				continue;

			ItemStack stack = this.menu.getSlot(i).getItem();
			ArrayList<ItemStack> aspects = AlchemyHintItem.getAspects(stack);
			if (aspects.isEmpty()) {
				cont = false;
				continue;
			}
			int blackPins = AlchemyHintItem.getBlackPins(stack);
			int whitePins = AlchemyHintItem.getWhitePins(stack);

			for (int j = 0; j < inputs.size(); ++j) {
				int slotX = x + SlateMenu.widthMargin + j * SlateMenu.layerWidth / inputs.size() + SlateMenu.layerWidth / (2 * inputs.size()) - 10;
				blit(graphics, slate, slotX, height, 218, 20, 20, 24);
				if (j < aspects.size())
					graphics.item(aspects.get(j), slotX + 2, height + 3);
			}

			//render hint
			int hintX = x + 155;
			int hintY = height + 2;
			blit(graphics, slate, hintX, hintY, 216, 0, 19, 19);

			if (inputs.size() <= 4) { //4 pin slots
				for (int h = 0; h < 2; ++h) {
					for (int v = 0; v < 2; ++v) {
						if (blackPins > 0) {
							blit(graphics, slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 198, 50, 6, 6);
							blackPins--;
						} else if (whitePins > 0) {
							blit(graphics, slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 204, 50, 6, 6);
							whitePins--;
						} else {
							blit(graphics, slate, hintX + 3 + v * 8, hintY + 3 + h * 8, 192, 50, 6, 6);
						}
					}
				}
			} else { //9 pin slots
				for (int h = 0; h < 3; ++h) {
					for (int v = 0; v < 3; ++v) {
						if (blackPins > 0) {
							blit(graphics, slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 198, 50, 6, 6);
							blackPins--;
						} else if (whitePins > 0) {
							blit(graphics, slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 204, 50, 6, 6);
							whitePins--;
						} else {
							blit(graphics, slate, hintX + 1 + v * 6, hintY + 1 + h * 6, 192, 50, 6, 6);
						}
					}
				}
			}
		}
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}
}

