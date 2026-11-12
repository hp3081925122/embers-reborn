"""Convert Block#onRemove(state, level, pos, newState, isMoving) to LegacyRemoveBlock#onRemoveLegacy."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
HDR = re.compile(r"(@Override\s+)?(?:public|protected) void onRemove\(\s*BlockState (\w+),\s*Level (\w+),\s*BlockPos (\w+),\s*BlockState (\w+),\s*boolean (\w+)\)\s*\{")
EXT = re.compile(r"\bclass (\w+)(?:<[^>{]*>)?\s+extends (\w+)(?:<[^>{]*>)?([^{]*)\{")

files = {f: f.read_bytes().decode("utf-8") for f in root.rglob("*.java")}
parent, has = {}, set()
for f, t in files.items():
    m = EXT.search(t)
    if m:
        parent[m.group(1)] = m.group(2)
    if HDR.search(t):
        has.add(f.stem)


def bridged(cls):
    p, seen = parent.get(cls), set()
    while p and p not in seen:
        if p in has:
            return True
        seen.add(p)
        p = parent.get(p)
    return False


n = 0
for f, t in files.items():
    cls = f.stem
    if cls not in has:
        continue
    b = bridged(cls)

    def rep(m):
        st, lv, p, ns, mv = m.group(2), m.group(3), m.group(4), m.group(5), m.group(6)
        head = ""
        if not b:
            head = ("@Override\n\tprotected void affectNeighborsAfterRemoval(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, boolean movedByPiston) {\n"
                    "\t\tif (!state.hasBlockEntity())\n"
                    "\t\t\tonRemoveLegacy(state, level, pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), movedByPiston);\n"
                    "\t\tsuper.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);\n"
                    "\t}\n\n\t")
        return f"{head}@Override\n\tpublic void onRemoveLegacy(BlockState {st}, Level {lv}, BlockPos {p}, BlockState {ns}, boolean {mv}) {{"

    new = HDR.sub(rep, t)
    if b:
        new = re.sub(r"super\.onRemove\((\w+),\s*(\w+),\s*(\w+),\s*(\w+),\s*(\w+)\)", r"super.onRemoveLegacy(\1, \2, \3, \4, \5)", new)
    else:
        new = re.sub(r"[ \t]*super\.onRemove\(\w+,\s*\w+,\s*\w+,\s*\w+,\s*\w+\);\r?\n", "", new)
        m = EXT.search(new)
        if m and m.group(1) == cls:
            tail = m.group(3)
            if "implements" in tail:
                tail2 = tail.rstrip() + ", com.rekindled.embers.util.LegacyRemoveBlock "
            else:
                tail2 = tail.rstrip() + " implements com.rekindled.embers.util.LegacyRemoveBlock "
            new = new[:m.start(3)] + tail2 + new[m.end(3):]
    f.write_bytes(new.encode("utf-8"))
    n += 1
    print(("  sub  " if b else "  root ") + cls)
print("converted:", n)
