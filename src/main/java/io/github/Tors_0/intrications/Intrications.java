package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.item.TeleportStaffItem;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intrications implements ModInitializer {
	public static final String ID = "intrications";
	public static final Logger LOGGER = LoggerFactory.getLogger(Intrications.ID);
	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Internal Intrications Initializing Immediately :3 (version {})", mod.metadata().version());

		// add mod items
		IntricationsItems.register();

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
}
