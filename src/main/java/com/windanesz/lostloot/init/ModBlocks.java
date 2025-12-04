package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.block.*;
import com.windanesz.lostloot.block.TileEntityGraveMarker;
import com.windanesz.lostloot.block.TileEntityLostLoot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(LostLoot.MODID)
@Mod.EventBusSubscriber
public class ModBlocks {

	private ModBlocks() {
	}

	public static final Block lost_cargo = placeholder();
	public static final Block skeleton_crate = placeholder();
	public static final Block lost_crate_potions = placeholder();
	public static final Block bush_crate = placeholder();
	public static final Block loot_scene_dummy = placeholder();
	public static final Block stone_circle = placeholder();
	public static final Block rose = placeholder();
	public static final Block grave_rose = placeholder();
	public static final Block grave_marker = placeholder();
	public static final Block forest_painting = placeholder();
	public static final Block tent = placeholder();
	public static final Block tent_abandoned = placeholder();
	public static final Block tent_abandoned_idol = placeholder();
	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() {
		return null;
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();
		registerBlock(registry, "lost_cargo", new BlockLostLoot(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MODID, "chests/lost_cargo")));
		registerBlock(registry, "lost_crate_potions", new BlockLostLoot(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MODID, "chests/lost_cargo")));
		registerBlock(registry, "skeleton_crate", new BlockRemains(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MODID, "chests/lost_cargo")));
		registerBlock(registry, "bush_crate", new BlockLostLootMultiBlock(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MODID, "chests/lost_cargo")));
		//registerBlock(registry, "loot_scene_dummy", new BlockLootSceneDummy(Material.IRON));
		registerBlock(registry, "stone_circle", new BlockStoneCircle(Material.ROCK).setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 0.1, 1)));
		registerBlock(registry, "grave_marker", new BlockGraveMarker(Material.ROCK).setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 0.4, 1)).setLootTable(new ResourceLocation(LostLoot.MODID, "chests/lost_cargo")));
		registerBlock(registry, "rose", new BlockRose().setCreativeTab(ModCreativeTab.lostLootTab));
		registerBlock(registry, "grave_rose", new BlockRose().setCreativeTab(ModCreativeTab.lostLootTab));
		registerBlock(registry, "tent", new BlockTent(Material.WOOD).setCreativeTab(ModCreativeTab.lostLootTab));
		registerBlock(registry, "tent_abandoned", new BlockTent(Material.WOOD).setCreativeTab(ModCreativeTab.lostLootTab));
		registerBlock(registry, "tent_abandoned_idol", new BlockTent(Material.WOOD).setCreativeTab(ModCreativeTab.lostLootTab));

	}

	public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block) {
		block.setRegistryName(LostLoot.MODID, name);
		block.setTranslationKey(block.getRegistryName().toString());
		registry.register(block);
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().isRemote) {
			return;
		}

		if (event.getHand() != EnumHand.MAIN_HAND) {
			return;
		}

		ItemStack heldItem = event.getItemStack();
		if (!heldItem.isEmpty() && heldItem.getItem() == Items.SHEARS) {
			net.minecraft.block.state.IBlockState state = event.getWorld().getBlockState(event.getPos());
			if (state.getBlock() == Blocks.DOUBLE_PLANT && state.getValue(BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.ROSE) {
				// Drop two red flowers
				Block.spawnAsEntity(event.getWorld(), event.getPos(), new ItemStack(ModBlocks.rose, 2));
				heldItem.damageItem(2, event.getEntityPlayer());
				event.getWorld().setBlockState(event.getPos(), Blocks.AIR.getDefaultState(), 3);
				event.setCanceled(true);
			}
		}
	}

	public static void registerTileEntities() {
		// Nope, these still don't have their own registry...
		GameRegistry.registerTileEntity(TileEntityLostLoot.class, new ResourceLocation(LostLoot.MODID, "lost_loot"));
		GameRegistry.registerTileEntity(TileEntityGraveMarker.class, new ResourceLocation(LostLoot.MODID, "grave_marker"));
		GameRegistry.registerTileEntity(TileEntityRemains.class, new ResourceLocation(LostLoot.MODID, "remains"));
		GameRegistry.registerTileEntity(TileEntityStoneCircle.class, new ResourceLocation(LostLoot.MODID, "stone_circle"));
	}
}
