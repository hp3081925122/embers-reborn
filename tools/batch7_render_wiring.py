import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"
e = j / "Embers.java"
t = e.read_bytes().decode("utf-8")
start = t.index("\t\t@OnlyIn(Dist.CLIENT)\n\t\t@SubscribeEvent\n\t\tpublic static void shaderRegistry(") if "\t\t@OnlyIn(Dist.CLIENT)\n\t\t@SubscribeEvent\n\t\tpublic static void shaderRegistry(" in t else t.index("\t\t@OnlyIn(Dist.CLIENT)\r\n\t\t@SubscribeEvent\r\n\t\tpublic static void shaderRegistry(")
end = t.index("\t}\n}", start) if "\t}\n}" in t[start:] else t.index("\t}\r\n}", start)
t = t[:start].rstrip() + "\n" + t[end:]
t = t.replace("com.rekindled.embers.render.EmbersParticleLayers.registerPipelines(event);",
              "com.rekindled.embers.render.EmbersParticleLayers.registerPipelines(event);\n\t\t\tcom.rekindled.embers.render.EmbersRenderTypes.registerPipelines(event);")
for imp in ("net.minecraft.client.renderer.ShaderInstance", "net.neoforged.neoforge.client.event.RegisterShadersEvent",
            "net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent"):
    t = re.sub(r"import " + re.escape(imp) + r";\r?\n", "", t)
e.write_bytes(t.encode("utf-8"))

# particles: layers that no longer exist under the old sheet names
for f in (j / "particle").glob("*.java"):
    s = f.read_bytes().decode("utf-8")
    s2 = s.replace("EmbersParticleLayers.TRANSLUCENT_NODEPTH", "EmbersParticleLayers.TRANSLUCENT_NODEPTH")
    if s2 != s:
        f.write_bytes(s2.encode("utf-8"))
print("ok")
