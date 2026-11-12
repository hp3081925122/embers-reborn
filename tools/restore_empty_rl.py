"""The AI replaced ~232 ResourceLocations with ResourceLocation.parse(MODID + ":empty").
Restore each from the matching line in the 1.20.1 source.

Run from project root: python tools/restore_empty_rl.py [--apply]
"""
import difflib
import io
import re
import sys
from pathlib import Path

NEW_ROOT = Path("src/main/java")
OLD_ROOT = Path("../_source-1201/src/main/java")
PLACEHOLDER = re.compile(r'ResourceLocation\.parse\((?:com\.rekindled\.embers\.)?Embers\.MODID \+ ":empty"\)')
OLD_START = re.compile(r"new ResourceLocation\(")
NEW_START = re.compile(r"ResourceLocation\.(?:fromNamespaceAndPath|parse|withDefaultNamespace|tryParse)\(")


def calls(line, start_re):
    """Return [(start, end, args_text)] for balanced calls matching start_re."""
    out = []
    for m in start_re.finditer(line):
        i, depth = m.end(), 1
        in_str = False
        while i < len(line) and depth:
            c = line[i]
            if c == '"' and line[i - 1] != "\\":
                in_str = not in_str
            elif not in_str:
                if c == "(":
                    depth += 1
                elif c == ")":
                    depth -= 1
            i += 1
        out.append((m.start(), i, line[m.end():i - 1]))
    return out


def split_args(s):
    depth, in_str, parts, cur = 0, False, [], ""
    for i, c in enumerate(s):
        if c == '"' and (i == 0 or s[i - 1] != "\\"):
            in_str = not in_str
        if not in_str:
            if c in "([{":
                depth += 1
            elif c in ")]}":
                depth -= 1
            elif c == "," and depth == 0:
                parts.append(cur.strip())
                cur = ""
                continue
        cur += c
    parts.append(cur.strip())
    return parts


def convert(old_args):
    a = split_args(old_args)
    if len(a) == 2:
        return f"ResourceLocation.fromNamespaceAndPath({a[0]}, {a[1]})"
    return f"ResourceLocation.parse({a[0]})"


def norm(line):
    line = re.sub(r"\s+", "", line)
    line = re.sub(r"(new)?ResourceLocation(\.\w+)?\(", "RL(", line)
    return line


apply = "--apply" in sys.argv
fixed = unresolved = 0
for p in NEW_ROOT.rglob("*.java"):
    text = io.open(p, encoding="utf-8", newline="").read()
    if not PLACEHOLDER.search(text):
        continue
    rel = p.relative_to(NEW_ROOT)
    op = OLD_ROOT / rel
    if not op.exists():
        print("NO OLD FILE", rel)
        unresolved += len(PLACEHOLDER.findall(text))
        continue
    nl = "\r\n" if "\r\n" in text else "\n"
    new_lines = text.split(nl)
    old_lines = io.open(op, encoding="utf-8").read().splitlines()
    sm = difflib.SequenceMatcher(None, [norm(l) for l in old_lines], [norm(l) for l in new_lines], autojunk=False)
    mapping = {}
    for tag, i1, i2, j1, j2 in sm.get_opcodes():
        if tag == "equal" or (tag == "replace" and i2 - i1 == j2 - j1):
            for k in range(j2 - j1):
                mapping[j1 + k] = i1 + k
    for j, line in enumerate(new_lines):
        if not PLACEHOLDER.search(line):
            continue
        i = mapping.get(j)
        # fallback: best fuzzy match among old lines
        if i is None or len(calls(old_lines[i], OLD_START)) != len(calls(line, NEW_START)):
            cands = difflib.get_close_matches(norm(line), [norm(l) for l in old_lines], n=1, cutoff=0.75)
            i = [norm(l) for l in old_lines].index(cands[0]) if cands else None
        if i is None:
            print(f"UNRESOLVED {rel}:{j+1}: {line.strip()[:140]}")
            unresolved += len(PLACEHOLDER.findall(line))
            continue
        oc = calls(old_lines[i], OLD_START)
        nc = calls(line, NEW_START)
        if len(oc) != len(nc):
            print(f"COUNT MISMATCH {rel}:{j+1} old={len(oc)} new={len(nc)}: {line.strip()[:140]}")
            unresolved += len(PLACEHOLDER.findall(line))
            continue
        out, last = "", 0
        for (s, e, _), (_, _, oargs) in zip(nc, oc):
            seg = line[s:e]
            out += line[last:s]
            if PLACEHOLDER.fullmatch(seg):
                out += convert(oargs)
                fixed += 1
            else:
                out += seg
            last = e
        out += line[last:]
        new_lines[j] = out
    if apply:
        io.open(p, "w", encoding="utf-8", newline="").write(nl.join(new_lines))
print(f"fixed={fixed} unresolved={unresolved} apply={apply}")
