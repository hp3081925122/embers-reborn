"""Give GenericRecipeBuilder recipes their real IDs (1.20.1 used recipe.getId()).

Usage: python fix_generic_recipe_ids.py <worktree> <ResourceLocation|Identifier>
"""
import re, sys, pathlib

tree = pathlib.Path(sys.argv[1])
rl = sys.argv[2]
java = tree / "src/main/java/com/rekindled/embers"

builder = java / "recipe/GenericRecipeBuilder.java"
s = builder.read_bytes().decode("utf-8")
s = s.replace("\tpublic Recipe<?> recipe;", "\tpublic " + rl + " id;\n\tpublic Recipe<?> recipe;")
s = s.replace("public static GenericRecipeBuilder create(Recipe<?> recipe) {",
              "public static GenericRecipeBuilder create(" + rl + " id, Recipe<?> recipe) {")
s = s.replace("\t\tbuilder.recipe = recipe;", "\t\tbuilder.id = id;\n\t\tbuilder.recipe = recipe;")
if rl == "ResourceLocation":
    new_accept = "consumer.accept(id, recipe, null);"
else:
    new_accept = ("consumer.accept(net.minecraft.resources.ResourceKey.create("
                  "net.minecraft.core.registries.Registries.RECIPE, id), recipe, null);")
s, n = re.subn(r"consumer\.accept\(net\.minecraft\.resources\.\w+\.parse\(\"embers_reborn:generic_recipe_\" \+ System\.nanoTime\(\)\), recipe, null\);",
               new_accept, s)
assert n == 1, "accept not found"
if "import net.minecraft.resources." + rl + ";" not in s:
    s = s.replace("import net.minecraft.world.item.crafting.Recipe;",
                  "import net.minecraft.resources." + rl + ";\nimport net.minecraft.world.item.crafting.Recipe;")
builder.write_bytes(s.encode("utf-8"))

recipes = java / "datagen/EmbersRecipes.java"
s = recipes.read_bytes().decode("utf-8")
pat = re.compile(r"GenericRecipeBuilder\.create\(new (\w+)\((" + rl + r"\.fromNamespaceAndPath\([^()]*\))\)\)")
s, n = pat.subn(lambda m: "GenericRecipeBuilder.create(" + m.group(2) + ", new " + m.group(1) + "(" + m.group(2) + "))", s)
assert n == 5, n
recipes.write_bytes(s.encode("utf-8"))
print("ok", n)
