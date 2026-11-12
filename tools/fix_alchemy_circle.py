import pathlib, re

f = pathlib.Path(__file__).resolve().parent.parent / "src/main/java/com/rekindled/embers/particle/AlchemyCircleParticle.java"
t = f.read_bytes().decode("utf-8")
start = t.index("\tpublic void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {")
end = re.search(r"\t@Override\r?\n\tpublic int getLightCoords", t[start:]).start() + start
new_method = (
    "\t/** Flat circle lying on the ground, drawn from both sides like the 1.21.1 render(). */\n"
    "\t@Override\n"
    "\tpublic void extract(net.minecraft.client.renderer.state.level.QuadParticleRenderState state, Camera camera, float partialTicks) {\n"
    "\t\tQuaternionf quaternion = new Quaternionf(QUAT);\n"
    "\t\tif (this.roll != 0.0F)\n"
    "\t\t\tquaternion.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));\n"
    "\t\tthis.extractRotatedQuad(state, camera, quaternion, partialTicks);\n"
    "\t\tthis.extractRotatedQuad(state, camera, new Quaternionf(quaternion).rotateY((float) Math.PI), partialTicks);\n"
    "\t}\n\n"
)
t = t[:start] + new_method + t[end:]
f.write_bytes(t.encode("utf-8"))
print("ok")
