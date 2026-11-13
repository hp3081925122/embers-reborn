"""Embers.java client registration for 26.1."""
import pathlib, re

root = pathlib.Path(__file__).resolve().parent.parent
j = root / "src/main/java/com/rekindled/embers"


def edit(p, pairs):
    t = p.read_bytes().decode("utf-8")
    for a, b in pairs:
        t2, n = re.subn(a, b, t, flags=re.S)
        print(f"{n:3} {p.name}: {a[:70]!r}")
        t = t2
    p.write_bytes(t.encode("utf-8"))


edit(j / "client/EmbersItemClient.java", [
    (r"return Embers\._getOrCreateTag\(stack\)\.contains\(\"type\"\);",
     "return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains(\"type\");"),
])

client_events = '''		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerFluidModels(net.neoforged.neoforge.client.event.RegisterFluidModelsEvent event) {
			com.rekindled.embers.fluidtypes.EmbersFluidClient.registerFluidModels(event);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerClientExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
			com.rekindled.embers.fluidtypes.EmbersFluidClient.registerClientExtensions(event);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerItemTints(RegisterColorHandlersEvent.ItemTintSources event) {
			com.rekindled.embers.client.EmbersItemClient.registerTints(event);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerItemProperties(net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent event) {
			com.rekindled.embers.client.EmbersItemClient.registerProperties(event);
		}
'''

edit(j / "Embers.java", [
    # fluid layers come from the fluid textures now; inflictor gem property is a conditional item model
    (r"\t\t\tItemBlockRenderTypes\.setRenderLayer\([^;]*;\r?\n", ""),
    (r"\t\t\tevent\.enqueueWork\(\(\) -> ItemProperties\.register\(RegistryManager\.INFLICTOR_GEM\.get\(\).*?\}\)\);\r?\n", ""),
    # item colours -> tint sources (see items/*.json)
    (r"\t\t@OnlyIn\(Dist\.CLIENT\)\r?\n\t\t@SubscribeEvent\r?\n\t\tstatic void registerItemColorHandlers\(RegisterColorHandlersEvent\.Item event\) \{.*?\n\t\t\}\r?\n", client_events),
    (r"static void addResourceListener\(RegisterClientReloadListenersEvent event\) \{\s*event\.registerReloadListener\(new DecimalFormats\(\)\);\s*event\.registerReloadListener\(new EmbersColors\(\)\);",
     "static void addResourceListener(net.neoforged.neoforge.client.event.AddClientReloadListenersEvent event) {\n"
     "\t\t\tevent.addListener(Identifier.fromNamespaceAndPath(MODID, \"decimal_formats\"), new DecimalFormats());\n"
     "\t\t\tevent.addListener(Identifier.fromNamespaceAndPath(MODID, \"colors\"), new EmbersColors());"),
    # 26.1 merged the jump meter and experience bar into the contextual info bar
    (r"VanillaGuiLayers\.JUMP_METER", "VanillaGuiLayers.CONTEXTUAL_INFO_BAR_BACKGROUND"),
    (r"VanillaGuiLayers\.EXPERIENCE_BAR", "VanillaGuiLayers.CONTEXTUAL_INFO_BAR"),
    (r"ModelEvent\.RegisterGeometryLoaders event", "ModelEvent.RegisterLoaders event"),
    (r"import net\.minecraft\.client\.renderer\.ItemBlockRenderTypes;\r?\n", ""),
    (r"import net\.minecraft\.client\.renderer\.item\.ItemProperties;\r?\n", ""),
    (r"import net\.minecraft\.client\.color\.item\.ItemColor;\r?\n", ""),
    (r"import net\.neoforged\.neoforge\.client\.event\.RegisterClientReloadListenersEvent;\r?\n", ""),
    (r"import net\.neoforged\.neoforge\.client\.model\.generators\.ItemModelProvider;\r?\n", ""),
    (r"import net\.neoforged\.neoforge\.common\.data\.ExistingFileHelper;\r?\n", ""),
    (r"import net\.minecraft\.advancements\.critereon\.", "import net.minecraft.advancements.criterion."),
])

cc = j / "compat/curios/CuriosCompat.java"
edit(cc, [
    (r"\tpublic static void registerColorHandler\(RegisterColorHandlersEvent\.Item event, ItemColor itemColor\) \{\s*event\.register\(itemColor, EMBER_BULB\.get\(\)\);\s*\}\r?\n\r?\n", ""),
    (r"import net\.neoforged\.neoforge\.client\.event\.RegisterColorHandlersEvent;\r?\n", ""),
    (r"import net\.minecraft\.client\.color\.item\.ItemColor;\r?\n", ""),
])
for name in ("item/EmberStorageItem.java", "item/TyrfingItem.java"):
    p = j / name
    t = p.read_bytes().decode("utf-8")
    m = re.search(r"\tpublic static class ColorHandler implements ItemColor \{", t)
    if m:
        depth, k = 0, t.index("{", m.start())
        while True:
            depth += {"{": 1, "}": -1}.get(t[k], 0)
            if depth == 0:
                break
            k += 1
        t = t[:m.start()].rstrip() + "\n" + t[k + 1:]
        t = re.sub(r"import net\.minecraft\.client\.color\.item\.ItemColor;\r?\n", "", t)
        p.write_bytes(t.encode("utf-8"))
        print("removed ColorHandler from", name)
