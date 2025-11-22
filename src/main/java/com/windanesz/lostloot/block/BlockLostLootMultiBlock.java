package com.windanesz.lostloot.block;

import com.windanesz.lostloot.block.tile.TileEntityLostLoot;
import com.windanesz.lostloot.init.ModBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

public class BlockLostLootMultiBlock extends BlockContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool SNOWY = PropertyBool.create("snowy");

	public ResourceLocation lootTable;

	public BlockLostLootMultiBlock(Material materialIn) {
		super(materialIn);
		setHardness(1.5F); // Example, adjust as needed
		setResistance(5.0F); // Example, adjust as needed
	}

	// Standard block properties for non-full blocks
	private static final AxisAlignedBB FULL_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D); // Placeholder for collision

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	// --- Placement Logic ---
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		if (!worldIn.isRemote) {


			// Place dummy blocks in a 3x3 area, centered around pos
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos currentPos = pos.add(x, 0, z); // Assuming Y-level is constant for now

					if (!currentPos.equals(pos)) { // Don't replace the master block
						// Check if the space is air or replaceable. If not, cancel placement? Or just don't place?
						// For now, let's assume it's clear.
						if (worldIn.isAirBlock(currentPos) || worldIn.getBlockState(currentPos).getBlock().isReplaceable(worldIn, currentPos)) {
							worldIn.setBlockState(currentPos, ModBlocks.loot_scene_dummy.getDefaultState(), 3); // Place dummy block

						} else {
							// If any adjacent block is not replaceable, break the master block to undo placement
							worldIn.setBlockToAir(pos);
							// Optionally, drop the item back to the player if it was not placed successfully
							if (placer instanceof EntityPlayer && !((EntityPlayer) placer).isCreative()) {
								spawnAsEntity(worldIn, pos, stack);
							}
							return; // Exit as placement failed
						}
					}
				}
			}
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			// When the master block is broken, destroy all associated dummy blocks.
			// This is called when the master block itself is removed (e.g., by player mining).
			// Iterate over the 3x3 area, if a block is a dummy or another loot scene block, set to air.
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					BlockPos currentPos = pos.add(x, 0, z);
					IBlockState currentBlockState = worldIn.getBlockState(currentPos);
					// Check if it's a loot scene block or a dummy block, but not this current breaking block
					if ((currentBlockState.getBlock() instanceof BlockLostLootMultiBlock || currentBlockState.getBlock() instanceof BlockLootSceneDummy) && !currentPos.equals(pos)) {
						// setBlockToAir will call breakBlock again on the dummy, which we've modified to just clean itself up.
						worldIn.setBlockToAir(currentPos);
					}
				}
			}
			// Drop the loot only from the central block when it's broken.
			dropLootScene(worldIn, pos);
		}
		// Always call super for particle effects, sound.
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		boolean isSnowy = worldIn.getBiome(pos).isSnowyBiome();
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite())
				.withProperty(SNOWY, isSnowy);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = state.getValue(FACING).getHorizontalIndex();

		if (state.getValue(SNOWY)) {
			i |= 4;
		}

		return i;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
				.withProperty(SNOWY, (meta & 4) != 0);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(SNOWY, worldIn.getBiome(pos).isSnowyBiome());
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, SNOWY);
	}


	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLostLoot();
	}

	private void dropLootScene(World worldIn, BlockPos pos) {
		// Drop the item form of this block, or whatever is configured in the loot table
		// Similar to BlockLostLoot's getDrops logic.
		if (this.lootTable != null) {
			LootTable loottable = worldIn.getLootTableManager().getLootTableFromLocation(this.lootTable);
			LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) worldIn);
			// Optionally add player context if needed. For now, generic context.

			for (ItemStack itemstack : loottable.generateLootForPools(worldIn.rand, lootcontext$builder.build())) {
				spawnAsEntity(worldIn, pos, itemstack);
			}
		} else {
			// Fallback: drop itself if no loot table is specified
			ItemStack dropStack = new ItemStack(this);
			spawnAsEntity(worldIn, pos, dropStack);
		}
	}


	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(net.minecraft.world.IBlockAccess iBlockAccess, BlockPos pos, IBlockState state, int fortune) {
		// This method is primarily for natural drops (survival mining).
		// Since we're controlling drops in breakBlock with dropLootScene, this should return an empty list
		// to prevent double drops.
		return Collections.emptyList();
	}

	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	public BlockLostLootMultiBlock setLootTable(ResourceLocation lootTable) {
		this.lootTable = lootTable;
		return this;
	}
}
