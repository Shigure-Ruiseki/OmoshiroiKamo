package ruiseki.omoshiroikamo.core.common.init;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CoreOreDict {

    public static void init() {
        register("dustRedstone", new ItemStack(Items.redstone));
        register("itemNetherStar", new ItemStack(Items.nether_star));
        register("itemGhastTear", new ItemStack(Items.ghast_tear));
        register("pearlEnder", new ItemStack(Items.ender_pearl));
        register("itemClay", new ItemStack(Items.clay_ball));
        register("itemLeather", new ItemStack(Items.leather));
        register("blockObsidian", Blocks.obsidian);
        register("blockHopper", Blocks.hopper);
        register("barsIron", Blocks.iron_bars);
        register("chestWood", Blocks.chest);
    }

    private static void register(String key, ItemStack stack) {
        OreDictionary.registerOre(key, stack);
    }

    private static void register(String key, Block block) {
        OreDictionary.registerOre(key, block);
    }
}
