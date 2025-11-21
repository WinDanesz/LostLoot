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
            // Translate to the center of the block, and up a bit
            GlStateManager.translate(x + 0.5, y + 0.4, z + 0.5);

            World world = te.getWorld();
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(flowerState, world.getCombinedLight(te.getPos(), flowerState.getLightValue(world, te.getPos())));
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}