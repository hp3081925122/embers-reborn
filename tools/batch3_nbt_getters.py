"""26.1: CompoundTag/ListTag getters return Optional. Rewrite to the *Or / *OrEmpty forms with 1.21.1 defaults."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
# single argument: a string literal, identifier/field chain, or simple call without commas
ARG = r'("(?:[^"\\]|\\.)*"|[\w.]+(?:\(\))?)'
defaults = {
    "Int": "0", "Long": "0L", "Short": "(short) 0", "Byte": "(byte) 0",
    "Float": "0.0F", "Double": "0.0D", "Boolean": "false", "String": '""',
}
rules = [(re.compile(r"\.get" + k + r"\(" + ARG + r"\)"), r".get" + k + r"Or(\1, " + v + ")") for k, v in defaults.items()]
rules += [
    (re.compile(r"\.getCompound\(" + ARG + r"\)"), r".getCompoundOrEmpty(\1)"),
    (re.compile(r"\.getList\(" + ARG + r",\s*(?:Tag\.\w+|\d+|net\.minecraft\.nbt\.Tag\.\w+)\)"), r".getListOrEmpty(\1)"),
    (re.compile(r"\.get(Int|Long|Byte)Array\(" + ARG + r"\)"), lambda m: f".get{m.group(1)}Array({m.group(2)}).orElse(new {m.group(1).lower()}[0])"),
]
# only touch receivers that look like NBT (tag/nbt/compound/list variables or chained NBT calls)
total = 0
for f in root.rglob("*.java"):
    t = f.read_bytes().decode("utf-8")
    if "CompoundTag" not in t and "ListTag" not in t:
        continue
    new = t
    for pat, rep in rules:
        new, n = pat.subn(rep, new)
        total += n
    if new != t:
        f.write_bytes(new.encode("utf-8"))
print("replacements:", total)
