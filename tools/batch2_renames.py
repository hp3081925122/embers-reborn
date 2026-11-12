import pathlib, re, sys

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
rules = [
    (re.compile(r"\bResourceLocation\b"), "Identifier"),
    (re.compile(r"\breadResourceLocation\("), "readIdentifier("),
    (re.compile(r"\bwriteResourceLocation\("), "writeIdentifier("),
    (re.compile(r"\.location\(\)"), ".identifier()"),
    (re.compile(r"\.isClientSide\b(?!\s*\()"), ".isClientSide()"),
]
changed = 0
counts = [0] * len(rules)
for f in root.rglob("*.java"):
    raw = f.read_bytes()
    text = raw.decode("utf-8")
    new = text
    for i, (pat, rep) in enumerate(rules):
        new, n = pat.subn(rep, new)
        counts[i] += n
    if new != text:
        f.write_bytes(new.encode("utf-8"))
        changed += 1
print("files changed:", changed)
for (pat, rep), n in zip(rules, counts):
    print(f"{n:5} {pat.pattern} -> {rep}")
