"""26.1 tool items: no DiggerItem/SwordItem/Tier, mining is driven only by the Tool component.

- clockwork tools extend Item with properties.tool(...); UNBREAKABLE is a Unit component now and its
  tooltip line is hidden through TOOLTIP_DISPLAY (1.20.1 tier durability -1 showed no "Unbreakable")
- PICKAXE_DIG/SHOVEL_DIG/AXE_DIG abilities were removed; digging already depends on the Tool
  component and getDestroySpeed() (0 without ember), so the dig ability overrides are dropped
- hurtEnemy returns void; isEnchantable is replaced by the material's enchantable(18) component
"""
import pathlib, re

ITEM = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/item"

def edit(name, fn):
    p = ITEM / name
    src = p.read_text(encoding="utf-8")
    new = fn(src)
    p.write_text(new, encoding="utf-8", newline="")
    print(("changed " if new != src else "UNCHANGED ") + name)

def drop_imports(src, *names):
    for n in names:
        src = re.sub(r"import net\.minecraft\.world\.item\.(component\.)?" + n + r";\r?\n", "", src)
    return src

def tool(src):
    src = drop_imports(src, "DiggerItem", "Tier", "Unbreakable")
    src = src.replace("import net.minecraft.world.item.ItemStack;",
                      "import net.minecraft.util.Unit;\nimport net.minecraft.world.item.Item;\nimport net.minecraft.world.item.ItemStack;\nimport net.minecraft.world.item.component.TooltipDisplay;")
    src = src.replace(".component(DataComponents.UNBREAKABLE, new Unbreakable(false)));",
                      ".component(DataComponents.UNBREAKABLE, Unit.INSTANCE)\n\t\t\t\t.component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));")
    src = src.replace("stack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));", "stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);")
    src = src.replace("public boolean hurtEnemy(", "public void hurtEnemy(").replace("return super.hurtEnemy(stack, target, attacker);", "super.hurtEnemy(stack, target, attacker);")
    src = re.sub(r"\t@Override\r?\n\tpublic boolean isEnchantable\(ItemStack pStack\) \{\r?\n\t\treturn true;\r?\n\t\}\r?\n\r?\n", "", src)
    src = src.replace("ItemTags.SWORD_ENCHANTABLE", "ItemTags.MELEE_WEAPON_ENCHANTABLE")
    return src

DIG = re.compile(r"\t@Override\r?\n\tpublic boolean canPerformAction\([^)]*\) \{\r?\n\t\treturn hasEmber\(stack\) && \(ability == [^;]*;\r?\n\t\}\r?\n\r?\n?")

edit("ClockworkToolItem.java", tool)
edit("ClockworkPickaxeItem.java", lambda s: DIG.sub("", drop_imports(s, "DiggerItem")))
edit("ClockworkHammerItem.java", lambda s: DIG.sub("", drop_imports(s, "DiggerItem")))
edit("ClockworkAxeItem.java", lambda s: drop_imports(s, "DiggerItem")
     .replace("canPerformAction(ItemStack stack, net.neoforged.neoforge.common.ItemAbility ability)", "canPerformAction(net.minecraft.world.item.ItemInstance stack, net.neoforged.neoforge.common.ItemAbility ability)")
     .replace("return hasEmber(stack) && net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS", "return stack instanceof ItemStack itemStack && hasEmber(itemStack) && net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS"))
edit("TyrfingItem.java", lambda s: drop_imports(s, "SwordItem", "Tier").replace("import net.minecraft.world.item.ItemStack;", "import net.minecraft.world.item.Item;\nimport net.minecraft.world.item.ItemStack;"))

# type-use annotation must sit next to the simple name
for p in ITEM.glob("*.java"):
    s = p.read_text(encoding="utf-8")
    n = s.replace("@org.jspecify.annotations.Nullable net.minecraft.world.entity.EquipmentSlot", "net.minecraft.world.entity.@org.jspecify.annotations.Nullable EquipmentSlot")
    if n != s:
        p.write_text(n, encoding="utf-8", newline="")
        print("annotation", p.name)
