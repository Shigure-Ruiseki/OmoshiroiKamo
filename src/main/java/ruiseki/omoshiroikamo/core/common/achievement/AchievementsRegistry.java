package ruiseki.omoshiroikamo.core.common.achievement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;

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
        craftingList.add(MultiBlockItems.ASSEMBLER.newItemStack());
    }

    public static void addBlocksToCraftingList() {
        craftingList.add(MultiBlockBlocks.MODIFIER_NULL.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_SPEED.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_PIEZO.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_ACCURACY.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_JUMP_BOOST.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_FLIGHT.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_RESISTANCE.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_HASTE.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_STRENGTH.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_NIGHT_VISION.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_WATER_BREATHING.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_REGENERATION.newItemStack());
        craftingList.add(MultiBlockBlocks.MODIFIER_SATURATION.newItemStack());
    }

    public static void addItemsToPickupList() {
        // add pickups if needed
    }

    public static void addBlocksToPickupList() {}

    public static Achievement getAchievementForItem(ItemStack stack) {
        Item item = stack.getItem();
        int meta = stack.getItemDamage();

        if (item == MultiBlockItems.ASSEMBLER.getItem()) {
            return MultiBlockAchievements.CRAFT_ASSEMBLER.get();
        }

        if (item == MultiBlockBlocks.MODIFIER_NULL.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_CORE.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_SPEED.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_SPEED.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_PIEZO.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_PIEZO.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_ACCURACY.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_ACCURACY.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_JUMP_BOOST.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_JUMP_BOOST.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_FLIGHT.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_FLIGHT.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_RESISTANCE.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_RESISTANCE.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_FIRE_RESISTANCE.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_FIRE_RES.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_HASTE.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_HASTE.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_STRENGTH.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_STRENGTH.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_NIGHT_VISION.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_NIGHT_VISION.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_WATER_BREATHING.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_WATER_BREATHING.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_REGENERATION.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_REGEN.get();
        }
        if (item == MultiBlockBlocks.MODIFIER_SATURATION.getItem()) {
            return MultiBlockAchievements.CRAFT_MODIFIER_SATURATION.get();
        }

        return null;
    }
}
