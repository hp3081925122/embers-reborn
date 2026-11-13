"""1.21.1 bug: the moving machine parts (bore blades, pump pistons, stamper arm, ...) were never registered as standalone
models and were looked up without the block/ prefix, so getModel always returned null and none of them rendered."""
import pathlib, re, sys

j = pathlib.Path(sys.argv[1]) / "src/main/java/com/rekindled/embers"
ce = j / "EmbersClientEvents.java"
t = ce.read_bytes().decode("utf-8")
IDC = "ResourceLocation" if "ResourceLocation.fromNamespaceAndPath" in t else "Identifier"
names = re.findall(r'getModel\(bakery, "(\w+)"\)', t)
assert len(names) == 18, names
t, n = re.subn(r'ModelResourceLocation\.standalone\(\s*(?:Identifier|ResourceLocation)\.fromNamespaceAndPath\(Embers\.MODID, name\)\)',
               'ModelResourceLocation.standalone(\n\t\t\t\t' + IDC + '.fromNamespaceAndPath(Embers.MODID, "block/" + name))', t)
assert n == 1, n
if "STANDALONE_MODELS" not in t:
    arr = ", ".join(f'"{x}"' for x in names)
    t = t.replace("\tpublic static void afterModelBake(ModelEvent.BakingCompleted event) {",
                  f"\tpublic static final String[] STANDALONE_MODELS = {{{arr}}};\n\n"
                  "\tpublic static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {\n"
                  "\t\tfor (String name : STANDALONE_MODELS)\n"
                  "\t\t\tevent.register(ModelResourceLocation.standalone(" + IDC + ".fromNamespaceAndPath(Embers.MODID, \"block/\" + name)));\n"
                  "\t}\n\n"
                  "\tpublic static void afterModelBake(ModelEvent.BakingCompleted event) {", 1)
ce.write_bytes(t.encode("utf-8"))

e = j / "Embers.java"
s = e.read_bytes().decode("utf-8")
if "registerAdditionalModels" not in s:
    s, n = re.subn(r"(\t\t@OnlyIn\(Dist\.CLIENT\)\r?\n\t\t@SubscribeEvent\r?\n\t\tstatic void onModelsBaked\()",
                   "\t\t@OnlyIn(Dist.CLIENT)\n\t\t@SubscribeEvent\n\t\tstatic void registerAdditionalModels(net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional event) {\n"
                   "\t\t\tEmbersClientEvents.registerAdditionalModels(event);\n\t\t}\n\n\\1", s, count=1)
    assert n == 1, "onModelsBaked not found"
    e.write_bytes(s.encode("utf-8"))
print("fixed", len(names), "models")
