"""One-off: move resource references from the old `embers` namespace to `embers_reborn`.

Run from the project root: python tools/renamespace.py
"""
import json
import re
from pathlib import Path

import nbtlib

ROOT = Path("src/main/resources")
OLD, NEW = "embers", "embers_reborn"
ID_RE = re.compile(r"(?<![\w.-])embers:")
TEXT_EXT = {".json", ".mcmeta", ".fsh", ".vsh", ".glsl"}


def fix_text(p: Path) -> bool:
    raw = p.read_bytes().decode("utf-8")
    new = ID_RE.sub(NEW + ":", raw)
    if new != raw:
        p.write_bytes(new.encode("utf-8"))
        return True
    return False


def fix_lang_key(k: str) -> str:
    # "embers.tooltip.x" -> "embers_reborn.tooltip.x"; "block.embers.x" -> "block.embers_reborn.x"
    parts = k.split(".")
    return ".".join(NEW if part == OLD else part for part in parts)


def fix_lang(p: Path) -> bool:
    text = p.read_bytes().decode("utf-8")
    data = json.loads(text)
    out = {fix_lang_key(k): v for k, v in data.items()}
    if list(out) == list(data):
        return False
    p.write_bytes((json.dumps(out, ensure_ascii=False, indent=2) + "\n").encode("utf-8"))
    return True


def walk_nbt(tag):
    changed = False
    if isinstance(tag, nbtlib.tag.Compound):
        for k in list(tag.keys()):
            v = tag[k]
            if isinstance(v, nbtlib.tag.String) and v.startswith(OLD + ":"):
                tag[k] = nbtlib.tag.String(NEW + v[len(OLD):])
                changed = True
            else:
                changed |= walk_nbt(v)
    elif isinstance(tag, nbtlib.tag.List):
        for i, v in enumerate(tag):
            if isinstance(v, nbtlib.tag.String) and v.startswith(OLD + ":"):
                tag[i] = nbtlib.tag.String(NEW + v[len(OLD):])
                changed = True
            else:
                changed |= walk_nbt(v)
    return changed


def fix_nbt(p: Path) -> bool:
    f = nbtlib.load(p)
    if walk_nbt(f):
        f.save()
        return True
    return False


counts = {"text": 0, "lang": 0, "nbt": 0}
for p in ROOT.rglob("*"):
    if not p.is_file():
        continue
    if p.suffix == ".nbt":
        counts["nbt"] += fix_nbt(p)
    elif p.parent.name == "lang" and p.suffix == ".json":
        counts["lang"] += fix_lang(p)
        counts["text"] += fix_text(p)
    elif p.suffix in TEXT_EXT:
        counts["text"] += fix_text(p)
print(counts)
