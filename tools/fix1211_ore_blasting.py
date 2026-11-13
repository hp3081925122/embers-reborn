"""1.21.1: ore blasting recipes were generated as smelting recipes (oreCooking ignored its serializer)."""
import pathlib, re, sys

f = pathlib.Path(sys.argv[1]) / "src/main/java/com/rekindled/embers/datagen/EmbersRecipes.java"
t = f.read_bytes().decode("utf-8")
old = "SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime)"
assert t.count(old) == 1, t.count(old)
t = t.replace(old, "(pCookingSerializer == RecipeSerializer.BLASTING_RECIPE"
                   " ? SimpleCookingRecipeBuilder.blasting(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime)"
                   " : SimpleCookingRecipeBuilder.smelting(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime))")
f.write_bytes(t.encode("utf-8"))
print("fixed", f)
