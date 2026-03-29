package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;

import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.item.ItemNBTHelpers;
import ruiseki.omoshiroikamo.module.storage.client.gui.handler.UpgradeItemStackHandler;

public class SmeltingUpgradeWrapper extends UpgradeWrapperBase implements ISmeltingUpgrade, IStorageUpgrade {

    protected UpgradeItemStackHandler handler;

    public SmeltingUpgradeWrapper(ItemStack upgrade, IStorageWrapper wrapper) {
        super(upgrade, wrapper);
        handler = new UpgradeItemStackHandler(3) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                NBTTagCompound tag = ItemNBTHelpers.getNBT(upgrade);
                tag.setTag(STORAGE_TAG, this.serializeNBT());
            }
        };
        NBTTagCompound handlerTag = ItemNBTHelpers.getCompound(upgrade, STORAGE_TAG, false);
        if (handlerTag != null) handler.deserializeNBT(handlerTag);
    }

    @Override
    public UpgradeItemStackHandler getStorage() {
        return handler;
    }

    @Override
    public void tick() {

        UpgradeItemStackHandler inv = getStorage();

        ItemStack input = inv.getStackInSlot(0);
        ItemStack fuel = inv.getStackInSlot(1);

        boolean burning = getBurnTime() > 0;

        // decrease burn time
        if (burning) {
            setBurnTime(getBurnTime() - 1);
            burning = getBurnTime() > 0;
        }

        boolean canSmelt = canSmelt(inv);

        // try start burning
        if (!burning && canSmelt && fuel != null) {

            int burn = TileEntityFurnace.getItemBurnTime(fuel);

            if (burn > 0) {

                setBurnTime(burn);
                setBurnTimeTotal(burn);
                burning = true;

                fuel.stackSize--;

                if (fuel.stackSize <= 0) {
                    inv.setStackInSlot(
                        1,
                        fuel.getItem()
                            .getContainerItem(fuel));
                } else {
                    inv.setStackInSlot(1, fuel);
                }
            }
        }

        // cooking
        if (burning && canSmelt) {

            setCookTime(getCookTime() + 1);

            if (getCookTime() >= getCookTimeTotal()) {

                setCookTime(0);
                smeltItem(inv);
            }

        } else if (getCookTime() > 0) {

            setCookTime(Math.max(0, getCookTime() - 2));
        }
    }

    private boolean canSmelt(UpgradeItemStackHandler inv) {

        ItemStack input = inv.getStackInSlot(0);
        ItemStack output = inv.getStackInSlot(2);

        if (input == null) return false;

        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(input);

        if (result == null) return false;

        if (output == null) return true;

        if (!output.isItemEqual(result)) return false;

        return output.stackSize + result.stackSize <= Math.min(output.getMaxStackSize(), inv.getSlotLimit(2));
    }

    private void smeltItem(UpgradeItemStackHandler inv) {

        ItemStack input = inv.getStackInSlot(0);
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(input);

        ItemStack output = inv.getStackInSlot(2);

        if (output == null) {
            inv.setStackInSlot(2, result.copy());
        } else {
            output.stackSize += result.stackSize;
            inv.setStackInSlot(2, output);
        }

        input.stackSize--;

        if (input.stackSize <= 0) {
            inv.setStackInSlot(0, null);
        } else {
            inv.setStackInSlot(0, input);
        }
    }

    @Override
    public int getBurnTime() {
        return ItemNBTHelpers.getInt(upgrade, BURN_TIME_TAG, 0);
    }

    @Override
    public int getBurnTimeTotal() {
        return ItemNBTHelpers.getInt(upgrade, BURN_TIME_TOTAL_TAG, 0);
    }

    @Override
    public int getCookTime() {
        return ItemNBTHelpers.getInt(upgrade, COOK_TIME_TAG, 0);
    }

    @Override
    public int getCookTimeTotal() {
        return ItemNBTHelpers.getInt(upgrade, COOK_TIME_TOTAL_TAG, 200);
    }

    @Override
    public void setBurnTime(int value) {
        ItemNBTHelpers.setInt(upgrade, BURN_TIME_TAG, value);
    }

    @Override
    public void setBurnTimeTotal(int value) {
        ItemNBTHelpers.setInt(upgrade, BURN_TIME_TOTAL_TAG, value);
    }

    @Override
    public void setCookTime(int value) {
        ItemNBTHelpers.setInt(upgrade, COOK_TIME_TAG, value);
    }

    @Override
    public void setCookTimeTotal(int value) {
        ItemNBTHelpers.setInt(upgrade, COOK_TIME_TOTAL_TAG, value);
    }

    public void addCookTime(int amount) {
        setCookTime(getCookTime() + amount);
    }

    public void decreaseBurnTime() {
        setBurnTime(Math.max(0, getBurnTime() - 1));
    }

    @Override
    public float getProgress() {
        int cook = getCookTime();
        int total = getCookTimeTotal();
        if (total == 0) return 0;
        return (float) cook / total;
    }

    @Override
    public void setProgress(float progress) {
        progress = Math.max(0F, Math.min(1F, progress));
        int total = getCookTimeTotal();
        setCookTime((int) (progress * total));
    }
}
