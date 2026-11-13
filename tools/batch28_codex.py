import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"


def load(rel):
    return (root / rel).read_bytes().decode("utf-8")


def save(rel, s):
    (root / rel).write_bytes(s.encode("utf-8"))


def sub(s, old, new, count=None, regex=False):
    if regex:
        s2, n = re.subn(old, new, s)
    else:
        n = s.count(old)
        s2 = s.replace(old, new)
    if n == 0 or (count is not None and n != count):
        raise SystemExit("expected %s got %d: %s" % (count, n, old[:70]))
    return s2


# --- additive textured GUI pipeline (1.20.1: blendFunc(SRC_ALPHA, ONE) around textured blits)
rt = load("render/EmbersRenderTypes.java")
anchor = "\t/** Opaque textured GUI quads: the heat bar end caps."
rt = sub(rt, anchor,
         "\t/** Textured GUI quads with additive blend: the Codex ember glow on the index page. */\n"
         "\tpublic static final RenderPipeline GLOW_GUI_TEXTURED_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)\n"
         "\t\t\t.withLocation(id(\"pipeline/glow_gui_textured\"))\n"
         "\t\t\t.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))\n"
         "\t\t\t.build();\n\n" + anchor, 1)
rt = sub(rt, "\t\tevent.registerPipeline(GLOW_GUI_PIPELINE);",
         "\t\tevent.registerPipeline(GLOW_GUI_PIPELINE);\n\t\tevent.registerPipeline(GLOW_GUI_TEXTURED_PIPELINE);", 1)
save("render/EmbersRenderTypes.java", rt)

# --- RenderUtil glow helpers take GlowMesh instead of BufferBuilder
ru = load("util/RenderUtil.java")
ru = sub(ru, "renderWavyEmberLine(BufferBuilder b", "renderWavyEmberLine(GlowMesh b", 2)
ru = sub(ru, "renderHighlightCircle(BufferBuilder b", "renderHighlightCircle(GlowMesh b", 2)
save("util/RenderUtil.java", ru)

# --- GuiCodex
g = load("gui/GuiCodex.java")
NL = r"\r?\n"
# imports of removed classes
for imp in ["com.mojang.blaze3d.platform.GlStateManager", "com.mojang.blaze3d.vertex.BufferUploader",
            "net.minecraft.client.renderer.GameRenderer", "net.minecraft.client.renderer.LightTexture",
            "com.mojang.blaze3d.vertex.Tesselator", "com.mojang.blaze3d.vertex.BufferBuilder",
            "com.mojang.blaze3d.systems.RenderSystem"]:
    g = re.sub(r"import " + re.escape(imp) + ";" + NL, "", g)
# drawIfNotEmpty helper
g = sub(g, r"\tprivate static void drawIfNotEmpty\(BufferBuilder b\) \{" + NL + r"(.*" + NL + r"){3}\t\}" + NL + NL, "", 1, regex=True)
# glow geometry
g = sub(g, r"[ \t]*Tesselator tess = Tesselator\.getInstance\(\);" + NL, "", regex=True)
g = sub(g, "BufferBuilder b = tess.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLES, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);",
        "com.rekindled.embers.util.GlowMesh b = com.rekindled.embers.util.GlowMesh.triangles();", 3)
g = sub(g, "BufferBuilder b = tess.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_STRIP, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR);",
        "com.rekindled.embers.util.GlowMesh b = com.rekindled.embers.util.GlowMesh.strip();", 1)
g = sub(g, "drawIfNotEmpty(b);", "b.submit(graphics);", 4)
# render state calls the pipelines now own (blend, shader, texture binding)
g = sub(g, r"[ \t]*RenderSystem\.(enableBlend|disableBlend|defaultBlendFunc)\(\);" + NL, "", regex=True)
g = sub(g, r"[ \t]*RenderSystem\.setShader\(GameRenderer::getPositionColorShader\);" + NL, "", regex=True)
g = sub(g, r"[ \t]*RenderSystem\.setShaderTexture\(0, [^;]*\);" + NL, "", regex=True)
# 26.1 Screen draws the background before extractRenderState
g = sub(g, r"[ \t]*this\.renderBackground\(graphics, 0, 0, 0\);" + NL, "", 1, regex=True)
# keys
g = sub(g, ".matches(keyCode, scanCode)", ".matches(event)", 2)

