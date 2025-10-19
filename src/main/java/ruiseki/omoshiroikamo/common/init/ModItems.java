package ruiseki.omoshiroikamo.common.init;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.handler.PoweredItemRenderer;
import ruiseki.omoshiroikamo.common.item.ItemAssembler;
import ruiseki.omoshiroikamo.common.item.ItemHammer;
import ruiseki.omoshiroikamo.common.item.ItemMaterial;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.item.ItemWireCoil;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBackpack;
import ruiseki.omoshiroikamo.common.item.backpack.ItemBatteryUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemCraftingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemLightUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemUpgrade;
import ruiseki.omoshiroikamo.common.ore.ItemOre;
import ruiseki.omoshiroikamo.common.util.Logger;

public enum ModItems {

    BACKPACK(true, ItemBackpack.create()),
    BASE_UPGRADE(true, ItemUpgrade.create()),
    STACK_UPGRADE(true, ItemStackUpgrade.create()),
    CRAFTING_UPGRADE(true, ItemCraftingUpgrade.create()),
    MAGNET_UPGRADE(true, ItemMagnetUpgrade.create()),
    FEEDING_UPGRADE(true, ItemFeedingUpgrade.create()),
    BATTERY_UPGRADE(true, ItemBatteryUpgrade.create()),
    EVERLASTING_UPGRADE(true, ItemEverlastingUpgrade.create()),
    LIGHT_UPGRADE(true, ItemLightUpgrade.create()),
    MATERIAL(true, ItemMaterial.create()),
    ORE(true, ItemOre.create()),
    HAMMER(true, ItemHammer.create()),
    ASSEMBLER(true, ItemAssembler.create()),
    STABILIZED_ENDER_PEAR(true, ItemOK.create(ModObject.itemStabilizedEnderPear, "ender_stabilized")),
    PHOTOVOLTAIC_CELL(true, ItemOK.create(ModObject.itemPhotovoltaicCell, "photovoltaic_cell")),
    WIRE_COIL(true, ItemWireCoil.create()),;

    public static final ModItems[] VALUES = values();

    public static void init() {
        for (ModItems item : VALUES) {
            if (!item.isEnabled()) {
                continue;
            }
            try {
                item.get()
                    .init();
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    private final boolean enabled;
    private final ItemOK item;

    ModItems(boolean enabled, ItemOK item) {
        this.enabled = enabled;
        this.item = item;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ItemOK get() {
        return item;
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.get(), count, meta);
    }

    public static void registerItemRenderer() {
        PoweredItemRenderer dsr = new PoweredItemRenderer();
        // MinecraftForgeClient.registerItemRenderer(itemOperationOrb, dsr);
    }
}
