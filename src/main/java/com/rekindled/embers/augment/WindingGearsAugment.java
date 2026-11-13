package com.rekindled.embers.augment;

import java.util.Map;
import java.util.WeakHashMap;
import net.neoforged.neoforge.client.event.ClientTickEvent;
// net.neoforged.neoforge.event.entity.living.LivingEvent removed in 1.21.1

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.datagen.EmbersSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class WindingGearsAugment extends AugmentBase {
	// ---- DataComponents 辅助方法 (1.21.1 迁移) ----
	private static net.minecraft.nbt.CompoundTag _getTag(net.minecraft.world.item.ItemStack s) {
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return (cd != null && !cd.isEmpty()) ? cd.copyTag() : null;
	}
	private static net.minecraft.nbt.CompoundTag _getOrCreateTag(net.minecraft.world.item.ItemStack s) {
		return s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
	}
	private static void _setTag(net.minecraft.world.item.ItemStack s, net.minecraft.nbt.CompoundTag t) {
		if (t == null || t.isEmpty()) s.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		else s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(t));
	}
	private static boolean _hasTag(net.minecraft.world.item.ItemStack s) {
		if (s.isEmpty()) return false;
		net.minecraft.world.item.component.CustomData cd = s.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
		return cd != null && !cd.isEmpty();
	}



	public static final Identifier TEXTURE_HUD = Identifier.fromNamespaceAndPath(com.rekindled.embers.Embers.MODID, "textures/gui/icons.png");
	public static final int BAR_U = 0;
	public static final int BAR_V = 32;
	public static final int BAR_WIDTH = 180;
	public static final int BAR_HEIGHT = 8;

	public static final String TAG_CHARGE = "windingGearsCharge";
	public static final String TAG_CHARGE_TIME = "windingGearsLastTime";
	public static final double MAX_CHARGE = 500.0;
	public static final int CHARGE_DECAY_DELAY = 20;
	public static final double CHARGE_DECAY = 0.25;

	static int ticks;
	static double angle, angleLast;
	static int spool, spoolLast;
	static ThreadLocal<Map<Entity, Double>> bounceLocal = ThreadLocal.withInitial(WeakHashMap::new);

	public WindingGearsAugment(Identifier id) {
		super(id, 0.0);
		NeoForge.EVENT_BUS.register(this);
	}

	@OnlyIn(Dist.CLIENT)
	private static int getBarY(int height) {
		return height - 31;
	}

	@OnlyIn(Dist.CLIENT)
	private static int getBarX(int width) {
		return width / 2 - 11 - 81;
	}

	public static ItemStack getHeldClockworkTool(LivingEntity entity) {
		ItemStack mainStack = entity.getMainHandItem();
		ItemStack offStack = entity.getOffhandItem();
		boolean isClockworkMain = isClockworkTool(mainStack);
		boolean isClockworkOff = isClockworkTool(offStack);
		if (isClockworkMain == isClockworkOff)
			return ItemStack.EMPTY;
		if (isClockworkMain)
			return mainStack;
		return offStack;
	}

	public static boolean isClockworkTool(ItemStack stack) {
		return AugmentUtil.hasHeat(stack) && AugmentUtil.hasAugment(stack, RegistryManager.WINDING_GEARS_AUGMENT);
	}

	public static double getChargeDecay(Level world, ItemStack stack) {
		return CHARGE_DECAY;
	}

	public static double getCharge(Level world, ItemStack stack) {
		if (_hasTag(stack)) {
			long dTime = getTimeSinceLastCharge(world, stack);
			return Math.max(0, _getTag(stack).getDoubleOr(TAG_CHARGE, 0.0D) - Math.max(0, dTime - CHARGE_DECAY_DELAY) * getChargeDecay(world,stack));
		}
		return 0;
	}

	private static long getTimeSinceLastCharge(Level world, ItemStack stack) {
		if (_hasTag(stack)) {
			long lastTime = _getTag(stack).getLongOr(TAG_CHARGE_TIME, 0L);
			long currentTime = world.getGameTime();
			if (lastTime > currentTime)
				return 0;
			else
				return currentTime - lastTime;
		}
		return Long.MAX_VALUE;
	}

	public static double getMaxCharge(Level world, ItemStack stack) {
		int level = getClockworkLevel(stack);
		return Math.min(200.0 * level, MAX_CHARGE);
	}

	private static int getClockworkLevel(ItemStack stack) {
		int level = AugmentUtil.getAugmentLevel(stack, RegistryManager.WINDING_GEARS_AUGMENT);
		return level;
	}

	public static void setCharge(Level world, ItemStack stack, double charge) {
		if (world.isClientSide())
			return;
		CompoundTag tagCompound = _getTag(stack);
		if (tagCompound != null) {
			tagCompound.putDouble(TAG_CHARGE, charge);
			tagCompound.putLong(TAG_CHARGE_TIME, world.getGameTime());
		}
	}

	public static void depleteCharge(Level world, ItemStack stack, double charge) {
		setCharge(world, stack, Math.max(0, getCharge(world, stack) - charge));
	}

	public static void addCharge(Level world, ItemStack stack, double charge) {
		if (world.isClientSide())
			return;
		setCharge(world, stack, Math.min(getMaxCharge(world, stack), getCharge(world, stack) + charge));
		CompoundTag tagCompound = _getTag(stack);
		if (tagCompound != null)
			tagCompound.putLong(TAG_CHARGE_TIME, world.getGameTime());
	}

	public static float getSpeedBonus(Level world,ItemStack stack) {
		double charge = getCharge(world,stack);
		return (float) Mth.clampedLerp(-0.2, 20.0, (charge - 50.0) / 300.0);
	}

	public static float getDamageBonus(Level world,ItemStack stack) {
		double charge = getCharge(world,stack);
		return (float) Mth.clampedLerp(1.0, 6.0, (charge - 50.0) / 300.0);
	}

	public static double getRotationSpeed(Level world,ItemStack stack) {
		long dTime = getTimeSinceLastCharge(world, stack);
		double charge = getCharge(world,stack);
		double standardSpeed = Mth.clampedLerp(0.0, 400.0, charge / 500.0);
		if (dTime > CHARGE_DECAY_DELAY && charge > 0)
			return Mth.clampedLerp(0, -10, (dTime - CHARGE_DECAY_DELAY) / 10.0);
		else
			return Mth.clampedLerp(standardSpeed, 0, (dTime - 10) / 10.0);
	}

	@SubscribeEvent
	public void onJump(LivingEvent.LivingJumpEvent event) {
		LivingEntity entity = event.getEntity();
		ItemStack stack = getHeldClockworkTool(entity);
		if (!stack.isEmpty() && isClockworkTool(entity.getItemBySlot(EquipmentSlot.FEET))) {
			double charge = getCharge(entity.level(), stack);
			double cost = Math.max(16, charge * (80.0 / 500.0));
			if (charge > 0) {
				double x = 0;
				double z = 0;
				if (entity.isSprinting() && charge > Math.max(40, cost * 1.5)) {
					x = entity.getDeltaMovement().x;
					z = entity.getDeltaMovement().z;
					cost = Math.max(40, cost * 1.5);
				}
				entity.setDeltaMovement(entity.getDeltaMovement().add(new Vec3(x, Mth.clampedLerp(0.0, 7.0 / 20.0, charge / 500.0), z)));
				if (charge >= cost)
					entity.playSound(EmbersSounds.WINDING_GEARS_SPRING.get(), 1.0f, 1.0f);
			}

			if (!entity.level().isClientSide())
				depleteCharge(entity.level(), stack, cost);
		}
	}

	@SubscribeEvent
	public void onTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Pre event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		Map<Entity,Double> bounce = bounceLocal.get();
		if (bounce.containsKey(entity)) {
			entity.setDeltaMovement(entity.getDeltaMovement().add(new Vec3(0, bounce.get(entity), 0)));
			bounce.remove(entity);
		}
	}

	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		LivingEntity entity = event.getEntity();
		ItemStack stack = getHeldClockworkTool(entity);
		if (!stack.isEmpty() && isClockworkTool(entity.getItemBySlot(EquipmentSlot.FEET))) {
			double spoolCost = Math.max(0, event.getDistance() - 1) * 5;
			if (getCharge(entity.level(), stack) >= spoolCost) {
				event.setDamageMultiplier(0);
				if (entity.getDeltaMovement().y < -0.5) {
					if (!entity.level().isClientSide())
						depleteCharge(entity.level(), stack,spoolCost);
					bounceLocal.get().put(entity,-entity.getDeltaMovement().y);
				}
			}
		}
	}

	@SubscribeEvent
	public void onAttack(LivingDamageEvent.Pre event) {
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof LivingEntity player) {
			ItemStack mainStack = player.getMainHandItem();
			//float damage = event.getNewDamage();
			if (isClockworkTool(mainStack)) {
				double charge = getCharge(player.level(), mainStack);
				double cost = 5;
				if (charge >= getMaxCharge(player.level(), mainStack)) {
					//event.setNewDamage(damage + getDamageBonus(mainStack));
					cost = charge;
				}
				if (!player.level().isClientSide())
					depleteCharge(player.level(), mainStack, cost);
			}
		}
	}

	@SubscribeEvent
	public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		ItemStack mainStack = player.getMainHandItem();
		float speed = event.getNewSpeed();
		if (isClockworkTool(mainStack)) {
			double charge = getCharge(player.level(), mainStack);
			if (charge > 0) {
				event.setNewSpeed(Math.max(Math.min(speed, 0.1f), speed + getSpeedBonus(player.level(), mainStack)));
			}
		}
	}

	@SubscribeEvent
	public void onBreak(net.neoforged.neoforge.event.level.block.BreakBlockEvent event) {
		Player player = event.getPlayer();
		if (!event.isCanceled() && player != null) {
			ItemStack mainStack = player.getMainHandItem();
			if (isClockworkTool(mainStack)) {
				double charge = getCharge(player.level(), mainStack);
				if (charge > 0) {
					if (!player.level().isClientSide())
						depleteCharge(player.level(), mainStack, 40);
				}
			}
		}
	}

	@SubscribeEvent
	public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();
		ItemStack stack = event.getItemStack();

		if (isClockworkTool(stack)) {
			int level = getClockworkLevel(stack);
			double maxCharge = getMaxCharge(player.level(),stack);

			if (level > 0) {
				double resonance = EmbersAPI.getEmberResonance(stack);
				double charge = getCharge(player.level(), stack);
				double addAmount = Math.max((0.025 + 0.01 * level) * (maxCharge - charge), 5 * resonance);
				addCharge(player.level(), stack, addAmount);
				player.swing(event.getHand());
				event.setCancellationResult(InteractionResult.PASS);
				event.setCanceled(true);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onClientUpdate(ClientTickEvent.Post event) {
		if (true) { // NeoForge 1.21.1: tick events fire once, no phase
			ticks++;
			Minecraft mc = Minecraft.getInstance();
			LocalPlayer player = mc.player;

			if (player != null) {
				ItemStack stack = getHeldClockworkTool(player);
				if (!stack.isEmpty()) {
					spoolLast = spool;
					spool = (int) (BAR_WIDTH * 4 * getCharge(player.level(), stack) / MAX_CHARGE);
					angleLast = angle;
					angle += getRotationSpeed(player.level(), stack);
					//Auto-Attack
					if(mc.options.keyAttack.isDown() && mc.hitResult instanceof EntityHitResult entityHit && canAutoAttack(player, stack, entityHit))
						mc.gameMode.attack(player, entityHit.getEntity());
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private boolean canAutoAttack(LocalPlayer player, ItemStack stack, EntityHitResult objectMouseOver) {
		return player.getAttackStrengthScale(0) >= 1.0f && getCharge(player.level(), stack) > 0 /* && !isInvulnerable(objectMouseOver.entityHit)*/;
	}

	@OnlyIn(Dist.CLIENT)
	private boolean isInvulnerable(Entity entity) {
		return entity.isInvulnerable() || (entity instanceof LivingEntity && entity.invulnerableTime > 0);
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderSpringUnderlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		int fill = (int) (spoolLast * (1 - partialTicks) + spool * partialTicks);
		fill += 16;
		Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		ItemStack stack = getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getBarX(width);
			int y = getBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = BAR_U;
			int v = BAR_V + 8;

			int evenWidth = (segs) * 8;
			int oddWidth = (segs) * 8 - 4;
			int evenFillBack = Mth.clamp(last - 16, 0, 8);
			int oddFillBack = Mth.clamp(last - 0, 0, 8);

			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x,  y,  u,  v,  evenWidth,  BAR_HEIGHT, 256, 256);
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x + evenWidth,  y + 8 - evenFillBack,  u + evenWidth,  v + 8 - evenFillBack,  8,  evenFillBack, 256, 256);
			v += 16;
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x,  y,  u,  v,  oddWidth,  BAR_HEIGHT, 256, 256);
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x + oddWidth,  y + 8 - oddFillBack,  u + oddWidth,  v + 8 - oddFillBack,  8,  oddFillBack, 256, 256);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderSpringOverlay(GuiGraphicsExtractor graphics, float partialTicks, int width, int height) {
		int fill = (int) (spoolLast * (1 - partialTicks) + spool * partialTicks);
		double currentAngle = angleLast * (1 - partialTicks) + angle * partialTicks;
		int gearFrame = (int) (currentAngle * 4f / 360f);
		int uGear = (gearFrame % 4) * 10;
		int vGear = 16;
		fill += 16;
		Minecraft mc = net.minecraft.client.Minecraft.getInstance();
		Player player = mc.player;
		if (player == null)
			return;
		ItemStack stack = getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getBarX(width);
			int y = getBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = BAR_U;
			int v = BAR_V;

			int evenWidth = (segs) * 8;
			int oddWidth = (segs) * 8 - 4;
			int evenFillFront = Mth.clamp(last - 24, 0, 8);
			int oddFillFront = Mth.clamp(last - 8, 0, 8);

			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x - 9,  y - 1,  uGear,  vGear,  10,  10, 256, 256);

			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x,  y,  u,  v,  evenWidth,  BAR_HEIGHT, 256, 256);
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x + evenWidth,  y,  u + evenWidth,  v,  8,  evenFillFront, 256, 256);
			v += 16;
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x,  y,  u,  v,  oddWidth,  BAR_HEIGHT, 256, 256);
			graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE_HUD,  x + oddWidth,  y,  u + oddWidth,  v,  8,  oddFillFront, 256, 256);
		}
	}
}
