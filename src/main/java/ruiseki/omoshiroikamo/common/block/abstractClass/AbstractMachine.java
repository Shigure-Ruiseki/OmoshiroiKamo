package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;
import ruiseki.omoshiroikamo.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.IEnergySink;
import ruiseki.omoshiroikamo.common.network.PacketCraftingState;
import ruiseki.omoshiroikamo.common.network.PacketHandler;

public abstract class AbstractMachine extends AbstractTE implements IEnergySink, ICraftingTile {

    protected final EnergyStorage energyStorage;

    private CraftingState craftingState = CraftingState.IDLE;
    protected boolean crafting = false;
    protected int craftingProgress = 0;

    public AbstractMachine(int energyCapacity, int energyMaxReceive) {
        this.energyStorage = new EnergyStorage(energyCapacity, energyMaxReceive) {

            @Override
            public void onEnergyChanged() {
                markDirty();
            }
        };
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {

        if (this.worldObj.isRemote) {
            return false;
        }

        if (!crafting && canStartCrafting()) {
            startCrafting();
        }

        if (crafting && canContinueCrafting()) {
            energyStorage.voidEnergy(getCraftingEnergyCost());
            advanceCraftingProgress();
        }

        CraftingState newCraftingState = updateCraftingState();
        if (craftingState != newCraftingState) {
            craftingState = newCraftingState;
            PacketHandler.sendToAllAround(new PacketCraftingState(this), this);
            markDirty();
        }

        return false;
    }

    protected void startCrafting() {
        crafting = true;
        markDirty();
    }

    public boolean canStartCrafting() {
        return isRedstoneActive()
            && energyStorage.getEnergyStored() >= getCraftingEnergyCost() * Math.max(1, (getCraftingDuration() - 1));
    }

    protected boolean canContinueCrafting() {
        return isRedstoneActive() && hasEnergyForCrafting();
    }

    private void advanceCraftingProgress() {
        craftingProgress++;
        if (craftingProgress >= getCraftingDuration()) {
            finishCrafting();
        }

        markDirty();
    }

    protected abstract int getCraftingDuration();

    protected abstract void finishCrafting();

    protected void resetCrafting() {
        crafting = false;
        craftingProgress = 0;
        markDirty();
    }

    public float getRelativeCraftingProgress() {
        return (float) craftingProgress / getCraftingDuration();
    }

    public boolean isCrafting() {
        return crafting;
    }

    public boolean hasEnergyForCrafting() {
        return energyStorage.getEnergyStored() >= getCraftingEnergyCost();
    }

    public abstract int getCraftingEnergyCost();

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    protected abstract CraftingState updateCraftingState();

    public CraftingState getCraftingState() {
        return craftingState;
    }

    public void setCraftingState(CraftingState newState) {
        craftingState = newState;
        sendBlockUpdate();
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public void setEnergyStored(int stored) {
        energyStorage.setEnergyStored(stored);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection forgeDirection) {
        return true;
    }
}
