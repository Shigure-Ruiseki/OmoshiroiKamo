package ruiseki.omoshiroikamo.common.block.backpack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.BackpackItemStackHandler;
import ruiseki.omoshiroikamo.client.gui.modularui2.handler.UpgradeItemStackHandler;
import ruiseki.omoshiroikamo.common.item.backpack.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemInceptionUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IFilterUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IMagnetUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IPickupUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapperFactory;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class BackpackHandler implements IItemHandlerModifiable {

    @Getter
    private final ItemStack backpack;
    @Getter
    private final TileEntity tile;
    @Getter
    private final BackpackItemStackHandler backpackHandler;
    @Getter
    private final UpgradeItemStackHandler upgradeHandler;
    @Getter
    public static final String BACKPACK_INV = "BackpackInv";
    @Getter
    public static final String UPGRADE_INV = "UpgradeInv";
    @Getter
    public static final String BACKPACK_SLOTS = "BackpackSlots";
    @Getter
    public static final String UPGRADE_SLOTS = "UpgradeSlots";

    @Getter
    @Setter
    private int backpackSlots;
    @Getter
    @Setter
    private int upgradeSlots;

    @Getter
    @Setter
    private int mainColor;
    @Getter
    @Setter
    private int accentColor;
    @Getter
    public static final String MAIN_COLOR = "MainColor";
    @Getter
    public static final String ACCENT_COLOR = "AccentColor";

    public BackpackHandler(ItemStack backpack, TileEntity tile) {
        this(backpack, tile, 120, 7);
    }

    public BackpackHandler(ItemStack backpack, TileEntity tile, BlockBackpack.ItemBackpack itemBackpack) {
        this(backpack, tile, itemBackpack.getBackpackSlots(), itemBackpack.getUpgradeSlots());
    }

    public BackpackHandler(ItemStack backpack, TileEntity tile, BlockBackpack blockBackpack) {
        this(backpack, tile, blockBackpack.getBackpackSlots(), blockBackpack.getUpgradeSlots());
    }

    public BackpackHandler(ItemStack backpack, TileEntity tile, int backpackSlots, int upgradeSlots) {
        this.backpack = backpack;
        this.tile = tile;
        this.backpackSlots = backpackSlots;
        this.upgradeSlots = upgradeSlots;
        this.mainColor = 0xFFCC613A;
        this.accentColor = 0xFF622E1A;

        this.backpackHandler = new BackpackItemStackHandler(backpackSlots, this) {

            @Override
            protected void onContentsChanged(int slots) {
                super.onContentsChanged(slots);
                writeToItem();
            }
        };

        this.upgradeHandler = new UpgradeItemStackHandler(upgradeSlots) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                writeToItem();
            }
        };

        readFromItem();
    }

    public String getDisplayName() {
        if (backpack != null) {
            return backpack.getItem()
                .getUnlocalizedName(backpack);
        }
        if (tile != null) {
            return tile.getBlockType()
                .getUnlocalizedName();
        }

        return "container.inventory";
    }

    @Override
    public int getSlots() {
        return backpackHandler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return backpackHandler.getStackInSlot(slot);
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        return backpackHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        return backpackHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return backpackHandler.getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        backpackHandler.setStackInSlot(slot, stack);
    }

    // ---------- UPGRADE ----------
    public int getTotalStackMultiplier() {
        List<ItemStack> stacks = upgradeHandler.getStacks();
        int result = 0;

        if (stacks.isEmpty()) {
            return 1;
        }

        for (ItemStack stack : stacks) {
            if (stack == null) {
                continue;
            }
            if (stack.getItem() instanceof ItemStackUpgrade upgrade) {
                result += upgrade.multiplier(stack);
            }
        }
        return result == 0 ? 1 : result;
    }

    public boolean canAddStackUpgrade(int newMultiplier) {
        int currentMultiplier = getTotalStackMultiplier() * 64;

        try {
            Math.multiplyExact(currentMultiplier, newMultiplier);
            return true;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    public boolean canRemoveStackUpgrade(int originalMultiplier) {
        return canReplaceStackUpgrade(originalMultiplier, 1);
    }

    public boolean canReplaceStackUpgrade(int oldMultiplier, int newMultiplier) {
        int newStackMultiplier = getTotalStackMultiplier() / oldMultiplier * newMultiplier;

        for (ItemStack stack : backpackHandler.getStacks()) {
            if (stack == null) {
                continue;
            }

            if (stack.stackSize > stack.getMaxStackSize() * newStackMultiplier) {
                return false;
            }
        }

        return true;
    }

    public boolean canNestBackpack() {
        for (ItemStack stack : upgradeHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemInceptionUpgrade) {
                return true;
            }
        }
        return false;
    }

    public boolean canRemoveInceptionUpgrade() {
        boolean containsBackpack = false;
        for (ItemStack stack : backpackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof BlockBackpack.ItemBackpack) {
                containsBackpack = true;
                break;
            }
        }

        if (!containsBackpack) {
            return true;
        }

        int inceptionCount = 0;
        for (ItemStack stack : upgradeHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemInceptionUpgrade) {
                inceptionCount++;
            }
        }

        return inceptionCount > 1;
    }

    public ItemStack getFeedingStack(int foodLevel, float health, float maxHealth) {
        for (IFeedingUpgrade upgrade : gatherCapabilityUpgrades(IFeedingUpgrade.class)) {
            ItemStack feedingStack = upgrade.getFeedingStack(backpackHandler, foodLevel, health, maxHealth);

            if (feedingStack != null && feedingStack.stackSize > 0) {
                return feedingStack;
            }
        }

        return null;
    }

    public List<Entity> getMagnetEntities(World world, AxisAlignedBB aabb) {
        Set<Entity> result = new HashSet<>();

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, aabb);
        List<EntityXPOrb> xps = world.getEntitiesWithinAABB(EntityXPOrb.class, aabb);
        for (IMagnetUpgrade upgrade : gatherCapabilityUpgrades(IMagnetUpgrade.class)) {
            if (upgrade.isCollectItem()) {
                for (EntityItem item : items) {
                    if (upgrade.canCollectItem(item.getEntityItem())) {
                        result.add(item);
                    }
                }
            }
            if (upgrade.isCollectExp()) {
                result.addAll(xps);
            }
        }

        return new ArrayList<>(result);
    }

    public boolean canInsert(ItemStack stack) {
        List<IFilterUpgrade> upgrades = gatherCapabilityUpgrades(IFilterUpgrade.class);
        if (upgrades.isEmpty()) {
            return true;
        }
        for (IFilterUpgrade upgrade : upgrades) {
            if (upgrade.canInsert(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canExtract(ItemStack stack) {
        List<IFilterUpgrade> upgrades = gatherCapabilityUpgrades(IFilterUpgrade.class);
        if (upgrades.isEmpty()) {
            return true;
        }
        for (IFilterUpgrade upgrade : upgrades) {
            if (upgrade.canExtract(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {

        for (IPickupUpgrade upgrade : gatherCapabilityUpgrades(IPickupUpgrade.class)) {
            if (upgrade.canPickup(stack)) {
                return true;
            }
        }
        return false;
    }

    private <T> List<T> gatherCapabilityUpgrades(Class<T> capabilityClass) {
        List<T> result = new ArrayList<>();

        for (ItemStack stack : upgradeHandler.getStacks()) {
            if (stack == null) {
                continue;
            }
            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper == null) {
                continue;
            }

            if (capabilityClass.isAssignableFrom(wrapper.getClass())) {
                result.add(capabilityClass.cast(wrapper));
            }
        }

        return result;
    }

    public boolean canImportant() {

        for (ItemStack stack : upgradeHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemEverlastingUpgrade) {
                return true;
            }
        }
        return false;
    }

    // ---------- ITEM STACK ----------
    public void writeToItem() {
        if (backpack == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(backpack);
        writeToNBT(tag);
        backpack.setTagCompound(tag);
    }

    public void readFromItem() {
        if (backpack == null) {
            return;
        }
        NBTTagCompound tag = ItemNBTUtils.getNBT(backpack);
        readFromNBT(tag);
    }

    // ---------- TILE ENTITY ----------
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(BACKPACK_SLOTS, getBackpackSlots());
        tag.setInteger(UPGRADE_SLOTS, getUpgradeSlots());
        tag.setInteger(MAIN_COLOR, getMainColor());
        tag.setInteger(ACCENT_COLOR, getAccentColor());
        tag.setTag(BACKPACK_INV, backpackHandler.serializeNBT());
        tag.setTag(UPGRADE_INV, upgradeHandler.serializeNBT());
    }

    public void readFromNBT(NBTTagCompound tag) {

        if (tag.hasKey(BACKPACK_SLOTS)) {
            backpackSlots = tag.getInteger(BACKPACK_SLOTS);
        }
        if (tag.hasKey(UPGRADE_SLOTS)) {
            upgradeSlots = tag.getInteger(UPGRADE_SLOTS);
        }

        if (tag.hasKey(MAIN_COLOR)) {
            mainColor = tag.getInteger(MAIN_COLOR);
        }
        if (tag.hasKey(ACCENT_COLOR)) {
            accentColor = tag.getInteger(ACCENT_COLOR);
        }

        if (tag.hasKey(BACKPACK_INV)) {
            backpackHandler.deserializeNBT(tag.getCompoundTag(BACKPACK_INV));
            if (backpackHandler.getSlots() != backpackSlots) {
                backpackHandler.resize(backpackSlots);
            }
        }
        if (tag.hasKey(UPGRADE_INV)) {
            upgradeHandler.deserializeNBT(tag.getCompoundTag(UPGRADE_INV));
            if (upgradeHandler.getSlots() != upgradeSlots) {
                upgradeHandler.resize(upgradeSlots);
            }
        }
    }

    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

}
