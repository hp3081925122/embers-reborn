"""Second recipe/tag pass for 26.1 plus the matching 1.21.1 port-bug fix.

1.21.1 bug (from the 1.20.1 port): oreCooking ignored its serializer argument and always built a smelting recipe,
so every *_from_blasting ore recipe was really a furnace recipe (100 ticks, wrong recipe type). 1.20.1 used
SimpleCookingRecipeBuilder.generic(..., serializer)."""
import pathlib, re, sys

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"
r = j / "datagen/EmbersRecipes.java"
t = r.read_bytes().decode("utf-8")


def sub(a, b, flags=0, expect=None):
    global t
    t, n = re.subn(a, b, t, flags=flags)
    print(f"{n:4}  {a[:70]}")
    if expect is not None and n != expect:
        sys.exit(f"expected {expect}")


sub(r"import net\.neoforged\.neoforge\.common\.conditions\.IConditionBuilder;\r?\n", "", expect=1)
sub(r" implements IConditionBuilder", "", expect=1)
sub(r"\bTags\.Items\.SLIMEBALLS\b", "Tags.Items.SLIME_BALLS")
sub(r"\.save\((\w+), getResource\(", r".save(\1, recipeKey(")
sub(r"\.save\((\w+), (deco\.\w+\.getId\(\))\)", r".save(\1, net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, \2))")
sub(r"new TagEmptyCondition\(([^;]*?)\.identifier\(\)\)", r"new TagEmptyCondition<>(\1)")
# helpers call instance has(): drop static
sub(r"protected static void (stonecutterResultFromBase|oreSmelting|oreBlasting|oreCooking)\(", r"protected void \1(")
# cooking needs a CookingBookCategory in 26.1
sub(r"(SimpleCookingRecipeBuilder\.(?:smelting|blasting)\(Ingredient\.of\((?:[^()]|\([^()]*\))*\), (?:RecipeCategory\.\w+|pCategory)), ",
    r"\1, net.minecraft.world.item.crafting.CookingBookCategory.MISC, ")
# oreCooking: choose blasting vs smelting instead of always smelting
sub(r"oreCooking\(pFinishedRecipeConsumer, RecipeSerializer\.SMELTING_RECIPE,", "oreCooking(pFinishedRecipeConsumer, false,", expect=1)
sub(r"oreCooking\(pFinishedRecipeConsumer, RecipeSerializer\.BLASTING_RECIPE,", "oreCooking(pFinishedRecipeConsumer, true,", expect=1)
sub(r"RecipeSerializer<\? extends AbstractCookingRecipe> pCookingSerializer", "boolean blasting", expect=1)
sub(r"SimpleCookingRecipeBuilder\.smelting\((Ingredient\.of\(itemlike\), pCategory, net\.minecraft\.world\.item\.crafting\.CookingBookCategory\.MISC, pResult, pExperience, pCookingTime\))",
    r"(blasting ? SimpleCookingRecipeBuilder.blasting(\1 : SimpleCookingRecipeBuilder.smelting(\1)", expect=1)
sub(r"(\tpublic static Identifier getResource\(String name\) \{)",
    "\tpublic static net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> recipeKey(String name) {\n"
    "\t\treturn net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, getResource(name));\n\t}\n\n\\1", expect=1)
r.write_bytes(t.encode("utf-8"))

it = j / "datagen/EmbersItemTags.java"
s = it.read_bytes().decode("utf-8")
s = s.replace("import net.minecraft.data.tags.ItemTagsProvider;", "import net.neoforged.neoforge.common.data.ItemTagsProvider;")
s = re.sub(r"import net\.minecraft\.data\.tags\.TagsProvider\.TagLookup;\r?\n", "", s)
it.write_bytes(s.encode("utf-8"))

# TagKey has location(), not identifier(): batch 2 renamed .location() everywhere, revert it on TagKey receivers javac flagged
err = (root / "build/c7.log.err").read_text(encoding="utf-8", errors="replace").splitlines()
fixes = set()
for i, l in enumerate(err):
    m = re.match(r"^(.*\.java):(\d+): error: cannot find symbol", l.strip())
    if m and i + 4 < len(err) and "method identifier()" in err[i + 3] and "TagKey" in err[i + 4]:
        fixes.add((m.group(1), int(m.group(2))))
for path in sorted({p for p, _ in fixes}):
    p = pathlib.Path(path)
    lines = p.read_bytes().decode("utf-8").split("\n")
    for _, ln in [x for x in fixes if x[0] == path]:
        lines[ln - 1] = lines[ln - 1].replace(".identifier()", ".location()")
    p.write_bytes("\n".join(lines).encode("utf-8"))
print("TagKey.location() restored on", len(fixes), "lines")
