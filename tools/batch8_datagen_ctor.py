"""26.1 datagen wiring: GatherDataEvent.Client, no ExistingFileHelper, models/blockstates become static JSON."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"


def edit(path, pairs):
    t = path.read_bytes().decode("utf-8")
    for a, b in pairs:
        if not re.search(a, t, flags=re.S):
            print("  MISS", path.name, a[:70])
        t = re.sub(a, b, t, flags=re.S)
    path.write_bytes(t.encode("utf-8"))


for name in ("EmbersBlockStates.java", "EmbersItemModels.java"):
    (j / "datagen" / name).unlink()

new_gather = '''public void gatherData(GatherDataEvent.Client event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		// blockstates, block/item models and client item definitions are static resources since 26.1 (tools/convert_assets_2612.py)
		gen.addProvider(true, new EmbersLang(output));
		gen.addProvider(true, new EmbersSounds(output));
		gen.addProvider(true, new EmbersLootTables(output, lookupProvider));
		gen.addProvider(true, new EmbersRecipes.Runner(output, lookupProvider));
		EmbersBlockTags blockTags = gen.addProvider(true, new EmbersBlockTags(output, lookupProvider));
		gen.addProvider(true, new EmbersItemTags(output, lookupProvider, blockTags.contentsGetter()));
		gen.addProvider(true, new EmbersFluidTags(output, lookupProvider));
		event.createDatapackRegistryObjects(new RegistrySetBuilder()
				.add(Registries.CONFIGURED_FEATURE, EmbersConfiguredFeatures::generate)
				.add(Registries.PLACED_FEATURE, EmbersPlacedFeatures::generate)
				.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, EmbersBiomeModifiers::generate)
				.add(Registries.DAMAGE_TYPE, EmbersDamageTypes::generate)
				.add(Registries.PROCESSOR_LIST, EmbersStructures::generateProcessors)
				.add(Registries.TEMPLATE_POOL, EmbersStructures::generatePools)
				.add(Registries.STRUCTURE, EmbersStructures::generateStructures)
				.add(Registries.STRUCTURE_SET, EmbersStructures::generateSets),
				Set.of(MODID));
		gen.addProvider(true, new EmbersDamageTypeTags(output, lookupProvider));
		gen.addProvider(true, new EmbersLootModifiers(output, lookupProvider));
	}

	public void registerRecipeSerializers('''

edit(j / "Embers.java", [
    (r"import com\.rekindled\.embers\.datagen\.EmbersBlockStates;\r?\n", ""),
    (r"import com\.rekindled\.embers\.datagen\.EmbersItemModels;\r?\n", ""),
    (r"public void gatherData\(GatherDataEvent event\) \{.*?\n\t\}\r?\n\r?\n\tpublic void registerRecipeSerializers\(", new_gather),
])

dg = j / "datagen"
edit(dg / "EmbersBlockTags.java", [
    (r"public EmbersBlockTags\(PackOutput output, CompletableFuture<HolderLookup\.Provider> lookupProvider, ExistingFileHelper existingFileHelper\) \{\s*super\([^;]*\);",
     "public EmbersBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {\n\t\tsuper(output, lookupProvider, Embers.MODID);"),
])
edit(dg / "EmbersFluidTags.java", [
    (r"public EmbersFluidTags\(PackOutput output, CompletableFuture<HolderLookup\.Provider> lookupProvider, ExistingFileHelper existingFileHelper\) \{\s*super\([^;]*\);",
     "public EmbersFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {\n\t\tsuper(output, lookupProvider, Embers.MODID);"),
])
edit(dg / "EmbersDamageTypeTags.java", [
    (r"public EmbersDamageTypeTags\(PackOutput output, CompletableFuture<HolderLookup\.Provider> lookupProvider, ExistingFileHelper existingFileHelper\) \{\s*super\([^;]*\);",
     "public EmbersDamageTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {\n\t\tsuper(output, Registries.DAMAGE_TYPE, lookupProvider, Embers.MODID);"),
])
edit(dg / "EmbersSounds.java", [
    (r"public EmbersSounds\(PackOutput generator, ExistingFileHelper helper\) \{\s*super\([^;]*\);",
     "public EmbersSounds(PackOutput generator) {\n\t\tsuper(generator, Embers.MODID);"),
])
print("done")
