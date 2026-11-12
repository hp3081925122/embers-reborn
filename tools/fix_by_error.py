"""Apply regex fixes only on source lines that javac reported with a given missing symbol."""
import pathlib, re, sys

err_log = pathlib.Path(sys.argv[1])
lines = err_log.read_text(encoding="utf-8", errors="replace").splitlines()
LOC = re.compile(r"^(.*\.java):(\d+): error: (.*)$")

# symbol regex -> list of (pattern, replacement) applied to the reported line
FIXES = {
    r"method (serializeNBT)\(Provider\)": [
        (r"\b([\w.]+)\.serializeNBT\(([^()]+)\)", r"com.rekindled.embers.util.NbtBridge.save(\1, \2)"),
    ],
    r"method (deserializeNBT)\(Provider,CompoundTag\)": [
        (r"\b([\w.]+)\.deserializeNBT\(([^,()]+),\s*", r"com.rekindled.embers.util.NbtBridge.load(\1, \2, "),
    ],
    r"method writeToNBT\(Provider,CompoundTag\)": [
        (r"\b([\w.]+)\.writeToNBT\(([^,()]+),\s*", r"com.rekindled.embers.util.NbtBridge.writeTank(\1, \2, "),
    ],
    r"method readFromNBT\(Provider,CompoundTag\)": [
        (r"\b([\w.]+)\.readFromNBT\(([^,()]+),\s*", r"com.rekindled.embers.util.NbtBridge.load(\1, \2, "),
    ],
}
targets = {}
for i, l in enumerate(lines):
    m = LOC.match(l.strip())
    if not m:
        continue
    for j in range(i + 1, min(i + 6, len(lines))):
        s = re.match(r"^\s*symbol:\s+(.*)$", lines[j])
        if s:
            for key, fx in FIXES.items():
                if re.match(key, s.group(1).strip()):
                    targets.setdefault((m.group(1), int(m.group(2))), fx)
            break
count = 0
by_file = {}
for (f, ln), fx in targets.items():
    by_file.setdefault(f, []).append((ln, fx))
for f, items in by_file.items():
    p = pathlib.Path(f)
    raw = p.read_bytes().decode("utf-8")
    src = raw.split("\n")
    for ln, fx in items:
        old = src[ln - 1]
        new = old
        for a, b in fx:
            new = re.sub(a, b, new)
        if new != old:
            src[ln - 1] = new
            count += 1
    p.write_bytes("\n".join(src).encode("utf-8"))
print("lines fixed:", count, "of", len(targets))
