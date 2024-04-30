package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.registry.*;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
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

		// add mod blocks
		IntricationsBlocks.initialize();
		// add mod items
		IntricationsItems.register();

		// let us remove recipes if items are disabled in config
		IntricationsRecipeConditions.register();

		IntricationsEntities.initialize();

		IntricationsDispenserBehavior.register();

		// create custom triggers for advancements
		IntricationsAdvancements.register();

		// put a mine in the jungle temple, why don't you
		LootTableEvents.MODIFY.register(((resourceManager, lootManager, id, tableBuilder, source) -> {
			if (source.isBuiltin() && LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.builder()
					.with(ItemEntry.builder(IntricationsItems.MINE));
				tableBuilder.pool(poolBuilder);
			}
		}));

		IntricationsItemCallbacks.register();
	}

	public static Identifier getIdentifier(String id) {
		return new Identifier(ID, id);
	}
}
