import pathlib, re

j = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"


def edit(rel, pairs):
    f = j / rel
    t = f.read_bytes().decode("utf-8")
    for a, b in pairs:
        if not re.search(a, t):
            print("  MISS", rel, a[:70])
        t = re.sub(a, b, t)
    f.write_bytes(t.encode("utf-8"))


edit("RegistryManager.java", [
    (r"new AshenArmorGemItem\(net\.minecraft\.core\.Holder\.direct\(AshenArmorMaterial\.INSTANCE\), ArmorItem\.Type\.(\w+), AshenArmorMaterial\.properties\(ArmorItem\.Type\.\w+\), ",
     r"new AshenArmorGemItem(AshenArmorMaterial.properties(net.minecraft.world.item.equipment.ArmorType.\1), "),
])
edit("item/AshenArmorGemItem.java", [
    (r"public AshenArmorGemItem\(net\.minecraft\.core\.Holder<net\.minecraft\.world\.item\.ArmorMaterial> material, Type type, Properties properties, ",
     "public AshenArmorGemItem(Properties properties, "),
    (r"super\(material, type, properties\);", "super(properties);"),
])
edit("item/AshenArmorItem.java", [
    (r"extends ArmorItem", "extends net.minecraft.world.item.Item"),
    (r"public AshenArmorItem\(net\.minecraft\.core\.Holder<net\.minecraft\.world\.item\.ArmorMaterial> material, Type type, Properties properties\) \{\s*super\(material, type, properties\);",
     "public AshenArmorItem(Properties properties) {\n\t\tsuper(properties);"),
    # texture now comes from the equipment asset assets/embers_reborn/equipment/ashen.json
    (r"\t@Override\s*public Identifier getArmorTexture\([^)]*\) \{[^}]*\}\s*", ""),
])
print("done")
