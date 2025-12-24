package ruiseki.omoshiroikamo.module.multiblock.common.init;

import net.minecraftforge.oredict.OreDictionary;

public class MultiBlockOreDicts {

    public static void preInit() {
        OreDictionary.registerOre("stoneBasalt", MultiBlockBlocks.BLOCK_BASALT.getBlock());
        OreDictionary.registerOre("stoneAlabaster", MultiBlockBlocks.BLOCK_ALABASTER.getBlock());
        OreDictionary.registerOre("stoneHardened", MultiBlockBlocks.BLOCK_HARDENED_STONE.getBlock());
    }
}
