package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.registry.IntricationsAdvancements;
import io.github.Tors_0.intrications.registry.IntricationsEntities;
import io.github.Tors_0.intrications.registry.IntricationsItems;
import io.github.Tors_0.intrications.registry.IntricationsRecipeConditions;
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
		LOGGER.info("Initializing Intrications Internals Immediately :3 (version {})", mod.metadata().version());

		// add mod items
		IntricationsItems.register();

		// let us remove recipes if items are disabled in config
		IntricationsRecipeConditions.register();

		IntricationsEntities.initialize();

		// create custom triggers for advancements
		IntricationsAdvancements.register();
	}

	public static Identifier asResource(String id) {
		return new Identifier(ID, id);
	}
}
