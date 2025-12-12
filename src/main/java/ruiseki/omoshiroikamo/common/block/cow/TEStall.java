package ruiseki.omoshiroikamo.common.block.cow;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
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
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketStall;
import ruiseki.omoshiroikamo.config.backport.CowConfig;

public class TEStall extends AbstractTE implements IFluidHandler, IProgressTile {

    public static final int amount = FluidContainerRegistry.BUCKET_VOLUME * 10;

    public final SmartTank tank = new SmartTank(amount);
    protected int lastUpdateLevel = -1;

    private boolean tankDirty = false;
    private Fluid lastFluid = null;

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

        float growthFactor = getCowGrowthModifier();
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

        int filledLevel = getFilledLevel();
        if (lastUpdateLevel != filledLevel) {
            lastUpdateLevel = filledLevel;
            tankDirty = false;
            return true;
        }

        if (tankDirty && shouldDoWorkThisTick(10)) {
            PacketHandler.sendToAllAround(new PacketStall(this), this);
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
            Fluid held = tank.getFluid() == null ? null
                : tank.getFluid()
                    .getFluid();
            if (lastFluid != held) {
                lastFluid = held;
                worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
            }
            tankDirty = false;
        }

        return false;
    }

    private int getFilledLevel() {
        int level = (int) Math.floor(16 * tank.getFilledRatio());
        if (level == 0 && tank.getFluidAmount() > 0) {
            level = 1;
        }
        return level;
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

        int bonusMultiplier = Math.max(0, cowGain / 5);
        if (bonusMultiplier > 0) {
            fluid.amount *= (1 + bonusMultiplier);
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
        FluidStack res = tank.drain(resource, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        FluidStack res = tank.drain(maxDrain, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
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

    private float getCowGrowthModifier() {
        int maxGrowth = Math.max(1, CowConfig.getMaxGrowthStat());
        int clampedGrowth = Math.max(1, Math.min(cowGrowth, maxGrowth));
        return (float) (maxGrowth - clampedGrowth + 1) / (float) maxGrowth;
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
