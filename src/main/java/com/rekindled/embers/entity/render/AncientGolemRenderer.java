package com.rekindled.embers.entity.render;

import com.rekindled.embers.Embers;
import com.rekindled.embers.entity.AncientGolemEntity;
import com.rekindled.embers.model.AncientGolemModel;
import com.rekindled.embers.model.AshenArmorModel;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@OnlyIn(Dist.CLIENT)
public class AncientGolemRenderer extends MobRenderer<AncientGolemEntity, LivingEntityRenderState, AncientGolemModel> {

	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Embers.MODID, "ancient_golem"), "main");
	public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Embers.MODID, "textures/entity/golem.png");

	public AncientGolemRenderer(EntityRendererProvider.Context context) {
		super(context, new AncientGolemModel(context.bakeLayer(LAYER_LOCATION)), 0.5f);
		this.addLayer(new AncientGolemEyeLayer(this));
		//I just need to get this context from somewhere
		AshenArmorModel.init(context);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTextureLocation(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
