package ruiseki.omoshiroikamo.module.cable.common.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.LogicNetwork;

public abstract class AbstractPart implements ICablePart {

    protected ICable cable;
    protected ForgeDirection side;

    protected int tickInterval = 1;
    protected int tickCounter = tickInterval;

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
    public BlockPos getPos() {
        return cable.getPos();
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
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("tickCounter", tickCounter);
        tag.setInteger("tickInterval", Math.max(1, this.tickInterval));
        tag.setInteger("priority", priority);
        tag.setInteger("channel", channel);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.tickCounter = tag.getInteger("tickCounter");
        this.tickInterval = Math.max(1, tag.getInteger("tickInterval"));
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

    public boolean shouldDoTickInterval() {
        tickCounter++;
        if (tickCounter < tickInterval) return false;
        tickCounter = 0;
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void rotateForSide(ForgeDirection side) {
        switch (side) {
            case NORTH -> {
                GL11.glTranslatef(0f, 0f, -0.001f);
            }
            case SOUTH -> {
                GL11.glTranslatef(0f, 0f, 0.001f);
                GL11.glRotatef(180f, 0f, 1f, 0f);
            }
            case WEST -> {
                GL11.glTranslatef(-0.001f, 0f, 0f);
                GL11.glRotatef(90f, 0f, 1f, 0f);
            }
            case EAST -> {
                GL11.glTranslatef(0.001f, 0f, 0f);
                GL11.glRotatef(-90f, 0f, 1f, 0f);
            }
            case UP -> {
                GL11.glTranslatef(0f, 0.501f, -0.5f);
                GL11.glRotatef(90f, 1f, 0f, 0f);
            }
            case DOWN -> {
                GL11.glTranslatef(0f, 0.499f, 0.5f);
                GL11.glRotatef(-90f, 1f, 0f, 0f);
            }
            default -> {}
        }
    }

    public LogicNetwork getLogicNetwork() {
        return getCable() != null ? (LogicNetwork) getCable().getNetwork(ILogicNet.class) : null;
    }

    public void markDirty() {
        getCable().dirty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
