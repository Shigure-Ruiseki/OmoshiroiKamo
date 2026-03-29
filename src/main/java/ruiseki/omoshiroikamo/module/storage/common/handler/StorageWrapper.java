package ruiseki.omoshiroikamo.module.storage.common.handler;

import java.util.*;

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
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.*;

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
    }

    @Override
    public String getDisplayName() {
        return hasCustomInventoryName() ? customName : LangHelpers.localize("container.inventory");
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

        // Void overflow early
        for (int i = 0; i < storageItemStackHandler.getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack != null && ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {
                if (slotStack.stackSize >= getSlotLimit(i)
                    && canVoid(stack, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.ALL)) {
                    return simulate ? stack : null;
                }
            }
        }

        ItemStack remaining = stack;
        for (int i = 0; i < storageItemStackHandler.getSlots() && remaining != null; i++) {
            int before = remaining.stackSize;
            remaining = insertItem(i, remaining, simulate);

            if (remaining != null && remaining.stackSize < before
                && canVoid(remaining, IVoidUpgrade.VoidType.OVERFLOW, IVoidUpgrade.VoidInput.ALL)) {
                return simulate ? remaining : null;
            }
        }

        return (remaining != null && remaining.stackSize > 0 && simulate) ? remaining : null;
    }

    @Override
    public ItemStack extractItem(ItemStack wanted, int amount, boolean simulate) {
        if (wanted == null || amount <= 0) return null;

        int remaining = amount;
        ItemStack result = null;

        for (int i = 0; i < storageItemStackHandler.getSlots() && remaining > 0; i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack != null && slotStack.isItemEqual(wanted)) {
                int take = Math.min(slotStack.stackSize, remaining);
                ItemStack extracted = extractItem(i, take, simulate);

                if (extracted != null) {
                    if (result == null) result = extracted.copy();
                    else result.stackSize += extracted.stackSize;
                    remaining -= extracted.stackSize;
                }
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

    public boolean isSlotMemorized(int slotIndex) {
        return storageItemStackHandler.memorizedSlotStack.get(slotIndex) != null;
    }

    public ItemStack getMemorizedStack(int slotIndex) {
        return storageItemStackHandler.memorizedSlotStack.get(slotIndex);
    }

    public void setMemoryStack(int slotIndex, boolean respectNBT) {
        ItemStack currentStack = getStackInSlot(slotIndex);
        if (currentStack == null) return;

        ItemStack copy = currentStack.copy();
        copy.stackSize = 1;
        storageItemStackHandler.memorizedSlotStack.set(slotIndex, copy);
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
        Map<Integer, ITickable> gathered = gatherCapabilityUpgrades(ITickable.class);
        if (gathered.isEmpty()) return;
        for (ITickable wrapper : gathered.values()) {
            wrapper.tick();
        }
    }

    public int getTotalStackMultiplier() {
        int result = 0;
        for (ItemStack stack : upgradeItemStackHandler.getStacks()) {
            if (stack != null && stack.getItem() instanceof ItemStackUpgrade up) {
                result += up.multiplier(stack);
            }
        }
        return result == 0 ? 1 : result;
    }

    public int getStackMultiplierExcluding(int excludeSlot, ItemStack replacement) {
        int result = 0;
        for (int i = 0; i < upgradeItemStackHandler.getSlots(); i++) {
            ItemStack stack = upgradeItemStackHandler.getStackInSlot(i);
            if (i == excludeSlot) {
                if (replacement != null && replacement.getItem() instanceof ItemStackUpgrade up) {
                    result += up.multiplier(replacement);
                }
            } else if (stack != null && stack.getItem() instanceof ItemStackUpgrade up) {
                result += up.multiplier(stack);
            }
        }
        return result == 0 ? 1 : result;
    }

    public boolean canAddStackUpgrade(int newMultiplier) {
        long result = (long) getTotalStackMultiplier() * 64L * newMultiplier;
        return result <= Integer.MAX_VALUE;
    }

    public boolean canReplaceStackUpgrade(int slotIndex, ItemStack replacement) {
        if (replacement == null) return true;

        ItemStack old = upgradeItemStackHandler.getStackInSlot(slotIndex);

        if (old != null && old.getItem() instanceof ItemStackUpgrade oldUp
            && replacement.getItem() instanceof ItemStackUpgrade newUp
            && oldUp.multiplier(old) == newUp.multiplier(replacement)) return true;

        int newMultiplier = getStackMultiplierExcluding(slotIndex, replacement);

        for (ItemStack stack : storageItemStackHandler.getStacks()) {
            if (stack != null && stack.stackSize > stack.getMaxStackSize() * newMultiplier) return false;
        }
        return true;
    }

    public boolean feed(EntityPlayer player, IItemHandler handler) {
        Map<Integer, IFeedingUpgrade> gathered = gatherCapabilityUpgrades(IFeedingUpgrade.class);
        if (gathered.isEmpty()) return false;
        for (IFeedingUpgrade wrapper : gathered.values()) {
            return wrapper.feed(player, handler);
        }
        return false;
    }

    public List<Entity> getMagnetEntities(World world, AxisAlignedBB aabb) {
        Map<Integer, IMagnetUpgrade> gathered = gatherCapabilityUpgrades(IMagnetUpgrade.class);
        Set<Entity> result = new HashSet<>();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, aabb);
        List<EntityXPOrb> xps = world.getEntitiesWithinAABB(EntityXPOrb.class, aabb);

        if (gathered.isEmpty()) return new ArrayList<>(result);
        for (IMagnetUpgrade wrapper : gathered.values()) {
            if (wrapper.isCollectItem()) {
                for (EntityItem item : items) {
                    if (wrapper.canCollectItem(item.getEntityItem())) result.add(item);
                }
            }
            if (wrapper.isCollectExp()) result.addAll(xps);
        }
        return new ArrayList<>(result);
    }

    public boolean canInsert(ItemStack stack) {
        Map<Integer, IFilterUpgrade> gathered = gatherCapabilityUpgrades(IFilterUpgrade.class);
        if (gathered.isEmpty()) return false;
        for (IFilterUpgrade wrapper : gathered.values()) {
            return wrapper.canInsert(stack);
        }
        return false;
    }

    public boolean canExtract(ItemStack stack) {
        Map<Integer, IFilterUpgrade> gathered = gatherCapabilityUpgrades(IFilterUpgrade.class);
        if (gathered.isEmpty()) return false;
        for (IFilterUpgrade wrapper : gathered.values()) {
            return wrapper.canExtract(stack);
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {
        Map<Integer, IPickupUpgrade> gathered = gatherCapabilityUpgrades(IPickupUpgrade.class);
        if (gathered.isEmpty()) return false;
        for (IPickupUpgrade wrapper : gathered.values()) {
            return wrapper.canPickup(stack);
        }
        return false;
    }

    public boolean canVoid(ItemStack stack, IVoidUpgrade.VoidType type, IVoidUpgrade.VoidInput input) {
        Map<Integer, IVoidUpgrade> gathered = gatherCapabilityUpgrades(IVoidUpgrade.class);
        if (gathered.isEmpty()) return false;
        for (IVoidUpgrade wrapper : gathered.values()) {
            if (wrapper.canVoid(stack) && wrapper.getVoidType() == type
                && (wrapper.getVoidInput() == input || input == IVoidUpgrade.VoidInput.AUTOMATION)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(STORAGE_SLOTS, storageSlots);
        tag.setInteger(UPGRADE_SLOTS, upgradeSlots);
        tag.setInteger(MAIN_COLOR, mainColor);
        tag.setInteger(ACCENT_COLOR, accentColor);

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
        if (hasCustomInventoryName()) tag.setString(CUSTOM_NAME_TAG, customName);

        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        if (tag.hasKey(STORAGE_SLOTS)) storageSlots = tag.getInteger(STORAGE_SLOTS);
        if (tag.hasKey(UPGRADE_SLOTS)) upgradeSlots = tag.getInteger(UPGRADE_SLOTS);
        if (tag.hasKey(MAIN_COLOR)) mainColor = tag.getInteger(MAIN_COLOR);
        if (tag.hasKey(ACCENT_COLOR)) accentColor = tag.getInteger(ACCENT_COLOR);

        if (tag.hasKey(BACKPACK_INV)) storageItemStackHandler.deserializeNBT(tag.getCompoundTag(BACKPACK_INV));
        if (tag.hasKey(UPGRADE_INV)) upgradeItemStackHandler.deserializeNBT(tag.getCompoundTag(UPGRADE_INV));

        byte[] respectArr = tag.getByteArray(MEMORY_STACK_RESPECT_NBT_TAG);
        for (int i = 0; i < respectArr.length && i < storageItemStackHandler.memorizedSlotRespectNbtList.size(); i++) {
            storageItemStackHandler.memorizedSlotRespectNbtList.set(i, respectArr[i] != 0);
        }

        byte[] lockedArr = tag.getByteArray(LOCKED_SLOTS_TAG);
        for (int i = 0; i < lockedArr.length && i < storageItemStackHandler.sortLockedSlots.size(); i++) {
            storageItemStackHandler.sortLockedSlots.set(i, lockedArr[i] != 0);
        }

        if (tag.hasKey(SORT_TYPE_TAG)) sortType = SortType.values()[tag.getByte(SORT_TYPE_TAG)];
        if (tag.hasKey(KEEP_TAB_TAG)) keepTab = tag.getBoolean(KEEP_TAB_TAG);

        if (tag.hasKey(CUSTOM_NAME_TAG)) customName = tag.getString(CUSTOM_NAME_TAG);
    }

    public <T> Map<Integer, T> gatherCapabilityUpgrades(Class<T> capabilityClass) {
        Map<Integer, T> result = new HashMap<>();
        for (int slot = 0; slot < upgradeSlots; slot++) {
            ItemStack stack = upgradeItemStackHandler.getStackInSlot(slot);
            if (stack == null) continue;

            UpgradeWrapperBase wrapper = UpgradeWrapperFactory.createWrapper(stack, this);
            if (wrapper != null && capabilityClass.isAssignableFrom(wrapper.getClass())) {
                result.put(slot, capabilityClass.cast(wrapper));
            }
        }
        return result;
    }
}
