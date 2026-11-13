import re, json, pathlib

base = pathlib.Path(__file__).resolve().parent.parent
root = base / "src/main/java/com/rekindled/embers"
assets = base / "src/main/resources/assets/embers_reborn"
NL = r"\r?\n"


def sub(s, old, new, count=None, regex=False):
    if regex:
        s2, n = re.subn(old, new, s)
    else:
        n = s.count(old)
        s2 = s.replace(old, new)
    if n == 0 or (count is not None and n != count):
        raise SystemExit("expected %s got %d: %s" % (count, n, old[:80]))
    return s2


def edit(rel, fn):
    f = root / rel
    s = f.read_bytes().decode("utf-8")
    f.write_bytes(fn(s).encode("utf-8"))


# ---- Ashen armor model: cape swing from the 26.1 avatar render state (1.20.1 read the player's cloak fields)
def armor_model(s):
    s = sub(s, r"\tpublic void setup\(LivingEntity entity, ItemStack itemStack\) \{" + NL + r"(?:.*" + NL + r")*?\t\}" + NL + NL + r"(?=\tpublic static void init)",
            "\t@Override\n"
            "\tpublic void setupAnim(HumanoidRenderState state) {\n"
            "\t\tsuper.setupAnim(state);\n"
            "\t\tif (cape != null) {\n"
            "\t\t\t// same formula as 1.20.1: capeLean and capeFlap are the vanilla cape values the old code computed by hand\n"
            "\t\t\tif (state instanceof net.minecraft.client.renderer.entity.state.AvatarRenderState avatar)\n"
            "\t\t\t\tthis.cape.xRot = (6.0F + avatar.capeLean / 2.0F + avatar.capeFlap) * ((float)Math.PI / 180F);\n"
            "\t\t\telse\n"
            "\t\t\t\tthis.cape.xRot = 0;\n"
            "\t\t}\n"
            "\t}\n\n", 1, regex=True)
    return s
edit("model/AshenArmorModel.java", armor_model)


# ---- Ashen armor item: client extension registered through RegisterClientExtensionsEvent
def armor_item(s):
    s = sub(s, r"import net\.minecraft\.world\.item\.ArmorItem;" + NL, "", 1, regex=True)
    s = sub(s, r"import net\.minecraft\.world\.item\.ArmorMaterial;" + NL, "", 1, regex=True)
    s = sub(s, r"\r?\n\t@Override" + NL + r"\tpublic void initializeClient\(Consumer<IClientItemExtensions> consumer\) \{" + NL
            + r".*" + NL + r"\t\}", "", 1, regex=True)
    return s
edit("item/AshenArmorItem.java", armor_item)


# ---- Alchemical note item: renderer is an item model now, tooltip name from getItemName
def note_item(s):
    s = sub(s, r"\r?\n\t@Override" + NL + r"\t@OnlyIn\(Dist\.CLIENT\)" + NL + r"\tpublic void initializeClient\(Consumer<IClientItemExtensions> consumer\) \{" + NL
            + r".*" + NL + r"\t\}" + NL, "\n", 1, regex=True)
    s = sub(s, r"\r?\n\t@OnlyIn\(Dist\.CLIENT\)" + NL + r"\tpublic static class AlchemicalNoteItemExtensions implements IClientItemExtensions \{" + NL
            + r"(?:.*" + NL + r")*?\t\}" + NL, "\n", 1, regex=True)
    s = sub(s, "tooltip.add(Component.translatable(result.getDescriptionId()).withStyle(ChatFormatting.GRAY));",
            "tooltip.add(result.getItemName().copy().withStyle(ChatFormatting.GRAY));", 1)
    s = re.sub(r"import net\.minecraft\.client\.renderer\.BlockEntityWithoutLevelRenderer;" + NL, "", s)
    s = re.sub(r"import com\.rekindled\.embers\.render\.AlchemicalNoteItemRenderer;" + NL, "", s)
    return s
edit("item/AlchemicalNoteItem.java", note_item)


