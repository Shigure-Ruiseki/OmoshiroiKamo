package ruiseki.omoshiroikamo.common.achievement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;

public class AchievementsRegistry {

    public static final List<ItemStack> craftingList = new ArrayList<>();
    public static final List<ItemStack> pickupList = new ArrayList<>();

    public static void init() {
        addItemsToCraftingList();
        addBlocksToCraftingList();
        addItemsToPickupList();
        addBlocksToPickupList();
    }

    public static void addItemsToCraftingList() {
        craftingList.add(ModItems.ASSEMBLER.newItemStack());
    }

    public static void addBlocksToCraftingList() {
        craftingList.add(ModBlocks.MODIFIER_NULL.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_SPEED.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_PIEZO.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_ACCURACY.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_JUMP_BOOST.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_FLIGHT.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_RESISTANCE.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_FIRE_RESISTANCE.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_HASTE.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_STRENGTH.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_NIGHT_VISION.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_WATER_BREATHING.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_REGENERATION.newItemStack());
        craftingList.add(ModBlocks.MODIFIER_SATURATION.newItemStack());
    }

    public static void addItemsToPickupList() {
        // add pickups if needed
    }

    public static void addBlocksToPickupList() {}

    public static Achievement getAchievementForItem(ItemStack stack) {
        Item item = stack.getItem();
        int meta = stack.getItemDamage();

        if (item == ModItems.ASSEMBLER.getItem()) {
            return ModAchievements.CRAFT_ASSEMBLER.get();
        }

        if (item == ModBlocks.MODIFIER_NULL.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_CORE.get();
        }
        if (item == ModBlocks.MODIFIER_SPEED.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_SPEED.get();
        }
        if (item == ModBlocks.MODIFIER_PIEZO.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_PIEZO.get();
        }
        if (item == ModBlocks.MODIFIER_ACCURACY.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_ACCURACY.get();
        }
        if (item == ModBlocks.MODIFIER_JUMP_BOOST.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_JUMP_BOOST.get();
        }
        if (item == ModBlocks.MODIFIER_FLIGHT.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_FLIGHT.get();
        }
        if (item == ModBlocks.MODIFIER_RESISTANCE.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_RESISTANCE.get();
        }
        if (item == ModBlocks.MODIFIER_FIRE_RESISTANCE.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_FIRE_RES.get();
        }
        if (item == ModBlocks.MODIFIER_HASTE.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_HASTE.get();
        }
        if (item == ModBlocks.MODIFIER_STRENGTH.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_STRENGTH.get();
        }
        if (item == ModBlocks.MODIFIER_NIGHT_VISION.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_NIGHT_VISION.get();
        }
        if (item == ModBlocks.MODIFIER_WATER_BREATHING.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_WATER_BREATHING.get();
        }
        if (item == ModBlocks.MODIFIER_REGENERATION.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_REGEN.get();
        }
        if (item == ModBlocks.MODIFIER_SATURATION.getItem()) {
            return ModAchievements.CRAFT_MODIFIER_SATURATION.get();
        }

        return null;
    }
}
