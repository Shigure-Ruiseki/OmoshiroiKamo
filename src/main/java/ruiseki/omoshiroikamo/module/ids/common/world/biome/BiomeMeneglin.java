package ruiseki.omoshiroikamo.module.ids.common.world.biome;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.config.backport.IDsConfig;

public class BiomeMeneglin extends BiomeGenBase {

    public BiomeMeneglin(int id) {
        super(id);
        this.setBiomeName("Meneglin");
        this.setHeight(new Height(0.4F, 0.4F));
        this.setTemperatureRainfall(0.75F, 0.25F);
        this.waterColorMultiplier = rgb(85, 221, 168);

        this.theBiomeDecorator.treesPerChunk = 3;
        this.theBiomeDecorator.flowersPerChunk = 70;
        this.flowers.clear();

        this.addFlower(Blocks.red_flower, BlockFlower.func_149856_f("blueOrchid"), 15);

        this.addFlower(Blocks.red_flower, BlockFlower.func_149856_f("oxeyeDaisy"), 10);

        this.addFlower(Blocks.red_flower, BlockFlower.func_149856_f("tulipWhite"), 8);

    }

    @Override
    public void decorate(World world, Random rand, int chunkX, int chunkZ) {
        if (!ArrayUtils.contains(IDsConfig.meneglinBiomeDimensionBlacklist, world.provider.dimensionId)) {
            super.decorate(world, rand, chunkX, chunkZ);
        }
    }

    @Override
    public BiomeDecorator createBiomeDecorator() {
        return new MeneglinBiomeDecorator();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        return rgb(85, 221, 168);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        return rgb(128, 208, 185);
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return rgb(178, 238, 233);
    }

    private int rgb(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }
}
