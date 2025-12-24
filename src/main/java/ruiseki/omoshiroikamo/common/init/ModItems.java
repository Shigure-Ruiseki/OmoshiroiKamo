package ruiseki.omoshiroikamo.common.init;

import static ruiseki.omoshiroikamo.config.backport.BackportConfigs.useEnvironmentalTech;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemAssembler;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemCrystal;
import ruiseki.omoshiroikamo.common.item.multiblock.ItemStructureWand;
import ruiseki.omoshiroikamo.common.util.Logger;

public enum ModItems {

    // spotless: off

    CRYSTAL(useEnvironmentalTech, new ItemCrystal()),
    ASSEMBLER(useEnvironmentalTech, new ItemAssembler()),
    STABILIZED_ENDER_PEAR(useEnvironmentalTech, new ItemOK().setName(ModObject.itemStabilizedEnderPear)
        .setTextureName("ender_stabilized")),
    PHOTOVOLTAIC_CELL(useEnvironmentalTech, new ItemOK().setName(ModObject.itemPhotovoltaicCell)
        .setTextureName("photovoltaic_cell")),

    STRUCTURE_WAND(true, new ItemStructureWand()),
    //
    ;
    // spotless: on

    public static final ModItems[] VALUES = values();

    public static void preInit() {
        for (ModItems item : VALUES) {
            if (item.isEnabled()) {
                try {
                    GameRegistry.registerItem(item.getItem(), item.getName());
                    Logger.info("Successfully initialized " + item.name());
                } catch (Exception e) {
                    Logger.error("Failed to initialize item: +" + item.name());
                }
            }
        }
    }

    @Getter
    private final boolean enabled;
    @Getter
    private final Item item;

    ModItems(boolean enabled, Item item) {
        this.enabled = enabled;
        this.item = item;
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }
}