# index page ember glow: additive textured blit tinted with the old shader colour
old_glow = re.search(r"[ \t]*RenderSystem\.blendFunc\(GlStateManager\.SourceFactor\.SRC_ALPHA, GlStateManager\.DestFactor\.ONE\);" + NL +
                     r"([ \t]*float sine = .*" + NL + r")"
                     r"[ \t]*RenderSystem\.setShaderColor\(EmbersColors\.EMBER\.x, EmbersColors\.EMBER\.y, EmbersColors\.EMBER\.z, sine\);" + NL +
                     r"([ \t]*for \(float i = 0; i < 4; i \+\+\)\{" + NL + r")"
                     r"[ \t]*blit\(graphics, PARTS, basePosX-16, basePosY\+208, 192, 0, 48, 48\);" + NL +
                     r"([ \t]*\}" + NL + r")"
                     r"[ \t]*RenderSystem\.setShaderColor\(1\.0F, 1\.0F, 1\.0F, 1\.0F\);" + NL, g)
if not old_glow:
    raise SystemExit("index glow block not found")
new_glow = (old_glow.group(1)
            + "\t\t\tint emberTint = net.minecraft.util.ARGB.colorFromFloat(sine, EmbersColors.EMBER.x, EmbersColors.EMBER.y, EmbersColors.EMBER.z);\n"
            + old_glow.group(2)
            + "\t\t\t\tgraphics.blit(com.rekindled.embers.render.EmbersRenderTypes.GLOW_GUI_TEXTURED_PIPELINE, PARTS, basePosX-16, basePosY+208, 192, 0, 48, 48, 48, 48, 256, 256, emberTint);\n"
            + old_glow.group(3))
g = g[:old_glow.start()] + new_glow + g[old_glow.end():]

# the remaining additive blendFunc calls sit in front of GlowMesh geometry, which is additive already
g = sub(g, r"[ \t]*RenderSystem\.blendFunc\(GlStateManager\.SourceFactor\.SRC_ALPHA, GlStateManager\.DestFactor\.ONE\);" + NL, "", regex=True)

# glowing modal rect (page arrows): tinted blits instead of setShaderColor
m = re.search(r"\tpublic void drawModalRectGlowing\(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int textureX, int textureY, int width, int height\) \{"
              + NL + r"(?:.*" + NL + r")*?\t\}" + NL, g)
if not m:
    raise SystemExit("drawModalRectGlowing not found")
body = m.group(0)
body = sub(body, "\t\tRenderSystem.setShaderColor(0,0,0,64f/255);", "\t\tint tint = net.minecraft.util.ARGB.colorFromFloat(64f/255, 0, 0, 0);", 1)
body = sub(body, "\t\tRenderSystem.setShaderColor(0,0,0,40f/255);", "\t\ttint = net.minecraft.util.ARGB.colorFromFloat(40f/255, 0, 0, 0);", 1)
body = sub(body, "\t\tRenderSystem.setShaderColor(color.x, color.y, color.z, 1.0f);", "\t\ttint = net.minecraft.util.ARGB.colorFromFloat(1.0f, color.x, color.y, color.z);", 1)
body = sub(body, r"blit\(graphics, texture, ([^;]*),textureX,textureY,width,height\);",
           r"blitTinted(graphics, texture, \1, textureX, textureY, width, height, tint);", regex=True)
body += ("\n\tprivate static void blitTinted(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, int u, int v, int width, int height, int tint) {\n"
         "\t\tgraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, 256, 256, tint);\n"
         "\t}\n")
g = g[:m.start()] + body + g[m.end():]
g = sub(g, r"[ \t]*RenderSystem\.setShaderColor\(1\.0F, 1\.0F, 1\.0F, 1\.0F\);" + NL, "", regex=True)

left = [l.strip() for l in g.splitlines() if re.search(r"\b(RenderSystem|GlStateManager|Tesselator|BufferBuilder)\b", l) and not l.strip().startswith(("//", "/*", "*", "GlStateManager.blendFunc(SourceFactor"))]
save("gui/GuiCodex.java", g)
print("leftover:", left)
