package com.windanesz.lostloot.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.entity.EntityModPainting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class RenderModPainting extends Render<EntityModPainting> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(LostLoot.MODID, "textures/blocks/paintings.png");

	public RenderModPainting(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityModPainting painting, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0F - painting.getRotation(), 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		this.bindEntityTexture(painting);
		//	GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		//	GlStateManager.translate(0, -2, 0);
		float f = 0.0625F;
		GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(painting));
		}

		int hauntingProgress = painting.getHauntingProgress();
		int textureV = 0;
		if (hauntingProgress >= 70) {
			textureV = 96;
		} else if (hauntingProgress >= 40) {
			textureV = 64;
		} else if (hauntingProgress >= 20) {
			textureV = 32;
		}
		this.renderPainting(painting, 32, 32, 0, textureV);

		// Render player torso and head
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -0.01); //Slightly offset to prevent z-fighting
		GlStateManager.translate(0, -10, 0); // Move down by 16 pixels
		ResourceLocation skin = DefaultPlayerSkin.getDefaultSkinLegacy();
		//ResourceLocation skin = Minecraft.getMinecraft().player.getLocationSkin();
		bindTexture(skin);
		GameProfile profile = painting.getPlayerProfile();
		if (profile != null) {
			Minecraft minecraft = Minecraft.getMinecraft();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				skin = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
			} else {
				UUID uuid = EntityPlayer.getUUID(profile);
				skin = DefaultPlayerSkin.getDefaultSkin(uuid);
			}
		}

		this.bindTexture(skin);
		renderPlayerSkin(painting);
		GlStateManager.popMatrix();


		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.doRender(painting, x, y, z, entityYaw, partialTicks);
	}




	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityModPainting entity) {
		return TEXTURE;
	}

	private void renderPainting(EntityModPainting painting, int width, int height, int textureU, int textureV) {
		float canvasSize = 128f;
		float f = (float) (-width) / 2.0F;
		float f1 = (float) (-height) / 2.0F;
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

		for (int i = 0; i < width / 16; ++i) {
			for (int j = 0; j < height / 16; ++j) {
				float f15 = f + (float) ((i + 1) * 16);
				float f16 = f + (float) (i * 16);
				float f17 = f1 + (float) ((j + 1) * 16);
				float f18 = f1 + (float) (j * 16);
				//this.setLightmap(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
				float f19 = (float) (textureU + width - i * 16) / canvasSize;
				float f20 = (float) (textureU + width - (i + 1) * 16) / canvasSize;
				float f21 = (float) (textureV + height - j * 16) / canvasSize;
				float f22 = (float) (textureV + height - (j + 1) * 16) / canvasSize;
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
				bufferbuilder.pos((double) f15, (double) f18, -0.5D).tex((double) f20, (double) f21).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, -0.5D).tex((double) f19, (double) f21).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, -0.5D).tex((double) f19, (double) f22).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, -0.5D).tex((double) f20, (double) f22).normal(0.0F, 0.0F, -1.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, 0.5D).tex(0.75D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, 0.5D).tex(0.8125D, 0.0D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, 0.5D).tex(0.8125D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f18, 0.5D).tex(0.75D, 0.0625D).normal(0.0F, 0.0F, 1.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, 1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f18, 0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, 0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, -0.5D).tex(0.8125D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f18, -0.5D).tex(0.75D, 0.001953125D).normal(0.0F, -1.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, 0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f18, 0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f18, -0.5D).tex(0.751953125D, 0.0625D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f15, (double) f17, -0.5D).tex(0.751953125D, 0.0D).normal(-1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, -0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, -0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f18, 0.5D).tex(0.751953125D, 0.0625D).normal(1.0F, 0.0F, 0.0F).endVertex();
				bufferbuilder.pos((double) f16, (double) f17, 0.5D).tex(0.751953125D, 0.0D).normal(1.0F, 0.0F, 0.0F).endVertex();
				tessellator.draw();
			}
		}
	}

	private void renderPlayerSkin(EntityModPainting painting) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		float scale = 1.0f;
		float headSize = 8 * scale;
		float bodyWidth = 8 * scale;
		float bodyHeight = 10 * scale;
		float armWidth = 4 * scale;
		float overlayScale = 1.0f;

		float x0 = -bodyWidth / 2;
		float x1 = bodyWidth / 2;

		float bodyY0 = -bodyHeight / 2;
		float bodyY1 = bodyHeight / 2;

		float headY0 = bodyY1;
		float headY1 = bodyY1 + headSize;

		float skinWidth = 64f;
		float skinHeight = 64f;

		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		// Body
		bufferbuilder.pos(x1, bodyY0, -0.5D).tex(28 / skinWidth, 30 / skinHeight).endVertex();
		bufferbuilder.pos(x0, bodyY0, -0.5D).tex(20 / skinWidth, 30 / skinHeight).endVertex();
		bufferbuilder.pos(x0, bodyY1, -0.5D).tex(20 / skinWidth, 20 / skinHeight).endVertex();
		bufferbuilder.pos(x1, bodyY1, -0.5D).tex(28 / skinWidth, 20 / skinHeight).endVertex();

		// Right Arm
		bufferbuilder.pos(x0, bodyY0, -0.5D).tex(48 / skinWidth, 30 / skinHeight).endVertex();
		bufferbuilder.pos(x0 - armWidth, bodyY0, -0.5D).tex(44 / skinWidth, 30 / skinHeight).endVertex();
		bufferbuilder.pos(x0 - armWidth, bodyY1, -0.5D).tex(44 / skinWidth, 20 / skinHeight).endVertex();
		bufferbuilder.pos(x0, bodyY1, -0.5D).tex(48 / skinWidth, 20 / skinHeight).endVertex();

		// Left Arm
		bufferbuilder.pos(x1 + armWidth, bodyY0, -0.5D).tex(40 / skinWidth, 62 / skinHeight).endVertex();
		bufferbuilder.pos(x1, bodyY0, -0.5D).tex(36 / skinWidth, 62 / skinHeight).endVertex();
		bufferbuilder.pos(x1, bodyY1, -0.5D).tex(36 / skinWidth, 52 / skinHeight).endVertex();
		bufferbuilder.pos(x1 + armWidth, bodyY1, -0.5D).tex(40 / skinWidth, 52 / skinHeight).endVertex();

		// Head
		bufferbuilder.pos(x1, headY0, -0.5D).tex(16 / skinWidth, 16 / skinHeight).endVertex();
		bufferbuilder.pos(x0, headY0, -0.5D).tex(8 / skinWidth, 16 / skinHeight).endVertex();
		bufferbuilder.pos(x0, headY1, -0.5D).tex(8 / skinWidth, 8 / skinHeight).endVertex();
		bufferbuilder.pos(x1, headY1, -0.5D).tex(16 / skinWidth, 8 / skinHeight).endVertex();

		// --- Overlay Layers ---

		// Body Overlay
		bufferbuilder.pos(x1 * overlayScale, bodyY0 * overlayScale, -0.51D).tex(28 / skinWidth, 46 / skinHeight).endVertex();
		bufferbuilder.pos(x0 * overlayScale, bodyY0 * overlayScale, -0.51D).tex(20 / skinWidth, 46 / skinHeight).endVertex();
		bufferbuilder.pos(x0 * overlayScale, bodyY1 * overlayScale, -0.51D).tex(20 / skinWidth, 36 / skinHeight).endVertex();
		bufferbuilder.pos(x1 * overlayScale, bodyY1 * overlayScale, -0.51D).tex(28 / skinWidth, 36 / skinHeight).endVertex();

		// Right Arm Overlay
		bufferbuilder.pos(x0 * overlayScale, bodyY0 * overlayScale, -0.51D).tex(48 / skinWidth, 46 / skinHeight).endVertex();
		bufferbuilder.pos((x0 - armWidth) * overlayScale, bodyY0 * overlayScale, -0.51D).tex(44 / skinWidth, 46 / skinHeight).endVertex();
		bufferbuilder.pos((x0 - armWidth) * overlayScale, bodyY1 * overlayScale, -0.51D).tex(44 / skinWidth, 36 / skinHeight).endVertex();
		bufferbuilder.pos(x0 * overlayScale, bodyY1 * overlayScale, -0.51D).tex(48 / skinWidth, 36 / skinHeight).endVertex();

		// Left Arm Overlay
		bufferbuilder.pos((x1 + armWidth) * overlayScale, bodyY0 * overlayScale, -0.51D).tex(56 / skinWidth, 62 / skinHeight).endVertex();
		bufferbuilder.pos(x1 * overlayScale, bodyY0 * overlayScale, -0.51D).tex(52 / skinWidth, 62 / skinHeight).endVertex();
		bufferbuilder.pos(x1 * overlayScale, bodyY1 * overlayScale, -0.51D).tex(52 / skinWidth, 52 / skinHeight).endVertex();
		bufferbuilder.pos((x1 + armWidth) * overlayScale, bodyY1 * overlayScale, -0.51D).tex(56 / skinWidth, 52 / skinHeight).endVertex();

		// Head Overlay
		bufferbuilder.pos(x1 * overlayScale, headY0 * overlayScale, -0.51D).tex(48 / skinWidth, 16 / skinHeight).endVertex();
		bufferbuilder.pos(x0 * overlayScale, headY0 * overlayScale, -0.51D).tex(40 / skinWidth, 16 / skinHeight).endVertex();
		bufferbuilder.pos(x0 * overlayScale, headY1 * overlayScale, -0.51D).tex(40 / skinWidth, 8 / skinHeight).endVertex();
		bufferbuilder.pos(x1 * overlayScale, headY1 * overlayScale, -0.51D).tex(48 / skinWidth, 8 / skinHeight).endVertex();

		tessellator.draw();
	}
}