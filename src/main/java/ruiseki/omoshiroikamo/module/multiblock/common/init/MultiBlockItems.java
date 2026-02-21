package ruiseki.omoshiroikamo.module.multiblock.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.module.multiblock.common.item.ItemAssembler;
import ruiseki.omoshiroikamo.module.multiblock.common.item.ItemCrystal;

public enum MultiBlockItems {

    // spotless: off

    CRYSTAL(new ItemCrystal()),
    ASSEMBLER(new ItemAssembler()),
    STABILIZED_ENDER_PEAR(new ItemOK().setName(ModObject.itemStabilizedEnderPear)
        .setTextureName("multiblock/ender_stabilized")),
    PHOTOVOLTAIC_CELL(new ItemOK().setName(ModObject.itemPhotovoltaicCell)
        .setTextureName("multiblock/photovoltaic_cell")),

    ;
    // spotless: on

    public static final MultiBlockItems[] VALUES = values();

    public static void preInit() {
        for (MultiBlockItems item : VALUES) {
            try {
                GameRegistry.registerItem(item.getItem(), item.getName());
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    @Getter
    private final Item item;

    MultiBlockItems(Item item) {
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
