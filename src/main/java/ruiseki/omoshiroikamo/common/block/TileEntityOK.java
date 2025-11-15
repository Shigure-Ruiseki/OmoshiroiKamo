package ruiseki.omoshiroikamo.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketProgress;

public abstract class TileEntityOK extends TileEntity {

    private final int checkOffset = (int) (Math.random() * 20);
    protected final boolean isProgressTile;

    protected int lastProgressScaled = -1;
    protected int ticksSinceLastProgressUpdate;

    public TileEntityOK() {
        isProgressTile = this instanceof IProgressTile;
    }

    @Override
    public final boolean canUpdate() {
        return shouldUpdate() || isProgressTile;
    }

    protected boolean shouldUpdate() {
        return true;
    }

    private long lastUpdate = 0;

    @Override
    public final void updateEntity() {
        if (worldObj.getTotalWorldTime() != lastUpdate) {
            lastUpdate = worldObj.getTotalWorldTime();
            doUpdate();
            if (isProgressTile && !worldObj.isRemote) {
                int curScaled = getProgressScaled(16);
                if (++ticksSinceLastProgressUpdate >= getProgressUpdateFreq() || curScaled != lastProgressScaled) {
                    sendTaskProgressPacket();
                    lastProgressScaled = curScaled;
                }
            }
        }
    }

    public final int getProgressScaled(int scale) {
        if (isProgressTile) {
            return getProgressScaled(scale, (IProgressTile) this);
        }
        return 0;
    }

    public static int getProgressScaled(int scale, IProgressTile tile) {
        return (int) (tile.getProgress() * scale);
    }

    protected void doUpdate() {

    }

    protected void sendTaskProgressPacket() {
        if (isProgressTile) {
            PacketHandler.sendToAllAround(new PacketProgress((IProgressTile) this), this);
        }
        ticksSinceLastProgressUpdate = 0;
    }

    /**
     * Controls how often progress updates. Has no effect if your TE is not {@link IProgressTile}.
     */
    protected int getProgressUpdateFreq() {
        return 20;
    }

    @Override
    public final void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
        readCommon(root);
    }

    @Override
    public final void writeToNBT(NBTTagCompound root) {
        super.writeToNBT(root);
        writeCommon(root);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeCommon(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readCommon(pkt.func_148857_g());
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    protected abstract void writeCommon(NBTTagCompound root);

    protected abstract void readCommon(NBTTagCompound root);

    protected void updateBlock() {
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    protected boolean isPoweredRedstone() {
        return worldObj.blockExists(xCoord, yCoord, zCoord)
            ? worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0
            : false;
    }

    /**
     * Called directly after the TE is constructed. This is the place to call non-final methods.
     * <p>
     * Note: This will not be called when the TE is loaded from the save. Hook into the nbt methods for that.
     */
    public void init() {}

    private BlockPos cachedLocation = null;

    public BlockPos getLocation() {
        return cachedLocation == null || !cachedLocation.equals(xCoord, yCoord, zCoord)
            ? (cachedLocation = new BlockPos(xCoord, yCoord, zCoord))
            : cachedLocation;
    }

    /**
     * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This
     * is staggered so the work of different TEs is stretched out over time.
     *
     * @see #shouldDoWorkThisTick(int, int) If you need to offset work ticks
     */
    protected boolean shouldDoWorkThisTick(int interval) {
        return shouldDoWorkThisTick(interval, 0);
    }

    /**
     * Call this with an interval (in ticks) to find out if the current tick is the one you want to do some work. This
     * is staggered so the work of different TEs is stretched out over time.
     * <p>
     * If you have different work items in your TE, use this variant to stagger your work.
     */
    protected boolean shouldDoWorkThisTick(int interval, int offset) {
        return (worldObj.getTotalWorldTime() + checkOffset + offset) % interval == 0;
    }
}
