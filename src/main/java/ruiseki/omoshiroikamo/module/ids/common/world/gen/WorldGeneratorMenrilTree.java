package ruiseki.omoshiroikamo.module.ids.common.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.world.gen.WorldGeneratorTree;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;

public class WorldGeneratorMenrilTree extends WorldGeneratorTree {

    public WorldGeneratorMenrilTree(boolean doNotify) {
        super(doNotify);
    }

    @Override
    protected int baseHeight() {
        return 7;
    }

    @Override
    protected int baseHeightRandomRange() {
        return 4;
    }

    @Override
    public BlockLeaves getLeaves() {
        return (BlockLeaves) IDsBlocks.MENRIL_LEAVES.getBlock();
    }

    @Override
    public BlockLog getLogs() {
        return (BlockLog) IDsBlocks.MENRIL_LOG.getBlock();
    }

    @Override
    public BlockSapling getSapling() {
        return (BlockSapling) IDsBlocks.MENRIL_SAPLING.getBlock();
    }

    @Override
    public boolean growTree(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(baseHeightRandomRange()) + baseHeight();
        int worldHeight = world.getHeight();
        Block block;

        if (y >= 1 && y + treeHeight + 1 <= worldHeight) {

            Block soil = world.getBlock(x, y - 1, z);

            if (soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, getSapling())
                && y < worldHeight - treeHeight - 1) {

                int xOffset, yOffset, zOffset;

                // ===== CHECK SPACE =====
                for (yOffset = y; yOffset <= y + 1 + treeHeight; ++yOffset) {

                    byte radius = 1;

                    if (yOffset == y) radius = 0;
                    if (yOffset >= y + 4) radius = 3;
                    if (yOffset >= y + 1 + treeHeight - 3) radius = 5;
                    if (yOffset >= y + 1 + treeHeight - 1) radius = 3;

                    if (yOffset >= 0 && yOffset < worldHeight) {
                        for (xOffset = x - radius; xOffset <= x + radius; ++xOffset) {
                            for (zOffset = z - radius; zOffset <= z + radius; ++zOffset) {

                                block = world.getBlock(xOffset, yOffset, zOffset);

                                if (!(block.isLeaves(world, xOffset, yOffset, zOffset)
                                    || world.isAirBlock(xOffset, yOffset, zOffset)
                                    || block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                }

                soil.onPlantGrow(world, x, y - 1, z, x, y, z);

                // ===== GENERATE LEAVES =====
                for (yOffset = y - 5 + treeHeight; yOffset <= y + treeHeight; ++yOffset) {
                    int center = 2;

                    for (xOffset = x - center; xOffset <= x + center; ++xOffset) {
                        for (zOffset = z - center; zOffset <= z + center; ++zOffset) {

                            int xPos = Math.abs(xOffset - x);
                            int zPos = Math.abs(zOffset - z);

                            block = world.getBlock(xOffset, yOffset, zOffset);

                            if ((xPos != center || zPos != center)
                                && !((yOffset == y + treeHeight || yOffset == y - 5 + treeHeight)
                                    && (xPos == center || zPos == center))
                                && (block.isLeaves(world, xOffset, yOffset, zOffset)
                                    || world.isAirBlock(xOffset, yOffset, zOffset)
                                    || block.canBeReplacedByLeaves(world, xOffset, yOffset, zOffset))) {

                                world.setBlock(xOffset, yOffset, zOffset, getLeaves(), 0, 2);
                            }
                        }
                    }
                }

                // ===== GENERATE TRUNK =====
                for (yOffset = 0; yOffset < treeHeight; ++yOffset) {
                    setLog(world, x, y + yOffset, z, false);

                    if (yOffset >= 1 + treeHeight - 5 && yOffset <= 1 + treeHeight - 1) {

                        setLog(world, x - 1, y + yOffset, z, false);
                        setLog(world, x + 1, y + yOffset, z, false);
                        setLog(world, x, y + yOffset, z - 1, false);
                        setLog(world, x, y + yOffset, z + 1, false);
                    }
                }

                // ===== STUMP =====
                setLog(world, x - 1, y, z, true);
                setLog(world, x + 1, y, z, true);
                setLog(world, x, y, z - 1, true);
                setLog(world, x, y, z + 1, true);

                return true;
            }
        }
        return false;
    }

    private void setLog(World world, int x, int y, int z, boolean stump) {
        Block block = world.getBlock(x, y, z);

        if (world.isAirBlock(x, y, z) || block.isLeaves(world, x, y, z) || block.isReplaceable(world, x, y, z)) {
            setBlockAndNotifyAdequately(world, x, y, z, getLogs(), 0);
        }
    }
}
