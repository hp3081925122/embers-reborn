import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent / "src" / "main" / "java"
recv = r"((?:\w+(?:\(\))?\.)*\w+(?:\(\))?)"
rules = [
    (re.compile(recv + r"\.getCapability\(\s*Capabilities\.ItemHandler\.BLOCK\s*,\s*"), r"LegacyCaps.item(\1, "),
    (re.compile(recv + r"\.getCapability\(\s*Capabilities\.FluidHandler\.BLOCK\s*,\s*"), r"LegacyCaps.fluid(\1, "),
    (re.compile(r"Capabilities\.ItemHandler\.BLOCK\.getCapability\("), "LegacyCaps.item("),
    (re.compile(r"Capabilities\.ItemHandler\.BLOCK\b"), "LegacyCaps.ITEM_BLOCK"),
    (re.compile(r"Capabilities\.FluidHandler\.BLOCK\b"), "LegacyCaps.FLUID_BLOCK"),
]
IMPORT = "import com.rekindled.embers.util.LegacyCaps;"
total = 0
for f in root.rglob("*.java"):
    if f.name in ("LegacyCaps.java", "BlockEntityCapabilities.java"):
        continue
    text = f.read_bytes().decode("utf-8")
    new = text
    for pat, rep in rules:
        new, n = pat.subn(rep, new)
        total += n
    if new != text:
        if IMPORT not in new and "package com.rekindled.embers.util;" not in new:
            new = re.sub(r"(package [\w.]+;\r?\n)", lambda m: m.group(1) + "\n" + IMPORT + "\n", new, count=1)
        f.write_bytes(new.encode("utf-8"))
        print("  ", f.relative_to(root))
print("replacements:", total)

# BlockEntityCapabilities: register through LegacyHandlers wrappers
bec = root / "com/rekindled/embers/blockentity/BlockEntityCapabilities.java"
t = bec.read_bytes().decode("utf-8")
t = t.replace("event.registerBlockEntity(ITEM,", "items(event,").replace("event.registerBlockEntity(FLUID,", "fluids(event,")
t = t.replace(
    "\tprivate static final BlockCapability<IItemHandler, Direction> ITEM = Capabilities.ItemHandler.BLOCK;\n"
    "\tprivate static final BlockCapability<IFluidHandler, Direction> FLUID = Capabilities.FluidHandler.BLOCK;\n",
    "\tprivate static <T extends net.minecraft.world.level.block.entity.BlockEntity> void items(RegisterCapabilitiesEvent event, BlockEntityType<T> type, java.util.function.BiFunction<T, Direction, IItemHandler> provider) {\n"
    "\t\tevent.registerBlockEntity(Capabilities.Item.BLOCK, type, (be, side) -> com.rekindled.embers.util.LegacyHandlers.items(provider.apply(be, side)));\n"
    "\t}\n\n"
    "\tprivate static <T extends net.minecraft.world.level.block.entity.BlockEntity> void fluids(RegisterCapabilitiesEvent event, BlockEntityType<T> type, java.util.function.BiFunction<T, Direction, IFluidHandler> provider) {\n"
    "\t\tevent.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (be, side) -> com.rekindled.embers.util.LegacyHandlers.fluids(provider.apply(be, side)));\n"
    "\t}\n",
)
bec.write_bytes(t.encode("utf-8"))
print("BEC remaining ITEM/FLUID refs:", len(re.findall(r"\b(ITEM|FLUID)\b", t)))
