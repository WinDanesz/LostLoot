package com.windanesz.lostloot.client;

import com.windanesz.lostloot.CommonProxy;
import com.windanesz.lostloot.client.renderer.RenderSpecter;
import com.windanesz.lostloot.entity.EntitySpecter;
import com.windanesz.lostloot.init.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerEntityRenderers();

	}

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
				Blocks.skeleton_crate,
				Blocks.bush_crate
		);
	}

	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecter.class, RenderSpecter::new);
	}
}
