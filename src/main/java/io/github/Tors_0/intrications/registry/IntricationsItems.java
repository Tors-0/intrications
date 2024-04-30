package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntricationsItems {
	public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));
	public static final Item TELEPORT_STAFF;
	public static final Item SPELLCASTING_STAFF;
	public static final Item MINELAYER_STAFF;
	public static final Item MINE;

	static {
		TELEPORT_STAFF =
			createItem("teleport_staff", new TeleportStaffItem(
				new QuiltItemSettings().maxCount(1).maxDamage(96)));

		SPELLCASTING_STAFF =
			createItem("spellcasting_staff", new SpellcastingStaffItem(
				new QuiltItemSettings().maxCount(1).maxDamage(96)));

		MINELAYER_STAFF =
			createItem("minelayer_staff", new MinelayerStaffItem(
				new QuiltItemSettings().maxCount(1).maxDamage(128)));

		MINE =
			createItem("mine", new MineItem(
				new QuiltItemSettings()));
	}


	/**
	 * Creates a new item of type T using the constructor from T
	 * @param name String name of the item (used in JSON)
	 * @param item _Item class that extends net.minecraft.Item
	 * @return A newly created _Item object
	 * @param <T> _Item class that extends net.minecraft.Item
	 */
	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(Intrications.ID, name));
		return item;
	}

	/**
	 * Registers all items into the Minecraft Item Registry
	 */
	public static void register() {
		ITEMS.keySet().forEach(item -> Registry.register(Registries.ITEM, ITEMS.get(item), item));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register(entries -> {
			entries.addItem(TELEPORT_STAFF);
			entries.addItem(SPELLCASTING_STAFF);
			entries.addItem(MINELAYER_STAFF);
			entries.addItem(MINE);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE_BLOCKS).register(entries -> {
			entries.addAfter(Items.SLIME_BLOCK, IntricationsBlocks.AIRY_SLIME.asItem());
		});
	}
}
