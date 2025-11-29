package com.windanesz.lostloot.block;

import com.windanesz.lostloot.Settings;
import com.windanesz.lostloot.init.ModBlocks;
import com.windanesz.lostloot.init.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;

import javax.annotation.Nullable;
import java.util.List;

public class BlockGraveMarker extends BlockLostLoot {
	public BlockGraveMarker(Material materialIn) {
		super(materialIn);
		setHardness(3.0F);
		setHarvestLevel("shovel", 0);
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
					if (!tileentitygravemarker.getFlowerPlaced()) {
						playerIn.addPotionEffect(new PotionEffect(ModPotions.bliss, (int) Settings.worldgenSettings.bliss_duration_for_flower));
						tileentitygravemarker.setFlowerPlaced(true);
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

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		if (!worldIn.isRemote && stack.getItem() instanceof ItemSpade) {
			// If harvested with a shovel, drop loot table contents
			if (this.lootTable != null) {
				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) worldIn)
						.withPlayer(player)
						.withLuck(player.getLuck());

				List<ItemStack> drops = worldIn.getLootTableManager().getLootTableFromLocation(this.lootTable).generateLootForPools(worldIn.rand, lootcontext$builder.build());
				for (ItemStack itemstack : drops) {
					spawnAsEntity(worldIn, pos, itemstack);
				}

				BlockPos posDown = pos.down();
				IBlockState stateDown = worldIn.getBlockState(posDown);
				Block blockDown = stateDown.getBlock();

				if (blockDown == Blocks.DIRT || blockDown == Blocks.GRASS) {
					worldIn.destroyBlock(posDown, true);
				}
			}
		} else {
			// If not harvested with a shovel, drop the grave marker itself.
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}
}
