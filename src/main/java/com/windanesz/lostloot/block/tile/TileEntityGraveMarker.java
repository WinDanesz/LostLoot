package com.windanesz.lostloot.block.tile;

import com.windanesz.lostloot.init.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class TileEntityGraveMarker extends TileEntity implements ITickable
{
    private Item flowerPotItem;
    private int flowerPotData;

    public TileEntityGraveMarker()
    {
    }

    public TileEntityGraveMarker(Item potItem, int potData)
    {
        this.flowerPotItem = potItem;
        this.flowerPotData = potData;
    }

    @Override
    public void update() {
        if (this.world.isRemote && this.getFlowerPotItem() == Item.getItemFromBlock(ModBlocks.grave_rose)) {
            if (this.world.getTotalWorldTime() % 15 == 0 && this.world.rand.nextInt(1) == 0) { // Spawn particles less frequently
                double x = (double) this.pos.getX() + 0.5D;
                double y = (double) this.pos.getY() + 0.4D;
                double z = (double) this.pos.getZ() + 0.5D;
                double xOffset = this.world.rand.nextDouble() * 0.6D - 0.3D;
                double zOffset = this.world.rand.nextDouble() * 0.6D - 0.3D;
                this.world.spawnParticle(EnumParticleTypes.SPELL, x + xOffset, y, z + zOffset, 0.0D, 0.1D, 1.0D);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(this.flowerPotItem);
        compound.setString("Item", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setInteger("Data", this.flowerPotData);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if (compound.hasKey("Item", 8))
        {
            this.flowerPotItem = Item.getByNameOrId(compound.getString("Item"));
        }
        else
        {
            this.flowerPotItem = Item.getItemById(compound.getInteger("Item"));
        }

        this.flowerPotData = compound.getInteger("Data");
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 5, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }



    public void setFlowerItemStack(ItemStack stack)
    {
        this.flowerPotItem = stack.getItem();
        this.flowerPotData = stack.getMetadata();
    }

    public ItemStack getFlowerItemStack()
    {
        return this.flowerPotItem == null ? ItemStack.EMPTY : new ItemStack(this.flowerPotItem, 1, this.flowerPotData);
    }

    @Nullable
    public Item getFlowerPotItem()
    {
        return this.flowerPotItem;
    }

    public int getFlowerPotData()
    {
        return this.flowerPotData;
    }
}