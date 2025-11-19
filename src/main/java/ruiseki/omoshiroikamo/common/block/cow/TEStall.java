package ruiseki.omoshiroikamo.common.block.cow;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;

public class TEStall extends AbstractTE implements IFluidHandler, IProgressTile {

    public static final int amount = FluidContainerRegistry.BUCKET_VOLUME * 10;

    public final SmartTank tank = new SmartTank(amount);

    private int progress;
    @Getter
    private int maxProgress;

    private float progressPercent;

    @Getter
    private int cowType;
    private int cowGain;
    private int cowGrowth;
    private boolean cowIsChild;
    @Getter
    private NBTTagCompound cowNBT;

    private final Random rand = new Random();
    private CowsRegistryItem cachedCowDesc;

    public void setCow(EntityCowsCow cow) {
        cowNBT = new NBTTagCompound();
        cow.writeEntityToNBT(cowNBT);

        cowType = cow.getType();
        cowGain = cow.getGain();
        cowGrowth = cow.getGrowth();
        cowIsChild = cow.isChild();

        cachedCowDesc = CowsRegistry.INSTANCE.getByType(cowType);
        getLocation().markBlockForUpdate();
        resetMaxProgress();
    }

    public boolean hasCow() {
        return cowNBT != null;
    }

    public EntityCowsCow getCow(World world) {
        if (cowNBT == null) {
            return null;
        }
        EntityCowsCow cow = new EntityCowsCow(world);
        cow.readEntityFromNBT(cowNBT);
        return cow;
    }

    public void removeCow() {
        cowNBT = null;
        cowType = 0;
        cowGain = 0;
        cowGrowth = 0;
        cowIsChild = false;

        progress = 0;
        maxProgress = 0;
        progressPercent = 0;

        cachedCowDesc = null;
        getLocation().markBlockForUpdate();
    }

    @Override
    public float getProgress() {
        return progressPercent;
    }

    @Override
    public void setProgress(float percent) {
        progressPercent = percent;
    }

    @Override
    public boolean isActive() {
        return progress > 0 && hasCow();
    }

    private void resetMaxProgress() {
        if (!hasCow() || cachedCowDesc == null) {
            return;
        }

        int min = cachedCowDesc.getMinTime();
        int max = cachedCowDesc.getMaxTime();
        int range = max - min;

        int baseTime = min + (range > 0 ? rand.nextInt(range) : 0);

        float growthFactor = (10f - cowGrowth + 1f) / 10f;
        maxProgress = Math.max(1, (int) (baseTime * growthFactor) * 2);

        progress = 0;
        progressPercent = 0;
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        if (worldObj.isRemote || !hasCow() || cowIsChild) {
            return false;
        }

        if (maxProgress <= 0) {
            resetMaxProgress();
            return false;
        }

        progress++;
        progressPercent = (progress * 100f) / maxProgress;

        if (progress < maxProgress) {
            return false;
        }

        produceMilk();

        return false;
    }

    private void produceMilk() {
        if (cachedCowDesc == null) {
            return;
        }

        FluidStack fluid = cachedCowDesc.createMilkFluid();
        if (fluid == null) {
            resetMaxProgress();
            return;
        }

        if (cowGain >= 10) {
            fluid.amount *= 3;
        } else if (cowGain >= 5) {
            fluid.amount *= 2;
        }

        int filled = tank.fill(fluid, true);

        if (filled >= fluid.amount) {
            resetMaxProgress();
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || !tank.canDrainFluidType(resource.getFluid())) {
            return null;
        }
        return tank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return tank.canDrainFluidType(fluid);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    protected void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        tank.writeCommon("tank", root);

        root.setInteger("progress", progress);
        root.setInteger("maxProgress", maxProgress);
        root.setInteger("cowType", cowType);
        root.setInteger("cowGain", cowGain);
        root.setInteger("cowGrowth", cowGrowth);
        root.setBoolean("cowIsChild", cowIsChild);

        if (cowNBT != null) {
            root.setTag("cowNBT", cowNBT);
        }
    }

    @Override
    protected void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        tank.readCommon("tank", root);

        progress = root.getInteger("progress");
        maxProgress = root.getInteger("maxProgress");
        cowType = root.getInteger("cowType");
        cowGain = root.getInteger("cowGain");
        cowGrowth = root.getInteger("cowGrowth");
        cowIsChild = root.getBoolean("cowIsChild");

        if (root.hasKey("cowNBT")) {
            cowNBT = root.getCompoundTag("cowNBT");
            cachedCowDesc = CowsRegistry.INSTANCE.getByType(cowType);
        }
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }
}
