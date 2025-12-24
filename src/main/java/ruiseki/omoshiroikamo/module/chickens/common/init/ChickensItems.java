package ruiseki.omoshiroikamo.module.chickens.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemAnalyzer;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemChicken;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemChickenCatcher;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemChickenSpawnEgg;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemColoredEgg;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemLiquidEgg;
import ruiseki.omoshiroikamo.module.chickens.common.item.ItemSolidXp;

public enum ChickensItems {

    // spotless: off

    ANALYZER(new ItemAnalyzer()),
    CHICKEN_CATCHER(new ItemChickenCatcher()),
    CHICKEN_SPAWN_EGG(new ItemChickenSpawnEgg()),
    CHICKEN(new ItemChicken()),
    COLORED_EGG(new ItemColoredEgg()),
    LIQUID_EGG(new ItemLiquidEgg()),
    SOLID_XP(new ItemSolidXp()),

    //
    ;
    // spotless: on

    public static final ChickensItems[] VALUES = values();

    public static void preInit() {
        for (ChickensItems item : VALUES) {
            if (BackportConfigs.useChicken) {
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
    private final Item item;

    ChickensItems(Item item) {
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
