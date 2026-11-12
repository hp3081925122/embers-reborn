"""Rewrite 1.21.1 saveAdditional/loadAdditional(CompoundTag, Provider) into 26.1 ValueOutput/ValueInput.

The old bodies are kept as saveAdditionalNbt/loadAdditionalNbt. The topmost mod class in each hierarchy
gets a wrapper that bridges ValueOutput/ValueInput to a CompoundTag via NbtBridge; subclasses only override
the *Nbt methods, so each class's data is written exactly once.
"""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
P = r"(?:net\.minecraft\.core\.)?HolderLookup\.Provider"
SAVE = re.compile(r"(@Override\s+)?(?:public|protected) void saveAdditional\(\s*CompoundTag (\w+),\s*" + P + r" (\w+)\)\s*\{")
LOAD = re.compile(r"(@Override\s+)?(?:public|protected) void loadAdditional\(\s*CompoundTag (\w+),\s*" + P + r" (\w+)\)\s*\{")
EXT = re.compile(r"\bclass (\w+)(?:<[^>{]*>)?\s+extends (\w+)")

files = {}
for f in root.rglob("*.java"):
    files[f] = f.read_bytes().decode("utf-8")

has = {"save": set(), "load": set()}
parent = {}
for f, t in files.items():
    m = EXT.search(t)
    if m:
        parent[m.group(1)] = m.group(2)
    name = f.stem
    if SAVE.search(t):
        has["save"].add(name)
    if LOAD.search(t):
        has["load"].add(name)


def ancestor_bridged(cls, kind):
    seen = set()
    p = parent.get(cls)
    while p and p not in seen:
        if p in has[kind]:
            return True
        seen.add(p)
        p = parent.get(p)
    return False


changed = 0
for f, t in files.items():
    cls = f.stem
    new = t
    if cls in has["save"]:
        bridged = ancestor_bridged(cls, "save")

        def rs(m, bridged=bridged):
            nbt, prov = m.group(2), m.group(3)
            head = ""
            if not bridged:
                head = ("@Override\n\tprotected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {\n"
                        "\t\tsuper.saveAdditional(output);\n"
                        "\t\tnet.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();\n"
                        "\t\tsaveAdditionalNbt(nbt, com.rekindled.embers.util.NbtBridge.provider(this));\n"
                        "\t\tcom.rekindled.embers.util.NbtBridge.write(output, nbt);\n"
                        "\t}\n\n\t")
            ov = "@Override\n\t" if bridged else ""
            return f"{head}{ov}public void saveAdditionalNbt(CompoundTag {nbt}, net.minecraft.core.HolderLookup.Provider {prov}) {{"

        new = SAVE.sub(rs, new)
        if bridged:
            new = re.sub(r"super\.saveAdditional\((\w+),\s*(\w+)\)", r"super.saveAdditionalNbt(\1, \2)", new)
        else:
            new = re.sub(r"[ \t]*super\.saveAdditional\(\w+,\s*\w+\);\r?\n", "", new)
    if cls in has["load"]:
        bridged = ancestor_bridged(cls, "load")

        def rl(m, bridged=bridged):
            nbt, prov = m.group(2), m.group(3)
            head = ""
            if not bridged:
                head = ("@Override\n\tprotected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {\n"
                        "\t\tsuper.loadAdditional(input);\n"
                        "\t\tloadAdditionalNbt(com.rekindled.embers.util.NbtBridge.read(input), input.lookup());\n"
                        "\t}\n\n\t")
            ov = "@Override\n\t" if bridged else ""
            return f"{head}{ov}public void loadAdditionalNbt(CompoundTag {nbt}, net.minecraft.core.HolderLookup.Provider {prov}) {{"

        new = LOAD.sub(rl, new)
        if bridged:
            new = re.sub(r"super\.loadAdditional\((\w+),\s*(\w+)\)", r"super.loadAdditionalNbt(\1, \2)", new)
        else:
            new = re.sub(r"[ \t]*super\.loadAdditional\(\w+,\s*\w+\);\r?\n", "", new)
    if new != t:
        f.write_bytes(new.encode("utf-8"))
        changed += 1
print("save classes:", len(has["save"]), "load classes:", len(has["load"]), "files changed:", changed)
left = [f.name for f, t in ((f, f.read_bytes().decode('utf-8')) for f in files) if re.search(r"(save|load)Additional\(\s*CompoundTag", t)]
print("unconverted:", left)
