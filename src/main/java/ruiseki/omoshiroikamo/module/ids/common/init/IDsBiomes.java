package ruiseki.omoshiroikamo.module.ids.common.init;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.world.biome.BiomeMeneglin;

public class IDsBiomes {

    public static BiomeGenBase MENEGILIN;

    public static void preInit() {
        MENEGILIN = new BiomeMeneglin(LibResources.BIOME_MENEGLIN);
        BiomeDictionary.registerBiomeType(MENEGILIN, BiomeDictionary.Type.FOREST);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(MENEGILIN, 8));
        BiomeManager.addSpawnBiome(MENEGILIN);
    }
}
