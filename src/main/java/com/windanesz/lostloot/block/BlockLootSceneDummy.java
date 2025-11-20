package com.windanesz.lostloot.block;


import net.minecraft.block.material.Material;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Collections;
import java.util.List;

public class BlockLootSceneDummy extends Block {

    public BlockLootSceneDummy(Material materialIn) {
        super(materialIn);
        setHardness(-1.0F); // Unbreakable by normal means
        setResistance(6000000.0F); // Max resistance
        disableStats(); // Don't give statistics for breaking this block
    }

    // --- Visual Properties ---
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
        return EnumBlockRenderType.INVISIBLE; // Completely invisible
    }

    // No bounding box, so players can walk through it
    private static final AxisAlignedBB EMPTY_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return EMPTY_AABB; // No collision
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, net.minecraft.world.IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB; // No collision with entities
    }

    // --- Interaction Delegation ---
    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!worldIn.isRemote) {
            // Search for the master BlockLootScene in a 3x3 area around the dummy block
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos masterCandidatePos = pos.add(x, 0, z);
                    IBlockState masterCandidateState = worldIn.getBlockState(masterCandidatePos);
                    if (masterCandidateState.getBlock() instanceof BlockLostLootMultiBlock) {
                        // Found the master block, destroy it.
                        // The master block's breakBlock will handle cleanup of all dummy blocks.
                        worldIn.destroyBlock(masterCandidatePos, !player.isCreative());
                        return; // Found and destroyed, no need to continue searching
                    }
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        // This breakBlock is called when the dummy block itself is broken.
        // The onBlockHarvested method should have already handled destroying the master block.
        // We just need to make sure this dummy block is correctly removed.
        // No need to search for master here, as onBlockHarvested takes care of it.
        super.breakBlock(worldIn, pos, state); // Still call super to perform standard cleanup
    }


    // Dummy blocks should never drop anything themselves
    @Override
    public List<ItemStack> getDrops(net.minecraft.world.IBlockAccess iBlockAccess, BlockPos pos, IBlockState state, int fortune) {
        return Collections.emptyList();
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        return false; // Cannot be collided with
    }

    @Override
    public int getLightOpacity(IBlockState state) {
        return 0; // Does not block light
    }

    @Override
    public boolean isAir(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos) {
        return true; // Behaves like air for pathfinding and similar checks
    }
}
