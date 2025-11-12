package ruiseki.omoshiroikamo.common.init;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public class OKWorldGenerator implements IWorldGenerator {

    public static final OKWorldGenerator INSTANCE = new OKWorldGenerator();
    private final WorldGenMinable hardened_stone = new WorldGenMinable(
        ModBlocks.BLOCK_HARDENED_STONE.get(),
        EnvironmentalConfig.worldGenConfig.hardenedStoneNodeSize);
    private final WorldGenMinable alabaster = new WorldGenMinable(
        ModBlocks.BLOCK_ALABASTER.get(),
        EnvironmentalConfig.worldGenConfig.alabasterNodeSize);
    private final WorldGenMinable basalt = new WorldGenMinable(
        ModBlocks.BLOCK_BASALT.get(),
        EnvironmentalConfig.worldGenConfig.basaltNodeSize);

    public static void preInit() {
        GameRegistry.registerWorldGenerator(INSTANCE, 0);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        if (world.provider instanceof WorldProviderSurface) {

            if (BackportConfigs.useEnvironmentalTech) {

                if (EnvironmentalConfig.worldGenConfig.enableHardenedStoneGeneration) {
                    this.runGeneration(
                        this.hardened_stone,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        EnvironmentalConfig.worldGenConfig.hardenedStoneNodes,
                        EnvironmentalConfig.worldGenConfig.hardenedStoneMinHeight,
                        EnvironmentalConfig.worldGenConfig.hardenedStoneMaxHeight);
                }

                if (EnvironmentalConfig.worldGenConfig.enableAlabasterGeneration) {
                    this.runGeneration(
                        this.alabaster,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        EnvironmentalConfig.worldGenConfig.alabasterNodes,
                        EnvironmentalConfig.worldGenConfig.alabasterMinHeight,
                        EnvironmentalConfig.worldGenConfig.alabasterMaxHeight);
                }

                if (EnvironmentalConfig.worldGenConfig.enableBasaltGeneration) {
                    this.runGeneration(
                        this.basalt,
                        world,
                        random,
                        chunkX,
                        chunkZ,
                        EnvironmentalConfig.worldGenConfig.basaltNodes,
                        EnvironmentalConfig.worldGenConfig.basaltMinHeight,
                        EnvironmentalConfig.worldGenConfig.basaltMaxHeight);
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
