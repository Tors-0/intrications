package io.github.Tors_0.intrications.entity.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.Tors_0.intrications.entity.MineEntity;
import io.github.Tors_0.intrications.entity.render.model.MineEntityModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import java.util.List;

@ClientOnly
public class MineEntityRenderer<T extends MineEntity, M extends MineEntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");
	private final M model;
	protected final List<FeatureRenderer<T, M>> features = Lists.<FeatureRenderer<T, M>>newArrayList();
	public MineEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		model = (M) new MineEntityModel<MineEntity>(context.getPart(MineEntityModel.LAYER_LOCATION));
		this.addFeature(new MineEyeFeatureRenderer<>(this));
	}

	@Override
	public void render(MineEntity mine, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = mine.getYaw();
		float m = mine.getPitch();

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0, -1.501F, 0.0);
		float n = 0.0F;
		float o = 0.0F;

		this.model.setAngles((T) mine, o, n, mine.age, h, m);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		boolean bl = !mine.isInvisible();
		boolean bl2 = !bl && !mine.isInvisibleTo(minecraftClient.player);
		boolean bl3 = minecraftClient.hasOutline(mine);
		RenderLayer renderLayer = RenderLayer.getEntitySolid(MineEntityModel.IDENTIFIER);
		if (renderLayer != null) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
			int p = OverlayTexture.getUv(0,false);
			this.model.render(matrixStack, vertexConsumer, i, p, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
		}

		if (!mine.isSpectator()) {
			for(FeatureRenderer<T, M> featureRenderer : this.features) {
				featureRenderer.render(matrixStack, vertexConsumerProvider, i, (T) mine, o, n, g, 0, h, m);
			}
		}

		matrixStack.pop();
		super.render((T) mine, f, g, matrixStack, vertexConsumerProvider, i);
	}
	protected final boolean addFeature(FeatureRenderer<T, M> feature) {
		return this.features.add(feature);
	}

	@Override
	public Identifier getTexture(T entity) {
		return TEXTURE;
	}

	@Override
	public M getModel() {
		return this.model;
	}
}
