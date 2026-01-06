package ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.block.CraftingState;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.config.backport.DMLConfig;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMachineTE;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.dml.client.gui.handler.ItemHandlerPristineMatter;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPristineMatter;

public class TELootFabricator extends AbstractMachineTE implements IOKEnergySink, ISidedInventory {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_START = 1;
    private static final int OUTPUT_SIZE = 16;

    public final ItemHandlerPristineMatter input = new ItemHandlerPristineMatter() {

        @Override
        protected void onMetadataChanged() {
            if (this.pristineMatterData != null && !isValidOutputItem()) outputItem = null;
            resetCrafting();
        }
    };

    public final ItemStackHandlerBase output = new ItemStackHandlerBase(16);

    @Getter
    public ItemStack outputItem = null;

    public TELootFabricator() {
        super(getEnergyCapacity(), getEnergyPerTick());
    }

    private static int getEnergyCapacity() {
        int energyCost = DMLConfig.lootFabricatorRfCost;
        long energyCapacity = 1_000_000L * (energyCost / 100);
        return (int) Math.min(energyCapacity, Integer.MAX_VALUE);
    }

    private static int getEnergyPerTick() {
        int energyCost = DMLConfig.lootFabricatorRfCost;
        long energyPerTick = 100L * energyCost;
        return (int) Math.min(energyPerTick, Integer.MAX_VALUE);
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (input.getStackInSlot(0) != null && !isValidOutputItem()) {
            outputItem = null;
            resetCrafting();
        }

        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public boolean canStartCrafting() {
        return super.canStartCrafting() && hasPristineMatter() && hasRoomForOutput() && isValidOutputItem();
    }

    @Override
    protected void finishCrafting() {
        resetCrafting();

        if (outputItem == null) {
            Logger.warn("Loot Fabricator at {} crafted without selected output!", getPos().toString());
            return;
        }

        if (!isValidOutputItem()) {
            Logger.warn("Loot Fabricator at {} crafted with invalid output selection!", getPos().toString());
            outputItem = null;
            return;
        }

        output.addItemToAvailableSlots(outputItem.copy());
        input.voidItem();
    }

    @Override
    protected int getCraftingDuration() {
        return DMLConfig.lootFabricatorPrecessingTime;
    }

    @Override
    public int getCraftingEnergyCost() {
        return DMLConfig.lootFabricatorRfCost;
    }

    @Override
    protected CraftingState updateCraftingState() {
        if (!isCrafting() && !hasPristineMatter()) {
            return CraftingState.IDLE;
        } else if (!canContinueCrafting() || (!this.isCrafting() && !canStartCrafting())) {
            return CraftingState.ERROR;
        }

        return CraftingState.RUNNING;
    }

    private boolean isValidOutputItem() {
        ModelRegistryItem pristineMatterMetadata = getPristineMatterData();
        return outputItem != null && pristineMatterMetadata != null && pristineMatterMetadata.hasLootItem(outputItem);
    }

    @Nullable
    public ModelRegistryItem getPristineMatterData() {
        return input.getPristineMatterData();
    }

    public void setOutputItem(ItemStack outputItem) {
        this.outputItem = outputItem;

        if (!isValidOutputItem()) {
            this.outputItem = null;
        }
    }

    public boolean hasPristineMatter() {
        ItemStack stack = input.getStackInSlot(0);
        return stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemPristineMatter;
    }

    public boolean hasRoomForOutput() {
        return output.hasRoomForItem(outputItem);
    }

    @Override
    public boolean isActive() {
        return isCrafting();
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag("input", input.serializeNBT());
        inventory.setTag("output", output.serializeNBT());
        root.setTag(INVENTORY_TAG, inventory);

        if (outputItem != null) {
            NBTTagCompound crafting = root.getCompoundTag(CRAFTING_TAG);
            crafting.setTag("outputItem", ItemNBTUtils.stackToNbt(outputItem));
            root.setTag(CRAFTING_TAG, crafting);
        }

    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        NBTTagCompound inventory = root.getCompoundTag(INVENTORY_TAG);
        input.deserializeNBT(inventory.getCompoundTag("input"));
        output.deserializeNBT(inventory.getCompoundTag("output"));

        NBTTagCompound crafting = root.getCompoundTag(CRAFTING_TAG);
        outputItem = ItemNBTUtils.nbtToStack(crafting.getCompoundTag("outputItem"));

    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new LootFabricatorPanel(this, data, syncManager, settings);
    }

    @Override
    public int getSizeInventory() {
        return 1 + OUTPUT_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == INPUT_SLOT) {
            return input.getStackInSlot(0);
        }

        int out = slot - OUTPUT_START;
        if (out >= 0 && out < OUTPUT_SIZE) {
            return output.getStackInSlot(out);
        }

        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return null;

        if (stack.stackSize <= count) {
            setInventorySlotContents(slot, null);
            return stack;
        }

        ItemStack split = stack.splitStack(count);
        markDirty();
        return split;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            setInventorySlotContents(slot, null);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            input.setStackInSlot(0, stack);
        } else {
            int out = slot - OUTPUT_START;
            if (out >= 0 && out < OUTPUT_SIZE) {
                output.setStackInSlot(out, stack);
            }
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.getLocalizedName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == INPUT_SLOT && stack != null && stack.getItem() instanceof ItemPristineMatter;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        int[] slots = new int[getSizeInventory()];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return slot == INPUT_SLOT && stack != null && stack.getItem() instanceof ItemPristineMatter;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot >= OUTPUT_START;
    }
}
