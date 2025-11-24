package com.windanesz.lostloot.item;

import com.windanesz.lostloot.entity.EntityModPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemModPainting extends Item {

	public ItemModPainting() {
	}

	/**
	 * Called when a Block is right-clicked with this Item
	 */
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		BlockPos blockpos = pos.offset(facing);

		if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, itemstack)) {
			EntityModPainting painting = this.createEntity(worldIn, blockpos, facing);

			if (painting != null && painting.onValidSurface()) {
				if (!worldIn.isRemote) {
					painting.playPlaceSound();
					painting.setProperties(facing.getHorizontalAngle(), 32, 32, "forest");
					worldIn.spawnEntity(painting);
					itemstack.shrink(1);
				}

			}

			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}


	@Nullable
	private EntityModPainting createEntity(World worldIn, BlockPos pos, EnumFacing clickedSide) {
		return new EntityModPainting(worldIn, pos, clickedSide);
	}
}
