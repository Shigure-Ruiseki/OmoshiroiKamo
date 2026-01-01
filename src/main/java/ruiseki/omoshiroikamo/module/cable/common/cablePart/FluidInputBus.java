package ruiseki.omoshiroikamo.module.cable.common.cablePart;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidInputBus extends AbstractCablePart implements IFluidHandler {

    public static final String ID = "fluid_input_bus";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[0];
    }

    // ========= Item =========

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    // ========= NBT =========

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        // no internal state yet
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        // no internal state yet
    }
}
