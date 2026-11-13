"""26.1 removed ItemTagsProvider#copy(blockTag, itemTag). Move the 21 copied tags into a BlockItemTagsProvider
that both the block and the item tag provider run, like vanilla's VanillaBlockTagsProvider/VanillaItemTagsProvider."""
import pathlib, re

d = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/datagen"
bt = d / "EmbersBlockTags.java"
it = d / "EmbersItemTags.java"
t_it = it.read_bytes().decode("utf-8")
names = re.findall(r"copy\(EmbersBlockTags\.(\w+), \w+\);", t_it)
assert len(names) == 21, names

t_bt = bt.read_bytes().decode("utf-8")
nl = "\r\n" if "\r\n" in t_bt else "\n"
lines = t_bt.split(nl)
pat = re.compile(r"^\s*tag\((" + "|".join(names) + r")\)\.")
moved = [l.strip() for l in lines if pat.match(l)]
assert len(moved) == 21, moved
lines = [l for l in lines if not pat.match(l)]
t_bt = nl.join(lines)
# run the shared tags from the block provider
t_bt, n = re.subn(r"(protected void addTags\(\s*HolderLookup\.Provider \w+\)\s*\{)",
                  r"\1" + nl + "\t\tnew EmbersBlockItemTags() {" + nl +
                  "\t\t\t@Override" + nl +
                  "\t\t\tprotected net.minecraft.data.tags.TagAppender<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.Block> tag(TagKey<net.minecraft.world.level.block.Block> blockTag, TagKey<net.minecraft.world.item.Item> itemTag) {" + nl +
                  "\t\t\t\treturn EmbersBlockTags.this.tag(blockTag);" + nl +
                  "\t\t\t}" + nl + "\t\t}.run();" + nl, t_bt, count=1)
assert n == 1, "addTags header not found in EmbersBlockTags"
bt.write_bytes(t_bt.encode("utf-8"))

imports = "\n".join(l for l in t_bt.split(nl) if l.startswith("import "))
body = "\n\t\t".join(re.sub(r"^tag\((\w+)\)", r"tag(EmbersBlockTags.\1, EmbersItemTags.\1)", m) for m in moved)
(d / "EmbersBlockItemTags.java").write_text(
    "package com.rekindled.embers.datagen;\n\n" + imports + "\n\n"
    "/** Tags that exist as both a block tag and an item tag with the same contents (was ItemTagsProvider#copy). */\n"
    "public abstract class EmbersBlockItemTags extends net.minecraft.data.tags.BlockItemTagsProvider {\n\n"
    "\t@Override\n\tpublic void run() {\n\t\t" + body + "\n\t}\n}\n", encoding="utf-8")

# item provider: drop copy() calls, run the shared tags through a block->item converter
t_it = re.sub(r"[ \t]*copy\(EmbersBlockTags\.\w+, \w+\);\r?\n", "", t_it)
t_it, n = re.subn(r"(protected void addTags\(\s*HolderLookup\.Provider \w+\)\s*\{)",
                  r"\1\n\t\tnew EmbersBlockItemTags() {\n\t\t\t@Override\n"
                  "\t\t\tprotected net.minecraft.data.tags.TagAppender<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.Block> tag(TagKey<net.minecraft.world.level.block.Block> blockTag, TagKey<Item> itemTag) {\n"
                  "\t\t\t\treturn new BlockToItemTagAppender(EmbersItemTags.this.tag(itemTag));\n\t\t\t}\n\t\t}.run();\n", t_it, count=1)
assert n == 1, "addTags header not found in EmbersItemTags"
t_it = re.sub(r"public EmbersItemTags\(PackOutput output, CompletableFuture<HolderLookup\.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper\) \{\s*super\([^;]*\);",
              "public EmbersItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {\n\t\tsuper(output, lookupProvider, Embers.MODID);", t_it)
it.write_bytes(t_it.encode("utf-8"))
print("moved", len(moved), "tags")
