package ruiseki.omoshiroikamo.core.common.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.block.IOKTile;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.persist.nbt.INBTProvider;
import ruiseki.omoshiroikamo.api.persist.nbt.NBTProviderComponent;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.core.common.network.PacketProgress;

public abstract class TileEntityOK extends TileEntity implements IOKTile, INBTProvider {

    @Delegate
    private INBTProvider nbtProviderComponent = new NBTProviderComponent(this);

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

    @Override
    public final void updateEntity() {
        doUpdate();
        if (isProgressTile && !worldObj.isRemote) {
            int curScaled = getProgressScaled(16);
            if (++ticksSinceLastProgressUpdate >= getProgressUpdateFreq() || curScaled != lastProgressScaled) {
                requestProgressSync();
                lastProgressScaled = curScaled;
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

    protected void doUpdate() {}

    protected void requestProgressSync() {
        if (!isProgressTile || worldObj.isRemote) return;
        PacketHandler.sendToAllAround(new PacketProgress((IProgressTile) this), this);
        ticksSinceLastProgressUpdate = 0;
    }

    /**
     * Controls how often progress updates.
     * Has no effect if your TE is not IProgressTile.
     */
    protected int getProgressUpdateFreq() {
        return 20;
    }

    @Override
    public void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
        readGeneratedFieldsFromNBT(root);
    }

    @Override
    public void writeToNBT(NBTTagCompound root) {
        super.writeToNBT(root);
        writeGeneratedFieldsToNBT(root);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    private BlockPos cachedPos = null;

    @Override
    public BlockPos getPos() {
        if (cachedPos == null || cachedPos.getX() != xCoord
            || cachedPos.getY() != yCoord
            || cachedPos.getZ() != zCoord
            || cachedPos.getWorld() != worldObj) {
            cachedPos = new BlockPos(this);
        }
        return cachedPos;
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
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
