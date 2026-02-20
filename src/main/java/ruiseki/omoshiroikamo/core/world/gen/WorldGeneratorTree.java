package ruiseki.omoshiroikamo.core.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

/**
 * WorldGenerator for trees.
 * 
 * @author rubensworks
 *
 */
public abstract class WorldGeneratorTree extends WorldGenerator {

    /**
     * Make a new instance.
     * 
     * @param doNotify If the generator should notify the world.
     */
    public WorldGeneratorTree(boolean doNotify) {
        super(doNotify);
    }

    @Override
    public boolean generate(World world, Random rand, int x, int retries, int z) {
        for (int c = 0; c < retries; c++) {
            int y = world.getActualHeight() - 1;
            BlockPos loopPos = new BlockPos(x, y, z);
            while (world.isAirBlock(x, y, z) && y > 0) {
                y--;
            }

            if (!growTree(world, rand, x, y + 1, z)) {
                retries--;
            }

            x += rand.nextInt(16) - 8;
            z += rand.nextInt(16) - 8;
        }

        return true;
    }

    protected abstract int baseHeight();

    protected abstract int baseHeightRandomRange();

    public abstract BlockLeaves getLeaves();

    public abstract BlockLog getLogs();

    public abstract BlockSapling getSapling();

    /**
     * Grow an Undead Tree at the given location.
     * 
     * @param world The world.
     * @param rand  Random object.
     * @param x,    y, z The position.
     * @return If the tree was grown.
     */
    public boolean growTree(World world, Random rand, int x, int y, int z) {

        int treeHeight = rand.nextInt(baseHeightRandomRange()) + baseHeight();
        int worldHeight = world.getHeight();
        Block block;

        if (y >= 1 && y + treeHeight + 1 <= worldHeight) {

            Block soil = world.getBlock(x, y - 1, z);

            if (soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, getSapling())
                && y < worldHeight - treeHeight - 1) {

                // Check space
                for (int yOffset = y; yOffset <= y + 1 + treeHeight; yOffset++) {

                    byte radius = 1;

                    if (yOffset == y) radius = 0;
                    if (yOffset >= y + treeHeight - 2) radius = 2;

                    if (yOffset >= 0 && yOffset < worldHeight) {

                        for (int xOffset = x - radius; xOffset <= x + radius; xOffset++) {
                            for (int zOffset = z - radius; zOffset <= z + radius; zOffset++) {

                                block = world.getBlock(xOffset, yOffset, zOffset);

                                if (block != null && !block.isLeaves(world, xOffset, yOffset, zOffset)
                                    && block != Blocks.air
                                    && !block.isReplaceable(world, xOffset, yOffset, zOffset)) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }

                soil.onPlantGrow(world, x, y - 1, z, x, y, z);

                // ===== LEAVES =====
                for (int yOffset = y - 3 + treeHeight; yOffset <= y + treeHeight; yOffset++) {

                    int var12 = yOffset - (y + treeHeight);
                    int center = 1 - var12 / 2;

                    for (int xOffset = x - center; xOffset <= x + center; xOffset++) {
                        for (int zOffset = z - center; zOffset <= z + center; zOffset++) {

                            int dx = Math.abs(xOffset - x);
                            int dz = Math.abs(zOffset - z);

                            if ((dx != center || dz != center || rand.nextInt(2) != 0 && var12 != 0)) {

                                block = world.getBlock(xOffset, yOffset, zOffset);

                                if (block == null || block == Blocks.air
                                    || block.isLeaves(world, xOffset, yOffset, zOffset)
                                    || block.isReplaceable(world, xOffset, yOffset, zOffset)) {

                                    setBlockAndNotifyAdequately(world, xOffset, yOffset, zOffset, getLeaves(), 0 // meta
                                                                                                                 // leaves
                                    );
                                }
                            }
                        }
                    }
                }

                // ===== LOGS =====
                for (int yOffset = 0; yOffset < treeHeight; yOffset++) {

                    block = world.getBlock(x, y + yOffset, z);

                    if (block == null || block == Blocks.air
                        || block.isLeaves(world, x, y + yOffset, z)
                        || block.isReplaceable(world, x, y + yOffset, z)) {

                        setBlockAndNotifyAdequately(world, x, y + yOffset, z, getLogs(), 0 // meta log (vertical)
                        );
                    }
                }

                return true;
            }
        }

        return false;
    }

}
