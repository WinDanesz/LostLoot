package com.windanesz.lostloot;

import com.windanesz.lostloot.init.Blocks;
import com.windanesz.lostloot.init.Loot;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LostLoot {

    public static final String MOD_ID = Tags.MOD_ID;
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static Settings settings = new Settings();

	@SidedProxy(clientSide = "com.windanesz.lostloot.client.ClientProxy", serverSide = "com.windanesz.lostloot.CommonProxy")
	public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        Blocks.registerTileEntities();
        Loot.register();
	}


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new LLWorldGen(), 0);
		proxy.registerColorHandlers();
    }
}
