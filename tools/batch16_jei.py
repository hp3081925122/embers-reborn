"""JEI for 26.1: rich tooltip callbacks, CATALYST -> CRAFTING_STATION, background drawn in draw()."""
import pathlib, re

JEI = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/compat/jei"

BG_OVERRIDE = re.compile(r"\t@Override\r?\n\tpublic IDrawable getBackground\(\) \{\r?\n\t\treturn background;\r?\n\t\}")
SIZE = ("\t@Override\n\tpublic int getWidth() {\n\t\treturn background.getWidth();\n\t}\n\n"
        "\t@Override\n\tpublic int getHeight() {\n\t\treturn background.getHeight();\n\t}")
DRAW = re.compile(r"(public void draw\(\w+ recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor (\w+), double mouseX, double mouseY\) \{\r?\n)")

changed = []
for p in sorted(JEI.glob("*.java")):
    src = p.read_text(encoding="utf-8")
    new = src.replace(".addTooltipCallback(", ".addRichTooltipCallback(")
    new = new.replace("RecipeIngredientRole.CATALYST", "RecipeIngredientRole.CRAFTING_STATION")
    if BG_OVERRIDE.search(new):
        new = BG_OVERRIDE.sub(SIZE, new)
        m = DRAW.search(new)
        if m:
            new = new[:m.end()] + f"\t\tbackground.draw({m.group(2)});\n" + new[m.end():]
        else:
            cls = re.search(r"implements IRecipeCategory<(\w+)>", new).group(1)
            i = new.rstrip().rfind("}")
            new = new[:i].rstrip() + (f"\n\n\t@Override\n\tpublic void draw({cls} recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, "
                                      f"net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {{\n"
                                      f"\t\tbackground.draw(guiGraphics);\n\t}}\n}}\n")
    if new != src:
        p.write_text(new, encoding="utf-8", newline="")
        changed.append(p.name)
print(len(changed), "files:", ", ".join(changed))
