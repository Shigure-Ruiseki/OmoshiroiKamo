package ruiseki.omoshiroikamo.module.dml.common.block.simulationCharmber;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.block.CraftingState;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.config.backport.DMLConfig;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMachineTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.dml.client.gui.handler.ItemHandlerDataModel;
import ruiseki.omoshiroikamo.module.dml.client.gui.handler.ItemHandlerPolymerClay;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemDataModel;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPolymerClay;

public class TESimulationChamber extends AbstractMachineTE
    implements IOKEnergySink, ISidedInventory, IGuiHolder<PosGuiData> {

    private static final int SLOT_DATA_MODEL = 0;
    private static final int SLOT_POLYMER = 1;
    private static final int SLOT_LIVING = 2;
    private static final int SLOT_PRISTINE = 3;

    public final ItemHandlerDataModel inputDataModel = new ItemHandlerDataModel() {

        @Override
        protected void onContentsChanged(int slot) {
            onDataModelChanged();
        }
    };
    public final ItemHandlerPolymerClay inputPolymer = new ItemHandlerPolymerClay();
    public final ItemStackHandlerBase outputLiving = new ItemStackHandlerBase();
    public final ItemStackHandlerBase outputPristine = new ItemStackHandlerBase();

    private boolean pristineSuccess = false;

    public TESimulationChamber() {
        super(DMLConfig.simulationChamberEnergyCapacity, DMLConfig.simulationChamberEnergyInMax);
    }

    @Override
    protected void startCrafting() {
        super.startCrafting();

        int pristineChance = DataModel.getPristineChance(getDataModel());
        int random = ThreadLocalRandom.current()
            .nextInt(100);
        pristineSuccess = (random < pristineChance);

        inputPolymer.voidItem();
    }

    @Override
    public boolean canStartCrafting() {
        return super.canStartCrafting() && hasPolymerClay() && canContinueCrafting();
    }

    @Override
    protected boolean canContinueCrafting() {
        return super.canContinueCrafting() && hasDataModel()
            && canDataModelSimulate()
            && !isLivingMatterOutputFull()
            && !isPristineMatterOutputFull();
    }

    @Override
    protected void finishCrafting() {
        ItemStack dataModel = getDataModel();
        ModelRegistryItem model = DataModel.getDataFromStack(dataModel);

        if (model == null) {
            resetCrafting();
            return;
        }

        DataModel.increaseSimulationCount(dataModel);

        ItemStack livingMatter = outputLiving.getStackInSlot(0);
        if (livingMatter == null) {
            outputLiving.setStackInSlot(0, model.getLivingMatter());
        } else {
            livingMatter.stackSize++;
        }

        if (pristineSuccess) {
            ItemStack pristineMatter = outputPristine.getStackInSlot(0);

            if (pristineMatter == null) {
                outputPristine.setStackInSlot(0, model.getPristineMatter());
            } else {
                pristineMatter.stackSize++;
            }
        }

        resetCrafting();
    }

    @Override
    protected void resetCrafting() {
        super.resetCrafting();
        pristineSuccess = false;
    }

    @Override
    protected int getCraftingDuration() {
        return DMLConfig.simulationChamberProcessingTime;
    }

    public boolean isPristineSuccess() {
        return pristineSuccess;
    }

    @Override
    public int getCraftingEnergyCost() {
        return DataModel.getSimulationEnergy(getDataModel());
    }

    @Override
    protected CraftingState updateCraftingState() {
        if (!hasDataModel()) return CraftingState.IDLE;
        else if (!canContinueCrafting() || (!this.isCrafting() && !canStartCrafting())) return CraftingState.ERROR;

        return CraftingState.RUNNING;
    }

    @Override
    public boolean isActive() {
        return isCrafting();
    }

    private void onDataModelChanged() {
        if (!worldObj.isRemote) {
            resetCrafting();
        }
    }

    public ItemStack getDataModel() {
        return inputDataModel.getStackInSlot(0);
    }

    public boolean hasDataModel() {
        return getDataModel() != null && getDataModel().getItem() instanceof ItemDataModel;
    }

    public boolean canDataModelSimulate() {
        return DataModel.canSimulate(getDataModel());
    }

    public ItemStack getPolymerClay() {
        return inputPolymer.getStackInSlot(0);
    }

    public boolean hasPolymerClay() {
        return getPolymerClay() != null && getPolymerClay().getItem() instanceof ItemPolymerClay;
    }

    public boolean isLivingMatterOutputFull() {
        ItemStack livingMatterStack = outputLiving.getStackInSlot(0);

        if (livingMatterStack == null) {
            return false;
        }

        boolean stackIsFull = (livingMatterStack.stackSize >= outputLiving.getSlotLimit(0));
        boolean stackMatchesDataModel = DataModel.isDataModelMatchesLivingMatter(getDataModel(), livingMatterStack);

        return (stackIsFull || !stackMatchesDataModel);
    }

    public boolean isPristineMatterOutputFull() {
        ItemStack pristineMatterStack = outputPristine.getStackInSlot(0);

        if (pristineMatterStack == null) {
            return false;
        }

        boolean stackIsFull = (pristineMatterStack.stackSize >= outputPristine.getSlotLimit(0));
        boolean stackMatchesDataModel = DataModel.isDataModelMatchesPristineMatter(getDataModel(), pristineMatterStack);

        return (stackIsFull || !stackMatchesDataModel);
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag("inputDataModel", inputDataModel.serializeNBT());
        inventory.setTag("inputPolymer", inputPolymer.serializeNBT());
        inventory.setTag("outputLiving", outputLiving.serializeNBT());
        inventory.setTag("outputPristine", outputPristine.serializeNBT());
        root.setTag(INVENTORY_TAG, inventory);

        root.getCompoundTag(CRAFTING_TAG)
            .setBoolean("pristineSuccess", pristineSuccess);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        NBTTagCompound inventory = root.getCompoundTag(INVENTORY_TAG);
        inputDataModel.deserializeNBT(inventory.getCompoundTag("inputDataModel"));
        inputPolymer.deserializeNBT(inventory.getCompoundTag("inputPolymer"));
        outputLiving.deserializeNBT(inventory.getCompoundTag("outputLiving"));
        outputPristine.deserializeNBT(inventory.getCompoundTag("outputPristine"));

        pristineSuccess = root.getCompoundTag(CRAFTING_TAG)
            .getBoolean("pristineSuccess");
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new SimulationChamberPanel(this, data, syncManager, settings);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[] { SLOT_DATA_MODEL, SLOT_POLYMER, SLOT_LIVING, SLOT_PRISTINE };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        switch (slot) {
            case SLOT_DATA_MODEL:
                return stack != null && stack.getItem() instanceof ItemDataModel;

            case SLOT_POLYMER:
                return stack != null && stack.getItem() instanceof ItemPolymerClay;

            default:
                return false;
        }
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot == SLOT_LIVING || slot == SLOT_PRISTINE;
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        switch (slot) {
            case 0:
                return inputDataModel.getStackInSlot(0);
            case 1:
                return inputPolymer.getStackInSlot(0);
            case 2:
                return outputLiving.getStackInSlot(0);
            case 3:
                return outputPristine.getStackInSlot(0);
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return null;

        ItemStack result;
        if (stack.stackSize <= count) {
            result = stack;
            setInventorySlotContents(slot, null);
        } else {
            result = stack.splitStack(count);
            if (stack.stackSize <= 0) {
                setInventorySlotContents(slot, null);
            }
        }
        markDirty();
        return result;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_DATA_MODEL:
                inputDataModel.setStackInSlot(0, stack);
                break;
            case SLOT_POLYMER:
                inputPolymer.setStackInSlot(0, stack);
                break;
            case SLOT_LIVING:
                outputLiving.setStackInSlot(0, stack);
                break;
            case SLOT_PRISTINE:
                outputPristine.setStackInSlot(0, stack);
                break;
        }
        markDirty();
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public String getInventoryName() {
        return getLocalizedName();
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null) return false;

        switch (index) {
            case SLOT_DATA_MODEL:
                return stack.getItem() instanceof ItemDataModel;

            case SLOT_POLYMER:
                return stack.getItem() instanceof ItemPolymerClay;

            case SLOT_LIVING:
            case SLOT_PRISTINE:
            default:
                return false;
        }
    }
}
