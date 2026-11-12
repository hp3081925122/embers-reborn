package com.rekindled.embers.compat.jei;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.IDawnstoneAnvilRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DawnstoneAnvilCategory implements IRecipeCategory<IDawnstoneAnvilRecipe> {

	private final IDrawable background;
	private final IDrawable icon;
	public static Component title = Component.translatable(Embers.MODID + ".jei.recipe.dawnstone_anvil");
	public static ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Embers.MODID, "textures/gui/jei_dawnstone_anvil.png");

	public DawnstoneAnvilCategory(IGuiHelper helper) {
		background = helper.createDrawable(texture, 0, 0, 110, 58);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(RegistryManager.DAWNSTONE_ANVIL_ITEM.get()));
	}

	@Override
	public RecipeType<IDawnstoneAnvilRecipe> getRecipeType() {
		return JEIPlugin.DAWNSTONE_ANVIL;
	}

	@Override
	public Component getTitle() {
		return title;
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, IDawnstoneAnvilRecipe recipe, IFocusGroup focuses) {
		List<ItemStack> top = recipe.getDisplayInputTop();
		List<ItemStack> bottom = recipe.getDisplayInputBottom();
		var second = builder.addSlot(RecipeIngredientRole.INPUT, 22, 19).addItemStacks(top);
		if (hasItems(top))
			second.addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(Embers.MODID + ".jei.recipe.dawnstone_anvil.second").withStyle(ChatFormatting.YELLOW)));
		var first = builder.addSlot(RecipeIngredientRole.INPUT, 22, 37).addItemStacks(bottom);
		if (hasItems(bottom))
			first.addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(Embers.MODID + ".jei.recipe.dawnstone_anvil.first").withStyle(ChatFormatting.YELLOW)));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 28).addItemStacks(recipe.getDisplayOutput()).addTooltipCallback((view, tooltip) -> {
			if (hasItems(bottom))
				tooltip.add(Component.translatable(Embers.MODID + ".jei.recipe.dawnstone_anvil.output_first", names(bottom)).withStyle(ChatFormatting.YELLOW));
			if (hasItems(top))
				tooltip.add(Component.translatable(Embers.MODID + ".jei.recipe.dawnstone_anvil.output_second", names(top)).withStyle(ChatFormatting.YELLOW));
		});
	}

	private static boolean hasItems(List<ItemStack> stacks) {
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty())
				return true;
		}
		return false;
	}

	private static Component names(List<ItemStack> stacks) {
		MutableComponent name = Component.empty();
		boolean any = false;
		for (ItemStack stack : stacks) {
			if (stack.isEmpty())
				continue;
			if (any)
				name.append("、");
			name.append(stack.getHoverName());
			any = true;
		}
		return name;
	}
}
