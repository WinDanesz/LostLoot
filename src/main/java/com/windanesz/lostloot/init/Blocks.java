package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.block.BlockLostCargo;
import com.windanesz.lostloot.block.TileEntityLostCargo;
import net.minecraft.block.Block;
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

    public static final Block LOST_CARGO = placeholder();

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder() {
        return null;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registerBlock(registry, "lost_cargo", new BlockLostCargo());
    }

    public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block) {
        block.setRegistryName(LostLoot.MOD_ID, name);
        block.setTranslationKey(block.getRegistryName().toString());
        registry.register(block);
    }

    public static void registerTileEntities() {
        // Nope, these still don't have their own registry...
        GameRegistry.registerTileEntity(TileEntityLostCargo.class, new ResourceLocation(LostLoot.MOD_ID, "lost_cargo"));
    }
}
