package com.windanesz.lostloot;

import com.windanesz.lostloot.block.BlockLostCargo;
import com.windanesz.lostloot.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class LLWorldGen implements IWorldGenerator {

    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ) {
        return ArrayUtils.contains(Settings.worldgenSettings.dimensionList, world.provider.getDimension()) && Settings.worldgenSettings.lostCargoFrequency > 0 && random.nextInt(Settings.worldgenSettings.lostCargoFrequency) == 0;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        random.setSeed((chunkX * 341873128712L + chunkZ * 132897987541L) ^ world.getSeed() + this.getRandomSeedModifier());
        if (this.canGenerate(random, world, chunkX, chunkZ)) {
            int attempts = 8 + random.nextInt(8); // Try 8-15 positions per chunk
            for (int i = 0; i < attempts; i++) {
                int x = (chunkX << 4) + random.nextInt(16);
                int z = (chunkZ << 4) + random.nextInt(16);
                int y = world.getHeight(x, z) - 1;
                BlockPos pos = new BlockPos(x, y, z);

                Biome biome = world.getBiome(pos);
                ResourceLocation biomeRL = biome.getRegistryName();
                // Whitelist/blacklist logic
                if (!LostLoot.settings.lostCargoBiomeWhitelist.isEmpty() && !LostLoot.settings.lostCargoBiomeWhitelist.contains(biomeRL))
                    continue;

                // Only spawn on surface: block below must be solid, and current block must be air/replaceable/grass/flower
                BlockPos placePos = pos.up();
                if (!world.isAirBlock(placePos) && !world.getBlockState(placePos).getMaterial().isReplaceable() && !isGrassOrFlower(world, placePos))
                    continue;
                if (!world.getBlockState(pos).isTopSolid()) continue;

                EnumFacing facing = EnumFacing.Plane.HORIZONTAL.random(random);
                world.setBlockState(placePos, Blocks.LOST_CARGO.getDefaultState().withProperty(BlockLostCargo.FACING, facing));
                break; // Only place one per chunk
            }
        }

    }   // Helper to check if block is grass or flower

    private boolean isGrassOrFlower(World world, BlockPos pos) {
        String name = world.getBlockState(pos).getBlock().getRegistryName().toString();
        return name.contains("grass") || name.contains("flower");
    }

    public long getRandomSeedModifier() {
        return 26542719L;
    }
}
