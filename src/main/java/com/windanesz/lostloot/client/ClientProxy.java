package com.windanesz.lostloot.client;

import com.windanesz.lostloot.CommonProxy;
import com.windanesz.lostloot.init.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeColorHelper;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerColorHandlers() {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(
				(state, world, pos, tintIndex) -> {
					// Only apply for tintindex 0
					if (tintIndex == 0 && world != null && pos != null) {
						return BiomeColorHelper.getGrassColorAtPos(world, pos);
					}
					return 0xFFFFFF;
				},
				Blocks.skeleton_crate
		);

	}
}
