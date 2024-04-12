package io.github.Tors_0.intrications;

import io.github.Tors_0.intrications.item.FireStaffItem;
import io.github.Tors_0.intrications.item.TeleportStaffItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IntricationsItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));
	Item TELEPORT_STAFF = createItem("teleport_staff", new TeleportStaffItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)));
	Item FIRE_STAFF = createItem("fire_staff", new FireStaffItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS)));

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
	static void register() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
		});
	}
}
