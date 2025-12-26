package ruiseki.omoshiroikamo.core.common.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.backport.muliblock.MultiblockWorldGenConfig;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public class OKWorldGenerator implements IWorldGenerator {

    public static final OKWorldGenerator INSTANCE = new OKWorldGenerator();
    private final WorldGenMinable hardened_stone = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock(),
        MultiblockWorldGenConfig.hardenedStoneNodeSize);
    private final WorldGenMinable alabaster = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_ALABASTER.getBlock(),
        MultiblockWorldGenConfig.alabasterNodeSize);
    private final WorldGenMinable basalt = new WorldGenMinable(
        MultiBlockBlocks.BLOCK_BASALT.getBlock(),
        MultiblockWorldGenConfig.basaltNodeSize);

    public static void preInit() {
        GameRegistry.registerWorldGenerator(INSTANCE, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (world.provider instanceof WorldProviderSurface) {

            if (BackportConfigs.useMultiBlock) {

                if (MultiblockWorldGenConfig.enableHardenedStoneGeneration) {
                    this.runGeneration(
                        this.hardened_stone,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        MultiblockWorldGenConfig.hardenedStoneNodes,
                        MultiblockWorldGenConfig.hardenedStoneMinHeight,
                        MultiblockWorldGenConfig.hardenedStoneMaxHeight);
                }

                if (MultiblockWorldGenConfig.enableAlabasterGeneration) {
                    this.runGeneration(
                        this.alabaster,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        MultiblockWorldGenConfig.alabasterNodes,
                        MultiblockWorldGenConfig.alabasterMinHeight,
                        MultiblockWorldGenConfig.alabasterMaxHeight);
                }

                if (MultiblockWorldGenConfig.enableBasaltGeneration) {
                    this.runGeneration(
                        this.basalt,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        MultiblockWorldGenConfig.basaltNodes,
                        MultiblockWorldGenConfig.basaltMinHeight,
                        MultiblockWorldGenConfig.basaltMaxHeight);
                }
            }

        }
    }

    public void runGeneration(WorldGenerator gen, World world, Random rand, int chunkX, int chunkZ, float chance,
        int minY, int maxY) {
        if (minY < 0 || maxY > 255 || minY >= maxY || chance <= 0) {
            return;
        }

        int heightDiff = maxY - minY;
        int attempts = (chance < 1 ? 1 : (int) chance);

        for (int i = 0; i < attempts; i++) {
            if (chance >= 1 || rand.nextFloat() < chance) {
                int x = (chunkX << 4) + rand.nextInt(16);
                int y = minY + rand.nextInt(heightDiff);
                int z = (chunkZ << 4) + rand.nextInt(16);
                gen.generate(world, rand, x, y, z);
            }
        }
    }

}
