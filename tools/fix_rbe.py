import io
import re

p = "src/main/java/com/rekindled/embers/RegistryManager.java"
t = io.open(p, encoding="utf-8", newline="").read()
nl = "\r\n" if "\r\n" in t else "\n"
n0 = t.count("= rBE(")
t, n = re.subn(r'(= rBE\("\w+", \w+::new, \w+)\.get\(\)\);', r"\1);", t)
old = nl.join([
    "\t\t\tBlock... blocks) {",
    "\t\tjava.util.function.Supplier sup = () -> BlockEntityType.Builder.of(factory, blocks).build(null);",
    "\t\tObject holder = BLOCK_ENTITY_TYPES_NEW.register(id, sup);",
    "\t\tBLOCK_ENTITY_TYPES_OLD.register(id, sup);",
])
new = nl.join([
    "\t\t\tjava.util.function.Supplier<? extends Block> block) {",
    "\t\tjava.util.function.Supplier sup = () -> BlockEntityType.Builder.of(factory, block.get()).build(null);",
    "\t\tObject holder = BLOCK_ENTITY_TYPES_NEW.register(id, sup);",
])
assert old in t, "helper not found"
t = t.replace(old, new)
io.open(p, "w", encoding="utf-8", newline="").write(t)
print(n0, n)
