package ruiseki.omoshiroikamo.module.storage.common.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;

import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockBackpack;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemEverlastingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemInceptionUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IFeedingUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IFilterUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IMagnetUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IPickupUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IVoidUpgrade;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapperFactory;
import ruiseki.omoshiroikamo.module.backpack.common.util.BackpackItemStackUtils;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.StorageItemStackHandler;

public class StorageWrapper implements IStorageWrapper {

    public final StorageItemStackHandler storageItemStackHandler;
    public final UpgradeItemStackHandler upgradeItemStackHandler;
    public SortType sortType;
    public boolean keepTab;
    public String customName;
    public int storageSlots;
    public int upgradeSlots;
    public int mainColor;
    public int accentColor;

    public static final String STORAGE_NBT = "StorageNBT";
    public static final String BACKPACK_INV = "StorageInventory";
    public static final String UPGRADE_INV = "UpgradeInventory";
    public static final String STORAGE_SLOTS = "StorageSlots";
    public static final String UPGRADE_SLOTS = "UpgradeSlots";
    public static final String MEMORY_STACK_ITEMS_TAG = "MemoryItems";
    public static final String MEMORY_STACK_RESPECT_NBT_TAG = "MemoryRespectNBT";
    public static final String SORT_TYPE_TAG = "SortType";
    public static final String LOCKED_SLOTS_TAG = "LockedSlots";
    public static final String KEEP_TAB_TAG = "KeepTab";
    public static final String CUSTOM_NAME_TAG = "CustomName";
    public static final String MAIN_COLOR = "MainColor";
    public static final String ACCENT_COLOR = "AccentColor";

    public StorageWrapper() {
        this(120, 7);
    }

    public StorageWrapper(int storageSlots, int upgradeSlots) {
        this.storageSlots = storageSlots;
        this.upgradeSlots = upgradeSlots;
        this.sortType = SortType.BY_NAME;
        this.keepTab = true;

        this.storageItemStackHandler = new StorageItemStackHandler(storageSlots, this);
        this.upgradeItemStackHandler = new UpgradeItemStackHandler(upgradeSlots);
    }

    public String getDisplayName() {
        if (hasCustomInventoryName()) {
            return this.customName;
        }
        return LangHelpers.localize("container.inventory");
    }

    @Override
    public int getSlots() {
        return storageItemStackHandler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return storageItemStackHandler.getStackInSlot(slot);
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        return storageItemStackHandler.prioritizedInsertion(slot, stack, simulate);
    }

    public @Nullable ItemStack insertItem(@Nullable ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) return null;

        // Void ANY
        if (canVoid(stack, IVoidUpgrade.VoidType.ANY, IVoidUpgrade.VoidInput.ALL)) {
            return simulate ? stack : null;
        }

        // Void Overflow early have max item
        for (int i = 0; i < storageItemStackHandler.getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack != null && ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {

                int limit = getSlotLimit(i);
                if (slotStack.stackSize >= limit
                    && canVoid(stack, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.ALL)) {
                    return null;
                }
            }
        }

        ItemStack remaining = stack;

        for (int i = 0; i < storageItemStackHandler.getSlots() && remaining != null; i++) {

            int before = remaining.stackSize;
            remaining = insertItem(i, remaining, simulate);

            boolean changed = (remaining == null) || (remaining.stackSize != before);

            // Void Overflow
            if (changed && remaining != null && remaining.stackSize > 0) {
                if (canVoid(remaining, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.ALL)) {
                    return simulate ? remaining : null;
                }
            }
        }

        if (remaining != null && remaining.stackSize > 0) {
            if (simulate) {
                return null;
            }
        }

