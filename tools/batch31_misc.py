import re, pathlib

base = pathlib.Path(__file__).resolve().parent.parent
root = base / "src/main/java/com/rekindled/embers"
NL = r"\r?\n"
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


# light packing
for r in ["blockentity/render/EmberBoreBlockEntityRenderer.java", "blockentity/render/MechanicalPumpBlockEntityRenderer.java"]:
    edit(r, ("LightTexture.pack(", "net.minecraft.util.LightCoordsUtil.pack("))
    edit(r, (r"import net\.minecraft\.client\.renderer\.LightTexture;" + NL, ""), regex=True)

edit("entity/EmberPacketEntity.java", ("this.moveTo(", "this.snapTo("))
# 1.20.1: fire only when the hit landed; 26.1 hurt() is void, hurtServer() reports it
edit("api/projectile/EffectDamage.java",
     ("if (entity.hurt(source.apply(projectileEntity), damage))",
      "if (entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel && entity.hurtServer(serverLevel, source.apply(projectileEntity), damage))"))
edit("apiimpl/EmbersAPIImpl.java", ("player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex())", "player.getItemBySlot(EquipmentSlot.HEAD)"))
# PvP moved from the server to each level in 26.1
edit("augment/FocalLensAugment.java",
     ("return server != null && server.isPvpAllowed() &&",
      "return server != null && world instanceof net.minecraft.server.level.ServerLevel serverLevel && serverLevel.isPvpAllowed() &&"))
edit("block/FluidDialBlock.java", ("contents.getDisplayName()", "contents.getHoverName()"))
edit("block/FluidVesselBlock.java", ("net.minecraft.core.cauldron.CauldronInteraction.EMPTY", "new net.minecraft.core.cauldron.CauldronInteraction.Dispatcher()"))
edit("block/MiniBoilerBlock.java",
     ("heldItem.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM) != null",
      "net.neoforged.neoforge.fluids.FluidUtil.getFluidHandler(heldItem).isPresent()"))

# particle option codecs: 26.1 VECTOR3F is a Codec<Vector3fc>
for f in (root / "particle").glob("*ParticleOptions.java"):
    s = f.read_bytes().decode("utf-8")
    if "ExtraCodecs.VECTOR3F.fieldOf(" in s:
        s = s.replace("ExtraCodecs.VECTOR3F.fieldOf(", "ExtraCodecs.VECTOR3F.<org.joml.Vector3f>xmap(org.joml.Vector3f::new, v -> v).fieldOf(")
        f.write_bytes(s.encode("utf-8"))

for r in ["blockentity/CatalyticPlugBlockEntity.java", "blockentity/WildfireStirlingBlockEntity.java"]:
    edit(r, (r"\.getFaceOcclusionShape\(level, pos\.relative\(planar\), (planar\.getOpposite\(\))\)", r".getFaceOcclusionShape(\1)"), regex=True)
edit("blockentity/StamperBlockEntity.java", ("blockEntity.cachedRecipe.assemble(context, level.registryAccess())", "blockEntity.cachedRecipe.assemble(context)"))
edit("blockentity/HearthCoilBlockEntity.java", (".assemble(wrapper, level.registryAccess())", ".assemble(wrapper)"))
for r in ["blockentity/ReservoirBlockEntity.java", "blockentity/EntropicEnumeratorBlockEntity.java"]:
    edit(r, ("\t\tsaveAdditional(nbt, provider);", "\t\tsaveAdditionalNbt(nbt, provider);"))

for r in ["item/CaminiteRingBlockItem.java", "item/GlimmerCrystalItem.java"]:
    edit(r, (r"(this\.use\([^;]*\))\.getResult\(\);", r"\1;"), regex=True)
edit("item/CodebreakingSlateItem.java", ("\t\treturn getDescription();", "\t\treturn getName(new ItemStack(this));"))
edit("network/message/MessageItemSound.java",
     ("BuiltInRegistries.ITEM.get(buf.readIdentifier())", "BuiltInRegistries.ITEM.getValue(buf.readIdentifier())"),
     ("BuiltInRegistries.SOUND_EVENT.get(buf.readIdentifier())", "BuiltInRegistries.SOUND_EVENT.getValue(buf.readIdentifier())"))
edit("network/PacketHandler.java", ("PacketDistributor.sendToServer(payload);", "net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);"))
edit("render/FluidRenderer.java",
     ("Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(sprite)",
      "Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS).getSprite(sprite)"))
# 1.21 getPixelRGBA returned ABGR; 26.1 getPixel returns ARGB
edit("util/EmbersColors.java", ("int color = image.getPixelRGBA(0, 0);", "int color = net.minecraft.util.ARGB.toABGR(image.getPixel(0, 0));"))
edit("gui/SlateScreen.java",
     (r"super\(pMenu, pPlayerInventory, pTitle\);" + NL + r"\t\tthis\.imageHeight = SlateMenu\.invHeight \+ SlateMenu\.slateHeight;" + NL + r"\t\tthis\.imageWidth = SlateMenu\.invWidth;",
      "super(pMenu, pPlayerInventory, pTitle, SlateMenu.invWidth, SlateMenu.invHeight + SlateMenu.slateHeight);"), regex=True)
edit("fluidtypes/SteamFluidType.java", ("entity.hasImpulse = true;", "entity.needsSync = true;"))

# item fluid capability for the fluid vessel (same component and capacity as 1.21.1)
edit("Embers.java",
     ("event.registerItem(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.ITEM,\n\t\t\t(stack, ctx) -> new net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack(RegistryManager.FLUID_CONTENT, stack, ConfigManager.FLUID_VESSEL_CAPACITY.get()),",
      "event.registerItem(net.neoforged.neoforge.capabilities.Capabilities.Fluid.ITEM,\n\t\t\t(stack, access) -> new net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler(access, RegistryManager.FLUID_CONTENT.get(), ConfigManager.FLUID_VESSEL_CAPACITY.get()),"))

at = base / "src/main/resources/META-INF/accesstransformer.cfg"
a = at.read_bytes().decode("utf-8")
lines = a.splitlines()
lines = [l.replace("Lnet/minecraft/resources/ResourceLocation;", "Lnet/minecraft/resources/Identifier;") if "getRandomNamedJigsaw" in l else l for l in lines]
if not any("handleRelativeFrictionAndCalculateMovement" in l for l in lines):
    lines.append("public net.minecraft.world.entity.LivingEntity handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3; # steam movement")
at.write_bytes(("\n".join(lines) + "\n").encode("utf-8"))

for m in misses:
    print("MISS", *m)
print("done")
