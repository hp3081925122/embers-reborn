"""Last datagen pass for 26.1: tag appender API, removed ExistingFileHelper, criterion package, WeightedList spawns."""
import pathlib, re

d = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/datagen"


def edit(name, pairs):
    f = d / name
    t = f.read_bytes().decode("utf-8")
    for a, b in pairs:
        t, n = re.subn(a, b, t)
        print(f"{n:4} {name}: {a[:60]}")
    f.write_bytes(t.encode("utf-8"))


for f in d.glob("*.java"):
    t = f.read_bytes().decode("utf-8")
    t2 = re.sub(r"import net\.neoforged\.neoforge\.common\.data\.ExistingFileHelper;\r?\n", "", t)
    t2 = t2.replace("net.minecraft.advancements.critereon.", "net.minecraft.advancements.criterion.")
    t2 = re.sub(r"\bTags\.Items\.SLIMEBALLS\b", "Tags.Items.SLIME_BALLS", t2)
    if t2 != t:
        f.write_bytes(t2.encode("utf-8"))

# optional entries given as an Identifier: write them as raw optional tag entries
optional_id = (r"\.addOptional\(((?:Identifier\.fromNamespaceAndPath\([^()]*\))|(?:CuriosCompat\.\w+\.getId\(\)))\)",
               r".add(net.minecraft.tags.TagEntry.optionalElement(\1))")
edit("EmbersItemTags.java", [optional_id])
edit("EmbersBlockTags.java", [optional_id,
                              (r"\bBlocks\.CHAIN\b", "Blocks.IRON_CHAIN"),
                              (r"\bIntrinsicTagAppender<Block>", "net.minecraft.data.tags.TagAppender<Block, Block>")])
edit("EmbersFluidTags.java", [(r"\.addOptionalTag\(METAL_TOOLTIPS\.location\(\)\)", ".addOptionalTag(METAL_TOOLTIPS)")])
edit("EmbersDamageTypeTags.java", [
    (r"extends TagsProvider<DamageType>", "extends net.minecraft.data.tags.KeyTagProvider<DamageType>"),
    (r"\.addOptional\(EmbersDamageTypes\.EMBER_KEY\.identifier\(\)\)", ".addOptional(EmbersDamageTypes.EMBER_KEY)"),
])
edit("EmbersRecipes.java", [
    (r"Ingredient\.of\((net\.minecraft\.tags\.ItemTags\.create\([^()]*\([^()]*\)\))\)", r"tag(\1)"),
    (r"(\.save\(ConsumerWrapperBuilder\.wrap\(\)(?:[^;])*?\.build\(consumer\)), getResource\(", r"\1, recipeKey("),
])
edit("EmbersBiomeModifiers.java", [
    (r"List\.of\(new MobSpawnSettings\.SpawnerData\(([^,]+), (\d+), (\d+), (\d+)\)\)",
     r"net.minecraft.util.random.WeightedList.of(new net.minecraft.util.random.Weighted<>(new MobSpawnSettings.SpawnerData(\1, \3, \4), \2))"),
])
edit("EmbersStructures.java", [
    (r"import net\.minecraft\.util\.random\.WeightedRandomList;", "import net.minecraft.util.random.WeightedList;\nimport net.minecraft.util.random.Weighted;"),
    (r"WeightedRandomList\.create\(new MobSpawnSettings\.SpawnerData\(([^,]+), (\d+), (\d+), (\d+)\)\)",
     r"WeightedList.of(new Weighted<>(new MobSpawnSettings.SpawnerData(\1, \3, \4), \2))"),
])
