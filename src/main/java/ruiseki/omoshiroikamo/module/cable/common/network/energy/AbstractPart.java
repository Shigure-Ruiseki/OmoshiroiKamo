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
    protected int tickInterval = 20;

    protected int priority = 0;
    protected int channel = 0;

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
        tag.setInteger("TICK_INTERVAL", this.tickInterval);
        tag.setInteger("priority", priority);
        tag.setInteger("channel", channel);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.tickCounter = tag.getInteger("TickCounter");
        this.tickInterval = tag.getInteger("TICK_INTERVAL");
        this.priority = tag.getInteger("priority");
        this.channel = tag.getInteger("channel");
    }

    @Override
    public int getTickInterval() {
        return this.tickInterval;
    }

    @Override
    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    @Override
    public TileEntity getTargetTE() {
        if (this.cable == null || this.side == null) return null;
        return this.cable.getPos()
            .offset(this.side)
            .getTileEntity(this.cable.getWorld());
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }

}
