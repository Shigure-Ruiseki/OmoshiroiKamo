package ruiseki.omoshiroikamo.module.machinery.common.tile.vis;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.TileVisNode;

/**
 * Vis Bridge - absorbs Vis from adjacent Vis Output Port and provides it to the
 * Thaumcraft Vis network.
 * Extends TileVisNode to participate in the Vis Relay network.
 * Acts as a Vis source (isSource = true).
 */
public class TileVisBridge extends TileVisNode {

    private static final int MAX_VIS_PER_ASPECT = 10000; // centiVis
    private static final int ABSORB_RATE = 1000; // centiVis per operation
    private static final int ABSORB_INTERVAL = 10; // ticks
    private static final int NETWORK_RANGE = 8; // blocks

    private final int checkOffset = (int) (Math.random() * 20);
    private AspectList storedVis = new AspectList();
    private ForgeDirection absorptionDirection = ForgeDirection.UNKNOWN;
    private boolean neighborChanged = false;

    @Override
    public int getRange() {
        return NETWORK_RANGE;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    /**
     * Called when Wand or child nodes request Vis.
     * This is only called on source nodes.
     */
    @Override
    public int consumeVis(Aspect aspect, int amount) {
        if (aspect == null) return 0;

        int available = storedVis.getAmount(aspect);
        int consumed = Math.min(available, amount);

        if (consumed > 0) {
            storedVis.reduce(aspect, consumed);
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }

        return consumed;
    }

    @Override
    public void updateEntity() {
        super.updateEntity(); // maintains Vis network connections

        if (worldObj.isRemote) return;

        // Handle neighbor changes
        if (neighborChanged) {
            neighborChanged = false;
            // Re-check if the target port is still valid
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        // Use staggered ticks to spread load
        if (shouldDoWorkThisTick(ABSORB_INTERVAL)) {
            absorbFromOutputPort();
        }
    }

    private void absorbFromOutputPort() {
        if (absorptionDirection == ForgeDirection.UNKNOWN) return;

        int nx = xCoord + absorptionDirection.offsetX;
        int ny = yCoord + absorptionDirection.offsetY;
        int nz = zCoord + absorptionDirection.offsetZ;

        TileEntity te = worldObj.getTileEntity(nx, ny, nz);
        if (!(te instanceof AbstractVisPortTE)) return;

        AbstractVisPortTE visPort = (AbstractVisPortTE) te;

        // Only absorb from OUTPUT ports
        if (visPort.getPortDirection() != IPortType.Direction.OUTPUT) {
            return;
        }

        // Absorb each primal aspect
        for (Aspect aspect : getPrimalAspects()) {
            int available = visPort.getVisAmount(aspect);
            if (available <= 0) continue;

            int currentStored = storedVis.getAmount(aspect);
            int space = MAX_VIS_PER_ASPECT - currentStored;
            if (space <= 0) continue;

            int toAbsorb = Math.min(Math.min(available, ABSORB_RATE), space);
            if (toAbsorb > 0) {
                visPort.drainVis(aspect, toAbsorb);
                storedVis.add(aspect, toAbsorb);
            }
        }

        markDirty();
    }

    private Aspect[] getPrimalAspects() {
        return new Aspect[] { Aspect.AIR, Aspect.WATER, Aspect.FIRE, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY };
    }

    public void setAbsorptionDirection(ForgeDirection direction) {
        this.absorptionDirection = direction;
        markDirty();
    }

    public ForgeDirection getAbsorptionDirection() {
        return absorptionDirection;
    }

    public AspectList getStoredVis() {
        return storedVis;
    }

    public int getMaxVisPerAspect() {
        return MAX_VIS_PER_ASPECT;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        absorptionDirection = ForgeDirection.getOrientation(nbt.getInteger("absorbDir"));

        storedVis = new AspectList();
        NBTTagList aspectList = nbt.getTagList("storedVis", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < aspectList.tagCount(); i++) {
            NBTTagCompound tag = aspectList.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(tag.getString("aspect"));
            int amount = tag.getInteger("amount");
            if (aspect != null && amount > 0) {
                storedVis.add(aspect, amount);
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger("absorbDir", absorptionDirection.ordinal());

        NBTTagList aspectList = new NBTTagList();
        for (Aspect aspect : storedVis.getAspects()) {
            if (aspect != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspect", aspect.getTag());
                tag.setInteger("amount", storedVis.getAmount(aspect));
                aspectList.appendTag(tag);
            }
        }
        nbt.setTag("storedVis", aspectList);
    }

    protected boolean shouldDoWorkThisTick(int interval) {
        return shouldDoWorkThisTick(interval, 0);
    }

    protected boolean shouldDoWorkThisTick(int interval, int offset) {
        return (worldObj.getTotalWorldTime() + checkOffset + offset) % interval == 0;
    }

    public boolean canPlayerAccess(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        neighborChanged = true;
    }
}
