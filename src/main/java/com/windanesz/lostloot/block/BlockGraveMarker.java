package com.windanesz.lostloot.block;

import com.windanesz.lostloot.block.tile.TileEntityGraveMarker;
import com.windanesz.lostloot.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockGraveMarker extends BlockLostLoot {
	public BlockGraveMarker(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityGraveMarker();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = playerIn.getHeldItem(hand);
		TileEntityGraveMarker tileentitygravemarker = (TileEntityGraveMarker) worldIn.getTileEntity(pos);

		if (tileentitygravemarker == null) {
			return false;
		} else {
			ItemStack itemstack1 = tileentitygravemarker.getFlowerItemStack();

			if (itemstack1.isEmpty()) {
				// Check if the item can be potted (is a flower)
				if (!itemstack.isEmpty() && canBePotted(itemstack)) {
					tileentitygravemarker.setFlowerItemStack(itemstack);
					if (!playerIn.capabilities.isCreativeMode) {
						itemstack.shrink(1);
					}
					tileentitygravemarker.markDirty();
					worldIn.notifyBlockUpdate(pos, state, state, 3);
					return true;
				}
			} else {
				if (itemstack.isEmpty()) {
					playerIn.setHeldItem(hand, itemstack1);
				} else if (!playerIn.addItemStackToInventory(itemstack1)) {
					playerIn.dropItem(itemstack1, false);
				}
				tileentitygravemarker.setFlowerItemStack(ItemStack.EMPTY);
				tileentitygravemarker.markDirty();
				worldIn.notifyBlockUpdate(pos, state, state, 3);
				return true;
			}
		}
		return false;
	}

	private boolean canBePotted(ItemStack stack) {
		// This is a simplified check. In a real mod, you'd want a more robust way
		// to determine if an item is a "flower" or can be placed in the grave marker.
		// For now, we'll check against a few common flower items.
		// You might want to use a tag system or a custom list of items.
		return stack.getItem() instanceof net.minecraft.item.ItemBlock && (
				getBlockFromItem(stack.getItem()) instanceof net.minecraft.block.BlockFlower ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.RED_MUSHROOM ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.BROWN_MUSHROOM ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.CACTUS ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.TALLGRASS ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.DEADBUSH ||
				getBlockFromItem(stack.getItem()) == net.minecraft.init.Blocks.YELLOW_FLOWER ||
				getBlockFromItem(stack.getItem()) == ModBlocks.rose ||
				getBlockFromItem(stack.getItem()) == ModBlocks.grave_rose
		);
	}
}
