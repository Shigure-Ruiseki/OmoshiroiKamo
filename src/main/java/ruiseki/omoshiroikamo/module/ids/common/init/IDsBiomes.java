package ruiseki.omoshiroikamo.module.ids.common.init;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

import ruiseki.omoshiroikamo.config.backport.IDsConfig;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.world.biome.BiomeMeneglin;

public class IDsBiomes {

    public static BiomeGenBase MENEGILIN;

    public static void preInit() {
        MENEGILIN = new BiomeMeneglin(LibResources.BIOME_MENEGLIN);
        if (IDsConfig.spawnWeight > 0) {
            BiomeManager
                .addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(MENEGILIN, IDsConfig.spawnWeight));
        }
        BiomeManager.addSpawnBiome(MENEGILIN);
        BiomeManager.addStrongholdBiome(MENEGILIN);
        BiomeManager.addVillageBiome(MENEGILIN, true);
        BiomeDictionary.registerBiomeType(MENEGILIN, BiomeDictionary.Type.COLD, BiomeDictionary.Type.MAGICAL);
    }
}
