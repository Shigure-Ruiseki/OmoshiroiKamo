package ruiseki.omoshiroikamo.module.ids.common.world.biome;

import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import ruiseki.omoshiroikamo.module.ids.common.world.gen.WorldGeneratorMenrilTree;

public class MeneglinBiomeDecorator extends BiomeDecorator {

    public static final WorldGeneratorMenrilTree MENRIL_TREE_GEN = new WorldGeneratorMenrilTree(false);

    @Override
    protected void genDecorations(BiomeGenBase biomeGenBaseIn) {
        super.genDecorations(biomeGenBaseIn);

        int trees = this.treesPerChunk / 3;
        if (this.randomGenerator.nextInt(10) == 0) {
            ++trees;
        }

        if (TerrainGen.decorate(
            this.currentWorld,
            this.randomGenerator,
            this.chunk_X,
            this.chunk_Z,
            DecorateBiomeEvent.Decorate.EventType.TREE)) {

            for (int i = 0; i < trees; ++i) {
                int x = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int z = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                int y = this.currentWorld.getHeightValue(x, z);
                MENRIL_TREE_GEN.growTree(this.currentWorld, this.randomGenerator, x, y, z);
            }
        }
    }

}
