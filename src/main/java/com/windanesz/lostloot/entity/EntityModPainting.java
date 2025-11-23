package com.windanesz.lostloot.entity;

import com.windanesz.lostloot.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityModPainting extends EntityHanging {

    public EntityModPainting(World worldIn) {
        super(worldIn);
    }

    public EntityModPainting(World worldIn, BlockPos pos, EnumFacing facing) {
        super(worldIn, pos);
        this.updateFacingWithBoundingBox(facing);
    }

    @Override
    public int getWidthPixels() {
        return 64; // 4 blocks * 16 pixels/block
    }

    @Override
    public int getHeightPixels() {
        return 64; // 4 blocks * 16 pixels/block
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

            if (brokenEntity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) brokenEntity;
                if (entityplayer.capabilities.isCreativeMode) {
                    return;
                }
            }
            this.entityDropItem(new ItemStack(ModItems.forest_painting), 0.0F);
        }
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) { super.writeEntityToNBT(compound); }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) { super.readEntityFromNBT(compound); }
}