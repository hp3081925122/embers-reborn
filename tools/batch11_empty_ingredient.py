"""Batch 11b: Ingredient.EMPTY -> null + Ingredients helper in specific files."""
import pathlib, re

base = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
H = "com.rekindled.embers.util.Ingredients"
FILES = {
    "research/ResearchBase.java": [],
    "research/ResearchManager.java": [
        (r"(\w+)\.lookupIngredient\.isEmpty\(\)", rf"{H}.isEmpty(\1.lookupIngredient)"),
        (r"(\w+)\.lookupIngredient\.getItems\(\)", rf"{H}.stacks(\1.lookupIngredient)"),
    ],
    "recipe/AlchemyRecipeForBabies.java": [],
    "recipe/AnvilBreakdownRecipe.java": [
        (r"!Misc\.getRepairIngredient\(([^()]*\([^()]*\))\)\.isEmpty\(\)", rf"!{H}.isEmpty(Misc.getRepairIngredient(\1))"),
        (r"!repairMaterial\.isEmpty\(\) && !repairMaterial\.isEmpty\(\)", rf"!{H}.isEmpty(repairMaterial)"),
        (r"!repairMaterial\.isEmpty\(\)", rf"!{H}.isEmpty(repairMaterial)"),
        (r"repairMaterial\.getItems\(\)", rf"{H}.stacks(repairMaterial)"),
    ],
    "recipe/AnvilDisplayRecipe.java": [
        (r"ingredient\.getItems\(\)", rf"{H}.stacks(ingredient)"),
    ],
    "util/Misc.java": [],
}
for rel, rules in FILES.items():
    p = base / rel
    s = p.read_bytes().decode("utf-8")
    o = s
    for a, b in rules:
        s = re.sub(a, b, s)
    s = re.sub(r"(?<![\w.])(?:net\.minecraft\.world\.item\.crafting\.)?Ingredient\.EMPTY\b", "null", s)
    print(rel, "changed" if s != o else "UNCHANGED")
    p.write_bytes(s.encode("utf-8"))
