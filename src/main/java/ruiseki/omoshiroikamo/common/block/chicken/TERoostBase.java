package ruiseki.omoshiroikamo.common.block.chicken;

import java.text.DecimalFormat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketProgress;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;

public abstract class TERoostBase extends AbstractStorageTE implements IProgressTile {

    protected static final DecimalFormat FORMATTER = new DecimalFormat("0.0%");

    protected int timeUntilNextDrop = 0;
    protected int timeElapsed = 0;
    protected int progress = 0;

    protected boolean needsCacheRefresh = true;
    protected DataChicken[] chickenCache;

    public TERoostBase() {
        super(new SlotDefinition().setItemSlots(3, 3));

        // Override inv to enforce slot limits specific to chickens
        this.inv = new ItemStackHandler(slotDefinition.getItemSlots()) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                onContentsChange(slot);
            }

            @Override
            public int getSlotLimit(int slot) {
                if (slot < getSizeChickenInventory()) {
                    return getChickenStackLimit();
                }
                return super.getSlotLimit(slot);
            }
        };

        chickenCache = new DataChicken[getSizeChickenInventory()];
    }

    @Override
    protected boolean processTasks(boolean redstoneChecksPassed) {
        if (!worldObj.isRemote) {
            updateTimerIfNeeded();
            spawnChickenDropIfNeeded();
            updateProgress();
        }

        return super.processTasks(redstoneChecksPassed);
    }

    /**
     * -----------------------------------------------------------
     * Chicken / Seed getters
     * -----------------------------------------------------------
     */

    public DataChicken getChickenData(int slot) {
        if (slot < 0 || slot >= getSizeChickenInventory()) {
            return null;
        }

        if (needsCacheRefresh) {
            for (int i = 0; i < getSizeChickenInventory(); i++) {
                ItemStack stack = getStackInSlot(i);

                if (stack == null) {
                    chickenCache[i] = null;
                } else {
                    chickenCache[i] = DataChicken.getDataFromStack(stack);
                }
            }
            needsCacheRefresh = false;
        }

        return chickenCache[slot];
    }

    public void needsCacheRefresh() {
        needsCacheRefresh = true;
    }

    protected boolean isFullChickens() {
        for (int i = 0; i < getSizeChickenInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!DataChicken.isChicken(stack)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isFullSeeds() {
        int needed = requiredSeedsForDrop();
        if (needed <= 0) {
            return true;
        }

        ItemStack seedStack = getStackInSlot(getSizeChickenInventory());
        if (seedStack == null || seedStack.getItem() == null) {
            return false;
        }

        return seedStack.stackSize >= needed;
    }

    /**
     * -----------------------------------------------------------
     * Timer
     * -----------------------------------------------------------
     */

    private int computeTimeIncrement() {
        if (!isFullChickens()) {
            return 0;
        }

        int time = Integer.MAX_VALUE;

        for (int i = 0; i < getSizeChickenInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) {
                return 0;
            }

            DataChicken chicken = DataChicken.getDataFromStack(stack);
            if (chicken == null) {
                return 0;
            }

            time = Math.min(time, chicken.getAddedTime(stack));
        }
        return time;
    }

    private void updateTimerIfNeeded() {
        if (isFullChickens() && isFullSeeds() && hasFreeOutputSlot()) {
            timeElapsed += computeTimeIncrement();
            markDirty();
        }
    }

    private void updateProgress() {

        if (!isFullChickens() || !isFullSeeds()) {
            boolean wasRunning = progress > 0;
            progress = 0;
            timeElapsed = 0;
            timeUntilNextDrop = 0;
            if (wasRunning) {
                PacketHandler.sendToAllAround(new PacketProgress(this), this);
            }
            return;
        }

        progress = timeUntilNextDrop == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextDrop);

        if (worldObj.getTotalWorldTime() % 5 == 0) {
            PacketHandler.sendToAllAround(new PacketProgress(this), this);
        }
    }

    private void resetTimer() {

        timeElapsed = 0;
        timeUntilNextDrop = 0;

        for (int i = 0; i < getSizeChickenInventory(); i++) {
            DataChicken chicken = getChickenData(i);
            if (chicken != null) {
                timeUntilNextDrop = Math.max(timeUntilNextDrop, chicken.getTime());
            }
        }

        if (timeUntilNextDrop > 0) {
            double speed = Math.max(0.001d, speedMultiplier());
            timeUntilNextDrop = (int) Math.max(1, Math.round(timeUntilNextDrop / speed));
        }

        markDirty();
    }

    /**
     * -----------------------------------------------------------
     * Drop logic
     * -----------------------------------------------------------
     */

    private void spawnChickenDropIfNeeded() {
        if (isFullChickens() && isFullSeeds() && hasFreeOutputSlot() && timeElapsed >= timeUntilNextDrop) {

            if (timeUntilNextDrop > 0) {
                decrStackSize(getSizeChickenInventory(), requiredSeedsForDrop());
                spawnChickenDrop();
            }

            resetTimer();
        }
    }

    protected boolean outputIsFull() {
        for (int i = slotDefinition.getMinItemOutput(); i <= slotDefinition.getMaxItemOutput(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack == null) {
                return false;
            }
            if (stack.stackSize < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    protected void putStackInOutput(ItemStack stack) {
        for (int i = slotDefinition.getMinItemOutput(); i <= slotDefinition.getMaxItemOutput(); i++) {
            stack = insertStack(stack, i);
        }
        markDirty();
    }

    private ItemStack insertStack(ItemStack stack, int index) {
        if (stack == null) {
            return null;
        }

        int max = Math.min(stack.getMaxStackSize(), getInventoryStackLimit());
        ItemStack outputStack = getStackInSlot(index);

        if (outputStack == null) {
            if (stack.stackSize >= max) {
                ItemStack insert = stack.copy();
                insert.stackSize = max;
                setInventorySlotContents(index, insert);
                stack.stackSize -= max;
                return stack.stackSize > 0 ? stack : null;
            } else {
                setInventorySlotContents(index, stack);
                return null;
            }
        }

        if (ItemUtils.areStackMergable(outputStack, stack)) {
            int space = max - outputStack.stackSize;
            int move = Math.min(stack.stackSize, space);

            outputStack.stackSize += move;
            stack.stackSize -= move;

            return stack.stackSize > 0 ? stack : null;
        }

        return stack;
    }

    /**
     * -----------------------------------------------------------
     * Progress UI
     * -----------------------------------------------------------
     */

    @Override
    public float getProgress() {
        return Math.max(0f, progress / 1000.0f);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = (int) (progress * 1000.0);
    }

    public String getFormattedProgress() {
        return formatProgress(getProgress());
    }

    public String formatProgress(double progress) {
        return FORMATTER.format(progress);
    }

    @Override
    public boolean isActive() {
        return isFullChickens() && hasFreeOutputSlot() && isFullSeeds();
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
            float hitZ) {
        openGui(player);
        return true;
    }

    /**
     * -----------------------------------------------------------
     * Inventory overrides
     * -----------------------------------------------------------
     */

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = super.decrStackSize(slot, amount);
        needsCacheRefresh();
        resetTimer();

        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);

        // Enforce chicken stack limit
        ItemStack slotStack = inv.getStackInSlot(slot);
        if (slotStack != null && slot < getSizeChickenInventory() && slotStack.stackSize > getChickenStackLimit()) {
            slotStack.stackSize = getChickenStackLimit();
            // setStackInSlot to notify changes
            inv.setStackInSlot(slot, slotStack);
        }

        needsCacheRefresh();
        resetTimer();
    }

    @Override
    public void onContentsChange(int slot) {
        super.onContentsChange(slot);
        needsCacheRefresh();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 2) {
            return requiredSeedsForDrop() > 0 && isSeed(stack);
        }
        return slot < getSizeChickenInventory() && slotDefinition.isInputSlot(slot) && DataChicken.isChicken(stack);
    }

    public static boolean isSeed(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemSeeds;
    }

    /**
     * -----------------------------------------------------------
     * Saving
     * -----------------------------------------------------------
     */

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setInteger("timeUntilNextDrop", timeUntilNextDrop);
        root.setInteger("timeElapsed", timeElapsed);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        timeUntilNextDrop = root.getInteger("timeUntilNextDrop");
        timeElapsed = root.getInteger("timeElapsed");
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {
    }

    /**
     * -----------------------------------------------------------
     * Abstracts for subclasses
     * -----------------------------------------------------------
     */

    protected abstract int getSizeChickenInventory();

    protected abstract void spawnChickenDrop();

    protected abstract int requiredSeedsForDrop();

    protected abstract double speedMultiplier();

    protected int getChickenStackLimit() {
        return ChickenConfig.getChickenStackLimit();
    }

    protected void playSpawnSound() {
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, "mob.chicken.plop", 0.5F, 0.8F);
    }

    protected void playPutChickenInSound() {
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, "random.pop", 1.0F, 1.0F);
    }

    protected void playPullChickenOutSound() {
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, "random.pop", 1.0F, 1.0F);
    }

    protected abstract boolean hasFreeOutputSlot();
}
