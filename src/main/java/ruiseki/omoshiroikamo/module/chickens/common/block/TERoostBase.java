package ruiseki.omoshiroikamo.module.chickens.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.network.packet.PacketProgress;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractStorageTE;
import ruiseki.omoshiroikamo.core.tileentity.IProgressTile;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.core.util.SlotDefinition;

public abstract class TERoostBase extends AbstractStorageTE implements IProgressTile {

    @NBTPersist
    protected int timeUntilNextDrop = 0;
    @NBTPersist
    protected int timeElapsed = 0;
    protected int progress = 0;

    protected boolean needsCacheRefresh = true;
    protected DataChicken[] chickenCache;

    public TERoostBase() {
        super(new SlotDefinition().setItemSlots(3, 3));

        // Override inv to enforce slot limits specific to chickens
        this.inv = new ItemStackHandlerBase(slotDefinition.getItemSlots()) {

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
    public boolean processTasks(boolean redstoneChecksPassed) {
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

        ItemStack foodStack = getStackInSlot(2);

        // Mutation check for Breeder
        if (isMutationFood(foodStack)) {
            if (foodStack != null && foodStack.stackSize >= needed) {
                return true;
            }
        }

        for (int i = 0; i < getSizeChickenInventory(); i++) {
            DataChicken chicken = getChickenData(i);
            if (chicken != null) {
                if (!chicken.getItem()
                    .getRecipes()
                    .isEmpty()) {
                    // If this specific chicken requires food, check if foodStack satisfies it
                    if (foodStack == null || foodStack.stackSize < needed
                        || !chicken.getItem()
                            .isFood(foodStack)) {
                        return false;
                    }
                }
            }
        }

        // If no chickens require food, or all required food is present
        return true;
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
                OmoshiroiKamo.instance.getPacketHandler()
                    .sendToAllAround(new PacketProgress(this), this);
            }
            return;
        }

        progress = timeUntilNextDrop == 0 ? 0 : (timeElapsed * 1000 / timeUntilNextDrop);

        if (worldObj.getTotalWorldTime() % 5 == 0) {
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(new PacketProgress(this), this);
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
                decrStackSize(2, requiredSeedsForDrop());
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

    @Override
    public boolean isActive() {
        return isFullChickens() && hasFreeOutputSlot() && isFullSeeds();
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
        if (slot < 3) {
            resetTimer();
        }

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
        if (slot < 3) {
            resetTimer();
        }
    }

    @Override
    public void onContentsChange(int slot) {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.forceClientUpdate = true;
        }
        super.onContentsChange(slot);
        needsCacheRefresh();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 2) {
            if (requiredSeedsForDrop() <= 0) {
                return false;
            }
            // Allow if it's a mutation food
            if (isMutationFood(stack)) {
                return true;
            }
            // Check if any current chicken requires food and this stack satisfies it
            for (int i = 0; i < getSizeChickenInventory(); i++) {
                DataChicken chicken = getChickenData(i);
                if (chicken != null && !chicken.getItem()
                    .getRecipes()
                    .isEmpty()) {
                    if (chicken.getItem()
                        .isFood(stack)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return slot < getSizeChickenInventory() && slotDefinition.isInputSlot(slot) && DataChicken.isChicken(stack);
    }

    protected boolean isMutationFood(ItemStack stack) {
        return false; // Overridden in TEBreeder
    }

    /**
     * -----------------------------------------------------------
     * Saving
     * -----------------------------------------------------------
     */

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        needsCacheRefresh();
    }

    @Override
    public void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}

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
