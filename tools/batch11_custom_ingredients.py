"""Batch 11d: HeatIngredient / AugmentIngredient getItems() -> 26.1 items() + display()."""
import pathlib, re

base = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/recipe"

HEAT = '''	@Override
	public java.util.stream.Stream<net.minecraft.core.Holder<net.minecraft.world.item.Item>> items() {
		return base.items();
	}

	/** JEI/recipe-book display: show the base items with heat applied (unless inverted), like 1.20.1 getItems(). */
	@Override
	public net.minecraft.world.item.crafting.display.SlotDisplay display() {
		if (inverted)
			return base.display();
		java.util.List<net.minecraft.world.item.crafting.display.SlotDisplay> list = new java.util.ArrayList<>();
		base.items().forEach(h -> {
			ItemStack stack = new ItemStack(h);
			AugmentUtil.setHeat(stack, 0);
			list.add(new net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(stack)));
		});
		return new net.minecraft.world.item.crafting.display.SlotDisplay.Composite(list);
	}

	/** Matching depends on heat data components, so vanilla must call test(); never simple. */
	@Override
	public boolean isSimple() {
		return false;
	}
'''

AUG = '''	@Override
	public java.util.stream.Stream<net.minecraft.core.Holder<net.minecraft.world.item.Item>> items() {
		return base.items();
	}

	/** JEI/recipe-book display: base items with the augment applied (or heat cleared if inverted), like 1.20.1 getItems(). */
	@Override
	public net.minecraft.world.item.crafting.display.SlotDisplay display() {
		java.util.List<net.minecraft.world.item.crafting.display.SlotDisplay> list = new java.util.ArrayList<>();
		base.items().forEach(h -> {
			ItemStack stack = new ItemStack(h);
			if (inverted)
				AugmentUtil.setHeat(stack, 0);
			else {
				AugmentUtil.setLevel(stack, AugmentUtil.getLevel(stack) + level);
				AugmentUtil.addAugment(stack, ItemStack.EMPTY, augment);
				AugmentUtil.setAugmentLevel(stack, augment, level);
			}
			list.add(new net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay(net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(stack)));
		});
		return new net.minecraft.world.item.crafting.display.SlotDisplay.Composite(list);
	}

	/** Matching depends on augment data components, so vanilla must call test(); never simple. */
	@Override
	public boolean isSimple() {
		return false;
	}
'''

for name, block, field in (("HeatIngredient.java", HEAT, "heatedMatchingStacks"), ("AugmentIngredient.java", AUG, "augmentedMatchingStacks")):
    p = base / name
    s = p.read_bytes().decode("utf-8")
    nl = "\r\n" if "\r\n" in s else "\n"
    a = s.rindex("\t@Override", 0, s.index("getItems() {"))  # the getItems override
    assert "getItems()" in s[a:a + 120], name
    b = s.index("return base.isSimple();", a)
    b = s.index("}", b) + 1
    s = s[:a] + block.replace("\n", nl).rstrip() + s[b:]
    s = re.sub(r"\tprivate ItemStack\[\] " + field + r";\r?\n", "", s)
    s = re.sub(r"\tprivate IntList packedMatchingStacks;\r?\n", "", s)
    p.write_bytes(s.encode("utf-8"))
    print(name, "ok")
