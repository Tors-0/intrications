package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.IntricationsConfig;
import io.github.Tors_0.intrications.item.FireStaffItem;
import io.github.Tors_0.intrications.item.SlimeStaffItem;
import io.github.Tors_0.intrications.item.TeleportStaffItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.config.api.Config;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntricationsItems {
	public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));
	public static final Item TELEPORT_STAFF;
	public static final Item FIRE_STAFF;
	public static final Item SLIME_STAFF;

	static {
		TELEPORT_STAFF =
			IntricationsConfig.INSTANCE.teleportStaffEnabled.value() ?
				createItem("teleport_staff", new TeleportStaffItem(new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(96)))
				: null;

		FIRE_STAFF =
			IntricationsConfig.INSTANCE.fireStaffEnabled.value() ?
				createItem("fire_staff", new FireStaffItem(
					new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(128)))
				: null;

		SLIME_STAFF =
			IntricationsConfig.INSTANCE.slimeStaffEnabled.value() ?
				createItem("slime_staff", new SlimeStaffItem(
					new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(96)))
				: null;
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
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
		});
	}
}
