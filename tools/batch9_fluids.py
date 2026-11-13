"""Remove 1.21.1 initializeClient overrides from fluid types and route fog-colour lookups through EmbersFluidClient."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"


def drop_method(t, header_regex):
    m = re.search(header_regex, t)
    if not m:
        return t, False
    i = t.index("{", m.end() - 1)
    depth = 0
    k = i
    while True:
        if t[k] == "{":
            depth += 1
        elif t[k] == "}":
            depth -= 1
            if depth == 0:
                break
        k += 1
    start = t.rfind("\n", 0, m.start()) + 1
    return t[:start] + t[k + 1:].lstrip("\r\n"), True


for name in ("EmbersFluidType", "MoltenMetalFluidType", "SteamFluidType", "ViscousFluidType"):
    f = j / "fluidtypes" / f"{name}.java"
    t = f.read_bytes().decode("utf-8")
    t, ok = drop_method(t, r"(?:@Override\s*)?public void initializeClient\(Consumer<IClientFluidTypeExtensions> consumer\)\s*\{")
    print(name, "initializeClient removed" if ok else "NOT FOUND")
    for imp in ("net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions", "com.mojang.blaze3d.systems.RenderSystem",
                "net.minecraft.client.renderer.FogRenderer", "com.mojang.blaze3d.shaders.FogShape", "net.minecraft.client.Camera",
                "net.minecraft.client.multiplayer.ClientLevel", "net.minecraft.client.Minecraft", "java.util.function.Consumer"):
        t = re.sub(r"import " + re.escape(imp) + r";\r?\n", "", t)
    f.write_bytes(t.encode("utf-8"))

pat = re.compile(r"IClientFluidTypeExtensions\.of\(([^;]*?)\.getFluidType\(\)\)\.modifyFogColor\([^;]*?new Vector3f\(1, 1, 1\)\)")
n = 0
for f in j.rglob("*.java"):
    t = f.read_bytes().decode("utf-8")
    t2, k = pat.subn(r"com.rekindled.embers.fluidtypes.EmbersFluidClient.fogColor(\1)", t)
    if k:
        t2 = re.sub(r"import net\.neoforged\.neoforge\.client\.extensions\.common\.IClientFluidTypeExtensions;\r?\n", "", t2)
        f.write_bytes(t2.encode("utf-8"))
        n += k
print("fog colour lookups:", n)
