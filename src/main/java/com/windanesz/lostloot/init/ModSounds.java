package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(LostLoot.MODID)
@Mod.EventBusSubscriber(modid = LostLoot.MODID)
public class ModSounds {
	private ModSounds() {}

	public static final SoundEvent SPECTER_HURT = createSound("entity.specter_hurt");
	// public static final SoundEvent IDOL_ACTIVATE = createSound("entity.specter_hurt");

	public static SoundEvent createSound(String name) {
		return createSound(LostLoot.MODID, name);
	}
	/**
	 * Creates a sound with the given name, to be read from {@code assets/[modID]/sounds.json}.
	 */
	public static SoundEvent createSound(String modID, String name) {
		// All the setRegistryName methods delegate to this one, it doesn't matter which you use.
		return new SoundEvent(new ResourceLocation(modID, name)).setRegistryName(name);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(SPECTER_HURT);
	}
}