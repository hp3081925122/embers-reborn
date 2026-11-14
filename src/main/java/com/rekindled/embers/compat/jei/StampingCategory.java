package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.IStampingRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class StampingCategory implements IRecipeCategory<IStampingRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.stamping");
	public static Identifier texture = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/gui/jei_stamp.png");
	double scale = 1.0 / 16.0;

	public StampingCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 108, 83);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.STAMPER_ITEM.get()));
	}

	@Override
	public RecipeType<IStampingRecipe> getRecipeType() {
		return JEIPlugin.STAMPING;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public int getWidth() {
		return background.getWidth();
	}

	@Override
	public int getHeight() {
		return background.getHeight();
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IStampingRecipe recipe, IFocusGroup focuses) {
		int amount = 0;

		for (FluidStack fluid : recipe.getDisplayInputFluid().getFluids()) {
			amount = fluid.getAmount();
		}

		var inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 8, 28);
		if (recipe.getDisplayInput() != null) {
			inputSlot.addIngredients(recipe.getDisplayInput());
		} else {
			Embers.LOGGER.debug("Skipping null display input ingredient in JEI StampingCategory for output {}", recipe.getResultItem());
		}

		var stampSlot = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 47, 7);
		if (recipe.getDisplayStamp() != null) {
			stampSlot.addIngredients(recipe.getDisplayStamp());
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 84, 28).addItemStack(recipe.getResultItem());

		builder.addSlot(RecipeIngredientRole.INPUT, 47, 48)
		.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer((int) (1500 * scale + amount * (1.0 - scale)), false, 16, 32)
		.addIngredients(NeoForgeTypes.FLUID_STACK, recipe.getDisplayInputFluid().getFluids());
	}

	@Override
	public void draw(IStampingRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
	}
}
