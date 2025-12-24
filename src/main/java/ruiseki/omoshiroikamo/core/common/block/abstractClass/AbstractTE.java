package ruiseki.omoshiroikamo.core.common.block.abstractClass;

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

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.api.redstone.RedstoneMode;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public abstract class AbstractTE extends TileEntityOK implements IGuiHolder<PosGuiData> {

    @Setter
    @Getter
    protected int facing;

    // Client sync monitoring
    protected boolean forceClientUpdate = true;
    protected boolean lastActive;
    protected int ticksSinceActiveChanged = 0;
    protected boolean isDirty = false;

    @Getter
    protected RedstoneMode redstoneMode = RedstoneMode.ALWAYS_ON;
    @Setter
    protected boolean redstonePowered = false;
    @Setter
    protected int redstoneLevel = 0;
    protected boolean redstoneStateDirty = true;

    protected boolean notifyNeighbours = false;

    public static String INVENTORY_TAG = "inventory";

    public ForgeDirection getFacingDir() {
        return ForgeDirection.getOrientation(facing);
    }

    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        return false;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int oldLevel = redstoneLevel;
        redstoneLevel = world.getStrongestIndirectPower(x, y, z);
        redstonePowered = redstoneLevel > 0;

        redstoneStateDirty = true;

        if (oldLevel != redstoneLevel) {
            notifyNeighbours = true;
        }
    }

    public String getMachineName() {
        return LibMisc.LANG.localize(
            this.worldObj.getBlock(xCoord, yCoord, zCoord)
                .getUnlocalizedName());
    }

    public abstract boolean isActive();

    public boolean isRedstoneActive() {
        return RedstoneMode.isActive(redstoneMode, redstonePowered);
    }

    public void setRedstoneMode(RedstoneMode mode) {
        redstoneMode = mode;
        forceClientUpdate = true;
    }

    public abstract boolean processTasks(boolean redstoneCheckPassed);

    @Override
    public void doUpdate() {
        if (worldObj.isRemote) {
            updateEntityClient();
            return;
        }

        boolean requiresClientSync = forceClientUpdate;
        boolean prevRedCheck = isRedstoneActive();

        if (redstoneStateDirty) {
            redstoneStateDirty = false;
        }

        requiresClientSync |= prevRedCheck != isRedstoneActive();
        requiresClientSync |= processTasks(isRedstoneActive());

        if (requiresClientSync) {
            requestRenderUpdate();
            markDirty();
        }

        if (notifyNeighbours) {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
            notifyNeighbours = false;
        }
    }

    public void updateEntityClient() {
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
            requestRenderUpdate();
            forceClientUpdate = false;
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        root.setInteger("facing", facing);

        NBTTagCompound redstoneTag = new NBTTagCompound();
        redstoneTag.setInteger("level", redstoneLevel);
        redstoneTag.setBoolean("powered", redstonePowered);
        redstoneTag.setInteger("mode", redstoneMode.getIndex());
        root.setTag("redstone", redstoneTag);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        setFacing(root.getInteger("facing"));

        NBTTagCompound redstoneTag = root.getCompoundTag("redstone");
        redstoneLevel = redstoneTag.getInteger("level");
        redstonePowered = redstoneTag.getBoolean("powered");
        redstoneMode = RedstoneMode.byIndex(redstoneTag.getInteger("mode"));
    }

    public void readFromItemStack(ItemStack stack) {
        if (stack == null || stack.stackTagCompound == null) return;
        readCommon(stack.stackTagCompound);
    }

    public void writeToItemStack(ItemStack stack) {
        if (stack == null) return;
        NBTTagCompound root = ItemNBTUtils.getNBT(stack);
        writeCommon(root);
    }

    public void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {
        writeToItemStack(stack);
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

    public void requestClientSync() {
        if (worldObj.isRemote) return;
        requestRenderUpdate();
        forceClientUpdate = true;
    }

    public void requestRenderUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
