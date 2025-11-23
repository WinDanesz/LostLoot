package com.windanesz.lostloot.client.renderer;

import com.windanesz.lostloot.LostLoot;
import com.windanesz.lostloot.entity.EntityModPainting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderModPainting extends Render<EntityModPainting> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LostLoot.MODID, "textures/blocks/me_in_the_forest.png");

    public RenderModPainting(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityModPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        float width = (float) entity.getWidthPixels() / 16.0F;
        float height = (float) entity.getHeightPixels() / 16.0F;
        float xOffset = -width / 2.0F;
        float yOffset = -height / 2.0F;

        // The origin block is the bottom-left, so we need to offset the rendering
        xOffset += (width / 2.0F) - 0.5f;
        yOffset += (height / 2.0F) - 0.5f;

        renderPainting(entity, width, height, xOffset, yOffset);

        if (this.renderOutlines) {
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

    private void renderPainting(EntityModPainting painting, float width, float height, float x, float y) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        float z = 0.0625F; // Thickness of painting

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        // Front face
        bufferbuilder.pos(x, y, -z).tex(1, 1).normal(0, 0, -1).endVertex();
        bufferbuilder.pos(x + width, y, -z).tex(0, 1).normal(0, 0, -1).endVertex();
        bufferbuilder.pos(x + width, y + height, -z).tex(0, 0).normal(0, 0, -1).endVertex();
        bufferbuilder.pos(x, y + height, -z).tex(1, 0).normal(0, 0, -1).endVertex();
        tessellator.draw();
    }
}