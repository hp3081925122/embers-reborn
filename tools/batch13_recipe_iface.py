"""26.1 Recipe interface: Embers machine recipes are never placed in a recipe book.

- getType() narrowed to RecipeType<IX>, getSerializer() to RecipeSerializer<? extends Class>
- getToastSymbol / getResultItem(Provider) / canCraftInDimensions / assemble(ctx, Provider) stay as
  mod helpers but lose @Override (vanilla removed them)
- adds the new abstract methods; isSpecial() = true so RecipeManager does not drop the recipe for
  having an impossible PlacementInfo, and the recipe book skips it (same as 1.20.1, where these
  recipes never showed in the book)
"""
import pathlib, re

ROOT = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/recipe"

DEFAULTS = """
	@Override
	public default ItemStack assemble({T} input) {{
		return ItemStack.EMPTY;
	}}

	/** Machine recipe, not a recipe book entry; also keeps RecipeManager from discarding it as unplaceable. */
	@Override
	public default boolean isSpecial() {{
		return true;
	}}

	@Override
	public default boolean showNotification() {{
		return false;
	}}

	@Override
	public default String group() {{
		return "";
	}}

	@Override
	public default net.minecraft.world.item.crafting.PlacementInfo placementInfo() {{
		return net.minecraft.world.item.crafting.PlacementInfo.NOT_PLACEABLE;
	}}

	@Override
	public default net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {{
		return net.minecraft.world.item.crafting.RecipeBookCategories.CRAFTING_MISC;
	}}
"""

NO_OVERRIDE = re.compile(r"@Override(\s*(?:@Deprecated\s*)?public (?:default )?(?:ItemStack getToastSymbol\(\)|ItemStack getResultItem\([^)]*Provider|boolean canCraftInDimensions\(|ItemStack assemble\([^)]*Provider))")


def strip_overrides(src):
    return NO_OVERRIDE.sub(lambda m: m.group(1).lstrip(), src)


changed = []
for p in sorted(ROOT.glob("I*Recipe.java")):
    src = p.read_text(encoding="utf-8")
    m = re.search(r"public interface (\w+) extends Recipe<(.+?)> \{", src)
    if not m:
        continue
    name, t = m.group(1), m.group(2)
    new = strip_overrides(src)
    new = re.sub(r"public default RecipeType<\?> getType\(\)", f"public default RecipeType<{name}> getType()", new)
    if "isSpecial()" not in new:
        i = new.rstrip().rfind("}")
        new = new[:i].rstrip() + "\n" + DEFAULTS.format(T=t) + "}\n"
    if new != src:
        p.write_text(new, encoding="utf-8")
        changed.append(p.name)

for p in sorted(ROOT.glob("*Recipe*.java")):
    if p.name.startswith("I") and p.name[1].isupper():
        continue
    src = p.read_text(encoding="utf-8")
    m = re.search(r"public (?:abstract )?class (\w+)", src)
    if not m:
        continue
    cls = m.group(1)
    new = strip_overrides(src)
    new = new.replace("public RecipeSerializer<?> getSerializer()", f"public RecipeSerializer<? extends {cls}> getSerializer()")
    new = new.replace("public RecipeType<?> getType()\n", "public RecipeType<IBoringRecipe> getType()\n").replace("public RecipeType<?> getType()\r\n", "public RecipeType<IBoringRecipe> getType()\r\n") if cls == "ExcavationRecipe" else new
    if new != src:
        p.write_text(new, encoding="utf-8")
        changed.append(p.name)

print(len(changed), "files:", ", ".join(changed))
