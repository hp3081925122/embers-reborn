import re, pathlib

base = pathlib.Path(__file__).resolve().parent.parent
root = base / "src/main/java/com/rekindled/embers"
misses = []


def edit(rel, *pairs, regex=False):
    f = root / rel
    s = f.read_bytes().decode("utf-8")
    for old, new in pairs:
        if regex:
            s, n = re.subn(old, new, s)
        else:
            n = s.count(old)
            s = s.replace(old, new)
        if n == 0:
            misses.append((rel, old[:70]))
    f.write_bytes(s.encode("utf-8"))


SD = "MobSpawnSettings.SpawnerData"
# explicit element type so javac picks the Weighted varargs overload, not of(E value)
edit("datagen/EmbersBiomeModifiers.java",
     ("net.minecraft.util.random.WeightedList.of(new net.minecraft.util.random.Weighted<>(",
      "net.minecraft.util.random.WeightedList.<" + SD + ">of(new net.minecraft.util.random.Weighted<" + SD + ">("))
edit("datagen/EmbersStructures.java",
     ("WeightedList.of(new Weighted<>(new MobSpawnSettings.SpawnerData(", "WeightedList.<" + SD + ">of(new Weighted<" + SD + ">(new MobSpawnSettings.SpawnerData("))

edit("compat/curios/CuriosCompat.java",
     ("BlockEntityType.Builder.of(ExplosionPedestalBlockEntity::new, EXPLOSION_PEDESTAL.get()).build(null)",
      "new BlockEntityType<>(ExplosionPedestalBlockEntity::new, EXPLOSION_PEDESTAL.get())"))

# display-only anvil recipes are never serialized; any anvil serializer satisfies the override
edit("recipe/AnvilDisplayRecipe.java",
     ("public RecipeSerializer<? extends AnvilDisplayRecipe> getSerializer() {", "public RecipeSerializer<? extends IDawnstoneAnvilRecipe> getSerializer() {"))

edit("Embers.java",
     ("new EmbersItemTags(output, lookupProvider, blockTags.contentsGetter())", "new EmbersItemTags(output, lookupProvider)"),
     (r"event\.registerItem\(net\.neoforged\.neoforge\.capabilities\.Capabilities\.FluidHandler\.ITEM,\r?\n\t\t\t\(stack, ctx\) -> new net\.neoforged\.neoforge\.fluids\.capability\.templates\.FluidHandlerItemStack\(RegistryManager\.FLUID_CONTENT, stack, ConfigManager\.FLUID_VESSEL_CAPACITY\.get\(\)\),",
      "event.registerItem(net.neoforged.neoforge.capabilities.Capabilities.Fluid.ITEM,\n\t\t\t(stack, access) -> new net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler(access, RegistryManager.FLUID_CONTENT.get(), ConfigManager.FLUID_VESSEL_CAPACITY.get()),"),
     regex=True)

# glow text wrapper has no callers left (tooltip glow uses GLOW_TEXT_PIPELINE directly)
(root / "render/SneakyBufferSourceWrapper.java").unlink()
print("misses", misses)
