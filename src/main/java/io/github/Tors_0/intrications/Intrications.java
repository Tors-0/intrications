package io.github.Tors_0.intrications;

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

		IntricationsItems.register();
	}
}
