package com.windanesz.lostloot.client;

import com.windanesz.lostloot.CommonProxy;
import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.block.tile.TileEntityGraveMarker;
import com.windanesz.lostloot.client.renderer.RenderSpecter;
import com.windanesz.lostloot.client.renderer.TileEntityGraveMarkerRenderer;
import com.windanesz.lostloot.entity.EntitySpecter;
import com.windanesz.lostloot.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		registerEntityRenderers();
		registerTileEntityRenderers();
	}

	@Override
	public void registerColorHandlers() {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
			// Only apply for tintindex 0
			if (tintIndex == 0 && world != null && pos != null) {
				return BiomeColorHelper.getGrassColorAtPos(world, pos);
			}
			return 0xFFFFFF;
		}, ModBlocks.skeleton_crate, ModBlocks.bush_crate);
	}

	@SubscribeEvent
	public static void registerItemModels(ModelRegistryEvent event) {

		for (Item item : Item.REGISTRY) {
			if (item.getRegistryName().getNamespace().equals(LostLoot.MODID)) {
				registerItemModel(item); // Standard item model
			}
		}
	}

	private void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySpecter.class, RenderSpecter::new);
	}

	private void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGraveMarker.class, new TileEntityGraveMarkerRenderer());
	}

	/**
	 * Registers an item model, using the item's registry name as the model name (this convention makes it easier to
	 * keep track of everything). Variant defaults to "normal". Registers the model for all metadata values.
	 * Author: Electroblob
	 */
	private static void registerItemModel(Item item) {
		// Changing the last parameter from null to "inventory" fixed the item/block model weirdness. No idea why!
		ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		// Assigns the model for all metadata values
		ModelLoader.setCustomMeshDefinition(item, s -> new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
