package com.rekindled.embers.render;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.blockentity.PipeBlockEntityBase;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;

public class BakedPipeModel implements BlockStateModel {
    private final BlockStateModelPart centerModel;
    private final BlockStateModelPart[] connectionModel;
    private final BlockStateModelPart[] endModel;
    @SuppressWarnings("unchecked")
    private final List<BlockStateModelPart>[] partCache = new List[729];

    public BakedPipeModel(BlockStateModelPart centerModel, BlockStateModelPart[] connectionModel, BlockStateModelPart[] endModel) {
        this.centerModel = centerModel;
        this.connectionModel = connectionModel;
        this.endModel = endModel;
    }

    public static int getCacheIndex(int[] data) {
        return (((((data[0] * 3 + data[1]) * 3 + data[2]) * 3 + data[3]) * 3 + data[4]) * 3) + data[5];
    }

    @Override
    @Deprecated
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        output.add(centerModel);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> output) {
        int[] sides = null;
        if (level != null && pos != null) {
            ModelData data = level.getModelData(pos);
            if (data != null) {
                sides = data.get(PipeBlockEntityBase.DATA_TYPE);
            }
        }
        if (sides == null || sides.length < 6) {
            output.add(centerModel);
            return;
        }
        int index = getCacheIndex(sides);
        List<BlockStateModelPart> parts = partCache[index];
        if (parts == null) {
            parts = new ArrayList<>(7);
            parts.add(centerModel);
            for (int i = 0; i < 6; i++) {
                if (sides[i] == 1) {
                    parts.add(connectionModel[i]);
                } else if (sides[i] == 2) {
                    parts.add(endModel[i]);
                }
            }
            partCache[index] = List.copyOf(parts);
        }
        output.addAll(parts);
    }

    @Override
    @Deprecated
    public Material.Baked particleMaterial() {
        return centerModel.particleMaterial();
    }

    @Override
    public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        return centerModel.particleMaterial();
    }

    @Override
    @Deprecated
    public int materialFlags() {
        return centerModel.materialFlags();
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        int flags = centerModel.materialFlags();
        for (BlockStateModelPart part : connectionModel) {
            flags |= part.materialFlags();
        }
        for (BlockStateModelPart part : endModel) {
            flags |= part.materialFlags();
        }
        return flags;
    }
}

