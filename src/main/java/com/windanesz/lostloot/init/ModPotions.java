package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.potion.PotionBliss;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(LostLoot.MODID)
@Mod.EventBusSubscriber
public class ModPotions {

	public static final Potion bliss = placeholder();

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() {
		return null;
	}

	public static void registerPotion(IForgeRegistry<Potion> registry, String name, Potion potion) {
		potion.setRegistryName(LostLoot.MODID, name);
		potion.setPotionName("potion." + potion.getRegistryName().toString());
		registry.register(potion);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Potion> event) {

		IForgeRegistry<Potion> registry = event.getRegistry();
		registerPotion(registry, "bliss", new PotionBliss("bliss",false, 0xf799e9, new ResourceLocation(LostLoot.MODID, "textures/gui/potion_bliss.png")));


		// Interestingly, setting the colour to black stops th e particles from rendering.
	}
}