package com.windanesz.lostloot.init;

import com.windanesz.lostloot.LostLoot;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ModCreativeTab {
	public static final CreativeTabs lostLootTab = new CreativeTabs(LostLoot.MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.grave_rose);
		}
	};
}
