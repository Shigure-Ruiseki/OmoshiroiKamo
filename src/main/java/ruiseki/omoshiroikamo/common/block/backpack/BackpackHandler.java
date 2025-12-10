package ruiseki.omoshiroikamo.common.block.backpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.UpgradeItemStackHandler;
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

    public static final String BACKPACK_INV = "BackpackInv";
    public static final String UPGRADE_INV = "UpgradeInv";
    public static final String BACKPACK_SLOTS = "BackpackSlots";
    public static final String UPGRADE_SLOTS = "UpgradeSlots";

    @Getter
    @Setter
    private SortType sortType;

    @Getter
    @Setter
    private boolean lockBackpack;

    @Getter
    @Setter
    private boolean searchBackpack;

    @Getter
    @Setter
    private String uuid;

    public static final String MEMORY_STACK_ITEMS_TAG = "MemoryItems";
    public static final String MEMORY_STACK_RESPECT_NBT_TAG = "MemoryRespectNBT";
    public static final String SORT_TYPE_TAG = "SortType";
    public static final String LOCKED_SLOTS_TAG = "LockedSlots";
    public static final String LOCKED_BACKPACK_TAG = "LockedBackpack";
    public static final String UUID_TAG = "UUID";
    public static final String SEARCH_BACKPACK = "SearchBackpack";

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
        this.sortType = SortType.BY_NAME;
        this.lockBackpack = false;
        this.uuid = "";
        this.searchBackpack = true;

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
        if (backpack != null && backpack.getItem() != null) {
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
        return backpackHandler.prioritizedInsertion(slot, stack, simulate);
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

    // Setting

    public boolean isSlotMemorized(int slotIndex) {
        return !(backpackHandler.memorizedSlotStack.get(slotIndex) == null);
    }

    public ItemStack getMemorizedStack(int slotIndex) {
        return backpackHandler.memorizedSlotStack.get(slotIndex);
    }

    public void setMemoryStack(int slotIndex, boolean respectNBT) {
        ItemStack currentStack = getStackInSlot(slotIndex);
        if (currentStack == null) return;

        ItemStack copiedStack = currentStack.copy();
        copiedStack.stackSize = 1;
        backpackHandler.memorizedSlotStack.set(slotIndex, copiedStack);
        backpackHandler.memorizedSlotRespectNbtList.set(slotIndex, respectNBT);
    }

    public void unsetMemoryStack(int slotIndex) {
        backpackHandler.memorizedSlotStack.set(slotIndex, null);
        backpackHandler.memorizedSlotRespectNbtList.set(slotIndex, false);
    }

    public boolean isMemoryStackRespectNBT(int slotIndex) {
        return backpackHandler.memorizedSlotRespectNbtList.get(slotIndex);
    }

    public void setMemoryStackRespectNBT(int slotIndex, boolean respect) {
        backpackHandler.memorizedSlotRespectNbtList.set(slotIndex, respect);
    }

    public boolean isSlotLocked(int slotIndex) {
        return backpackHandler.sortLockedSlots.get(slotIndex);
    }

    public void setSlotLocked(int slotIndex, boolean locked) {
        backpackHandler.sortLockedSlots.set(slotIndex, locked);
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
        long result = (long) getTotalStackMultiplier() * 64L * newMultiplier;
        return result == (int) result;
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
        for (IFeedingUpgrade upgrade : gatherCapabilityUpgrades(IFeedingUpgrade.class).values()) {
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

    public <T> Map<Integer, T> gatherCapabilityUpgrades(Class<T> capabilityClass) {
        Map<Integer, T> result = new HashMap<>();

        for (int i = 0; i < upgradeSlots; i++) {
            ItemStack stack = upgradeHandler.getStackInSlot(i);
            if (stack == null) continue;

            UpgradeWrapper wrapper = UpgradeWrapperFactory.createWrapper(stack);
            if (wrapper == null) continue;

            if (capabilityClass.isAssignableFrom(wrapper.getClass())) {
                result.put(i, capabilityClass.cast(wrapper));
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

    public boolean canPlayerAccess(UUID playerUUID) {
        return !isLockBackpack() || playerUUID.equals(UUID.fromString(getUuid()));
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

        NBTTagCompound memoryTag = new NBTTagCompound();
        BackpackItemStackHelper.saveAllSlotsExtended(memoryTag, backpackHandler.memorizedSlotStack);
        tag.setTag(MEMORY_STACK_ITEMS_TAG, memoryTag);

        List<Boolean> respectList = backpackHandler.memorizedSlotRespectNbtList;
        byte[] respectBytes = new byte[respectList.size()];
        for (int i = 0; i < respectList.size(); i++) {
            respectBytes[i] = (byte) (respectList.get(i) ? 1 : 0);
        }
        tag.setByteArray(MEMORY_STACK_RESPECT_NBT_TAG, respectBytes);

        List<Boolean> locked = backpackHandler.sortLockedSlots;
        byte[] lockedBytes = new byte[locked.size()];
        for (int i = 0; i < locked.size(); i++) {
            lockedBytes[i] = (byte) (locked.get(i) ? 1 : 0);
        }
        tag.setByteArray(LOCKED_SLOTS_TAG, lockedBytes);

        tag.setByte(SORT_TYPE_TAG, (byte) sortType.ordinal());

        tag.setBoolean(LOCKED_BACKPACK_TAG, lockBackpack);

        tag.setBoolean(SEARCH_BACKPACK, searchBackpack);

        tag.setString(UUID_TAG, uuid);
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

            BackpackItemStackHelper.loadAllItemsExtended(tag.getCompoundTag(BACKPACK_INV), backpackHandler.getStacks());
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

        if (tag.hasKey(MEMORY_STACK_ITEMS_TAG)) {
            BackpackItemStackHelper
                .loadAllItemsExtended(tag.getCompoundTag(MEMORY_STACK_ITEMS_TAG), backpackHandler.memorizedSlotStack);
        }

        byte[] respectArr = tag.getByteArray(MEMORY_STACK_RESPECT_NBT_TAG);
        int maxRespect = backpackHandler.memorizedSlotRespectNbtList.size();
        for (int i = 0; i < respectArr.length && i < maxRespect; i++) {
            setMemoryStackRespectNBT(i, respectArr[i] != 0);
        }

        byte[] lockedArr = tag.getByteArray(LOCKED_SLOTS_TAG);
        int maxLocked = backpackHandler.sortLockedSlots.size();
        for (int i = 0; i < lockedArr.length && i < maxLocked; i++) {
            setSlotLocked(i, lockedArr[i] != 0);
        }

        if (tag.hasKey(SORT_TYPE_TAG)) {
            sortType = SortType.values()[tag.getByte(SORT_TYPE_TAG)];
        }

        if (tag.hasKey(LOCKED_BACKPACK_TAG)) {
            lockBackpack = tag.getBoolean(LOCKED_BACKPACK_TAG);
        }

        if (tag.hasKey(SEARCH_BACKPACK)) {
            searchBackpack = tag.getBoolean(SEARCH_BACKPACK);
        }

        if (tag.hasKey(UUID_TAG)) {
            uuid = tag.getString(UUID_TAG);
        }
    }

    public static int ceilDiv(int a, int b) {
        return (a + b - 1) / b;
    }

}
