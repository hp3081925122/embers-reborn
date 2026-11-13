"""26.1 renames with a 1:1 replacement (behaviour unchanged)."""
import pathlib, re

SRC = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"

RULES = [
    # Screen.hasControlDown() moved to Minecraft
    (r"\bScreen\.hasControlDown\(\)", "net.minecraft.client.Minecraft.getInstance().hasControlDown()"),
    (r"\bScreen\.hasShiftDown\(\)", "net.minecraft.client.Minecraft.getInstance().hasShiftDown()"),
    # per-level profiler is gone, there is one global profiler
    (r"\b\w+(?:\.\w+\(\))*\.getProfiler\(\)", "net.minecraft.util.profiling.Profiler.get()"),
    (r"\.getMinBuildHeight\(\)", ".getMinY()"),
    # UseAnim -> ItemUseAnimation (same constants)
    (r"import net\.minecraft\.world\.item\.UseAnim;", "import net.minecraft.world.item.ItemUseAnimation;"),
    (r"\bUseAnim\b", "ItemUseAnimation"),
    # damage type holder lookup
    (r"registryAccess\(\)\.registry\(Registries\.DAMAGE_TYPE\)\.get\(\)\.getHolderOrThrow\(",
     "registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow("),
]

changed = []
for p in SRC.rglob("*.java"):
    src = p.read_text(encoding="utf-8")
    new = src
    for rx, rep in RULES:
        new = re.sub(rx, rep, new)
    # files that return InteractionResult but lost the import (InteractionResultHolder was removed)
    if re.search(r"\bInteractionResult\b", new) and "import net.minecraft.world.InteractionResult;" not in new \
            and "net.minecraft.world.InteractionResult " not in new:
        new = re.sub(r"(\npackage [\w.]+;\r?\n)", r"\1\nimport net.minecraft.world.InteractionResult;\n", new, count=1) \
            if not new.startswith("package") else re.sub(r"^(package [\w.]+;\r?\n)", r"\1\nimport net.minecraft.world.InteractionResult;\n", new, count=1)
    if new != src:
        p.write_text(new, encoding="utf-8", newline="")
        changed.append(p.relative_to(SRC).as_posix())
print(len(changed), "files:", ", ".join(changed))
