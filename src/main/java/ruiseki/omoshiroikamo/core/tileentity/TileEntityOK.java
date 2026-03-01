package ruiseki.omoshiroikamo.core.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.capabilities.CapabilityDispatcher;
import ruiseki.omoshiroikamo.core.capabilities.ICapabilitySerializable;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.core.event.OKEventFactory;
import ruiseki.omoshiroikamo.core.persist.nbt.INBTProvider;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTProviderComponent;

public abstract class TileEntityOK extends TileEntity
    implements ITile, INBTProvider, ICapabilitySerializable, IOrientable {

    @Delegate
    private final INBTProvider nbtProvider = new NBTProviderComponent(this);

    @NBTPersist
    private ForgeDirection forward = ForgeDirection.UNKNOWN;
    @NBTPersist
    private ForgeDirection up = ForgeDirection.UNKNOWN;

    private static final int UPDATE_BACKOFF_TICKS = 1;
    private boolean shouldSendUpdate = false;
    private int sendUpdateBackoff = 0;
    private final boolean ticking;

    private BlockPos cachedPos;
    private final int randomOffset = (int) (Math.random() * 20);

    private final CapabilityDispatcher capabilities;

    public TileEntityOK() {
        this.capabilities = OKEventFactory.gatherCapabilities(this);
        this.sendUpdateBackoff = (int) (Math.random() * UPDATE_BACKOFF_TICKS);
        this.ticking = this instanceof ITickingTile;
    }

    /**
     * for dormant chunk cache.
     */
    public void onChunkLoad() {
        if (this.isInvalid()) {
            this.validate();
        }
    }

    @Override
    public boolean canBeRotated() {
        return true;
    }

    @Override
    public ForgeDirection getForward() {
        return this.forward;
    }

    @Override
    public ForgeDirection getUp() {
        return this.up;
    }

    @Override
    public void setOrientation(final ForgeDirection inForward, final ForgeDirection inUp) {
        this.forward = inForward;
        this.up = inUp;
        this.onSendUpdate();
        worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, Blocks.air);
    }

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y,
        int z) {
        return (oldBlock != newBlock);
    }

    protected boolean isTicking() {
        return ticking;
    }

    /**
     * Send a world update for the coordinates of this tile entity.
     * This will always send lag-safe updates, so calling this many times per tick will
     * not cause multiple packets to be sent, more info in the class javadoc.
     */
    public final void sendUpdate() {
        if (!isTicking()) {
            throw new RuntimeException("If you want to update, you must implement ITickingTile. This is a mod error.");
        }
        shouldSendUpdate = true;
    }

    /**
     * Send an immediate world update for the coordinates of this tile entity.
     * This does the same as {@link TileEntityOK#sendUpdate()} but will
     * ignore the update backoff.
     */
    public final void sendImmediateUpdate() {
        sendUpdate();
        sendUpdateBackoff = 0;
    }

    /**
     * Do not override this method (you won't even be able to do so).
     * Use updateTileEntity() instead.
     */
    private void updateTicking() {
        doUpdate();
        trySendActualUpdate();
    }

    /**
     * Override this method instead of {@link TileEntityOK#updateEntity()}.
     * This method is called each tick.
     */
    protected void doUpdate() {}

    private void trySendActualUpdate() {
        sendUpdateBackoff--;
        if (sendUpdateBackoff <= 0) {
            sendUpdateBackoff = getUpdateBackoffTicks();

            if (shouldSendUpdate) {
                shouldSendUpdate = false;

                beforeSendUpdate();
                onSendUpdate();
                afterSendUpdate();
            }
        }
    }

    /**
     * Called when an update will is sent.
     * This contains the logic to send the update, so make sure to call the super!
     */
    protected void onSendUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /**
     * Called when before update is sent.
     */
    protected void beforeSendUpdate() {

    }

    /**
     * Called when after update is sent. (Not necessarily received yet!)
     */
    protected void afterSendUpdate() {

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
        onUpdateReceived();
    }

    @Override
    public final void writeToNBT(NBTTagCompound root) {
        super.writeToNBT(root);
        writeCommon(root);
    }

    @Override
    public final void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
        readCommon(root);
    }

    public void writeCommon(NBTTagCompound tag) {
        writeGeneratedFieldsToNBT(tag);

        if (capabilities != null) {
            tag.setTag("OKCaps", capabilities.serializeNBT());
        }
    }

    public void readCommon(NBTTagCompound tag) {
        if (tag == null) return;
        readGeneratedFieldsFromNBT(tag);

        if (capabilities != null && tag.hasKey("OKCaps")) {
            capabilities.deserializeNBT(tag.getCompoundTag("OKCaps"));
        }
    }

    /**
     * This method is called when the tile entity receives
     * an update (ie a data packet) from the server.
     * If this tile entity uses NBT, then the NBT will have
     * already been updated when this method is called.
     */
    public void onUpdateReceived() {

    }

    /**
     * @return The minimum amount of ticks between two consecutive sent packets.
     */
    protected int getUpdateBackoffTicks() {
        return UPDATE_BACKOFF_TICKS;
    }

    /**
     * Called when the blockState of this tile entity is destroyed.
     */
    public void destroy() {
        invalidate();
    }

    /**
     * If this entity is interactable with a player.
     *
     * @param player The player that is checked.
     * @return If the given player can interact.
     */
    public boolean canInteractWith(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    /**
     * Get the NBT tag for this tile entity.
     *
     * @return The NBT tag that is created with the
     *         {@link TileEntityOK#writeToNBT(net.minecraft.nbt.NBTTagCompound)} method.
     */
    public NBTTagCompound getNBTTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return capabilities != null && capabilities.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, @Nullable ForgeDirection facing) {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret);
        return ret;
    }

    @Override
    public BlockPos getPos() {
        if (cachedPos == null || cachedPos.getX() != xCoord
            || cachedPos.getY() != yCoord
            || cachedPos.getZ() != zCoord) {

            cachedPos = new BlockPos(this);
        }
        return cachedPos;
    }

    @Override
    public DimPos getDimPos() {
        return DimPos.of(worldObj, getPos());
    }

    @Override
    public int getX() {
        return xCoord;
    }

    @Override
    public int getY() {
        return yCoord;
    }

    @Override
    public int getZ() {
        return zCoord;
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    @Override
    public int getWorldID() {
        return worldObj.provider.dimensionId;
    }

    @Override
    public Block getBlock() {
        return getBlockType();
    }

    @Override
    public int getMeta() {
        return getBlockMetadata();
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    public void mark() {
        markDirty();
    }

    @Override
    public void updateTEState() {
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateTELight() {
        worldObj.updateLightByType(EnumSkyBlock.Sky, xCoord, yCoord, zCoord);
        worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
    }

    protected boolean shouldDoWorkThisTick(int interval) {
        return (worldObj.getTotalWorldTime() + randomOffset) % interval == 0;
    }

    public boolean requiresTESR() {
        return false;
    }

    public interface ITickingTile {

        void updateEntity();
    }

    public static class TickingTileComponent implements ITickingTile {

        private final TileEntityOK tile;

        public TickingTileComponent(TileEntityOK tile) {
            this.tile = tile;
        }

        @Override
        public final void updateEntity() {
            tile.updateTicking();
        }
    }

}
