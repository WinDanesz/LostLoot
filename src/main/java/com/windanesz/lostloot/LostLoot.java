package com.windanesz.lostloot;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.windanesz.lostloot.capability.PlayerCapability;
import com.windanesz.lostloot.init.ModBlocks;
import com.windanesz.lostloot.init.ModLoot;
import com.windanesz.lostloot.network.PacketHandler;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.UUID;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LostLoot {

	/**
	 * title-cased modname
	 */
	public static final String MODNAME = "LostLoot";
	public static final String MODID = Tags.MOD_ID;
	public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
	public static Settings settings = new Settings();

	@SidedProxy(clientSide = "com.windanesz.lostloot.client.ClientProxy", serverSide = "com.windanesz.lostloot.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
		ModBlocks.registerTileEntities();
		ModLoot.register();
		PlayerCapability.register();
	}


	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new LLWorldGen(), 0);
		proxy.registerColorHandlers();
		PacketHandler.initPackets();
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSetHauntingProgress());
		event.registerServerCommand(new CommandGetHauntingProgress());
	}

}
