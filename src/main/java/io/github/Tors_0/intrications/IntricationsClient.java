package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.entity.render.MagicMissileEntityRenderer;
import io.github.Tors_0.intrications.entity.render.MineEntityRenderer;
import io.github.Tors_0.intrications.entity.render.model.MineEntityModel;
import io.github.Tors_0.intrications.item.TeleportStaffItem;
import io.github.Tors_0.intrications.registry.IntricationsBlocks;
import io.github.Tors_0.intrications.registry.IntricationsEntities;
import io.github.Tors_0.intrications.registry.IntricationsItems;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

@ClientOnly
public class IntricationsClient implements ClientModInitializer {
	static {
		// create model predicate providers
		ModelPredicateProviderRegistry.register(
			IntricationsItems.TELEPORT_STAFF,
			new Identifier("pull"),
			((itemStack, clientWorld, livingEntity, i) -> {
				if (livingEntity == null) {
					return 0f;
				} else {
					return livingEntity.getActiveItem() != itemStack ? 0f : TeleportStaffItem.getChargePercentage(itemStack, livingEntity.getItemUseTimeLeft());
				}
			})
		);
	}

	@Override
	public void onInitializeClient(ModContainer mod) {
		// register entity renderer for magic missile
		EntityRendererRegistry.register(IntricationsEntities.MAGIC_MISSILE, MagicMissileEntityRenderer::new);
		// and land mines
		EntityRendererRegistry.register(IntricationsEntities.MINE, MineEntityRenderer::new);
		// and slime balls
		EntityRendererRegistry.register(IntricationsEntities.SLIMEBALL, FlyingItemEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(MineEntityModel.LAYER_LOCATION, MineEntityModel::getTexturedModelData);

		BlockRenderLayerMap.put(RenderLayer.getTranslucent(), IntricationsBlocks.AIRY_SLIME);
	}
}
