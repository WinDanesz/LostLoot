package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.block.BlockLootSceneDummy;
import com.windanesz.lostloot.block.BlockLostLoot;
import com.windanesz.lostloot.block.BlockLostLootMultiBlock;
import com.windanesz.lostloot.block.TileEntityLostLoot;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(LostLoot.MOD_ID)
@Mod.EventBusSubscriber
public class Items {

    private Items() {}


    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder() {
        return null;
    }

    // New method for ItemBlock registration
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        // Register ItemBlock for BlockLootScene
    }

    // Helper for registering ItemBlocks
    private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        registry.register(itemBlock);
    }

}
