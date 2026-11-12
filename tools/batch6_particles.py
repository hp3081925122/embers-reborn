"""26.1 particles: TextureSheetParticle -> SingleQuadParticle, sprite passed in, Layer instead of ParticleRenderType."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java" / "com" / "rekindled" / "embers"
SPRITE_T = "net.minecraft.client.renderer.texture.TextureAtlasSprite"
report = []
for f in sorted((root / "particle").glob("*Particle.java")):
    t = f.read_bytes().decode("utf-8")
    cls = f.stem
    new = t.replace("import net.minecraft.client.particle.TextureSheetParticle;", "import net.minecraft.client.particle.SingleQuadParticle;")
    new = new.replace("extends TextureSheetParticle", "extends SingleQuadParticle")
    # constructor: append sprite parameter and pass it to super
    ctor = re.search(r"public " + cls + r"\(([^)]*)\)\s*\{", new)
    if ctor:
        params = ctor.group(1)
        new = new[:ctor.start()] + f"public {cls}({params}, {SPRITE_T} sprite) {{" + new[ctor.end():]
        sup = re.compile(r"super\((\w+),\s*(\w+),\s*(\w+),\s*(\w+)((?:,\s*\w+){3})?\);")
        m = sup.search(new, ctor.start())
        if m:
            new = new[:m.start()] + m.group(0)[:-2] + ", sprite);" + new[m.end():]
    # render type -> layer
    new = re.sub(r"public ParticleRenderType getRenderType\(\)", "public SingleQuadParticle.Layer getLayer()", new)
    new = new.replace("EmbersRenderTypes.PARTICLE_SHEET_", "com.rekindled.embers.render.EmbersParticleLayers.")
    new = re.sub(r"(?:protected|public) int getLightColor\(float (\w+)\)", r"public int getLightCoords(float \1)", new)
    new = re.sub(r"super\.getLightColor\(", "super.getLightCoords(", new)
    # provider
    pm = re.search(r"public static class Provider implements ParticleProvider\.Sprite<(\w+)>\s*\{\s*public \w+ createParticle\((\w+) (\w+),\s*ClientLevel (\w+),([^)]*)\)\s*\{\s*return new " + cls + r"\(([^;]*)\);\s*\}\s*\}", new)
    if pm:
        opt, _, _, _, rest, args = pm.groups()
        prov = (f"public static class Provider implements net.minecraft.client.particle.ParticleProvider<{opt}> {{\n"
                f"\t\tprivate final net.minecraft.client.particle.SpriteSet sprites;\n\n"
                f"\t\tpublic Provider(net.minecraft.client.particle.SpriteSet sprites) {{\n\t\t\tthis.sprites = sprites;\n\t\t}}\n\n"
                f"\t\t@Override\n\t\tpublic net.minecraft.client.particle.Particle createParticle({pm.group(2)} {pm.group(3)}, ClientLevel {pm.group(4)},{rest}, net.minecraft.util.RandomSource random) {{\n"
                f"\t\t\treturn new {cls}({args}, sprites.get(random));\n\t\t}}\n\t}}")
        new = new[:pm.start()] + prov + new[pm.end():]
    report.append(f"{cls}: ctor={'y' if ctor else 'N'} provider={'y' if pm else 'N'} render={'MANUAL' if 'void render(' in new else '-'}")
    if new != t:
        f.write_bytes(new.encode("utf-8"))

emb = root / "Embers.java"
t = emb.read_bytes().decode("utf-8")
t2, n = re.subn(r"event\.registerSprite\(([^,]+),\s*new (\w+)\.Provider\(\)\)", r"event.registerSpriteSet(\1, \2.Provider::new)", t)
emb.write_bytes(t2.encode("utf-8"))
print("\n".join(report))
print("registrations:", n)
