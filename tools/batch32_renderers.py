import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
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


# helpers replacing the removed BakedModel queries (isGui3d, ground transform scale)
HELPERS = '''
	/**
	 * 26.1 has no BakedModel to ask; 3D (block-lit) item models are what 1.20.1 called gui3d.
	 */
	public static boolean isGui3d(ItemStack stack, @Nullable Level level) {
		net.minecraft.client.renderer.item.ItemStackRenderState state = new net.minecraft.client.renderer.item.ItemStackRenderState();
		net.minecraft.client.Minecraft.getInstance().getItemModelResolver().updateForTopItem(state, stack, ItemDisplayContext.GROUND, level, null, 0);
		return state.usesBlockLight();
	}

	/**
	 * Y scale of the GROUND display transform: 0.25 for block/block models, 0.5 for item/generated,
	 * the two vanilla parents nearly every item uses. The transform itself is private in 26.1.
	 */
	public static float groundScaleY(ItemStack stack, @Nullable Level level) {
		return isGui3d(stack, level) ? 0.25F : 0.5F;
	}
'''
f = root / "render/LegacyBlockEntityRenderer.java"
s = f.read_bytes().decode("utf-8")
i = s.index("\tpublic static void item(ItemStack stack")
s = s[:i] + HELPERS.lstrip("\n") + "\n" + s[i:]
f.write_bytes(s.encode("utf-8"))

for f in (root / "blockentity/render").glob("*.java"):
    s = f.read_bytes().decode("utf-8")
    o = s
    s = s.replace("bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y()", "groundScaleY(stack, blockEntity.getLevel())")
    s = s.replace("bakedmodel.isGui3d()", "isGui3d(stack, blockEntity.getLevel())")
    # render state lives in the render types now (cull/depth set per pipeline)
    s = re.sub(r"[ \t]*RenderSystem\.(enableCull|disableCull|enableDepthTest|setShaderTexture)\([^;]*\);" + NL, "", s)
    if s != o:
        f.write_bytes(s.encode("utf-8"))
        print("renderer", f.name)
print("misses", misses)
