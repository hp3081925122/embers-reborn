package com.rekindled.embers.compat.jei;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.FluidIngredient;
import com.rekindled.embers.recipe.IMixingRecipe;

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
import net.neoforged.neoforge.fluids.FluidType;

public class MixingCategory implements IRecipeCategory<IMixingRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.mixing");
	public static Identifier texture = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/gui/jei_mixer.png");
	double scale = 1.0 / 1000.0;

	public MixingCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 108, 124);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.MIXER_CENTRIFUGE_ITEM.get()));
	}

	@Override
	public RecipeType<IMixingRecipe> getRecipeType() {
		return JEIPlugin.MIXING;
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
	public void setRecipe(IRecipeLayoutBuilder builder, IMixingRecipe recipe, IFocusGroup focuses) {
		int count = 0;
		int capacity = FluidType.BUCKET_VOLUME * 8;

		for (FluidIngredient ingredient : recipe.getDisplayInputFluids()) {
			if (count > 3)
				break;
			if (count == 2) {
				builder.addSlot(RecipeIngredientRole.INPUT, 9, 7)
				.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
				.setFluidRenderer((int) (capacity * scale + ingredient.getFluids().get(0).getAmount() * (1.0 - scale)), false, 16, 32)
				.addIngredients(NeoForgeTypes.FLUID_STACK, ingredient.getFluids());
			} else if (count == 0) {
				builder.addSlot(RecipeIngredientRole.INPUT, 34, 7)
				.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
				.setFluidRenderer((int) (capacity * scale + ingredient.getFluids().get(0).getAmount() * (1.0 - scale)), false, 16, 32)
				.addIngredients(NeoForgeTypes.FLUID_STACK, ingredient.getFluids());
			} else if (count == 1) {
				builder.addSlot(RecipeIngredientRole.INPUT, 59, 7)
				.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
				.setFluidRenderer((int) (capacity * scale + ingredient.getFluids().get(0).getAmount() * (1.0 - scale)), false, 16, 32)
				.addIngredients(NeoForgeTypes.FLUID_STACK, ingredient.getFluids());
			} else if (count == 3) {
				builder.addSlot(RecipeIngredientRole.INPUT, 83, 7)
				.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
				.setFluidRenderer((int) (capacity * scale + ingredient.getFluids().get(0).getAmount() * (1.0 - scale)), false, 16, 32)
				.addIngredients(NeoForgeTypes.FLUID_STACK, ingredient.getFluids());
			}
			count++;
		}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 46, 84)
		.addRichTooltipCallback(IngotTooltipCallback.INSTANCE)
		.setFluidRenderer((int) (capacity * scale + recipe.getDisplayOutput().getAmount() * (1.0 - scale)), false, 16, 32)
		.addIngredient(NeoForgeTypes.FLUID_STACK, recipe.getDisplayOutput());
	}

	@Override
	public void draw(IMixingRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		background.draw(guiGraphics);
	}
}
