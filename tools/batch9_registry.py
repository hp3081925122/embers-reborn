"""26.1 registration fixes: RecipeSerializer record, EntityType/BlockEntityType builders, spawn egg, small renames."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"


def rd(p):
    return p.read_bytes().decode("utf-8")


def wr(p, t):
    p.write_bytes(t.encode("utf-8"))


# 1. RegCtx regex earlier also hit the deferred register's own bus registration
e = j / "Embers.java"
t = rd(e)
t = t.replace("com.rekindled.embers.util.RegCtx.block(RegistryManager.BLOCKS, modEventBus);", "RegistryManager.BLOCKS.register(modEventBus);")
t = t.replace("com.rekindled.embers.util.RegCtx.item(RegistryManager.ITEMS, modEventBus);", "RegistryManager.ITEMS.register(modEventBus);")
wr(e, t)

# 2. RecipeSerializer became a record(codec, streamCodec): keep each Serializer class as a plain codec holder
n = 0
for f in j.rglob("*.java"):
    s = rd(f)
    m = re.search(r"public static class Serializer implements RecipeSerializer<(\w+)>", s)
    if not m:
        continue
    rec = m.group(1)
    s = s.replace(m.group(0), "public static class Serializer")
    s, k = re.subn(r"public static final Serializer SERIALIZER = new Serializer\(\);",
                   f"public static final RecipeSerializer<{rec}> SERIALIZER = new RecipeSerializer<>(new Serializer().codec(), new Serializer().streamCodec());", s)
    # codec()/streamCodec() no longer override anything
    s = re.sub(r"@Override(\s+public (?:com\.mojang\.serialization\.)?MapCodec<" + rec + r"> codec\(\))", r"\1", s)
    s = re.sub(r"@Override(\s+public (?:net\.minecraft\.network\.codec\.)?StreamCodec<[^>]*> streamCodec\(\))", r"\1", s)
    if k != 1:
        print("  check SERIALIZER field in", f.name)
    wr(f, s)
    n += 1
print("serializers:", n)

# 3. RegistryManager API changes
r = j / "RegistryManager.java"
t = rd(r)
pairs = [
    (r"builder\.build\(Embers\.MODID \+ \":\" \+ name\)",
     "builder.build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Embers.MODID, name)))"),
    (r"\.noCollission\(\)", ".noCollision()"),
    (r"BlockEntityType\.Builder\.of\(([^;]*?), block\.get\(\)\)\.build\(null\)", r"new BlockEntityType<>(\1, block.get())"),
    (r"BlockEntityType\.Builder\.of\(((?:\([^()]*\)\s*->\s*new CrystalSeedBlockEntity\([^()]*\)))\s*,\s*BLOCK\.get\(\)\)\.build\(null\)",
     r"new BlockEntityType<>(\1, BLOCK.get())"),
    (r"new DeferredSpawnEggItem\(([^,]+), (?:0x)?[0-9A-Fa-fx]+, (?:0x)?[0-9A-Fa-fx]+, new Item\.Properties\(\)\)",
     r"new net.minecraft.world.item.SpawnEggItem(new Item.Properties().spawnEgg(\1.get()))"),
]
for a, b in pairs:
    t, k = re.subn(a, b, t)
    print(f"{k:4} {a[:60]}")
t = re.sub(r"import net\.neoforged\.neoforge\.common\.DeferredSpawnEggItem;\r?\n", "", t)
for imp in ("ArmorItem", "PickaxeItem", "SwordItem", "Tier"):
    t = re.sub(r"import net\.minecraft\.world\.item\." + imp + r";\r?\n", "", t)
wr(r, t)
