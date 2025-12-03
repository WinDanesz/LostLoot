package com.windanesz.lostloot.client.renderer;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.client.model.ModelGoblin;
import com.windanesz.lostloot.client.model.ModelSpecter;
import com.windanesz.lostloot.entity.EntityGoblin;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class RenderGoblin extends RenderBiped<EntityGoblin> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(LostLoot.MODID, "textures/entity/goblin.png");

	public RenderGoblin(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGoblin(), 0.2F);
		LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this)
		{
			protected void initArmor()
			{
				this.modelLeggings = new ModelGoblin();
				this.modelArmor = new ModelGoblin();
			}
		};
		this.addLayer(layerbipedarmor);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityGoblin entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityGoblin entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected void preRenderCallback(EntityGoblin entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	}
}