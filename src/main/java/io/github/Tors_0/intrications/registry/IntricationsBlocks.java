package io.github.Tors_0.intrications.registry;

import io.github.Tors_0.intrications.Intrications;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.SlimeBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IntricationsBlocks {
	Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();

//	Block MOD_BLOCK = createBlock("mod_block", new ModBlock(QuiltBlockSettings.of(Material.SOME_MATERIAL, MapColor.SOME_COLOR).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.COPPER)), true);

	Block AIRY_SLIME = createBlock("airy_slime", new SlimeBlock(
		QuiltBlockSettings.of(Material.ORGANIC_PRODUCT, MapColor.PALE_GREEN).breakInstantly().slipperiness(0.8F).sounds(BlockSoundGroup.SLIME).nonOpaque()),
		true);

	static void initialize() {
		BLOCKS.keySet().forEach(block -> Registry.register(Registry.BLOCK, BLOCKS.get(block), block));
	}

	private static <T extends Block> T createBlock(String name, T block, boolean createItem) {
		BLOCKS.put(block, Intrications.getIdentifier(name));
		if (createItem) {
			IntricationsItems.ITEMS.put(new BlockItem(block, new QuiltItemSettings()), BLOCKS.get(block));
		}
		return block;
	}
}
