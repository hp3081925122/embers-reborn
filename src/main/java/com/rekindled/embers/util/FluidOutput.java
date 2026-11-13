package com.rekindled.embers.util;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public class FluidOutput {

	public static final com.mojang.serialization.Codec<FluidOutput> CODEC =
		com.mojang.serialization.Codec.either(
			// tag + amount form
			com.mojang.serialization.codecs.RecordCodecBuilder.<FluidOutput>create(inst -> inst.group(
				net.minecraft.resources.Identifier.CODEC.fieldOf("tag").forGetter(o -> o.tag.location()),
				com.mojang.serialization.Codec.INT.optionalFieldOf("amount", 1).forGetter(o -> o.amount)
			).apply(inst, (tag, amt) -> new FluidOutput(net.minecraft.tags.FluidTags.create(tag), amt))),
			// fluid + amount form (FluidStack-like)
			FluidStackTemplate.CODEC.xmap(FluidOutput::new, o -> o.template))
		.xmap(e -> e.map(o -> o, o -> o),
			o -> o.tag != null ? com.mojang.datafixers.util.Either.left(o) : com.mojang.datafixers.util.Either.right(o));

	public static final net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, FluidOutput> STREAM_CODEC =
		net.minecraft.network.codec.StreamCodec.of(
			(buf, o) -> o.toNetwork(buf),
			buf -> FluidOutput.fromNetwork(buf));

	public static final FluidOutput EMPTY = new FluidOutput(net.minecraft.tags.FluidTags.create(net.minecraft.resources.Identifier.withDefaultNamespace("empty")), -1);

	public FluidStackTemplate template;
	public TagKey<Fluid> tag;
	public int amount = 0;
	private FluidStack stack;

	public FluidOutput(FluidStackTemplate template) {
		this.template = template;
	}

	public FluidOutput(Fluid fluid, int amount) {
		this(new FluidStackTemplate(fluid, amount));
	}

	public FluidOutput(TagKey<Fluid> tag, int amount) {
		this.tag = tag;
		this.amount = amount;
	}

	public boolean isEmpty() {
		return amount < 0;
	}

	public FluidStack getStack() {
		if (stack == null)
			stack = template != null ? template.create() : new FluidStack(Misc.getTaggedFluid(tag), amount);
		return stack;
	}

	public static FluidOutput fromJson(JsonObject json) {
		if (json.has("tag")) {
			return new FluidOutput(FluidTags.create(Identifier.parse(GsonHelper.getAsString(json, "tag"))), GsonHelper.getAsInt(json, "amount", 1));
		}
		return new FluidOutput(FluidStackTemplate.fromNonEmptyStack(Misc.deserializeFluidStack(json)));
	}

	public JsonObject toJson() {
		if (tag != null) {
			JsonObject json = new JsonObject();
			json.addProperty("tag", tag.location().toString());
			json.addProperty("amount", amount);
			return json;
		}
		return Misc.serializeFluidStack(getStack());
	}

	public static FluidOutput fromNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			return new FluidOutput(FluidTags.create(buffer.readIdentifier()), buffer.readInt());
		}
		return new FluidOutput(FluidStackTemplate.STREAM_CODEC.decode(buffer));
	}

	public void toNetwork(net.minecraft.network.RegistryFriendlyByteBuf buffer) {
		if (tag != null) {
			buffer.writeBoolean(true);
			buffer.writeIdentifier(tag.location());
			buffer.writeInt(amount);
		} else {
			buffer.writeBoolean(false);
			FluidStackTemplate.STREAM_CODEC.encode(buffer, template);
		}
	}
}
