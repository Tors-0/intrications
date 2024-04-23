package io.github.Tors_0.intrications.entity.render;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.entity.MagicMissileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.quiltmc.loader.api.minecraft.ClientOnly;

@ClientOnly
public class MagicMissileEntityRenderer extends ProjectileEntityRenderer<MagicMissileEntity> {
	public static final Identifier TEXTURE = Intrications.getIdentifier("textures/entity/projectiles/magic_missile.png");
	public MagicMissileEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(MagicMissileEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		Vec3d backPos = entity.getPos()
			.subtract(entity.getVelocity().normalize());
		entity.world.addParticle(ParticleTypes.END_ROD,
			backPos.x, backPos.y, backPos.z,
			-entity.getVelocity().x, -entity.getVelocity().y, -entity.getVelocity().z);
		super.render(entity, f, g, matrixStack, vertexConsumerProvider, 255);
	}

	@Override
	public Identifier getTexture(MagicMissileEntity missileEntity) {
		return TEXTURE;
	}
}