        return remaining;
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        return storageItemStackHandler.extractItem(slot, amount, simulate);
    }

    public ItemStack extractItem(ItemStack wanted, int amount, boolean simulate) {
        if (wanted == null || amount <= 0) return null;

        int remaining = amount;
        ItemStack result = null;

        for (int i = 0; i < storageItemStackHandler.getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack != null && slotStack.isItemEqual(wanted)) {
                int take = Math.min(slotStack.stackSize, remaining);
                ItemStack extracted = extractItem(i, take, simulate);

                if (result == null) {
                    result = extracted;
                } else if (extracted != null) {
                    result.stackSize += extracted.stackSize;
                }

                remaining -= take;
                if (remaining <= 0) break;
            }
        }

        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return storageItemStackHandler.getSlotLimit(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        storageItemStackHandler.setStackInSlot(slot, stack);
    }

    // Setting

    public boolean isSlotMemorized(int slotIndex) {
        return !(storageItemStackHandler.memorizedSlotStack.get(slotIndex) == null);
    }

    public ItemStack getMemorizedStack(int slotIndex) {
        return storageItemStackHandler.memorizedSlotStack.get(slotIndex);
    }

    public void setMemoryStack(int slotIndex, boolean respectNBT) {
        ItemStack currentStack = getStackInSlot(slotIndex);
        if (currentStack == null) return;

        ItemStack copiedStack = currentStack.copy();
        copiedStack.stackSize = 1;
        storageItemStackHandler.memorizedSlotStack.set(slotIndex, copiedStack);
        storageItemStackHandler.memorizedSlotRespectNbtList.set(slotIndex, respectNBT);
    }

    public void unsetMemoryStack(int slotIndex) {
        storageItemStackHandler.memorizedSlotStack.set(slotIndex, null);
        storageItemStackHandler.memorizedSlotRespectNbtList.set(slotIndex, false);
    }

    public boolean isMemoryStackRespectNBT(int slotIndex) {
        return storageItemStackHandler.memorizedSlotRespectNbtList.get(slotIndex);
    }

    public void setMemoryStackRespectNBT(int slotIndex, boolean respect) {
        storageItemStackHandler.memorizedSlotRespectNbtList.set(slotIndex, respect);
    }

    public boolean isSlotLocked(int slotIndex) {
        return storageItemStackHandler.sortLockedSlots.get(slotIndex);
    }

    public void setSlotLocked(int slotIndex, boolean locked) {
        storageItemStackHandler.sortLockedSlots.set(slotIndex, locked);
    }

    @Override
    public int getMainColor() {
        return mainColor;
    }

    @Override
    public int getAccentColor() {
        return accentColor;
    }

    @Override
    public void setColors(int mainColor, int accentColor) {
        this.mainColor = mainColor;
        this.accentColor = accentColor;
    }

    // ---------- UPGRADE ----------
    public int getTotalStackMultiplier() {
        List<ItemStack> stacks = upgradeItemStackHandler.getStacks();
        int result = 0;
        boolean hasstackUpdateOmega = false;

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
            if (stack.equals(BackpackItems.STACK_UPGRADE.newItemStack(1, 4))) {
                hasstackUpdateOmega = true;
            }
        }
        if (hasstackUpdateOmega) return BackpackConfig.stackUpgradeTierOmegaMul;
        else return result == 0 ? 1 : result;
    }

    public int getStackMultiplierExcluding(int excludeSlot, ItemStack replacement) {
        List<ItemStack> stacks = upgradeItemStackHandler.getStacks();

        int result = 0;
        for (int i = 0; i < upgradeItemStackHandler.getSlots(); i++) {
            ItemStack stack = stacks.get(i);

            if (i == excludeSlot) {
                if (replacement != null && replacement.getItem() instanceof ItemStackUpgrade up) {
                    result += up.multiplier(replacement);
                }
                continue;
            }

            if (stack == null) {
                continue;
            }

            if (stack.getItem() instanceof ItemStackUpgrade up) {
                result += up.multiplier(stack);
            }
        }
        return result == 0 ? 1 : result;
    }

    public boolean canAddStackUpgrade(int newMultiplier) {
        long result = (long) getTotalStackMultiplier() * 64L * newMultiplier;
        return result == (int) result;
    }

    public boolean canReplaceStackUpgrade(int slotIndex, ItemStack replacement) {
        ItemStack old = upgradeItemStackHandler.getStacks()
            .get(slotIndex);

        if (old != null && replacement != null
            && old.getItem() instanceof ItemStackUpgrade oldUp
            && replacement.getItem() instanceof ItemStackUpgrade newUp
            && oldUp.multiplier(old) == newUp.multiplier(replacement)) {
            return true;
        }

        int newStackMultiplier = getStackMultiplierExcluding(slotIndex, replacement);

        for (ItemStack stack : storageItemStackHandler.getStacks()) {
            if (stack == null) continue;

            if (stack.stackSize > stack.getMaxStackSize() * newStackMultiplier) {
                return false;
            }
        }

        return true;
    }

    public boolean canNestBackpack() {
        for (ItemStack stack : upgradeItemStackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemInceptionUpgrade) {
                return true;
            }
        }
        return false;
    }

    public boolean canRemoveInceptionUpgrade() {
        boolean containsBackpack = false;
        for (ItemStack stack : storageItemStackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof BlockBackpack.ItemBackpack) {
                containsBackpack = true;
                break;
            }
        }

        if (!containsBackpack) {
            return true;
        }

        int inceptionCount = 0;
        for (ItemStack stack : upgradeItemStackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemInceptionUpgrade) {
                inceptionCount++;
            }
        }

        return inceptionCount > 1;
    }

    public boolean feed(EntityPlayer player, IItemHandler handler) {
        for (IFeedingUpgrade upgrade : gatherCapabilityUpgrades(IFeedingUpgrade.class).values()) {
            if (upgrade.feed(player, handler)) {
                return true;
            }
        }
        return false;
    }

    public List<Entity> getMagnetEntities(World world, AxisAlignedBB aabb) {
        Set<Entity> result = new HashSet<>();

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, aabb);
        List<EntityXPOrb> xps = world.getEntitiesWithinAABB(EntityXPOrb.class, aabb);

        for (IMagnetUpgrade upgrade : gatherCapabilityUpgrades(IMagnetUpgrade.class).values()) {
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
        Map<Integer, IFilterUpgrade> upgrades = gatherCapabilityUpgrades(IFilterUpgrade.class);

        if (upgrades.isEmpty()) {
            return true;
        }

        for (IFilterUpgrade upgrade : upgrades.values()) {
            if (upgrade.canInsert(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canExtract(ItemStack stack) {
        Map<Integer, IFilterUpgrade> upgrades = gatherCapabilityUpgrades(IFilterUpgrade.class);

        if (upgrades.isEmpty()) {
            return true;
        }

        for (IFilterUpgrade upgrade : upgrades.values()) {
            if (upgrade.canExtract(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {

        for (IPickupUpgrade upgrade : gatherCapabilityUpgrades(IPickupUpgrade.class).values()) {
            if (upgrade.canPickup(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean canVoid(ItemStack newStack, IVoidUpgrade.VoidType type, IVoidUpgrade.VoidInput input) {

        for (IVoidUpgrade upgrade : gatherCapabilityUpgrades(IVoidUpgrade.class).values()) {
            if (upgrade.canVoid(newStack) && upgrade.getVoidType() == type
                && (upgrade.getVoidInput() == input || input == IVoidUpgrade.VoidInput.AUTOMATION)) {
                return true;
            }
        }
        return false;
    }

    public boolean canImportant() {

        for (ItemStack stack : upgradeItemStackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemEverlastingUpgrade) {
                return true;
            }
        }
        return false;
    }
    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    // ---------- TILE ENTITY ----------
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(STORAGE_SLOTS, storageSlots);
        tag.setInteger(UPGRADE_SLOTS, upgradeSlots);
        tag.setInteger(MAIN_COLOR, getMainColor());
        tag.setInteger(ACCENT_COLOR, getAccentColor());

        tag.setTag(BACKPACK_INV, storageItemStackHandler.serializeNBT());
        tag.setTag(UPGRADE_INV, upgradeItemStackHandler.serializeNBT());

        NBTTagCompound memoryTag = new NBTTagCompound();
        BackpackItemStackUtils.saveAllSlotsExtended(memoryTag, storageItemStackHandler.memorizedSlotStack);
        tag.setTag(MEMORY_STACK_ITEMS_TAG, memoryTag);

        List<Boolean> respectList = storageItemStackHandler.memorizedSlotRespectNbtList;
        byte[] respectBytes = new byte[respectList.size()];
        for (int i = 0; i < respectList.size(); i++) {
            respectBytes[i] = (byte) (respectList.get(i) ? 1 : 0);
        }
        tag.setByteArray(MEMORY_STACK_RESPECT_NBT_TAG, respectBytes);

        List<Boolean> locked = storageItemStackHandler.sortLockedSlots;
        byte[] lockedBytes = new byte[locked.size()];
        for (int i = 0; i < locked.size(); i++) {
            lockedBytes[i] = (byte) (locked.get(i) ? 1 : 0);
        }
        tag.setByteArray(LOCKED_SLOTS_TAG, lockedBytes);

        tag.setByte(SORT_TYPE_TAG, (byte) sortType.ordinal());

        tag.setBoolean(KEEP_TAB_TAG, keepTab);

        if (hasCustomInventoryName()) {
            tag.setString(CUSTOM_NAME_TAG, this.customName);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {

        if (tag.hasKey(STORAGE_SLOTS)) {
            storageSlots = tag.getInteger(STORAGE_SLOTS);
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
            storageItemStackHandler.deserializeNBT(tag.getCompoundTag(BACKPACK_INV));

            BackpackItemStackUtils.loadAllItemsExtended(tag.getCompoundTag(BACKPACK_INV), storageItemStackHandler.getStacks());
            if (storageItemStackHandler.getSlots() != storageSlots) {
                storageItemStackHandler.resize(storageSlots);
            }
        }
        if (tag.hasKey(UPGRADE_INV)) {
            upgradeItemStackHandler.deserializeNBT(tag.getCompoundTag(UPGRADE_INV));
            if (upgradeItemStackHandler.getSlots() != upgradeSlots) {
                upgradeItemStackHandler.resize(upgradeSlots);
            }
        }

        if (tag.hasKey(MEMORY_STACK_ITEMS_TAG)) {
            BackpackItemStackUtils
                .loadAllItemsExtended(tag.getCompoundTag(MEMORY_STACK_ITEMS_TAG), storageItemStackHandler.memorizedSlotStack);
        }

        byte[] respectArr = tag.getByteArray(MEMORY_STACK_RESPECT_NBT_TAG);
        int maxRespect = storageItemStackHandler.memorizedSlotRespectNbtList.size();
        for (int i = 0; i < respectArr.length && i < maxRespect; i++) {
            setMemoryStackRespectNBT(i, respectArr[i] != 0);
        }

        byte[] lockedArr = tag.getByteArray(LOCKED_SLOTS_TAG);
        int maxLocked = storageItemStackHandler.sortLockedSlots.size();
        for (int i = 0; i < lockedArr.length && i < maxLocked; i++) {
            setSlotLocked(i, lockedArr[i] != 0);
        }

        if (tag.hasKey(SORT_TYPE_TAG)) {
            sortType = SortType.values()[tag.getByte(SORT_TYPE_TAG)];
        }

        if (tag.hasKey(KEEP_TAB_TAG)) {
            keepTab = tag.getBoolean(KEEP_TAB_TAG);
        }

        if (tag.hasKey("display", 10)) {
            NBTTagCompound display = tag.getCompoundTag("display");
            if (display.hasKey("Name", 8)) {
                customName = display.getString("Name");
            }
        } else {
            customName = tag.getString(CUSTOM_NAME_TAG);
        }
    }

    public <T> Map<Integer, T> gatherCapabilityUpgrades(Class<T> capabilityClass) {
        Map<Integer, T> result = new HashMap<>();

        for (int i = 0; i < upgradeSlots; i++) {
            ItemStack stack = upgradeItemStackHandler.getStackInSlot(i);
            if (stack == null) continue;

            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper == null) continue;

            if (capabilityClass.isAssignableFrom(wrapper.getClass())) {
                result.put(i, capabilityClass.cast(wrapper));
            }
        }

        return result;
    }
}
