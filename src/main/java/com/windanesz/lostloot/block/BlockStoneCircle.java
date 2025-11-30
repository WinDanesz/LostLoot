package com.windanesz.lostloot.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockStoneCircle extends BlockContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool SNOWY = PropertyBool.create("snowy");
	public ResourceLocation lootTable;
	public AxisAlignedBB boundingBox;

	public BlockStoneCircle(Material materialmaterialn) {
		super(materialmaterialn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(SNOWY, false));
		setHardness(1.5F);
		setResistance(5.0F);
		this.boundingBox = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D); // Default AABB
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityStoneCircle();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		}

		TileEntity te = worldIn.getTileEntity(pos);
		if (!(te instanceof TileEntityStoneCircle)) {
			return false;
		}

		TileEntityStoneCircle stoneCircle = (TileEntityStoneCircle) te;
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (heldItem.getItem() == Items.STICK) {
			if (stoneCircle.getPair() != null) {
				// Already paired, teleport
				teleportPlayerToTwin(playerIn, stoneCircle.getPair(), worldIn.provider.getDimension());
			} else {
				// Not paired, try to create a pair
				playerIn.sendMessage(new TextComponentString("This stone circle feels dormant... You feel a pull to a distant location."));
				BlockPos twinPos = findAndGenerateTwinAltar(worldIn, pos);
				if (twinPos != null) {
					// Link them up
					TileEntity otherTe = worldIn.getTileEntity(twinPos);
					if (otherTe instanceof TileEntityStoneCircle) {
						TileEntityStoneCircle otherStoneCircle = (TileEntityStoneCircle) otherTe;

						stoneCircle.setPair(twinPos);
						otherStoneCircle.setPair(pos);

						playerIn.sendMessage(new TextComponentString(TextFormatting.GREEN + "The stone circle resonates with another one far away!"));
					}
				} else {
					playerIn.sendMessage(new TextComponentString(TextFormatting.RED + "The magic fizzles. No suitable location could be found."));
				}
			}
			return true;
		}

		return false;
	}


	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return this.boundingBox;
	}

	public BlockStoneCircle setBoundingBox(AxisAlignedBB boundingBox) {
		this.boundingBox = boundingBox;
		return this;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		boolean isSnowy = worldIn.getBiome(pos).isSnowyBiome();
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(SNOWY, isSnowy);
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
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
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
		return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(SNOWY, (meta & 4) != 0);
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (!worldIn.isRemote && player.isCreative() && this.lootTable != null) {
			LootTable loottable = worldIn.getLootTableManager().getLootTableFromLocation(this.lootTable);
			LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) worldIn).withPlayer(player).withLuck(player.getLuck());

			for (ItemStack itemstack : loottable.generateLootForPools(worldIn.rand, lootcontext$builder.build())) {
				spawnAsEntity(worldIn, pos, itemstack);
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess iBlockAccess, BlockPos pos, IBlockState state, int fortune) {
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

	private BlockPos findAndGenerateTwinAltar(World world, BlockPos originalPos) {
		Random random = world.rand;
		int minDistance = 1000; // Minimum 1000 blocks away
		int maxDistance = 5000; // Maximum 5000 blocks away
		int attempts = 20;

		for (int i = 0; i < attempts; i++) {
			// Pick random direction and distance
			double angle = random.nextDouble() * 2 * Math.PI;
			int distance = minDistance + random.nextInt(maxDistance - minDistance);

			int x = originalPos.getX() + (int) (Math.cos(angle) * distance);
			int z = originalPos.getZ() + (int) (Math.sin(angle) * distance);

			// Find suitable ground level
			BlockPos surfacePos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

			// Check if location is suitable
			if (canGenerateAltarAt(world, surfacePos.down())) {
				// Generate the twin altar
				generateTwinAltar(world, surfacePos);
				return surfacePos;
			}
		}

		return null; // Failed to find suitable location
	}

	private boolean canGenerateAltarAt(World world, BlockPos pos) {
		// 'pos' is the block *below* where the Stone Circle would sit.
        // We check a 3x3 area centered at pos for the base, and centered at pos.up() for the altar itself.

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos groundCheckPos = pos.add(x, 0, z); // The block below the potential altar
				BlockPos altarCheckPos = groundCheckPos.up(); // The block where the altar would sit

				IBlockState groundState = world.getBlockState(groundCheckPos);
                // Ensure the ground is a solid, non-air, non-liquid block.
                if (!groundState.getMaterial().isSolid() || groundState.getMaterial() == Material.AIR || groundState.getMaterial() == Material.WATER || groundState.getMaterial() == Material.LAVA) {
                    return false;
                }

				IBlockState altarState = world.getBlockState(altarCheckPos);
                Material altarMaterial = altarState.getMaterial();
                // Ensure the space where the altar will sit can be replaced or is air.
                // It should NOT be solid, non-plant blocks, or liquid.
                if (altarMaterial.isSolid() && altarMaterial != Material.PLANTS && altarMaterial != Material.VINE && altarMaterial != Material.WEB && altarMaterial != Material.SNOW) {
                    return false; // Cannot place altar on solid, non-plant blocks
                }
                if (altarMaterial == Material.WATER || altarMaterial == Material.LAVA) {
                    return false; // Cannot place altar in liquid
                }

                // Ensure the space above the altar is also clear
                IBlockState aboveAltarState = world.getBlockState(altarCheckPos.up());
                Material aboveAltarMaterial = aboveAltarState.getMaterial();
                if (aboveAltarMaterial.isSolid() && aboveAltarMaterial != Material.PLANTS && aboveAltarMaterial != Material.VINE && aboveAltarMaterial != Material.WEB && aboveAltarMaterial != Material.SNOW) {
                    return false;
                }
                if (aboveAltarMaterial == Material.WATER || aboveAltarMaterial == Material.LAVA) {
                    return false;
                }
			}
		}
		return true;
	}

	private void generateTwinAltar(World world, BlockPos newPos) {
        // Clear a 3x3 area centered on newPos (where the stone circle will sit)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos clearPos = newPos.add(x, 0, z);
                IBlockState stateToClear = world.getBlockState(clearPos);
                // Clear out soft blocks, plants, etc.
                if (stateToClear.getMaterial() == Material.PLANTS || stateToClear.getMaterial() == Material.VINE || stateToClear.getMaterial() == Material.WEB || stateToClear.getMaterial() == Material.SNOW) {
                    world.setBlockToAir(clearPos);
                }
                // Clear the block above, just in case (e.g. tall grass)
                BlockPos clearAbovePos = clearPos.up();
                IBlockState stateToClearAbove = world.getBlockState(clearAbovePos);
                if (stateToClearAbove.getMaterial() == Material.PLANTS || stateToClearAbove.getMaterial() == Material.VINE || stateToClearAbove.getMaterial() == Material.WEB || stateToClearAbove.getMaterial() == Material.SNOW) {
                    world.setBlockToAir(clearAbovePos);
                }
            }
        }

		IBlockState state = this.getDefaultState().withProperty(SNOWY, world.getBiome(newPos).isSnowyBiome());
		world.setBlockState(newPos, state);
	}

	private void teleportPlayerToTwin(EntityPlayer player, BlockPos twinPos, int dimension) {
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;

			if (playerMP.dimension != dimension) {
				// Cross-dimension teleport
				playerMP.getServer().getPlayerList().transferPlayerToDimension(playerMP, dimension, new net.minecraftforge.common.util.ITeleporter() {
					@Override
					public void placeEntity(World world, Entity entity, float yaw) {
						// Teleport above the altar structure
						entity.setPositionAndUpdate(twinPos.getX() + 0.5, twinPos.getY() + 1, twinPos.getZ() + 0.5);
					}
				});
			} else {
				// Same dimension teleport - place above the altar
				playerMP.setPositionAndUpdate(twinPos.getX() + 0.5, twinPos.getY() + 1, twinPos.getZ() + 0.5);
			}

			// Play teleport sound
			playerMP.world.playSound(null, playerMP.posX, playerMP.posY, playerMP.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

			player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "You have been teleported!"));
		}
	}
}
