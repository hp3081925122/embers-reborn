import re, pathlib

rec = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/recipe"
GOT = "net.minecraft.core.registries.BuiltInRegistries.ITEM.getOrThrow("
# lines where batch 19 wrapped an ItemLike (not a TagKey) in getOrThrow
undo = {"AlchemyRecipeBuilder.java": [116, 122], "AnvilAugmentRecipeBuilder.java": [68], "StampingRecipeBuilder.java": [89]}
KEY = "net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, build().getId())"

for f in rec.glob("*Builder.java"):
    raw = f.read_bytes().decode("utf-8")
    lines = raw.split("\n")
    for n in undo.get(f.name, []):
        l = lines[n - 1]
        assert GOT in l, (f.name, n, l)
        lines[n - 1] = re.sub(re.escape(GOT) + r"(\w+)\)", r"\1", l)
    s = "\n".join(lines)
    s = s.replace("consumer.accept(build().getId(), build(), null);", "consumer.accept(" + KEY + ", build(), null);")
    if s != raw:
        f.write_bytes(s.encode("utf-8"))
        print(f.name)
