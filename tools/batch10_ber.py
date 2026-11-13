"""Move the 30 block entity renderers onto LegacyBlockEntityRenderer and the machine part models onto standalone models."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"
PART = "net.minecraft.client.renderer.block.dispatch.BlockStateModelPart"
DROP_IMPORTS = ["net.minecraft.client.resources.model.BakedModel", "net.minecraft.client.renderer.entity.ItemRenderer",
                "net.minecraft.client.renderer.block.BlockRenderDispatcher", "net.minecraft.client.renderer.Sheets",
                "net.neoforged.neoforge.client.model.data.ModelData", "net.minecraft.client.renderer.blockentity.BlockEntityRenderer"]

ARG = r"((?:[^,()]|\([^()]*\))+)"
rules = [
    (r"implements BlockEntityRenderer<(\w+)>", r"extends com.rekindled.embers.render.LegacyBlockEntityRenderer<\1>"),
    (r"[ \t]*private final ItemRenderer \w+;\r?\n", ""),
    (r"[ \t]*this\.\w+ = \w+\.getItemRenderer\(\);\r?\n", ""),
    (r"[ \t]*BakedModel \w+ = this\.itemRenderer\.getModel\([^;]*\);\r?\n", ""),
    (r"[ \t]*BlockRenderDispatcher \w+ = Minecraft\.getInstance\(\)\.getBlockRenderer\(\);\r?\n", ""),
    (r"this\.itemRenderer\.render\(" + ARG + r", " + ARG + r", false, (\w+), \w+, " + ARG + r", " + ARG + r", \w+\);",
     r"item(\1, \2, \3, \4, \5, blockEntity.getLevel(), seed);"),
    (r"\w+\.getModelRenderer\(\)\.renderModel\((\w+)\.last\(\), \w+\.getBuffer\(Sheets\.solidBlockSheet\(\)\), \w+, " + ARG +
     r", 0\.0f, 0\.0f, 0\.0f, " + ARG + r", " + ARG + r", ModelData\.EMPTY, Sheets\.solidBlockSheet\(\)\);",
     r"model(\1, \2, \3, \4);"),
    (r"public static BakedModel\b", "public static " + PART),
    (r"public static BakedModel\[\]", "public static " + PART + "[]"),
]
counts = {}
for f in sorted((j / "blockentity/render").glob("*.java")):
    t = f.read_bytes().decode("utf-8")
    for a, b in rules:
        t, n = re.subn(a, b, t)
        counts[a[:40]] = counts.get(a[:40], 0) + n
    t = re.sub(r"public static BakedModel\[\]\[\]\[\]", "public static " + PART + "[][][]", t)
    t = re.sub(r"new BakedModel\[", "new " + PART + "[", t)
    for imp in DROP_IMPORTS:
        t = re.sub(r"import " + re.escape(imp) + r";\r?\n", "", t)
    f.write_bytes(t.encode("utf-8"))
for k, v in counts.items():
    print(f"{v:4} {k}")

# leftovers that need hand edits
for f in sorted((j / "blockentity/render").glob("*.java")):
    t = f.read_bytes().decode("utf-8")
    for pat in ("itemRenderer", "renderModel(", "BakedModel", "getBlockRenderer"):
        if pat in t:
            print("  LEFT", f.name, pat)
    if "item(" in t and not re.search(r"\bint seed\b", t):
        print("  NO SEED", f.name)

# excavation buckets drawn from the bore blade render event
u = j / "upgrade/ExcavationBucketsUpgrade.java"
t = u.read_bytes().decode("utf-8")
t, n = re.subn(r"Minecraft\.getInstance\(\)\.getBlockRenderer\(\)\.getModelRenderer\(\)\.renderModel\(renderEvent\.getPose\(\)\.last\(\), [^;]*?buckets, 0\.0f, 0\.0f, 0\.0f, renderEvent\.getLight\(\), renderEvent\.getOverlay\(\), ModelData\.EMPTY, Sheets\.solidBlockSheet\(\)\);",
               "com.rekindled.embers.render.LegacyBlockEntityRenderer.model(renderEvent.getPose(), buckets, renderEvent.getLight(), renderEvent.getOverlay());", t)
t = t.replace("public static BakedModel buckets;", "public static " + PART + " buckets;")
for imp in DROP_IMPORTS:
    t = re.sub(r"import " + re.escape(imp) + r";\r?\n", "", t)
u.write_bytes(t.encode("utf-8"))
print("excavation buckets:", n)

# model lookup after baking
ce = j / "EmbersClientEvents.java"
t = ce.read_bytes().decode("utf-8")
t = re.sub(r"public static BakedModel getModel\(ModelBakery bakery, String name\) \{.*?\n\t\}",
           "public static " + PART + " getModel(String name) {\n\t\treturn com.rekindled.embers.render.EmbersStandaloneModels.get(name);\n\t}", t, flags=re.S)
t = re.sub(r'getModel\(bakery, "', 'getModel("', t)
t = re.sub(r"[ \t]*bakedModels = event\.getModels\(\);\r?\n", "", t)
t = re.sub(r"[ \t]*ModelBakery bakery = event\.getModelBakery\(\);\r?\n", "", t)
t = re.sub(r"[ \t]*public static java\.util\.Map<ModelResourceLocation, BakedModel> bakedModels = null;\r?\n", "", t)
ce.write_bytes(t.encode("utf-8"))

e = j / "Embers.java"
t = e.read_bytes().decode("utf-8")
if "EmbersStandaloneModels.register" not in t:
    t, n = re.subn(r"(\t\t@OnlyIn\(Dist\.CLIENT\)\r?\n\t\t@SubscribeEvent\r?\n\t\tstatic void onModelsBaked\()",
                   "\t\t@OnlyIn(Dist.CLIENT)\n\t\t@SubscribeEvent\n\t\tstatic void registerStandaloneModels(net.neoforged.neoforge.client.event.ModelEvent.RegisterStandalone event) {\n"
                   "\t\t\tcom.rekindled.embers.render.EmbersStandaloneModels.register(event);\n\t\t}\n\n\\1", t, count=1)
    print("standalone registration:", n)
e.write_bytes(t.encode("utf-8"))
