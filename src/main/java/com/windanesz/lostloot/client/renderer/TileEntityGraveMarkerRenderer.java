package com.windanesz.lostloot.client.renderer;

import com.windanesz.lostloot.block.TileEntityGraveMarker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityGraveMarkerRenderer extends TileEntitySpecialRenderer<TileEntityGraveMarker> {
    @Override
    public void render(TileEntityGraveMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack flowerStack = te.getFlowerItemStack();

        if (!flowerStack.isEmpty() && flowerStack.getItem() instanceof ItemBlock) {
            Block flowerBlock = ((ItemBlock) flowerStack.getItem()).getBlock();
            IBlockState flowerState = flowerBlock.getStateFromMeta(flowerStack.getMetadata());

            GlStateManager.pushMatrix();

            // Translate to the block's position.
            GlStateManager.translate(x, y, z);

            // Translate to the center of the block and slightly up for rendering.
            GlStateManager.translate(0.5, 0.4, 0.5);
            GlStateManager.scale(0.8, 0.8, 0.8);
            
            // Translate the block model to its center before rendering
            GlStateManager.translate(-0.5, -0.5, -0.5);

            World world = te.getWorld();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(flowerState, 1.0f);
            GlStateManager.enableLighting();

            GlStateManager.popMatrix();
        }
    }
}