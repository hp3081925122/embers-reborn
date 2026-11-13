package com.rekindled.embers.entity.render;

import com.rekindled.embers.Embers;
import com.rekindled.embers.model.AncientGolemModel;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class AncientGolemEyeLayer extends EyesLayer<LivingEntityRenderState, AncientGolemModel> {
	private static final RenderType GOLEM_EYE = net.minecraft.client.renderer.rendertype.RenderTypes.eyes(Identifier.fromNamespaceAndPath(Embers.MODID, "textures/entity/golem_overlay.png"));

	public AncientGolemEyeLayer(RenderLayerParent<LivingEntityRenderState, AncientGolemModel> p_117507_) {
		super(p_117507_);
	}

	public RenderType renderType() {
		return GOLEM_EYE;
	}
}
