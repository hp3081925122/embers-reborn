package com.rekindled.embers.worldgen;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersBiomeModifiers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.Tags;

public class CaveStructurePiece extends PoolElementStructurePiece {

	public Structure.GenerationContext context;
	public int maxHeight;
	public boolean resolved = false;
	public int resolvedCaveHeight = Integer.MIN_VALUE;

	public CaveStructurePiece(StructureTemplateManager pStructureTemplateManager, StructurePoolElement pElement, BlockPos pPosition, int pGroundLevelDelta, Rotation pRotation, BoundingBox pBox, Structure.GenerationContext context, int maxHeight) {
		super(pStructureTemplateManager, pElement, pPosition, pGroundLevelDelta, pRotation, pBox, LiquidSettings.APPLY_WATERLOGGING);
		this.context = context;
		this.maxHeight = maxHeight;
	}

	public CaveStructurePiece(StructurePieceSerializationContext serializationContext, CompoundTag tag) {
		super(serializationContext, tag);
		this.maxHeight = tag.getIntOr("max_height", 64);
		this.resolved = tag.getBooleanOr("cave_resolved", false);
		this.resolvedCaveHeight = tag.getIntOr("cave_height", Integer.MIN_VALUE);
		if (this.resolved && this.resolvedCaveHeight != Integer.MIN_VALUE && this.position.getY() != this.resolvedCaveHeight) {
			this.move(0, this.resolvedCaveHeight - this.position.getY(), 0);
		}
	}

	@Override
	public StructurePieceType getType() {
		return RegistryManager.CAVE_STRUCTURE_PIECE.get();
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext serializationContext, CompoundTag tag) {
		super.addAdditionalSaveData(serializationContext, tag);
		tag.putInt("max_height", this.maxHeight);
		tag.putBoolean("cave_resolved", this.resolved);
		tag.putInt("cave_height", this.resolvedCaveHeight);
	}

	@Override
	public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
		if (!this.resolved) {
			BlockPos center = StructureTemplate.transform(new BlockPos(this.getBoundingBox().getXSpan() / 2, 0, this.getBoundingBox().getZSpan() / 2), Mirror.NONE, this.getRotation(), BlockPos.ZERO).offset(this.position);
			this.resolvedCaveHeight = getCaveFloor(center, this.maxHeight, worldGenLevel, this.context);
			this.resolved = true;
			if (this.resolvedCaveHeight != Integer.MIN_VALUE) {
				this.move(0, this.resolvedCaveHeight - this.position.getY(), 0);
			}
		}
		if (this.resolvedCaveHeight != Integer.MIN_VALUE) {
			super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, new BlockPos(pos.getX(), this.resolvedCaveHeight, pos.getZ()));
		}
	}

	public static int getCaveFloor(BlockPos endPos, int maxHeight, WorldGenLevel level, Structure.GenerationContext context) {
		List<Integer> heights = new ArrayList<Integer>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(endPos.getX(), level.getMinY() + 9, endPos.getZ());
		for (int i = level.getMinY() + 9; i <= endPos.getY() && i <= maxHeight; i++) {
			pos.setY(i);
			boolean solid = !level.getBlockState(pos).canBeReplaced();
			pos.setY(i + 1);
			boolean replaceable = level.getBlockState(pos).canBeReplaced();
			if (solid && replaceable && isValidBiome(pos, level, context)) {
				heights.add(i);
			}
		}
		if (heights.isEmpty())
			return Integer.MIN_VALUE;
		if (heights.size() > 1)
			return heights.get(1);
		return heights.get(0);
	}

	public static boolean isValidBiome(BlockPos blockpos, WorldGenLevel level, Structure.GenerationContext pContext) {
		if (pContext != null) {
			return pContext.validBiome().test(pContext.chunkGenerator().getBiomeSource().getNoiseBiome(QuartPos.fromBlock(blockpos.getX()), QuartPos.fromBlock(blockpos.getY()), QuartPos.fromBlock(blockpos.getZ()), pContext.randomState().sampler()));
		}
		Holder<Biome> biome = level.getBiome(blockpos);
		return biome.is(BiomeTags.IS_OVERWORLD) && !biome.is(Tags.Biomes.IS_MUSHROOM) && !biome.is(Biomes.DEEP_DARK) && !biome.is(EmbersBiomeModifiers.NO_MONSTERS);
	}
}
