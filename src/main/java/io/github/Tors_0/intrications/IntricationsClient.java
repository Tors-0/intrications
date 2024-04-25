package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.entity.render.MagicMissileEntityRenderer;
import io.github.Tors_0.intrications.entity.render.MineEntityRenderer;
import io.github.Tors_0.intrications.entity.render.model.MineEntityModel;
import io.github.Tors_0.intrications.item.TeleportStaffItem;
import io.github.Tors_0.intrications.registry.IntricationsEntities;
import io.github.Tors_0.intrications.registry.IntricationsItems;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

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

		EntityModelLayerRegistry.registerModelLayer(MineEntityModel.LAYER_LOCATION, MineEntityModel::getTexturedModelData);
	}
}
