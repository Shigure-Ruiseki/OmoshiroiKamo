package ruiseki.omoshiroikamo.common.init;

import net.minecraftforge.oredict.OreDictionary;

import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

public class MobOreDicts {

    public static void preInit() {
        if (BackportConfigs.useEnvironmentalTech) {
            OreDictionary.registerOre("stoneBasalt", ModBlocks.BLOCK_BASALT.get());
            OreDictionary.registerOre("stoneAlabaster", ModBlocks.BLOCK_ALABASTER.get());
            OreDictionary.registerOre("stoneHardened", ModBlocks.BLOCK_HARDENED_STONE.get());
        }
    }
}
