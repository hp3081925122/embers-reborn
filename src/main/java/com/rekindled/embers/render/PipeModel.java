package com.rekindled.embers.render;

import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

/**
 * Unbaked pipe block state model. 26.1 has no block-model geometry loaders that can produce a
 * per-position model, so this is a custom block state model referenced directly from the blockstate file:
 * <pre>{"type": "embers_reborn:pipe", "center": {"model": "...", "y": 90}, "connection": "...", "connection2": "...", "end": "...", "end2": "..."}</pre>
 * The center keeps the variant rotation (1.20.1 baked it with the variant's ModelState);
 * connections and ends always use the six fixed world rotations, one per direction (D, U, N, S, W, E).
 */
public record PipeModel(Variant center, Identifier connection, Identifier connection2, Identifier end, Identifier end2) implements CustomUnbakedBlockStateModel {

	public static final MapCodec<PipeModel> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			Variant.MAP_CODEC.codec().fieldOf("center").forGetter(PipeModel::center),
			Identifier.CODEC.fieldOf("connection").forGetter(PipeModel::connection),
			Identifier.CODEC.fieldOf("connection2").forGetter(PipeModel::connection2),
			Identifier.CODEC.fieldOf("end").forGetter(PipeModel::end),
			Identifier.CODEC.fieldOf("end2").forGetter(PipeModel::end2)
	).apply(inst, PipeModel::new));

	@Override
	public MapCodec<PipeModel> codec() {
		return CODEC;
	}

	@Override
	public BlockStateModel bake(ModelBaker baker) {
		return new BakedPipeModel(center.bake(baker), getRotatedModels(baker, connection, connection2), getRotatedModels(baker, end, end2));
	}

	/** Same six rotations as 1.20.1: X0_Y0, X180_Y0, X90_Y180, X90_Y0, X90_Y90, X90_Y270 */
	public static BlockStateModelPart[] getRotatedModels(ModelBaker baker, Identifier model, Identifier model2) {
		return new BlockStateModelPart[] {
				rotated(model, Quadrant.R0, Quadrant.R0).bake(baker),
				rotated(model2, Quadrant.R180, Quadrant.R0).bake(baker),
				rotated(model, Quadrant.R90, Quadrant.R180).bake(baker),
				rotated(model2, Quadrant.R90, Quadrant.R0).bake(baker),
				rotated(model, Quadrant.R90, Quadrant.R90).bake(baker),
				rotated(model2, Quadrant.R90, Quadrant.R270).bake(baker)
		};
	}

	private static Variant rotated(Identifier model, Quadrant x, Quadrant y) {
		return new Variant(model).withXRot(x).withYRot(y);
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver) {
		center.resolveDependencies(resolver);
		resolver.markDependency(connection);
		resolver.markDependency(connection2);
		resolver.markDependency(end);
		resolver.markDependency(end2);
	}
}
