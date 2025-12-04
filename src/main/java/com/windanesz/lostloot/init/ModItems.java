package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.item.ItemGoblinIdol;
import com.windanesz.lostloot.item.ItemModPainting;
import com.windanesz.lostloot.item.ItemGraveRose;
import com.windanesz.lostloot.item.ItemRuneOfSkimming;
import net.minecraft.block.Block;
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
public class ModItems {

	private ModItems() {
	}

	public static final Item grave_rose = placeholder();
	public static final Item rune_of_skimming = placeholder();
	public static final Item goblin_idol = placeholder();

	public static final Item painting_in_the_woods = placeholder();
	public static final Item painting_portrait = placeholder();
	public static final Item painting_the_bloodcurling = placeholder();
	public static final Item painting_wheel = placeholder();
	public static final Item painting_wizardry = placeholder();
	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() {
		return null;
	}

	// New method for ItemBlock registration
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registerItem(registry, "grave_rose", new ItemGraveRose(ModBlocks.grave_rose).setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "rune_of_skimming", new ItemRuneOfSkimming().setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "goblin_idol", new ItemGoblinIdol().setCreativeTab(ModCreativeTab.lostLootTab));

		registerItem(registry, "painting_in_the_woods", new ItemModPainting(ItemModPainting.EnumPainting.PAINTING_IN_THE_WOODS).setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "painting_portrait", new ItemModPainting(ItemModPainting.EnumPainting.PAINTING_PORTRAIT).setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "painting_the_bloodcurling", new ItemModPainting(ItemModPainting.EnumPainting.PAINTING_THE_BLOODCURLING).setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "painting_wheel", new ItemModPainting(ItemModPainting.EnumPainting.PAINTING_WHEEL).setCreativeTab(ModCreativeTab.lostLootTab));
		registerItem(registry, "painting_wizardry", new ItemModPainting(ItemModPainting.EnumPainting.PAINTING_WIZARDRY).setCreativeTab(ModCreativeTab.lostLootTab));

		// Register ItemBlocks
		registerItemBlock(registry, ModBlocks.lost_cargo);
		registerItemBlock(registry, ModBlocks.skeleton_crate);
		registerItemBlock(registry, ModBlocks.lost_crate_potions);
		registerItemBlock(registry, ModBlocks.bush_crate);
		registerItemBlock(registry, ModBlocks.stone_circle);
		registerItemBlock(registry, ModBlocks.grave_marker);
		registerItemBlock(registry, ModBlocks.rose);
		registerItemBlock(registry, ModBlocks.tent);
		registerItemBlock(registry, ModBlocks.tent_abandoned);
		registerItemBlock(registry, ModBlocks.tent_abandoned_idol);
	}

	// Helper for registering ItemBlocks
	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		ItemBlock itemBlock = new ItemBlock(block);
		itemBlock.setRegistryName(block.getRegistryName());
		block.setCreativeTab(ModCreativeTab.lostLootTab);
		registry.register(itemBlock);
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
