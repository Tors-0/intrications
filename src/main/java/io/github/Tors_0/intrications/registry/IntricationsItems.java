package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import io.github.Tors_0.intrications.item.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public class IntricationsItems {
	public static final Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	// Item NAME = createItem("name", new ModItem(new QuiltItemSettings()));
	public static final Item TELEPORT_STAFF;
	public static final Item FIRE_STAFF;
	public static final Item SLIME_STAFF;
	public static final Item SPELLCASTING_STAFF;
	public static final Item MINELAYER_STAFF;
	public static final Item MINE;

	static {
		TELEPORT_STAFF =
			createItem("teleport_staff", new TeleportStaffItem(
				new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(96)));

		FIRE_STAFF =
			createItem("fire_staff", new FireStaffItem(
				new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(128)));

		SLIME_STAFF =
			createItem("slime_staff", new SlimeStaffItem(
				new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(128)));

		SPELLCASTING_STAFF =
			createItem("spellcasting_staff", new SpellcastingStaffItem(
				new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(96)));

		MINELAYER_STAFF =
			createItem("minelayer_staff", new MinelayerStaffItem(
				new QuiltItemSettings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(128)));

		MINE =
			createItem("mine", new MineItem(
				new QuiltItemSettings().group(ItemGroup.REDSTONE)));
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
