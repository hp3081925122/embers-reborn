import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"


def body_span(s, start):
    """Return (open_brace_index, close_brace_index) of the method body starting after index start."""
    i = s.index("{", start)
    depth = 0
    for j in range(i, len(s)):
        c = s[j]
        if c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                return i, j
    raise ValueError("unbalanced")


HOVER = re.compile(r"public void appendHoverText\(ItemStack (\w+), net\.minecraft\.world\.item\.Item\.TooltipContext (\w+), List<Component> (\w+), TooltipFlag (\w+)\)")
SUPER_HOVER = re.compile(r"super\.appendHoverText\((\w+), (\w+), (\w+), (\w+)\);")
REL = re.compile(r"public void releaseUsing\(ItemStack \w+, Level \w+, LivingEntity \w+, int \w+\)")
READ = re.compile(r"protected void readAdditionalSaveData\(CompoundTag (\w+)\)")
ADD = re.compile(r"protected void addAdditionalSaveData\(CompoundTag (\w+)\)")

changed = {}
for f in root.rglob("*.java"):
    raw = f.read_bytes().decode("utf-8")
    s = raw

    # appendHoverText: Consumer instead of List, plus TooltipDisplay
    while True:
        m = HOVER.search(s)
        if not m:
            break
        stack, ctx, tip, flag = m.groups()
        sig = ("public void appendHoverText(ItemStack %s, net.minecraft.world.item.Item.TooltipContext %s, "
               "net.minecraft.world.item.component.TooltipDisplay tooltipDisplay, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag %s)") % (stack, ctx, flag)
        s = s[:m.start()] + sig + s[m.end():]
        ob, cb = body_span(s, m.start())
        body = s[ob + 1:cb]
        body = SUPER_HOVER.sub(lambda mm: "super.appendHoverText(%s, %s, tooltipDisplay, tooltipAdder, %s);" % (mm.group(1), mm.group(2), mm.group(4)), body)
        body = "\n\t\tList<Component> %s = new com.rekindled.embers.util.TooltipList(tooltipAdder);" % tip + body
        s = s[:ob + 1] + body + s[cb:]

    # releaseUsing returns boolean
    m = REL.search(s)
    if m:
        s = s[:m.start()] + m.group(0).replace("public void", "public boolean") + s[m.end():]
        ob, cb = body_span(s, m.start())
        body = s[ob + 1:cb].replace("return;", "return false;")
        body = body.rstrip() + "\n\t\treturn true;\n\t"
        s = s[:ob + 1] + body + s[cb:]

    # entity save data: ValueInput / ValueOutput through NbtBridge
    m = READ.search(s)
    if m:
        n = m.group(1)
        s = s[:m.start()] + "protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput valueInput)" + s[m.end():]
        ob, cb = body_span(s, m.start())
        s = s[:ob + 1] + "\n\t\tCompoundTag %s = com.rekindled.embers.util.NbtBridge.read(valueInput);" % n + s[ob + 1:]
    m = ADD.search(s)
    if m:
        n = m.group(1)
        s = s[:m.start()] + "protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput valueOutput)" + s[m.end():]
        ob, cb = body_span(s, m.start())
        body = "\n\t\tCompoundTag %s = new CompoundTag();" % n + s[ob + 1:cb].rstrip() + \
               "\n\t\tcom.rekindled.embers.util.NbtBridge.write(valueOutput, %s);\n\t" % n
        s = s[:ob + 1] + body + s[cb:]

    # block overrides with new parameters
    s = re.sub(r"public int getAnalogOutputSignal\(BlockState (\w+), Level (\w+), BlockPos (\w+)\)",
               r"protected int getAnalogOutputSignal(BlockState \1, Level \2, BlockPos \3, net.minecraft.core.Direction direction)", s)
    s = re.sub(r"public ItemStack getCloneItemStack\((net\.minecraft\.world\.level\.)?LevelReader (\w+), BlockPos (\w+), BlockState (\w+)\)",
               r"protected ItemStack getCloneItemStack(net.minecraft.world.level.LevelReader \2, BlockPos \3, BlockState \4, boolean includeData)", s)
    # block entity client sync
    s = s.replace(
        "public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, net.minecraft.core.HolderLookup.Provider lookupProvider) {\n\t\tsuper.onDataPacket(net, pkt, lookupProvider);",
        "public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.world.level.storage.ValueInput valueInput) {\n\t\tsuper.onDataPacket(net, valueInput);")
    s = s.replace(
        "public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, net.minecraft.core.HolderLookup.Provider lookupProvider) {\r\n\t\tsuper.onDataPacket(net, pkt, lookupProvider);",
        "public void onDataPacket(net.minecraft.network.Connection net, net.minecraft.world.level.storage.ValueInput valueInput) {\r\n\t\tsuper.onDataPacket(net, valueInput);")
    # fuel
    s = s.replace("public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {",
                  "public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, net.minecraft.world.level.block.entity.FuelValues fuelValues) {")

    if s != raw:
        f.write_bytes(s.encode("utf-8"))
        changed[f.name] = 1

print(sorted(changed))
