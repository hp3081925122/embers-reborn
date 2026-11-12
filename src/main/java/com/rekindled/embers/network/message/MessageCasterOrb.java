package com.rekindled.embers.network.message;

import java.util.UUID;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.event.EmberProjectileEvent;
import com.rekindled.embers.api.projectile.EffectDamage;
import com.rekindled.embers.api.projectile.IProjectilePreset;
import com.rekindled.embers.api.projectile.ProjectileFireball;
import com.rekindled.embers.augment.CasterOrbAugment;
import com.rekindled.embers.damage.DamageEmber;
import com.rekindled.embers.datagen.EmbersDamageTypes;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageCasterOrb implements CustomPacketPayload {

	double lookX = 0;
	double lookY = 0;
	double lookZ = 0;

	public static final CustomPacketPayload.Type<MessageCasterOrb> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:caster_orb"));

	public static final StreamCodec<FriendlyByteBuf, MessageCasterOrb> STREAM_CODEC =
		StreamCodec.ofMember(MessageCasterOrb::write, MessageCasterOrb::read);

	public MessageCasterOrb(double lookX, double lookY, double lookZ) {
		this.lookX = lookX;
		this.lookY = lookY;
		this.lookZ = lookZ;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeDouble(lookX);
		buf.writeDouble(lookY);
		buf.writeDouble(lookZ);
	}

	public static MessageCasterOrb read(FriendlyByteBuf buf) {
		return new MessageCasterOrb(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}

	public static void handle(MessageCasterOrb msg, IPayloadContext ctx) {
		if (!ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) ctx.player();
				ItemStack heldStack = player.getMainHandItem();
				if (AugmentUtil.hasHeat(heldStack)) {
					int level = AugmentUtil.getAugmentLevel(heldStack, RegistryManager.CASTER_ORB_AUGMENT);
					UUID uuid = player.getUUID();
					if (level > 0 && EmberInventoryUtil.getEmberTotal(player) > RegistryManager.CASTER_ORB_AUGMENT.getCost() && !CasterOrbAugment.hasCooldown(uuid)) {
						float handmod = player.getMainArm() == HumanoidArm.RIGHT ? 1.0f : -1.0f;
						float offX = handmod * 0.5f * (float) Math.sin(Math.toRadians(-player.getYHeadRot() - 90));
						float offZ = handmod * 0.5f * (float) Math.cos(Math.toRadians(-player.getYHeadRot() - 90));
						EmberInventoryUtil.removeEmber(player, RegistryManager.CASTER_ORB_AUGMENT.getCost());
						double lookDist = Math.sqrt(msg.lookX * msg.lookX + msg.lookY * msg.lookY + msg.lookZ * msg.lookZ);
						if (lookDist == 0)
							return;
						double xVel = (msg.lookX / lookDist) * 0.5;
						double yVel = (msg.lookY / lookDist) * 0.5;
						double zVel = (msg.lookZ / lookDist) * 0.5;
						double xOrigin = player.getX() + offX;
						double yOrigin = player.getY() + player.getEyeHeight();
						double zOrigin = player.getZ() + offZ;

						double resonance = EmbersAPI.getEmberResonance(heldStack);
						double value = 8.0 * (Math.atan(0.6 * (level)) / (1.25));
						value *= resonance;

						DamageSource damage = new DamageEmber(player.level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(EmbersDamageTypes.EMBER_KEY), player, true);
						EffectDamage effect = new EffectDamage((float) value, e -> damage, 1, 1.0);
						ProjectileFireball fireball = new ProjectileFireball(player, new Vec3(xOrigin, yOrigin, zOrigin), new Vec3(xVel, yVel, zVel), value, 160, effect);
						EmberProjectileEvent event = new EmberProjectileEvent(player, heldStack, 0.0, fireball);
						NeoForge.EVENT_BUS.post(event);
						if (!event.isCanceled()) {
							for (IProjectilePreset projectile : event.getProjectiles()) {
								projectile.shoot(player.level());
							}
						}
						player.level().playSound(null, xOrigin, yOrigin, zOrigin, EmbersSounds.FIREBALL.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
						CasterOrbAugment.setCooldown(uuid, 20);
					}
				}
			});
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
