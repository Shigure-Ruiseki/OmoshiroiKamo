package ruiseki.omoshiroikamo.module.ids.common.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICablePart;

public abstract class AbstractPart implements ICablePart {

    protected ICable cable;
    protected ForgeDirection side;

    protected int tickInterval = 1;

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
        tag.setInteger("tickInterval", Math.max(1, this.tickInterval));
        tag.setInteger("priority", priority);
        tag.setInteger("channel", channel);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
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

    public World getWorld() {
        return getCable().getWorld();
    }

    public int targetX() {
        return getPos().offset(getSide()).x;
    }

    public int targetY() {
        return getPos().offset(getSide()).y;
    }

    public int targetZ() {
        return getPos().offset(getSide()).z;
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

    public boolean shouldTickNow() {
        if (tickInterval <= 1) return true;
        return getWorld().getTotalWorldTime() % tickInterval == 0;
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

    public void markDirty() {
        getCable().dirty();
    }

    public void notifyNeighbors() {
        getCable().notifyNeighbors();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
