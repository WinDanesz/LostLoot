package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.block.*;
import com.windanesz.lostloot.block.tile.TileEntityGraveMarker;
import com.windanesz.lostloot.block.tile.TileEntityLostLoot;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(LostLoot.MOD_ID)
@Mod.EventBusSubscriber
public class Blocks {

    private Blocks() {}

    public static final Block lost_cargo = placeholder();
    public static final Block skeleton_crate = placeholder();
    public static final Block lost_crate_potions = placeholder();
    public static final Block bush_crate = placeholder();
    public static final Block loot_scene_dummy = placeholder();
    public static final Block stone_circle = placeholder();

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder() {
        return null;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerBlock(registry, "lost_cargo", new BlockLostLoot(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MOD_ID, "chests/lost_cargo")));
        registerBlock(registry, "skeleton_crate", new BlockLostLoot(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MOD_ID, "chests/lost_cargo")));
        registerBlock(registry, "lost_crate_potions", new BlockLostLoot(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MOD_ID, "chests/lost_cargo")));
        registerBlock(registry, "bush_crate", new BlockLostLootMultiBlock(Material.WOOD).setLootTable(new ResourceLocation(LostLoot.MOD_ID, "chests/lost_cargo")));
        //registerBlock(registry, "loot_scene_dummy", new BlockLootSceneDummy(Material.IRON));
        registerBlock(registry, "stone_circle", new BlockLostLoot(Material.ROCK).setBoundingBox(new AxisAlignedBB(0,0,0,1,0.1,1)));
        registerBlock(registry, "grave_marker", new BlockGraveMarker(Material.ROCK).setBoundingBox(new AxisAlignedBB(0,0,0,1,0.4,1)));
    }

    public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block) {
        block.setRegistryName(LostLoot.MOD_ID, name);
        block.setTranslationKey(block.getRegistryName().toString());
        registry.register(block);
    }

    public static void registerTileEntities() {
        // Nope, these still don't have their own registry...
        GameRegistry.registerTileEntity(TileEntityLostLoot.class, new ResourceLocation(LostLoot.MOD_ID, "lost_loot"));
        GameRegistry.registerTileEntity(TileEntityGraveMarker.class, new ResourceLocation(LostLoot.MOD_ID, "grave_marker"));
    }
}
