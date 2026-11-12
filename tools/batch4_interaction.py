"""26.1 block/item interaction API: InteractionResult unification, updateShape, DirectionProperty, appendHoverText."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"


def method_body(text, open_brace):
    """Return index just past the brace that closes the one at open_brace."""
    depth, i, n = 0, open_brace, len(text)
    in_str = None
    while i < n:
        c = text[i]
        if in_str:
            if c == "\\":
                i += 2
                continue
            if c == in_str:
                in_str = None
        elif c in "\"'":
            in_str = c
        elif c == "/" and text.startswith("//", i):
            i = text.index("\n", i)
            continue
        elif c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return i + 1
        i += 1
    return n


simple = [
    (r"\bItemInteractionResult\.sidedSuccess\([^()]*\)", "InteractionResult.SUCCESS"),
    (r"\bItemInteractionResult\.PASS_TO_DEFAULT_BLOCK_INTERACTION\b", "InteractionResult.TRY_WITH_EMPTY_HAND"),
    (r"\bItemInteractionResult\.SKIP_DEFAULT_BLOCK_INTERACTION\b", "InteractionResult.PASS"),
    (r"\bItemInteractionResult\.CONSUME_PARTIAL\b", "InteractionResult.CONSUME"),
    (r"\bItemInteractionResult\.(SUCCESS|CONSUME|FAIL)\b", r"InteractionResult.\1"),
    (r"\bInteractionResult\.sidedSuccess\([^()]*(?:\([^()]*\))?[^()]*\)", "InteractionResult.SUCCESS"),
    (r"\bInteractionResultHolder\.(?:sidedSuccess)\(([^,()]+(?:\([^()]*\))?),[^()]*(?:\([^()]*\))?\)", r"InteractionResult.SUCCESS.heldItemTransformedTo(\1)"),
    (r"\bInteractionResultHolder\.success\(", "InteractionResult.SUCCESS.heldItemTransformedTo("),
    (r"\bInteractionResultHolder\.consume\(", "InteractionResult.CONSUME.heldItemTransformedTo("),
    (r"\bInteractionResultHolder\.fail\([^()]*(?:\([^()]*\))?\)", "InteractionResult.FAIL"),
    (r"\bInteractionResultHolder\.pass\([^()]*(?:\([^()]*\))?\)", "InteractionResult.PASS"),
    (r"\bInteractionResultHolder<ItemStack>", "InteractionResult"),
    (r"\bItemInteractionResult\b", "InteractionResult"),
    (r"import net\.minecraft\.world\.InteractionResultHolder;\r?\n", ""),
    (r"\bDirectionProperty\b", "EnumProperty<Direction>"),
    (r"import net\.minecraft\.world\.level\.block\.state\.properties\.EnumProperty<Direction>;", "import net.minecraft.world.level.block.state.properties.EnumProperty;"),
]
simple = [(re.compile(a), b) for a, b in simple]

UPD = re.compile(r"(?:public|protected) BlockState updateShape\(\s*BlockState (\w+),\s*Direction (\w+),\s*BlockState (\w+),\s*LevelAccessor (\w+),\s*BlockPos (\w+),\s*BlockPos (\w+)\)\s*\{")
HOVER = re.compile(r"public void appendHoverText\(\s*ItemStack (\w+),\s*(?:Item\.)?TooltipContext (\w+),\s*List<Component> (\w+),\s*TooltipFlag (\w+)\)\s*\{")

stats = {"simple": 0, "updateShape": 0, "hover": 0}
for f in root.rglob("*.java"):
    t = f.read_bytes().decode("utf-8")
    new = t
    for pat, rep in simple:
        new, n = pat.subn(rep, new)
        stats["simple"] += n

    out, pos = [], 0
    for m in UPD.finditer(new):
        st, d, ns, lv, p, np_ = m.groups()
        end = method_body(new, m.end() - 1)
        body = new[m.end():end]
        body = re.sub(r"super\.updateShape\(\s*" + st + r",\s*" + d + r",\s*" + ns + r",\s*" + lv + r",\s*" + p + r",\s*" + np_ + r"\)",
                      f"super.updateShape({st}, {lv}, ticks, {p}, {d}, {np_}, {ns}, random)", body)
        body = re.sub(r"\b" + lv + r"\.scheduleTick\(", "ticks.scheduleTick(", body)
        head = (f"protected BlockState updateShape(BlockState {st}, net.minecraft.world.level.LevelReader {lv}, "
                f"net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos {p}, Direction {d}, BlockPos {np_}, "
                f"BlockState {ns}, net.minecraft.util.RandomSource random) {{")
        out.append(new[pos:m.start()] + head + body)
        pos = end
        stats["updateShape"] += 1
    out.append(new[pos:])
    new = "".join(out)

    out, pos = [], 0
    for m in HOVER.finditer(new):
        stack, ctx, tip, flag = m.groups()
        end = method_body(new, m.end() - 1)
        body = new[m.end():end]
        body = re.sub(r"\b" + tip + r"\.add\(", tip + ".accept(", body)
        body = re.sub(r"super\.appendHoverText\(\s*" + stack + r",\s*" + ctx + r",\s*" + tip + r",\s*" + flag + r"\)",
                      f"super.appendHoverText({stack}, {ctx}, display, {tip}, {flag})", body)
        head = (f"public void appendHoverText(ItemStack {stack}, Item.TooltipContext {ctx}, "
                f"net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> {tip}, TooltipFlag {flag}) {{")
        out.append(new[pos:m.start()] + head + body)
        pos = end
        stats["hover"] += 1
    out.append(new[pos:])
    new = "".join(out)

    if new != t:
        f.write_bytes(new.encode("utf-8"))
print(stats)
