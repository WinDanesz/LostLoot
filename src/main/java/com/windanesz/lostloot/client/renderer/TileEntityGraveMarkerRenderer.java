package com.windanesz.lostloot.client.renderer;

import com.windanesz.lostloot.block.TileEntityGraveMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityGraveMarkerRenderer extends TileEntitySpecialRenderer<TileEntityGraveMarker> {
    @Override
    public void render(TileEntityGraveMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack flowerStack = te.getFlowerItemStack();

        if (!flowerStack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 0.6D, z + 0.5D); // Center and slightly raise the item
            GlStateManager.scale(0.5F, 0.5F, 0.5F); // Scale down the item

            // Render the item
            RenderHelper.enableStandardItemLighting();
            Minecraft.getMinecraft().getRenderItem().renderItem(flowerStack, ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }
}