"""Batch 11f: HUD overlays (gauge, shifting scales, winding gears) on GuiGraphicsExtractor.
1.21.1 blit(tex, x, y, u, v, w, h) assumed a 256x256 texture;
blit(tex, x, y, z, u, v, w, h, texW, texH) had a z offset that 26.1 GUI no longer has."""
import pathlib, re

base = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
P = "net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED"
ARG = r"([^,()]+(?:\([^()]*\))?[^,()]*)"  # one argument, allows one nested call

def blit7(m):
    return f"graphics.blit({P}, {m.group(1)}, {m.group(2)}, {m.group(3)}, {m.group(4)}, {m.group(5)}, {m.group(6)}, {m.group(7)}, 256, 256);"

def blit10(m):
    g = [x.strip() for x in m.groups()]
    # tex, x, y, z, u, v, w, h, texW, texH -> drop z
    return f"graphics.blit({P}, {g[0]}, {g[1]}, {g[2]}, {g[4]}, {g[5]}, {g[6]}, {g[7]}, {g[8]}, {g[9]});"

r7 = re.compile(r"graphics\.blit\(" + ",".join([ARG] * 7) + r"\);")
r10 = re.compile(r"graphics\.blit\(" + ",".join([ARG] * 10) + r"\);")

for rel in ["augment/ShiftingScalesAugment.java", "augment/WindingGearsAugment.java", "EmbersClientEvents.java"]:
    p = base / rel
    s = p.read_bytes().decode("utf-8")
    o = s
    s = re.sub(r"\bnet\.minecraft\.client\.gui\.GuiGraphics\b", "net.minecraft.client.gui.GuiGraphicsExtractor", s)
    s = re.sub(r"\bGuiGraphics\b", "GuiGraphicsExtractor", s)
    s = re.sub(r"GuiGraphicsExtractorExtractor", "GuiGraphicsExtractor", s)
    s = r10.sub(blit10, s)
    s = r7.sub(blit7, s)
    # 26.1 GUI pose is a Matrix3x2fStack
    s = s.replace("graphics.pose().pushPose();", "graphics.pose().pushMatrix();")
    s = s.replace("graphics.pose().popPose();", "graphics.pose().popMatrix();")
    s = re.sub(r"graphics\.pose\(\)\.translate\(([^,()]+),\s*([^,()]+),\s*0\);", r"graphics.pose().translate((float) (\1), (float) (\2));", s)
    s = re.sub(r"graphics\.pose\(\)\.mulPose\(Axis\.ZP\.rotationDegrees\(([^;]+)\)\);", r"graphics.pose().rotate((float) Math.toRadians(\1));", s)
    if "drawString(" in s:
        s = re.sub(r"graphics\.drawString\(", "graphics.text(", s)
    left = [l.strip() for l in s.splitlines() if re.search(r"graphics\.blit\((?!net\.minecraft)", l)]
    p.write_bytes(s.encode("utf-8"))
    print(rel, "changed" if s != o else "UNCHANGED", "| unconverted blit:", left)
