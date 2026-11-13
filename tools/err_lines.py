"""List error locations with symbol/location and the source line. Usage: err_lines.py LOG [regex-on-symbol-or-message] [max]"""
import pathlib, re, sys

log = pathlib.Path(sys.argv[1]).read_text(encoding="utf-8", errors="replace").splitlines()
flt = re.compile(sys.argv[2]) if len(sys.argv) > 2 else None
mx = int(sys.argv[3]) if len(sys.argv) > 3 else 400
LOC = re.compile(r"^(.*\.java):(\d+): error: (.*)$")
n = 0
for i, l in enumerate(log):
    m = LOC.match(l.strip())
    if not m:
        continue
    msg = m.group(3)
    sym = ""
    loc = ""
    for j in range(i + 1, min(i + 6, len(log))):
        s = re.match(r"^\s*symbol:\s+(.*)$", log[j])
        if s:
            sym = s.group(1).strip()
        s = re.match(r"^\s*location:\s+(.*)$", log[j])
        if s:
            loc = s.group(1).strip()
        if LOC.match(log[j].strip()):
            break
    key = f"{msg} {sym}"
    if flt and not flt.search(key):
        continue
    f = m.group(1)
    short = re.sub(r"^.*\\com\\rekindled\\embers\\", "", f)
    try:
        src = pathlib.Path(f).read_text(encoding="utf-8", errors="replace").splitlines()[int(m.group(2)) - 1].strip()
    except Exception:
        src = "?"
    print(f"{short}:{m.group(2)} | {msg[:80]} | {sym} | {loc[:50]}\n    {src[:160]}")
    n += 1
    if n >= mx:
        break
print("count", n)
