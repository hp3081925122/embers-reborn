package com.rekindled.embers.upgrade;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.capabilities.BlockCapability;
import java.util.Optional;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DefaultUpgradeProvider implements IUpgradeProvider {

	protected final Identifier id;
	protected final BlockEntity tile;
	// holder field removed (LazyOptional not used in NeoForge 1.21.1)

	public DefaultUpgradeProvider(Identifier id, BlockEntity tile) {
		this.id = id;
		this.tile = tile;
		// holder init removed
	}

	@Override
	public Identifier getUpgradeId() {
		return id;
	}

	public void invalidate() {
		// holder.invalidate() removed
	}

}
