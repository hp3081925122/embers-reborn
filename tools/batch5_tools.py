"""26.1 tools: Tier/SwordItem/PickaxeItem/DiggerItem -> ToolMaterial + Item.Properties.sword/pickaxe/tool."""
import pathlib, re

j = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"


def edit(rel, pairs):
    f = j / rel
    t = f.read_bytes().decode("utf-8")
    for a, b in pairs:
        n = len(re.findall(a, t))
        if n == 0:
            print("  MISS", rel, a[:60])
        t = re.sub(a, b, t)
    f.write_bytes(t.encode("utf-8"))


edit("RegistryManager.java", [
    (r"public ToolSet\(String name, Tier tier\)", "public ToolSet(String name, net.minecraft.world.item.ToolMaterial tier)"),
    (r"new SwordItem\(tier, new Item\.Properties\(\)\.attributes\(SwordItem\.createAttributes\(tier, 3, -2\.4F\)\)\)",
     "new Item(new Item.Properties().sword(tier, 3, -2.4F))"),
    (r"new ShovelItem\(tier, new Item\.Properties\(\)\.attributes\(ShovelItem\.createAttributes\(tier, 1\.5F, -3\.0F\)\)\)",
     "new ShovelItem(tier, 1.5F, -3.0F, new Item.Properties())"),
    (r"new PickaxeItem\(tier, new Item\.Properties\(\)\.attributes\(PickaxeItem\.createAttributes\(tier, 1\.0F, -2\.8F\)\)\)",
     "new Item(new Item.Properties().pickaxe(tier, 1.0F, -2.8F))"),
    (r"new AxeItem\(tier, new Item\.Properties\(\)\.attributes\(AxeItem\.createAttributes\(tier, 6\.0F, -3\.0F\)\)\)",
     "new AxeItem(tier, 6.0F, -3.0F, new Item.Properties())"),
    (r"new HoeItem\(tier, new Item\.Properties\(\)\.attributes\(HoeItem\.createAttributes\(tier, -tier\.getAttackDamageBonus\(\), Math\.min\(0\.0F, tier\.getAttackDamageBonus\(\) - 3\.0F\)\)\)\)",
     "new HoeItem(tier, -tier.attackDamageBonus(), Math.min(0.0F, tier.attackDamageBonus() - 3.0F), new Item.Properties())"),
    (r"new TyrfingItem\(EmbersTiers\.TYRFING, new Item\.Properties\(\)\.attributes\(SwordItem\.createAttributes\(EmbersTiers\.TYRFING, 3, -2\.4F\)\)\)",
     "new TyrfingItem(new Item.Properties().sword(EmbersTiers.TYRFING, 3, -2.4F))"),
])
edit("item/TyrfingItem.java", [
    (r"extends SwordItem", "extends Item"),
    (r"public TyrfingItem\(Tier tier, Properties prop\) \{\s*super\(tier, prop\);", "public TyrfingItem(Properties prop) {\n\t\tsuper(prop);"),
])
edit("item/ClockworkToolItem.java", [
    (r"extends DiggerItem", "extends Item"),
    (r"public ClockworkToolItem\(Tier tier, TagKey<Block> blocks, Properties properties\) \{",
     "public ClockworkToolItem(net.minecraft.world.item.ToolMaterial tier, TagKey<Block> blocks, float attackDamage, float attackSpeed, Properties properties) {"),
    (r"super\(tier, blocks, properties\.component\(", "super(properties.tool(tier, blocks, attackDamage, attackSpeed, 0.0F).component("),
])
for rel, mat, tag, dmg, spd in [
    ("item/ClockworkAxeItem.java", "CLOCKWORK_AXE", "BlockTags.MINEABLE_WITH_AXE", "2.0F", "-3.0F"),
    ("item/ClockworkHammerItem.java", "CLOCKWORK_HAMMER", "EmbersBlockTags.MINABLE_WITH_HAMMER", "3.0F", "-3.2F"),
    ("item/ClockworkPickaxeItem.java", "CLOCKWORK_PICK", "EmbersBlockTags.MINABLE_WITH_PICKAXE_SHOVEL", "1.0F", "-2.8F"),
]:
    edit(rel, [(r"super\(EmbersTiers\." + mat + r", " + re.escape(tag) + r", properties\.attributes\(DiggerItem\.createAttributes\(EmbersTiers\." + mat + r", " + re.escape(dmg) + r", " + re.escape(spd) + r"\)\)\);",
                f"super(EmbersTiers.{mat}, {tag}, {dmg}, {spd}, properties);")])
print("done")
