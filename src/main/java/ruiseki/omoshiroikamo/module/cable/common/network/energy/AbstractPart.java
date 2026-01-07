package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;

public abstract class AbstractPart implements ICablePart {

    protected ICable cable;
    protected ForgeDirection side;

    protected int tickCounter = 0;
    protected int TICK_INTERVAL = 20;

    @Override
    public ICable getCable() {
        return cable;
    }

    @Override
    public ForgeDirection getSide() {
        return side;
    }

    @Override
    public void setSide(ForgeDirection side) {
        this.side = side;
    }

    @Override
    public void setCable(ICable cable, ForgeDirection side) {
        this.cable = cable;
        setSide(side);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("TickCounter", tickCounter);
        tag.setInteger("TICK_INTERVAL", TICK_INTERVAL);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        tickCounter = tag.getInteger("TickCounter");
        TICK_INTERVAL = tag.getInteger("TICK_INTERVAL");
    }

    @Override
    public TileEntity getTargetTE() {
        if (cable == null || side == null) return null;
        return cable.getPos()
            .offset(side)
            .getTileEntity(cable.getWorld());
    }
}
