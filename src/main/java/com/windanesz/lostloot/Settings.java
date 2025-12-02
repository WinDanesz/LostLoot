package com.windanesz.lostloot;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Config(modid = LostLoot.MODID, name = "LostLoot")
public class Settings {

    // These are set after config load, not part of config fields
    public List<ResourceLocation> lostCargoBiomeWhitelist = Arrays.asList(toResourceLocations(worldgenSettings.lostCargoBiomeWhitelist));
    public List<ResourceLocation> lostCargoBiomeBlacklist = Arrays.asList(toResourceLocations(worldgenSettings.lostCargoBiomeBlacklist));

    public static ResourceLocation[] toResourceLocations(String... strings) {
        return Arrays.stream(strings).filter(s -> s != null && !s.trim().isEmpty()).map(s -> new ResourceLocation(s.toLowerCase(Locale.ROOT).trim())).toArray(ResourceLocation[]::new);
    }


    @Config.Name("Worldgen Settings")
    @Config.LangKey("settings.lostloot:general_settings")
    public static WorldgenSettings worldgenSettings = new WorldgenSettings();

	@Config.Name("Misc Settings")
	@Config.LangKey("settings.lostloot:general_settings")
	public static MiscSettings miscSettings = new MiscSettings();

    @Config.Name("Client Settings")
    @Config.LangKey("settings.lostloot:client_settings")
    public static ClientSettings clientSettings = new ClientSettings();

    public static class WorldgenSettings {

        @Config.Name("Lost Loot Dimensions")
        @Config.Comment("[Server-only] List of dimension ids where loot spawns.")
        @Config.RequiresMcRestart
        public int[] dimensionList = {0};

        @Config.Name("Lost Cargo Frequency")
        @Config.Comment("How many Lost Cargo blocks to generate per chunk (default: 1)")
        public int lostCargoFrequency = 1;

        @Config.Name("Lost Cargo Biome Whitelist")
        @Config.Comment("Biomes where Lost Cargo can generate (empty = all biomes allowed)")
        public String[] lostCargoBiomeWhitelist = new String[0];

        @Config.Name("Lost Cargo Biome Blacklist")
        @Config.Comment("Biomes where Lost Cargo cannot generate")
        public String[] lostCargoBiomeBlacklist = new String[0];

        @Config.Name("Stone Circle Chance")
        @Config.Comment("Chance for a Stone Circle to generate in a chunk. 1 in X chance. Set to 0 to disable. Default: 1000")
        public int stoneCircleChance = 50;

        @Config.Name("Remains Chance")
        @Config.Comment("Chance for (skeletal) Remains to generate in a chunk. 1 in X chance. Set to 0 to disable. Default: 50")
        public int remainsChance = 50;
    }

    public static class MiscSettings {

		@Config.Name("Grave Rose Chance")
		@Config.Comment("The chance of a rose turning into a grave rose when it is placed in a grave marker.")
		@Config.RequiresMcRestart
		public double graveRoseChance = 0.05D;

		@Config.Name("Rune of Skimming Max Teleport Distance")
		@Config.Comment("Maximum distance (in blocks) the Rune of Skimming can teleport the player. Default: 150")
		public int runeOfSkimmingMaxDistance = 150;

		@Config.Name("Rune of Skimming Cooldown")
		@Config.Comment("Cooldown (in ticks) after using the Rune of Skimming. Default: 100 (5 seconds)")
		public int runeOfSkimmingCooldown = 100;

		@Config.Name("Bliss Healing Amount")
		@Config.Comment("The amount of health restored per bliss-tick (0.0 - 1.0, where 1.0 = is one heart).")
		public double blissHealingAmount = 0.33D;

		@Config.Name("Bliss Duration (ticks) for placing a flower on a grave")
		@Config.Comment("The duration of the bliss effect in ticks.")
		public double blissDurationForFlower = 3600;

		@Config.Name("Bliss Duration (ticks) for burying remains")
		@Config.Comment("The duration of the bliss effect in ticks.")
		public double blissDurationForBurying = 1600;

		@Config.Name("Haunting Gained by Breaking Remains")
		@Config.Comment("The amount of haunting gained by breaking remains.")
		public int hauntingGainedByBreakingRemains = 5;

		@Config.Name("Haunting Gained by Breaking Grave")
		@Config.Comment("The amount of haunting gained by breaking a grave.")
		public int hauntingGainedByBreakingGrave = 2;

		@Config.Name("Haunting Reduced by Burying Remains")
		@Config.Comment("The amount of haunting reduced by burying remains.")
		public int hauntingReducedByBuryingRemains = 5;

		@Config.Name("Haunting Reduced by Placing a Flower on a Grave")
		@Config.Comment("The amount of haunting reduced by placing a flower on a grave.")
		public int hauntingReducedByPlacingFlowerOnGrave = 2;
    }

    public static class ClientSettings {
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = LostLoot.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(LostLoot.MODID)) {
                ConfigManager.sync(LostLoot.MODID, Config.Type.INSTANCE);
            }
        }
    }
}