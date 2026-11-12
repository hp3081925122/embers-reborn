import pathlib, re, json

root = pathlib.Path(__file__).resolve().parent.parent
java = root / "src/main/java"
n = {"ITEMS": 0, "BLOCKS": 0}
for f in java.rglob("*.java"):
    t = f.read_bytes().decode("utf-8")
    new = t
    for reg, helper in (("ITEMS", "item"), ("BLOCKS", "block")):
        new, k = re.subn(r"(?<![\w.])((?:RegistryManager\.)?" + reg + r")\.register\(", r"com.rekindled.embers.util.RegCtx." + helper + r"(\1, ", new)
        n[reg] += k
    if new != t:
        f.write_bytes(new.encode("utf-8"))
print(n)
mj = root / "src/main/resources/embers_reborn.mixins.json"
d = json.loads(mj.read_text(encoding="utf-8"))
for m in ("PropertiesIdMixin$ItemProps", "PropertiesIdMixin$BlockProps"):
    if m not in d["mixins"]:
        d["mixins"].append(m)
mj.write_text(json.dumps(d, indent=2) + "\n", encoding="utf-8")
print(d["mixins"])
