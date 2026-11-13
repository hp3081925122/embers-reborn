import re, pathlib

root = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers"
misses = []


def edit(rel, *pairs):
    f = root / rel
    s = f.read_bytes().decode("utf-8")
    for old, new in pairs:
        n = s.count(old)
        if n == 0:
            misses.append((rel, old[:70]))
        s = s.replace(old, new)
    f.write_bytes(s.encode("utf-8"))


# sprites from the 26.1 fluid model (1.21.1 asked IClientFluidTypeExtensions for texture ids)
HELPERS = '''
	/** Still sprite of the fluid's world model; the missing texture when the fluid has no model. */
	public static net.minecraft.client.renderer.texture.TextureAtlasSprite stillSprite(FluidStack stack) {
		FluidModel model = model(stack.getFluid());
		return model != null ? model.stillMaterial().sprite() : missingSprite();
	}

	/** Flowing sprite of the fluid's world model; the missing texture when the fluid has no model. */
	public static net.minecraft.client.renderer.texture.TextureAtlasSprite flowingSprite(FluidStack stack) {
		FluidModel model = model(stack.getFluid());
		return model != null ? model.flowingMaterial().sprite() : missingSprite();
	}

	private static net.minecraft.client.renderer.texture.TextureAtlasSprite missingSprite() {
		return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS)
				.getSprite(net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation());
	}
'''
f = root / "fluidtypes/EmbersFluidClient.java"
s = f.read_bytes().decode("utf-8")
anchor = "\t/** Tint for a fluid stack as the world fluid model applies it;"
assert anchor in s
s = s.replace(anchor, HELPERS.lstrip("\n") + "\n" + anchor, 1)
f.write_bytes(s.encode("utf-8"))

C = "com.rekindled.embers.fluidtypes.EmbersFluidClient"
edit("render/FluidRenderer.java",
     ("getBlockSprite(clientType.getStillTexture(fluid))", C + ".stillSprite(fluid)"),
     ("getBlockSprite(clientType.getFlowingTexture(fluid))", C + ".flowingSprite(fluid)"),
     ("clientType.getTintColor(fluid)", C + ".tint(fluid)"))
edit("blockentity/render/ReservoirBlockEntityRenderer.java",
     ("FluidRenderer.getBlockSprite(clientType.getStillTexture(fluid))", C + ".stillSprite(fluid)"),
     ("clientType.getTintColor(fluid)", C + ".tint(fluid)"))
print("misses", misses)
