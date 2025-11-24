package ruiseki.omoshiroikamo.common.block.backpack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.BackpackItemStackHandler;
import ruiseki.omoshiroikamo.common.item.backpack.ItemStackUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedPickupUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.DepositUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.FeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IFeedingUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.PickupUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.RestockUpgradeWrapper;
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
    private final ItemStackHandler upgradeHandler;
    @Getter
    private static final String BACKPACK_INV = "BackpackInv";
    @Getter
    private static final String UPGRADE_INV = "UpgradeInv";
    @Getter
    private static final String BACKPACK_SLOTS = "BackpackSlots";
    @Getter
    private static final String UPGRADE_SLOTS = "UpgradeSlots";
    @Getter
    private static final String OPEN_GUI = "OpenGui";

    @Getter
    @Setter
    private int backpackSlots;
    @Getter
    @Setter
    private int upgradeSlots;

    public BackpackHandler(ItemStack backpack, TileEntity tile) {
        this(backpack, tile, 120, 7);
    }

    public BackpackHandler(ItemStack backpack, TileEntity tile, BlockBackpack.ItemBackpack itemBackpack) {
        this(backpack, tile, itemBackpack.getBackpackSlots(), itemBackpack.getUpgradeSlots());
    }

    public BackpackHandler(ItemStack backpack, TileEntity tile, int backpackSlots, int upgradeSlots) {
        this.backpack = backpack;
        this.tile = tile;
        this.backpackSlots = backpackSlots;
        this.upgradeSlots = upgradeSlots;

        this.backpackHandler = new BackpackItemStackHandler(backpackSlots, this) {

            @Override
            protected void onContentsChanged(int slots) {
                super.onContentsChanged(slots);
                writeToItem();
            }
        };

        this.upgradeHandler = new ItemStackHandler(upgradeSlots) {

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

    public ItemStack getFeedingStack(int foodLevel, float health, float maxHealth) {

        List<IFeedingUpgrade> list = new ArrayList<>();
        list.addAll(gatherCapabilityUpgrades(AdvancedFeedingUpgradeWrapper.class));
        list.addAll(gatherCapabilityUpgrades(FeedingUpgradeWrapper.class));

        for (IFeedingUpgrade upgrade : list) {
            ItemStack feedingStack = upgrade.getFeedingStack(backpackHandler, foodLevel, health, maxHealth);

            if (feedingStack != null && feedingStack.stackSize > 0) {
                return feedingStack;
            }
        }

        return null;
    }

    public boolean canDeposit(int slotIndex) {
        ItemStack stack = getStackInSlot(slotIndex);
        for (DepositUpgradeWrapper upgrade : gatherCapabilityUpgrades(DepositUpgradeWrapper.class)) {
            if (upgrade.canDeposit(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canRestock(ItemStack stack) {
        for (RestockUpgradeWrapper upgrade : gatherCapabilityUpgrades(RestockUpgradeWrapper.class)) {
            if (upgrade.canRestock(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {

        for (AdvancedPickupUpgradeWrapper upgrade : gatherCapabilityUpgrades(AdvancedPickupUpgradeWrapper.class)) {
            if (upgrade.canPickup(stack)) {
                return true;
            }
        }

        for (PickupUpgradeWrapper upgrade : gatherCapabilityUpgrades(PickupUpgradeWrapper.class)) {
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

            if (capabilityClass.isInstance(wrapper)) {
                result.add(capabilityClass.cast(wrapper));
            }
        }

        return result;
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
        tag.setTag(BACKPACK_INV, backpackHandler.serializeNBT());
        tag.setTag(UPGRADE_INV, upgradeHandler.serializeNBT());
    }

    public void readFromNBT(NBTTagCompound tag) {

        if (tag.hasKey(BACKPACK_SLOTS)) {
            backpackSlots = tag.getInteger(BACKPACK_SLOTS);
            if (backpackHandler.getSlots() != backpackSlots) {
                backpackHandler.setSize(backpackSlots);
            }
        }
        if (tag.hasKey(UPGRADE_SLOTS)) {
            upgradeSlots = tag.getInteger(UPGRADE_SLOTS);
            if (upgradeHandler.getSlots() != upgradeSlots) {
                upgradeHandler.setSize(upgradeSlots);
            }
        }

        if (tag.hasKey(BACKPACK_INV)) {
            backpackHandler.deserializeNBT(tag.getCompoundTag(BACKPACK_INV));
        }
        if (tag.hasKey(UPGRADE_INV)) {
            upgradeHandler.deserializeNBT(tag.getCompoundTag(UPGRADE_INV));
        }
    }

    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

}
