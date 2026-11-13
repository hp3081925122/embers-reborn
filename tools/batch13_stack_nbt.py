"""ItemStack/FluidStack NBT on NbtBridge helpers (26.1 removed save/parse), plus small recipe API renames."""
import pathlib, re

SRC = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
NB = "com.rekindled.embers.util.NbtBridge"
EMPTY = "net.minecraft.core.RegistryAccess.EMPTY"

def prov(p):
    return f"{NB}.registries()" if p.strip() == EMPTY else p

RULES = [
    (re.compile(r"([\w.]+)\.saveOptional\(([\w.]+)\)"), lambda m: f"{NB}.saveOptional({m.group(1)}, {prov(m.group(2))})"),
    (re.compile(r"\b(augmentStack|stack)\.save\(([\w.]+)\)"), lambda m: f"{NB}.save({m.group(1)}, {prov(m.group(2))})"),
    (re.compile(r"\(net\.minecraft\.nbt\.CompoundTag\) lastEscaped\.save\(provider, new net\.minecraft\.nbt\.CompoundTag\(\)\)"),
     lambda m: f"{NB}.saveOptional(lastEscaped, provider)"),
    (re.compile(r"contents\.save\(provider, tank\);"), lambda m: f"tank.merge((CompoundTag) {NB}.saveOptional(contents, provider));"),
    (re.compile(r"(?:net\.minecraft\.world\.item\.)?ItemStack\.parse\(([\w.]+),"), lambda m: f"{NB}.parse({prov(m.group(1))},"),
    (re.compile(r"FluidStack\.parseOptional\(([\w.]+),"), lambda m: f"{NB}.parseOptionalFluid({prov(m.group(1))},"),
    (re.compile(r"\.getWeight\(\)\.asInt\(\)"), lambda m: ".weight()"),
    (re.compile(r"FriendlyByteBuf::(read|write)ResourceLocation"), lambda m: f"FriendlyByteBuf::{m.group(1)}Identifier"),
    (re.compile(r"BuiltInRegistries\.FLUID\.get\("), lambda m: "BuiltInRegistries.FLUID.getValue("),
    (re.compile(r"WeightedRandom\.getRandomItem\(level\.getRandom\(\), stacks\)"), lambda m: "WeightedRandom.getRandomItem(level.getRandom(), stacks, WeightedItemStack::weight)"),
    (re.compile(r"public RecipeType<\?> getType\(\) \{(\s*return RegistryManager\.EXCAVATION)"), lambda m: "public RecipeType<IBoringRecipe> getType() {" + m.group(1)),
    # anvil recipes: tag check instead of an Ingredient built from a tag
    (re.compile(r"\tIngredient blacklist = Ingredient\.of\(EmbersItemTags\.REPAIR_BLACKLIST\);\r?\n"), lambda m: ""),
    (re.compile(r"!blacklist\.test\((\w+)\)"), lambda m: f"!{m.group(1)}.is(EmbersItemTags.REPAIR_BLACKLIST)"),
    # 1.20.1 Forge isRepairable() == damageable; isValidRepairItem moved to ItemStack
    (re.compile(r"(\w+)\.isRepairable\(\)"), lambda m: f"{m.group(1)}.isDamageableItem()"),
    (re.compile(r"tool\.getItem\(\)\.isValidRepairItem\(tool, "), lambda m: "tool.isValidRepairItem("),
    (re.compile(r"for \(ItemStack stack : tool\.getItems\(\)\)"), lambda m: "for (ItemStack stack : com.rekindled.embers.util.Ingredients.stacks(tool))"),
]

changed = []
for p in SRC.rglob("*.java"):
    if p.name == "NbtBridge.java":
        continue
    src = p.read_text(encoding="utf-8")
    new = src
    for rx, fn in RULES:
        new = rx.sub(fn, new)
    if new != src:
        p.write_text(new, encoding="utf-8", newline="")
        changed.append(p.relative_to(SRC).as_posix())
print(len(changed), "files:")
print("\n".join(changed))
