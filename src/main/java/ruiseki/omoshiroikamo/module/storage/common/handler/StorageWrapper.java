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
import ruiseki.omoshiroikamo.core.helper.ItemStackHelpers;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.StorageItemStackHandler;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.UpgradeItemStackHandler;
import ruiseki.omoshiroikamo.module.storage.common.block.BlockBarrel;
import ruiseki.omoshiroikamo.module.storage.common.item.ItemStackUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IFeedingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IFilterUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IMagnetUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IPickupUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.ITickable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IVoidUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperBase;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperFactory;

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

    private final Map<Integer, UpgradeWrapperBase> wrapperCache = new HashMap<>();

    public StorageWrapper() {
        this(120, 7);
    }

    public StorageWrapper(BlockBarrel.ItemBarrel barrel) {
        this(barrel.slots, barrel.upgradeSlots);
    }

    public StorageWrapper(int storageSlots, int upgradeSlots) {
        this.storageSlots = storageSlots;
        this.upgradeSlots = upgradeSlots;
        this.sortType = SortType.BY_NAME;
        this.keepTab = true;

        this.storageItemStackHandler = new StorageItemStackHandler(storageSlots, this);
        this.upgradeItemStackHandler = new UpgradeItemStackHandler(upgradeSlots);
        upgradeItemStackHandler.setOnSlotChanged((slot, stack) -> { rebuildWrapperCache(); });

    }

    @Override
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

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        return storageItemStackHandler.extractItem(slot, amount, simulate);
    }

    @Override
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

    public void tick() {
        if (wrapperCache.isEmpty()) return;
        Map<Integer, UpgradeWrapperBase> snapshot = new HashMap<>(wrapperCache);

        for (Map.Entry<Integer, UpgradeWrapperBase> entry : snapshot.entrySet()) {
            UpgradeWrapperBase wrapper = entry.getValue();
            if (wrapper instanceof ITickable tickable) {
                tickable.tick();
            }
        }
    }

    // ---------- UPGRADE ----------
    public int getTotalStackMultiplier() {
        List<ItemStack> stacks = upgradeItemStackHandler.getStacks();
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

    public boolean feed(EntityPlayer player, IItemHandler handler) {
        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IFeedingUpgrade upgrade) {
                if (upgrade.feed(player, handler)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Entity> getMagnetEntities(World world, AxisAlignedBB aabb) {
        Set<Entity> result = new HashSet<>();

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, aabb);
        List<EntityXPOrb> xps = world.getEntitiesWithinAABB(EntityXPOrb.class, aabb);

        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IMagnetUpgrade upgrade) {
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
        }
        return new ArrayList<>(result);
    }

    public boolean canInsert(ItemStack stack) {
        if (wrapperCache.isEmpty()) return true;
        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IFilterUpgrade upgrade) {
                if (upgrade.canInsert(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canExtract(ItemStack stack) {
        if (wrapperCache.isEmpty()) return true;
        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IFilterUpgrade upgrade) {
                if (upgrade.canExtract(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {
        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IPickupUpgrade upgrade) {
                if (upgrade.canPickup(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canVoid(ItemStack newStack, IVoidUpgrade.VoidType type, IVoidUpgrade.VoidInput input) {
        for (UpgradeWrapperBase wrapper : wrapperCache.values()) {
            if (wrapper instanceof IVoidUpgrade upgrade) {
                if (upgrade.canVoid(newStack) && upgrade.getVoidType() == type
                    && (upgrade.getVoidInput() == input || input == IVoidUpgrade.VoidInput.AUTOMATION)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasCustomInventoryName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(STORAGE_SLOTS, storageSlots);
        tag.setInteger(UPGRADE_SLOTS, upgradeSlots);
        tag.setInteger(MAIN_COLOR, getMainColor());
        tag.setInteger(ACCENT_COLOR, getAccentColor());

        tag.setTag(BACKPACK_INV, storageItemStackHandler.serializeNBT());
        tag.setTag(UPGRADE_INV, upgradeItemStackHandler.serializeNBT());

        NBTTagCompound memoryTag = new NBTTagCompound();
        ItemStackHelpers.saveAllSlotsExtended(memoryTag, storageItemStackHandler.memorizedSlotStack);
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
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
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

            ItemStackHelpers
                .loadAllItemsExtended(tag.getCompoundTag(BACKPACK_INV), storageItemStackHandler.getStacks());
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
            ItemStackHelpers.loadAllItemsExtended(
                tag.getCompoundTag(MEMORY_STACK_ITEMS_TAG),
                storageItemStackHandler.memorizedSlotStack);
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

        rebuildWrapperCache();
    }

    public <T> Map<Integer, T> gatherCapabilityUpgrades(Class<T> capabilityClass) {
        Map<Integer, T> result = new HashMap<>();

        for (int slotIndex = 0; slotIndex < upgradeSlots; slotIndex++) {
            int finalSlotIndex = slotIndex;
            ItemStack stack = upgradeItemStackHandler.getStackInSlot(slotIndex);
            if (stack == null) continue;

            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this);
            if (wrapper == null) continue;

            if (capabilityClass.isAssignableFrom(wrapper.getClass())) {
                result.put(slotIndex, capabilityClass.cast(wrapper));
            }
        }

        return result;
    }

    private void rebuildWrapperCache() {
        wrapperCache.clear();

        for (int slot = 0; slot < upgradeSlots; slot++) {
            ItemStack stack = upgradeItemStackHandler.getStackInSlot(slot);
            if (stack == null) continue;

            int finalSlot = slot;

            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this);

            if (wrapper != null) {
                wrapperCache.put(slot, wrapper);
            }
        }
    }
}
