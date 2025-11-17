package com.windanesz.lostloot.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLostLoot extends BlockContainer {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");
	public ResourceLocation lootTable;

	public BlockLostLoot(Material materialmaterialn) {
		super(materialmaterialn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(SNOWY, false));
		setHardness(1.5F);
		setResistance(5.0F);
	}

    @Override
    public TileEntityLostLoot createNewTileEntity(World world, int meta) {
        return new TileEntityLostLoot();
    }

    private static final AxisAlignedBB HALF_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, net.minecraft.world.IBlockAccess source, BlockPos pos) {
        return HALF_BLOCK_AABB;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        boolean isSnowy = worldIn.getBiome(pos).isSnowyBiome();
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite())
                .withProperty(SNOWY, isSnowy);
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
    public boolean isSideSolid(IBlockState base_state, net.minecraft.world.IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT; // or TRANSLUCENT if you want full alpha blending
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, SNOWY);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!worldIn.isRemote && player.isCreative()) {
            LootTable loottable = worldIn.getLootTableManager().getLootTableFromLocation(this.lootTable);
            LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)worldIn).withPlayer(player).withLuck(player.getLuck());

            for (ItemStack itemstack : loottable.generateLootForPools(worldIn.rand, lootcontext$builder.build())) {
                spawnAsEntity(worldIn, pos, itemstack);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

	@Override
	public List<ItemStack> getDrops(net.minecraft.world.IBlockAccess iBlockAccess, BlockPos pos, IBlockState state, int fortune) {
		if (!(iBlockAccess instanceof WorldServer) || ((WorldServer) iBlockAccess).isRemote) {
			return Collections.emptyList();
		}

		World realWorld = (World) iBlockAccess;
		LootTable lootTable = realWorld.getLootTableManager().getLootTableFromLocation(this.lootTable);
		LootContext.Builder builder = new LootContext.Builder((WorldServer) realWorld);
		builder.withLuck(0);
		// Optionally add more context (e.g., player, tile entity)
		return lootTable.generateLootForPools(realWorld.rand, builder.build());
	}

	public ResourceLocation getLootTable() {
		return this.lootTable;
	}

	public BlockLostLoot setLootTable(ResourceLocation lootTable) {
		this.lootTable = lootTable;
		return this;
	}
}
