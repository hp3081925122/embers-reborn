import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
ITEMS = "net.minecraft.core.registries.BuiltInRegistries.ITEM"

changed = {}
for f in root.rglob("*.java"):
    raw = f.read_bytes().decode("utf-8")
    s = raw
    # Level.random is protected in 26.1
    s = re.sub(r"\b(myLevel|world|level)\.random\b", r"\1.getRandom()", s)
    # ChunkPos is a record: no BlockPos constructor, fields become accessors
    s = re.sub(r"new ChunkPos\((?=[^,()]*(\([^()]*\))?[^,()]*\))", "ChunkPos.containing(", s)
    s = re.sub(r"\b(chunkPos[AB]?)\.(x|z)\b(?!\()", r"\1.\2()", s)
    # AnvilUpdateEvent cost -> xp cost
    s = s.replace("event.setCost(", "event.setXpCost(").replace("event.getCost()", "event.getXpCost()")
    # Player.displayClientMessage(msg, false) -> sendSystemMessage(msg)
    s = re.sub(r"player\.displayClientMessage\((.*)\), false\);", r"player.sendSystemMessage(\1));", s)
    # Ingredient.of(TagKey) / Ingredient.of(ItemStack) in recipe builders and research
    if f.parent.name in ("recipe", "research"):
        s = re.sub(r"Ingredient\.of\((tag|aspect|input|EmbersItemTags\.\w+)\)", r"Ingredient.of(" + ITEMS + r".getOrThrow(\1))", s)
        s = s.replace("Ingredient.of(itemStack)", "Ingredient.of(itemStack.getItem())")
    if s != raw:
        f.write_bytes(s.encode("utf-8"))
        changed[f.name] = sum(1 for a, b in zip(raw.splitlines(), s.splitlines()) if a != b)

for k, v in sorted(changed.items()):
    print(k, v)
