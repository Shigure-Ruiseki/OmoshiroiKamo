package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiFactories;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import ruiseki.omoshiroikamo.common.block.TileEntityOK;

public abstract class AbstractTE extends TileEntityOK implements IGuiHolder<PosGuiData> {

    public short facing;

    // Client sync monitoring
    protected boolean forceClientUpdate = true;
    protected boolean lastActive;
    protected int ticksSinceActiveChanged = 0;

    protected boolean redstoneCheckPassed;

    private boolean redstoneStateDirty = true;

    protected boolean notifyNeighbours = false;

    public boolean isDirty = false;

    public short getFacing() {
        return facing;
    }

    public void setFacing(short facing) {
        this.facing = facing;
    }

    public ForgeDirection getFacingDir() {
        return ForgeDirection.getOrientation(facing);
    }

    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block nbid) {
        redstoneStateDirty = true;
    }

    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {
        writeToItemStack(stack);
    }

    // Special

    public String getMachineName() {
        return this.worldObj.getBlock(xCoord, yCoord, zCoord)
            .getUnlocalizedName();
    }

    public abstract boolean isActive();

    protected abstract boolean processTasks(boolean redstoneCheckPassed);

    @Override
    protected void doUpdate() {
        if (worldObj.isRemote) {
            updateEntityClient();
            return;
        } // else is server, do all logic only on the server

        boolean requiresClientSync = forceClientUpdate;
        boolean prevRedCheck = redstoneCheckPassed;

        if (redstoneStateDirty) {
            redstoneCheckPassed = isPoweredRedstone();
            redstoneStateDirty = false;
        }

        requiresClientSync |= prevRedCheck != redstoneCheckPassed;

        requiresClientSync |= processTasks(redstoneCheckPassed);

        if (requiresClientSync) {

            // this will cause 'getPacketDescription()' to be called and its result
            // will be sent to the PacketHandler on the other end of
            // client/server connection
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            // And this will make sure our current tile entity state is saved
            markDirty();
        }

        if (notifyNeighbours) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            notifyNeighbours = false;
        }
    }

    protected void updateEntityClient() {
        if (isActive() != lastActive) {
            ticksSinceActiveChanged++;
            if (ticksSinceActiveChanged > 20 || isActive()) {
                ticksSinceActiveChanged = 0;
                lastActive = isActive();
                forceClientUpdate = true;
            }
        }

        if (forceClientUpdate) {
            if (worldObj.rand.nextInt(1024) <= (isDirty ? 256 : 0)) {
                isDirty = !isDirty;
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            forceClientUpdate = false;
        }
    }

    public void setForceClientUpdate(boolean forceClientUpdate) {
        this.forceClientUpdate = forceClientUpdate;
    }

    @Override
    protected void writeCommon(NBTTagCompound root) {
        root.setShort("facing", facing);
        root.setBoolean("redstoneCheckPassed", redstoneCheckPassed);
        root.setBoolean("forceClientUpdate", forceClientUpdate);
        forceClientUpdate = false;
    }

    @Override
    protected void readCommon(NBTTagCompound root) {
        setFacing(root.getShort("facing"));
        redstoneCheckPassed = root.getBoolean("redstoneCheckPassed");
        forceClientUpdate = root.getBoolean("forceClientUpdate");
    }

    public boolean isServerSide() {
        return !this.worldObj.isRemote;
    }

    public void readFromItemStack(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) {
            return;
        }
        readCommon(stack.stackTagCompound);
    }

    public void writeToItemStack(ItemStack stack) {
        if (stack == null) {
            return;
        }
        if (stack.stackTagCompound == null) {
            stack.stackTagCompound = new NBTTagCompound();
        }

        NBTTagCompound root = stack.stackTagCompound;
        root.setBoolean("te.abstractMachine", true);
        writeCommon(root);
    }

    public void openGui(EntityPlayer player) {
        if (!worldObj.isRemote) {
            GuiFactories.tileEntity()
                .open(player, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new ModularPanel("base");
    }

    @Override
    public BlockPos getLocation() {
        return new BlockPos(xCoord, yCoord, zCoord);
    }

}
