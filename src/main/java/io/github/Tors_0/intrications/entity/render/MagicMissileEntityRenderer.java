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
		// when the following code is uncommented, only a single particle spawns, then no more spawn
//		if (entity.world instanceof ClientWorld clientWorld) {
//			clientWorld.addParticle(ParticleTypes.END_ROD,
//				entity.getParticleX(0.5), entity.getRandomBodyY(), entity.getParticleZ(0.5),
//				0, 0, 0);
//		}
		super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(MagicMissileEntity missileEntity) {
		return TEXTURE;
	}
}
