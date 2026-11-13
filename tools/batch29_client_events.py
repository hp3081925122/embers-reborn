import re, pathlib

base = pathlib.Path(__file__).resolve().parent.parent
root = base / "src/main/java/com/rekindled/embers"
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


f = root / "EmbersClientEvents.java"
s = f.read_bytes().decode("utf-8")

# imports of removed classes (all unused after this batch)
for imp in ["org.lwjgl.opengl.GL30C", "com.mojang.blaze3d.pipeline.RenderTarget", "com.mojang.blaze3d.pipeline.TextureTarget",
            "com.mojang.blaze3d.platform.GlStateManager", "net.minecraft.client.resources.model.BakedModel",
            "net.minecraft.client.resources.model.BlockModelRotation", "net.minecraft.client.resources.model.Material",
            "net.minecraft.client.resources.model.ModelResourceLocation", "net.neoforged.neoforge.client.event.RenderHighlightEvent"]:
    s = sub(s, r"import " + re.escape(imp) + ";" + NL, "", 1, regex=True)

# HUD layer type
s = sub(s, "public static final net.minecraft.client.gui.LayeredDraw.Layer INGAME_OVERLAY",
        "public static final net.neoforged.neoforge.client.gui.GuiLayer INGAME_OVERLAY", 1)

# item-use slowdown cancel: 26.1 moved impulses into ClientInput.moveVector (made public by AT);
# the event still fires before LocalPlayer.modifyInput applies the 0.2 item-use multiplier
s = sub(s, "\t\t\tevent.getInput().forwardImpulse /= 0.2f;\n\t\t\tevent.getInput().leftImpulse /= 0.2f;",
        "\t\t\tevent.getInput().moveVector = event.getInput().moveVector.scale(1.0f / 0.2f);", 1) if \
    "\t\t\tevent.getInput().forwardImpulse /= 0.2f;\n" in s else \
    sub(s, "\t\t\tevent.getInput().forwardImpulse /= 0.2f;\r\n\t\t\tevent.getInput().leftImpulse /= 0.2f;",
        "\t\t\tevent.getInput().moveVector = event.getInput().moveVector.scale(1.0f / 0.2f);", 1)

# block outline: RenderHighlightEvent.Block -> ExtractBlockOutlineRenderStateEvent
s = sub(s, "public static void onBlockHighlight(RenderHighlightEvent.Block event) {",
        "public static void onBlockHighlight(net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent event) {", 1)
s = sub(s, "event.getTarget().getBlockPos().equals(target.pos)", "event.getBlockPos().equals(target.pos)", 1)

# level render: stage subclasses, camera from the level render state
s = sub(s, "public static void onLevelRender(RenderLevelStageEvent event) {\n\t\tif (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {",
        "public static void onLevelRender(RenderLevelStageEvent.AfterTranslucentBlocks event) {\n\t\t{", 1) if \
    "public static void onLevelRender(RenderLevelStageEvent event) {\n" in s else \
    sub(s, "public static void onLevelRender(RenderLevelStageEvent event) {\r\n\t\tif (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {",
        "public static void onLevelRender(RenderLevelStageEvent.AfterTranslucentBlocks event) {\r\n\t\t{", 1)
s = sub(s, "Vec3 camPos = event.getCamera().getPosition();", "Vec3 camPos = event.getLevelRenderState().cameraRenderState.pos;", 2)
s = sub(s, "event.getPartialTick().getGameTimeDeltaPartialTick(false)",
        "Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)", 2)
# flush the glow lines inside the stage (1.21 flushed the shared buffer source after the event)
s = sub(s, "\t\t\t} else {\n\t\t\t\tlastEmitter = null;\n\t\t\t}\n",
        "\t\t\t} else {\n\t\t\t\tlastEmitter = null;\n\t\t\t}\n\t\t\tmc.renderBuffers().bufferSource().endBatch(EmbersRenderTypes.GLOW_LINES);\n", 1) if \
    "\t\t\t} else {\n\t\t\t\tlastEmitter = null;\n" in s else \
    sub(s, "\t\t\t} else {\r\n\t\t\t\tlastEmitter = null;\r\n\t\t\t}\r\n",
        "\t\t\t} else {\r\n\t\t\t\tlastEmitter = null;\r\n\t\t\t}\r\n\t\t\tmc.renderBuffers().bufferSource().endBatch(EmbersRenderTypes.GLOW_LINES);\r\n", 1)

# codex shortcut selects the codex hotbar slot
s = sub(s, "mc.player.getInventory().selected = codexIndex;", "mc.player.getInventory().setSelectedSlot(codexIndex);", 1)

# depth buffer copy only fed the soft particle edges, which 26.1 particle layers cannot sample (dropped in batch 13)
s = sub(s, r"\tpublic static RenderTarget depthBuffer;" + NL + NL + r"\tpublic static void onWorldRender\(RenderLevelStageEvent event\) \{" + NL
        + r"(?:.*" + NL + r")*?\t\}" + NL, "", 1, regex=True)
f.write_bytes(s.encode("utf-8"))

f = root / "Embers.java"
s = f.read_bytes().decode("utf-8")
s = sub(s, r"[ \t]*NeoForge\.EVENT_BUS\.addListener\(EmbersClientEvents::onWorldRender\);" + NL, "", 1, regex=True)
f.write_bytes(s.encode("utf-8"))

at = base / "src/main/resources/META-INF/accesstransformer.cfg"
a = at.read_bytes().decode("utf-8")
if "ClientInput moveVector" not in a:
    a = a.rstrip("\r\n") + "\npublic net.minecraft.client.player.ClientInput moveVector # item use slowdown cancel\n"
    at.write_bytes(a.encode("utf-8"))
print("ok")
