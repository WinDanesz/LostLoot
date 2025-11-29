package com.windanesz.lostloot.potion;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.Settings;
import com.windanesz.lostloot.client.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBliss extends Potion {

	private final ResourceLocation texture;

	public PotionBliss(String name, boolean isBadEffectIn, int liquidColorIn, ResourceLocation texture) {
		super(isBadEffectIn, liquidColorIn);
		this.setPotionName("potion." + LostLoot.MODID + ":" + name);
		this.texture = texture;

	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		int k = 50 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
	}

	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()) {
            // Use the value from settings for healing.
            entityLivingBaseIn.heal((float) Settings.worldgenSettings.bliss_healing_amount);
        }
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) {
		drawIcon(x + 6, y + 7, effect, mc);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc, float alpha) {
		net.minecraft.client.renderer.GlStateManager.color(1, 1, 1, alpha);
		drawIcon(x + 3, y + 3, effect, mc);
	}

	@SideOnly(Side.CLIENT)
	protected void drawIcon(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc) {
		mc.renderEngine.bindTexture(texture);
		Utils.drawTexturedRect(x, y, 0, 0, 18, 18, 18, 18);
	}
}
