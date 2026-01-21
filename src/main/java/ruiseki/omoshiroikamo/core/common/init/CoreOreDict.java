package ruiseki.omoshiroikamo.core.common.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CoreOreDict {

    public static void init() {
        register("itemNetherStar", new ItemStack(Items.nether_star));
        register("itemGhastTear", new ItemStack(Items.ghast_tear));
        register("pearlEnder", new ItemStack(Items.ender_pearl));
        register("itemClay", new ItemStack(Items.clay_ball));
        register("itemLeather", new ItemStack(Items.leather));
        register("blockObsidian", new ItemStack(Blocks.obsidian));
        register("blockHopper", new ItemStack(Blocks.hopper));
        register("barsIron", new ItemStack(Blocks.iron_bars));
        register("chestWood", new ItemStack(Blocks.chest));
    }

    private static void register(String key, ItemStack stack) {
        if (!OreDictionary.doesOreNameExist(key)) {
            OreDictionary.registerOre(key, stack);
        }
    }
}
