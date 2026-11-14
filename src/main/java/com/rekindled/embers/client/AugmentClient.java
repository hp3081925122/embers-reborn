package com.rekindled.embers.client;

import java.util.Iterator;
import java.util.Random;

import com.rekindled.embers.augment.CasterOrbAugment;
import com.rekindled.embers.augment.ShiftingScalesAugment;
import com.rekindled.embers.augment.ShiftingScalesAugment.ShardParticle;
import com.rekindled.embers.augment.WindingGearsAugment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class AugmentClient {

	private AugmentClient() {}

	public static void onClientTickPre(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			CasterOrbAugment.prevCooledStrength = mc.player.getAttackStrengthScale(0);
		}
		if (CasterOrbAugment.cooldownTicks > 0) {
			CasterOrbAugment.cooldownTicks--;
		}

		Iterator<ShardParticle> iterator = ShiftingScalesAugment.shards.iterator();
		while (iterator.hasNext()) {
			ShardParticle particle = iterator.next();
			particle.update();
		}
	}

	public static void onClientTickPost(ClientTickEvent.Post event) {
		WindingGearsAugment.ticks++;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;

		if (player != null) {
			ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
			if (!stack.isEmpty()) {
				WindingGearsAugment.spoolLast = WindingGearsAugment.spool;
				WindingGearsAugment.spool = (int) (WindingGearsAugment.BAR_WIDTH * 4 * WindingGearsAugment.getCharge(player.level(), stack) / WindingGearsAugment.MAX_CHARGE);
				WindingGearsAugment.angleLast = WindingGearsAugment.angle;
				WindingGearsAugment.angle += WindingGearsAugment.getRotationSpeed(player.level(), stack);
				if (mc.options.keyAttack.isDown() && mc.hitResult instanceof EntityHitResult entityHit && canAutoAttack(player, stack, entityHit))
					mc.gameMode.attack(player, entityHit.getEntity());
			}
		}
	}

	private static boolean canAutoAttack(LocalPlayer player, ItemStack stack, EntityHitResult objectMouseOver) {
		return player.getAttackStrengthScale(0) >= 1.0f && WindingGearsAugment.getCharge(player.level(), stack) > 0;
	}

	public static void renderScalesHeartsOverlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.isCreative() || mc.player.isSpectator())
			return;
		int x = getScalesBarX(width);
		int y = getScalesBarY(height);

		int scales = ShiftingScalesAugment.scales;
		int segs = scales / 3;
		int last = scales % 3;
		if (last > 0)
			segs++;
		int u = 18;
		int v = 0;

		for (int i = 0; i < segs; i++) {
			if (i == segs - 1)
				u = ((last + 2) % 3) * 9;
			graphics.blit(RenderPipelines.GUI_TEXTURED, ShiftingScalesAugment.TEXTURE_HUD, x + 8 * (i % 10), y - 10 * (i / 10), u, v, 9, 9, 256, 256);
		}
	}

	public static void renderScalesIngameOverlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.isCreative() || mc.player.isSpectator())
			return;
		Iterator<ShardParticle> iterator = ShiftingScalesAugment.shards.iterator();
		while (iterator.hasNext()) {
			ShardParticle particle = iterator.next();
			if (particle.getY() > height)
				iterator.remove();
			int u = particle.getFrame() % 8 > 4 ? 5 : 0;
			int v = 9;
			graphics.blit(RenderPipelines.GUI_TEXTURED, ShiftingScalesAugment.TEXTURE_HUD, (int) particle.getX() - 2, (int) particle.getY() - 2, u, v, 5, 5, 256, 256);
		}
		int scales = ShiftingScalesAugment.scales;
		int scalesLast = ShiftingScalesAugment.scalesLast;
		Random random = new Random();
		if (scales < scalesLast) {
			int x = getScalesBarX(width);
			int y = getScalesBarY(height);

			int segsLast = scalesLast / 3;
			int lastLast = scalesLast % 3;
			if (lastLast > 0)
				segsLast++;
			int segs = scales / 3;
			int last = scales % 3;
			if (last > 0)
				segs++;

			for (int i = 0; i < Math.max(segs, segsLast); i++) {
				int currentScale = i * 3 + last;
				if (currentScale < scales)
					continue;
				int xHeart = x + 8 * (i % 10) + 4;
				int yHeart = y - 10 * (i / 10) + 4;
				int pieces = 2;
				if (lastLast == 1 && i == Math.max(segs, segsLast) - 1)
					pieces = 1;
				for (int e = 0; e < pieces; e++)
					ShiftingScalesAugment.shards.add(new ShardParticle(xHeart, yHeart, random.nextInt(8), (random.nextDouble() - 0.5) * 10, (random.nextDouble() - 0.5) * 10, 0.5));
			}
		}
		ShiftingScalesAugment.scalesLast = scales;
	}

	private static int getScalesBarY(int height) {
		return height - 39;
	}

	private static int getScalesBarX(int width) {
		return width / 2 - 11 - 8 * 10;
	}

	public static void renderSpringUnderlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		int fill = (int) (WindingGearsAugment.spoolLast * (1 - partialTicks) + WindingGearsAugment.spool * partialTicks);
		fill += 16;
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getSpringBarX(width);
			int y = getSpringBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = WindingGearsAugment.BAR_U;
			int v = WindingGearsAugment.BAR_V + 8;

			int evenWidth = (segs) * 8;
			int oddWidth = (segs) * 8 - 4;
			int evenFillBack = Mth.clamp(last - 16, 0, 8);
			int oddFillBack = Mth.clamp(last - 0, 0, 8);

			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x, y, u, v, evenWidth, WindingGearsAugment.BAR_HEIGHT, 256, 256);
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x + evenWidth, y + 8 - evenFillBack, u + evenWidth, v + 8 - evenFillBack, 8, evenFillBack, 256, 256);
			v += 16;
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x, y, u, v, oddWidth, WindingGearsAugment.BAR_HEIGHT, 256, 256);
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x + oddWidth, y + 8 - oddFillBack, u + oddWidth, v + 8 - oddFillBack, 8, oddFillBack, 256, 256);
		}
	}

	public static void renderSpringOverlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		int fill = (int) (WindingGearsAugment.spoolLast * (1 - partialTicks) + WindingGearsAugment.spool * partialTicks);
		double currentAngle = WindingGearsAugment.angleLast * (1 - partialTicks) + WindingGearsAugment.angle * partialTicks;
		int gearFrame = (int) (currentAngle * 4f / 360f);
		int uGear = (gearFrame % 4) * 10;
		int vGear = 16;
		fill += 16;
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getSpringBarX(width);
			int y = getSpringBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = WindingGearsAugment.BAR_U;
			int v = WindingGearsAugment.BAR_V;

			int evenWidth = (segs) * 8;
			int oddWidth = (segs) * 8 - 4;
			int evenFillFront = Mth.clamp(last - 24, 0, 8);
			int oddFillFront = Mth.clamp(last - 8, 0, 8);

			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x - 9, y - 1, uGear, vGear, 10, 10, 256, 256);

			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x, y, u, v, evenWidth, WindingGearsAugment.BAR_HEIGHT, 256, 256);
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x + evenWidth, y, u + evenWidth, v, 8, evenFillFront, 256, 256);
			v += 16;
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x, y, u, v, oddWidth, WindingGearsAugment.BAR_HEIGHT, 256, 256);
			graphics.blit(RenderPipelines.GUI_TEXTURED, WindingGearsAugment.TEXTURE_HUD, x + oddWidth, y, u + oddWidth, v, 8, oddFillFront, 256, 256);
		}
	}

	private static int getSpringBarY(int height) {
		return height - 31;
	}

	private static int getSpringBarX(int width) {
		return width / 2 - 11 - 81;
	}
}
