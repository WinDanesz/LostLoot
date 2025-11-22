package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(LostLoot.MODID)
@Mod.EventBusSubscriber
public class Items {

	private Items() {
	}

	public static final Item grave_rose = placeholder();

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() {
		return null;
	}

	// New method for ItemBlock registration
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registerItem(registry, "grave_rose", new Item());
		registerItemBlock(registry, ModBlocks.rose);
		// Register ItemBlock for BlockLootScene
	}

	// Helper for registering ItemBlocks
	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		registry.register(itemBlock);
		itemBlock.setCreativeTab(CreativeTabs.DECORATIONS);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(LostLoot.MODID, name);
		item.setTranslationKey(item.getRegistryName().toString());
		registry.register(item);
	}

}