# ---- registration
def embers(s):
    s = sub(s, "\t\t\tcom.rekindled.embers.fluidtypes.EmbersFluidClient.registerClientExtensions(event);\n",
            "\t\t\tcom.rekindled.embers.fluidtypes.EmbersFluidClient.registerClientExtensions(event);\n"
            "\t\t\tevent.registerItem(com.rekindled.embers.model.AshenArmorModel.ARMOR_MODEL_GETTER, RegistryManager.ASHEN_GOGGLES.get(), RegistryManager.ASHEN_CLOAK.get(), RegistryManager.ASHEN_LEGGINGS.get(), RegistryManager.ASHEN_BOOTS.get());\n"
            "\t\t}\n\n"
            "\t\t@OnlyIn(Dist.CLIENT)\n"
            "\t\t@SubscribeEvent\n"
            "\t\tstatic void registerSpecialModels(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {\n"
            "\t\t\tevent.register(Identifier.fromNamespaceAndPath(MODID, \"alchemical_note\"), com.rekindled.embers.render.AlchemicalNoteItemRenderer.Unbaked.MAP_CODEC);\n", 1) \
        if "registerClientExtensions(event);\n" in s else \
        sub(s, "\t\t\tcom.rekindled.embers.fluidtypes.EmbersFluidClient.registerClientExtensions(event);\r\n",
            "\t\t\tcom.rekindled.embers.fluidtypes.EmbersFluidClient.registerClientExtensions(event);\r\n"
            "\t\t\tevent.registerItem(com.rekindled.embers.model.AshenArmorModel.ARMOR_MODEL_GETTER, RegistryManager.ASHEN_GOGGLES.get(), RegistryManager.ASHEN_CLOAK.get(), RegistryManager.ASHEN_LEGGINGS.get(), RegistryManager.ASHEN_BOOTS.get());\r\n"
            "\t\t}\r\n\r\n"
            "\t\t@OnlyIn(Dist.CLIENT)\r\n"
            "\t\t@SubscribeEvent\r\n"
            "\t\tstatic void registerSpecialModels(net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent event) {\r\n"
            "\t\t\tevent.register(Identifier.fromNamespaceAndPath(MODID, \"alchemical_note\"), com.rekindled.embers.render.AlchemicalNoteItemRenderer.Unbaked.MAP_CODEC);\r\n", 1)
    return s
edit("Embers.java", embers)


# ---- access transformer: 26.1 signatures of the map-hand helpers
at = base / "src/main/resources/META-INF/accesstransformer.cfg"
a = at.read_bytes().decode("utf-8")
a = sub(a, "renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFFLnet/minecraft/world/entity/HumanoidArm;)V",
        "renderPlayerArm(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;IFFLnet/minecraft/world/entity/HumanoidArm;)V", 1)
a = sub(a, "renderMapHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/HumanoidArm;)V",
        "renderMapHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/world/entity/HumanoidArm;)V", 1)
at.write_bytes(a.encode("utf-8"))


# ---- item definition: 1.21.1 separate_transforms -> select on display context
def special(mode):
    return {"type": "minecraft:special", "base": "embers_reborn:item/alchemical_note_special",
            "model": {"type": "embers_reborn:alchemical_note", "mode": mode}}

flat = {"type": "minecraft:model", "model": "embers_reborn:item/alchemical_note_item"}
item_def = {"model": {
    "type": "minecraft:select",
    "property": "minecraft:display_context",
    "cases": [
        {"when": "firstperson_righthand", "model": special("first_person_right")},
        {"when": "firstperson_lefthand", "model": special("first_person_left")},
        {"when": "fixed", "model": special("fixed")},
        {"when": "gui", "model": {"type": "minecraft:composite", "models": [flat, special("gui")]}},
    ],
    "fallback": flat,
}}
(assets / "items/alchemical_note.json").write_text(json.dumps(item_def, indent=2) + "\n", encoding="utf-8")
# identity display transforms like builtin/entity, so the 1.20.1 renderer math still applies
(assets / "models/item/alchemical_note_special.json").write_text(
    json.dumps({"textures": {"particle": "embers_reborn:item/alchemical_note_item"}}, indent=2) + "\n", encoding="utf-8")
(assets / "models/item/alchemical_note.json").unlink()
print("ok")
