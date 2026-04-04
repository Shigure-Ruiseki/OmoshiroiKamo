package ruiseki.omoshiroikamo.module.machinery.common.init;

import net.minecraftforge.oredict.OreDictionary;

import ruiseki.omoshiroikamo.module.machinery.common.item.EnumMaterial;

/**
 * Add OreDictionary entries for machinery items.
 */
public class MachineryOreDict {

    public static void init() {
        for (EnumMaterial material : EnumMaterial.values()) {

            OreDictionary
                .registerOre("ingot" + material.getOreName(), MachineryItems.INGOT.newItemStack(1, material.getMeta()));

            OreDictionary
                .registerOre("plate" + material.getOreName(), MachineryItems.PLATE.newItemStack(1, material.getMeta()));

            OreDictionary
                .registerOre("dust" + material.getOreName(), MachineryItems.DUST.newItemStack(1, material.getMeta()));
        }
    }
}
