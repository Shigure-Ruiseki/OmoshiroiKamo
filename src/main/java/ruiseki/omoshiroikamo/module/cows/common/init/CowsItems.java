package ruiseki.omoshiroikamo.module.cows.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cows.common.item.ItemCowHalter;
import ruiseki.omoshiroikamo.module.cows.common.item.ItemCowSpawnEgg;

public enum CowsItems {

    // spotless: off

    COW_HALTER(new ItemCowHalter()),
    COW_SPAWN_EGG(new ItemCowSpawnEgg()),

    //
    ;
    // spotless: on

    public static final CowsItems[] VALUES = values();

    public static void preInit() {
        for (CowsItems item : VALUES) {
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

    CowsItems(Item item) {
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
