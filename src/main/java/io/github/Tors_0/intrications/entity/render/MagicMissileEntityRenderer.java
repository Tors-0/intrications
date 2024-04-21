package io.github.Tors_0.intrications.entity.render;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.entity.MagicMissileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class MagicMissileEntityRenderer extends ProjectileEntityRenderer<MagicMissileEntity> {
	public static final Identifier TEXTURE = Intrications.getIdentifier("textures/entity/projectiles/magic_missile.png");
	public MagicMissileEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(MagicMissileEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(entity, f, g, matrixStack, vertexConsumerProvider, 255);
	}

	@Override
	public Identifier getTexture(MagicMissileEntity missileEntity) {
		return TEXTURE;
	}
}
