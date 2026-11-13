"""Batch 11e: GuiGraphics -> GuiGraphicsExtractor in JEI categories and Misc.drawComponents."""
import pathlib, re

base = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
FILES = ["compat/jei/BoringCategory.java", "compat/jei/CatalysisCombustionCategory.java",
         "compat/jei/EmberActivationCategory.java", "compat/jei/GaseousFuelCategory.java",
         "compat/jei/MetalCoefficientCategory.java", "util/Misc.java"]
for rel in FILES:
    p = base / rel
    s = p.read_bytes().decode("utf-8")
    o = s
    s = re.sub(r"\bGuiGraphics\b", "GuiGraphicsExtractor", s)
    if rel.endswith("Misc.java"):
        # 26.1 text colors are ARGB; 0xFFFFFF would be fully transparent
        s = s.replace("guiGraphics.drawString(fontRenderer, component, x, y, 0xFFFFFF);",
                      "guiGraphics.text(fontRenderer, component, x, y, 0xFFFFFFFF);")
        assert "guiGraphics.drawString" not in s, rel
    p.write_bytes(s.encode("utf-8"))
    print(rel, "changed" if s != o else "UNCHANGED")
