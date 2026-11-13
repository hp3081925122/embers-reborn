import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
BBE = "net.neoforged.neoforge.event.level.block.BreakBlockEvent"
ESR = "net.minecraft.world.entity.EntitySpawnReason.TRIGGERED"

changed = {}
for f in root.rglob("*.java"):
    raw = f.read_bytes().decode("utf-8")
    s = raw
    s = s.replace("import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;", "import " + BBE + ";")
    s = s.replace("BlockEvent.BreakEvent", BBE)
    s = re.sub(r"\bonBreak\(BreakEvent ", "onBreak(BreakBlockEvent ", s)
    s = s.replace(".isNaturalSpawningAllowed(", ".canSpawnEntitiesInChunk(")
    s = s.replace(".setUnsaved(true)", ".markUnsaved()")
    # EntityType.create(Level) -> create(Level, EntitySpawnReason)
    s = re.sub(r"(\.get\(\)\.create\()(level\(\)|level|this\.level|world)\)", r"\1\2, " + ESR + ")", s)
    if s != raw:
        f.write_bytes(s.encode("utf-8"))
        changed[f.name] = sum(1 for a, b in zip(raw.splitlines(), s.splitlines()) if a != b)

for k, v in sorted(changed.items()):
    print(k, v)
