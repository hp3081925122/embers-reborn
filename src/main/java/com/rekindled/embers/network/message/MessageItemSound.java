package com.rekindled.embers.network.message;

import org.apache.commons.lang3.Validate;

import com.rekindled.embers.datagen.EmbersSounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageItemSound implements CustomPacketPayload {

	private final int id;
	private final Item item;
	private final SoundEvent sound;
	private final SoundSource source;
	private final boolean repeat;
	private final float volume;
	private final float pitch;

	public static final CustomPacketPayload.Type<MessageItemSound> TYPE =
		new CustomPacketPayload.Type<>(Identifier.parse("embers_reborn:item_sound"));

	public static final StreamCodec<FriendlyByteBuf, MessageItemSound> STREAM_CODEC =
		StreamCodec.ofMember(MessageItemSound::write, MessageItemSound::read);

	public MessageItemSound(Entity entity, Item item, SoundEvent sound, SoundSource source, boolean repeat, float volume, float pitch) {
		this(entity.getId(), item, sound, source, repeat, volume, pitch);
	}

	public MessageItemSound(int id, Item item, SoundEvent sound, SoundSource source, boolean repeat, float volume, float pitch) {
		Validate.notNull(sound, "sound");
		this.item = item;
		this.sound = sound;
		this.source = source;
		this.id = id;
		this.repeat = repeat;
		this.volume = volume;
		this.pitch = pitch;
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(id);
		buf.writeIdentifier(BuiltInRegistries.ITEM.getKey(item));
		buf.writeIdentifier(BuiltInRegistries.SOUND_EVENT.getKey(sound));
		buf.writeEnum(source);
		buf.writeBoolean(repeat);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
	}

	public static MessageItemSound read(FriendlyByteBuf buf) {
		int id = buf.readVarInt();
		Item item = BuiltInRegistries.ITEM.getValue(buf.readIdentifier());
		SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(buf.readIdentifier());
		SoundSource source = buf.readEnum(SoundSource.class);
		boolean repeat = buf.readBoolean();
		float volume = buf.readFloat();
		float pitch = buf.readFloat();
		return new MessageItemSound(id, item, sound, source, repeat, volume, pitch);
	}

	@SuppressWarnings("resource")
	public static void handle(MessageItemSound msg, IPayloadContext ctx) {
		if (ctx.flow().isClientbound()) {
			ctx.enqueueWork(() -> {
				if (com.rekindled.embers.client.ClientAccess.level().getEntity(msg.id) instanceof LivingEntity entity)
					EmbersSounds.playItemSoundClient(entity, msg.item, msg.sound, msg.source, msg.repeat, msg.volume, msg.pitch);
			});
		}
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
