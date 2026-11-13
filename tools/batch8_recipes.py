"""26.1 RecipeProvider: (registries, output) constructor, buildRecipes() without args, Runner for the data generator,
instance shaped()/shapeless()/has(), Ingredient from a tag via tag()."""
import pathlib, re

f = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/datagen/EmbersRecipes.java"
t = f.read_bytes().decode("utf-8")
pairs = [
    (r"public EmbersRecipes\(PackOutput gen, java\.util\.concurrent\.CompletableFuture<net\.minecraft\.core\.HolderLookup\.Provider> lookupProvider\) \{\s*super\(gen, lookupProvider\);",
     "public EmbersRecipes(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output) {\n\t\tsuper(registries, output);"),
    (r"(@Override\s*)?public void buildRecipes\(RecipeOutput consumer\) \{",
     "@Override\n\tprotected void buildRecipes() {\n\t\tbuildAll(this.output);\n\t}\n\n"
     "\tpublic static class Runner extends RecipeProvider.Runner {\n"
     "\t\tpublic Runner(PackOutput output, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> registries) {\n"
     "\t\t\tsuper(output, registries);\n\t\t}\n\n"
     "\t\t@Override\n\t\tprotected RecipeProvider createRecipeProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output) {\n"
     "\t\t\treturn new EmbersRecipes(registries, output);\n\t\t}\n\n"
     "\t\t@Override\n\t\tpublic String getName() {\n\t\t\treturn \"Embers recipes\";\n\t\t}\n\t}\n\n"
     "\tprivate void buildAll(RecipeOutput consumer) {"),
    (r"\bShapedRecipeBuilder\.shaped\(", "shaped("),
    (r"\bShapelessRecipeBuilder\.shapeless\(", "shapeless("),
]
for a, b in pairs:
    t, n = re.subn(a, b, t, flags=re.S)
    print(f"{n:4}  {a[:60]}")
# Ingredient.of(<tag constant>) -> tag(<tag constant>): tag constants are UPPER_CASE fields of *Tags classes or Tags.Items
t, n = re.subn(r"\bIngredient\.of\(((?:Embers\w*Tags|Tags\.Items|ItemTags)\.[A-Z0-9_]+)\)", r"tag(\1)", t)
print(f"{n:4}  Ingredient.of(TAG) -> tag(TAG)")
f.write_bytes(t.encode("utf-8"))
