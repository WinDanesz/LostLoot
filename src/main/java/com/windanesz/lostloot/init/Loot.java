package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class Loot {

	private Loot() {
	}

	/**
	 * Called from the preInit method in the main mod class to register the custom dungeon loot.
	 */
	public static void register() {
		LootTableList.register(new ResourceLocation(LostLoot.MOD_ID, "chests/lost_cargo"));
		//LootTableList.register(new ResourceLocation(LostLoot.MOD_ID, "chests/skeleton_crate"));
	}
}