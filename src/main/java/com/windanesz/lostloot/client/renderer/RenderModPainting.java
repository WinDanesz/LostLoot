package com.windanesz.lostloot.client.renderer;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.entity.EntityModPainting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderModPainting extends Render<EntityModPainting> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(LostLoot.MODID, "textures/blocks/paintings.png");
	private float rotation = -1f;

	public RenderModPainting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityModPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (rotation == -1) {
			//entity.getDataManager().get(ROTATION)
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0F - entity.getRotation(), 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.bindEntityTexture(entity);
		//EntityPainting.EnumArt entitypainting$enumart = entity.art;
		float f = 0.0625F;
		GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);

		if (this.renderOutlines)
		{
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		this.renderPainting(entity, 32, 32, 0, 0,0);

		if (this.renderOutlines)
		{
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityModPainting entity) {
		return TEXTURE;
	}

	private void renderPainting(EntityModPainting painting, float width, float height, float textureU, float textureV, float rotationYaw) {
		float canvasSize = 128f;
		float f = (float)(-width) / 2.0F;
		float f1 = (float)(-height) / 2.0F;
		float f2 = 0.5F;
		float f3 = 0.75F;
		float f4 = 0.8125F;
		float f5 = 0.0F;
		float f6 = 0.0625F;
		float f7 = 0.75F;
		float f8 = 0.8125F;
		float f9 = 0.001953125F;
		float f10 = 0.001953125F;
		float f11 = 0.7519531F;
		float f12 = 0.7519531F;
		float f13 = 0.0F;
		float f14 = 0.0625F;

		for (int i = 0; i < width / 16; ++i)
		{
			for (int j = 0; j < height / 16; ++j)
			{
				float f15 = f + (float)((i + 1) * 16);
				float f16 = f + (float)(i * 16);
				float f17 = f1 + (float)((j + 1) * 16);
				float f18 = f1 + (float)(j * 16);
				//this.setLightmap(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
				float f19 = (float)(textureU + width - i * 16) / canvasSize;
				float f20 = (float)(textureU + width - (i + 1) * 16) / canvasSize;
				float f21 = (float)(textureV + height - j * 16) / canvasSize;
				float f22 = (float)(textureV + height - (j + 1) * 16) / canvasSize;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
				bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex((double)f20, (double)f21).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex((double)f19, (double)f21).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex((double)f19, (double)f22).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex((double)f20, (double)f22).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex(0.75D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex(0.8125D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex(0.8125D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex(0.75D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, 0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f18, 0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f18, -0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f15, (double)f17, -0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, -0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, -0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f18, 0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double)f16, (double)f17, 0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
				tessellator.draw();
			}
		}
	}
}