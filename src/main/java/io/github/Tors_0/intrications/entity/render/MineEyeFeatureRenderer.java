package io.github.Tors_0.intrications.entity.render;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.entity.MineEntity;
import io.github.Tors_0.intrications.entity.render.model.MineEntityModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;

public class MineEyeFeatureRenderer<T extends MineEntity, M extends MineEntityModel<T>> extends EyesFeatureRenderer<T, M> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(Intrications.getIdentifier("textures/entity/mine/mine_eye.png"));
	public MineEyeFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesLayer() {
		return SKIN;
	}
}
