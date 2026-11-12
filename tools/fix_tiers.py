"""1.20.1 -> 1.21.1 port bug: SimpleTier's first argument is the 'incorrect for drops' tag, but the port passed the
mod's (empty) needs_*_tool tags, so lead/silver/dawnstone tools could harvest every block. Restore the 1.20.1
harvest levels: lead/tyrfing/silver/dawnstone = iron (2), clockwork pick/axe = diamond (3), clockwork hammer = stone (1)."""
import pathlib, sys

repo = pathlib.Path(sys.argv[1])
f = repo / "src/main/java/com/rekindled/embers/util/EmbersTiers.java"
t = f.read_bytes().decode("utf-8")
mapping = {
    "EmbersBlockTags.NEEDS_LEAD_TOOL,": "BlockTags.INCORRECT_FOR_IRON_TOOL,",
    "EmbersBlockTags.NEEDS_TYRFING,": "BlockTags.INCORRECT_FOR_IRON_TOOL,",
    "EmbersBlockTags.NEEDS_SILVER_TOOL,": "BlockTags.INCORRECT_FOR_IRON_TOOL,",
    "EmbersBlockTags.NEEDS_DAWNSTONE_TOOL,": "BlockTags.INCORRECT_FOR_IRON_TOOL,",
    "EmbersBlockTags.NEEDS_CLOCKWORK_TOOL,": "BlockTags.INCORRECT_FOR_DIAMOND_TOOL,",
    "EmbersBlockTags.NEEDS_CLOCKWORK_HAMMER,": "BlockTags.INCORRECT_FOR_STONE_TOOL,",
}
for a, b in mapping.items():
    t = t.replace(a, b)
if "import net.minecraft.tags.BlockTags;" not in t:
    t = t.replace("import net.minecraft.world.item.Tier;", "import net.minecraft.tags.BlockTags;\nimport net.minecraft.world.item.Tier;")
f.write_bytes(t.encode("utf-8"))
print(t.count("BlockTags.INCORRECT_FOR_"), "tiers fixed in", f)
