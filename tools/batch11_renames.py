"""Batch 11: simple 26.1 renames across src/main/java."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
RULES = [
    (re.compile(r"\.getNormal\(\)"), ".getUnitVec3i()"),
    (re.compile(r"\.getTimer\(\)"), ".getDeltaTracker()"),
    (re.compile(r"InteractionResult\.sidedSuccess\([^()]*(?:\([^()]*\))?[^()]*\)"), "InteractionResult.SUCCESS"),
    (re.compile(r"InteractionResult\.CONSUME_PARTIAL\b"), "InteractionResult.CONSUME"),
    (re.compile(r"Ingredient\.CODEC_NONEMPTY\b"), "Ingredient.CODEC"),
]
changed = 0
for p in root.rglob("*.java"):
    s = p.read_bytes().decode("utf-8")
    o = s
    for rx, rep in RULES:
        s = rx.sub(rep, s)
    if s != o:
        p.write_bytes(s.encode("utf-8"))
        changed += 1
        print("changed", p.relative_to(root))
print("files", changed)
