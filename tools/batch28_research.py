import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
f = root / "research/ResearchManager.java"
s = f.read_bytes().decode("utf-8")

TAG = "Ingredient.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow(%s))"
START = "Ingredient.fromValues("


def close_of(text, open_idx):
    depth = 0
    for j in range(open_idx, len(text)):
        if text[j] == "(":
            depth += 1
        elif text[j] == ")":
            depth -= 1
            if depth == 0:
                return j
    raise ValueError


n = 0
while START in s:
    i = s.index(START)
    end = close_of(s, i + len(START) - 1)
    inner = s[i + len(START):end]
    parts = []
    for p in re.finditer(r"new TagValue\(([\w.]+)\)|new ItemValue\(new ItemStack\(([\w.]+\.get\(\))\)\)", inner):
        parts.append(TAG % p.group(1) if p.group(1) else "Ingredient.of(%s)" % p.group(2))
    expected = inner.count("new TagValue(") + inner.count("new ItemValue(")
    assert len(parts) == expected, (inner, parts)
    s = s[:i] + "net.neoforged.neoforge.common.crafting.CompoundIngredient.of(" + ", ".join(parts) + ")" + s[end + 1:]
    n += 1
print("fromValues", n)
s = re.sub(r"import net\.minecraft\.world\.item\.crafting\.Ingredient\.(ItemValue|TagValue);\r?\n", "", s)
f.write_bytes(s.encode("utf-8"))

f = root / "research/capability/DefaultResearchCapability.java"
s = f.read_bytes().decode("utf-8")
s = s.replace("checkmarksTag.getAllKeys()", "checkmarksTag.keySet()")
f.write_bytes(s.encode("utf-8"))
