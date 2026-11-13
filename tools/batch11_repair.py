"""Batch 11c: getRepairIngredient via the 26.1 REPAIRABLE component; AnvilRepairRecipe null-safe."""
import pathlib, re

base = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
H = "com.rekindled.embers.util.Ingredients"

p = base / "util/Misc.java"
s = p.read_bytes().decode("utf-8")
start = s.index("public static Ingredient getRepairIngredient(Item item) {")
end = s.index("return null;", start)
end = s.index("}", end) + 1
nl = "\r\n" if "\r\n" in s[start:end] else "\n"
body = nl.join([
    "public static Ingredient getRepairIngredient(Item item) {",
    "\t\t// 26.1: tool tiers and armor materials are gone; repair items live in the REPAIRABLE component",
    "\t\tnet.minecraft.world.item.enchantment.Repairable repairable = new ItemStack(item).get(net.minecraft.core.component.DataComponents.REPAIRABLE);",
    "\t\tif (repairable == null || repairable.items().size() == 0)",
    "\t\t\treturn null;",
    "\t\treturn Ingredient.of(repairable.items());",
    "\t}",
])
s = s[:start] + body + s[end:]
p.write_bytes(s.encode("utf-8"))
print("Misc ok")

p = base / "recipe/AnvilRepairRecipe.java"
s = p.read_bytes().decode("utf-8")
o = s
s = s.replace("!repairMaterial.isEmpty() && !repairMaterial.isEmpty()", f"!{H}.isEmpty(repairMaterial)")
s = re.sub(r"!repairMaterial\.isEmpty\(\)", f"!{H}.isEmpty(repairMaterial)", s)
s = re.sub(r"repairMaterial\.getItems\(\)", f"{H}.stacks(repairMaterial)", s)
s = re.sub(r"!Misc\.getRepairIngredient\(([^()]*\([^()]*\))\)\.isEmpty\(\)", rf"!{H}.isEmpty(Misc.getRepairIngredient(\1))", s)
s = re.sub(r"Misc\.getRepairIngredient\(([^()]*\([^()]*\))\)\.test\(", rf"{H}.testNonEmpty(Misc.getRepairIngredient(\1), ", s)
p.write_bytes(s.encode("utf-8"))
print("AnvilRepair", "changed" if s != o else "UNCHANGED")

p = base / "research/ResearchBase.java"
s = p.read_bytes().decode("utf-8")
s = s.replace("this.lookupIngredient = Ingredient.of(icon);", "this.lookupIngredient = icon.isEmpty() ? null : Ingredient.of(icon.getItem());")
p.write_bytes(s.encode("utf-8"))
print("ResearchBase ok")
