package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.GuiFactories;

import lombok.Getter;
import lombok.Setter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaTileInfoProvider;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Base class for tile entities in Omoshiroi Kamo mod.
 * <p>
 * Provides common functionality such as:
 * <ul>
 * <li>Redstone handling</li>
 * <li>Client-side synchronization</li>
 * <li>GUI handling via ModularUI</li>
 * <li>Interaction with ItemStacks for saving/loading</li>
 * </ul>
 */
public abstract class AbstractTE extends TileEntityOK implements IWailaTileInfoProvider {

    /** Forces client-side update to render changes. */
    protected boolean forceClientUpdate = true;

    /** Last known active state (for client sync). */
    protected boolean lastActive;

    /** Ticks since last active state change. */
    protected int ticksSinceActiveChanged = 0;

    /** Randomly toggled for visual updates. */
    protected boolean isDirty = false;

    /** Redstone mode of the machine (ALWAYS_ON, PULSE, etc.). */
    @Getter
    protected RedstoneMode redstoneMode = RedstoneMode.ALWAYS_ON;

    /** Current redstone power state. */
    @Setter
    protected boolean redstonePowered = false;

    /** Current redstone power level (0-15). */
    @Setter
    protected int redstoneLevel = 0;

    /** Flag indicating if redstone state needs updating. */
    protected boolean redstoneStateDirty = true;

    /** Flag indicating that neighbor blocks need notification. */
    protected boolean notifyNeighbours = false;

    /** NBT tag name for inventory data. */
    public static String INVENTORY_TAG = "inventory";

    /**
     * Returns the facing direction of the tile entity based on block state.
     *
     * @return facing direction
     */
    public ForgeDirection getFacing() {
        return BlockStateUtils.getFacingProp(worldObj, xCoord, yCoord, zCoord);
    }

    /**
     * Called when a player right-clicks the block.
     *
     * @param world  the world
     * @param player the player
     * @param side   the side of the block clicked
     * @param hitX   local hit X coordinate
     * @param hitY   local hit Y coordinate
     * @param hitZ   local hit Z coordinate
     * @return true if the block handled the activation
     */
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        return false;
    }

    /**
     * Handles neighbor block changes, updates redstone state, and flags neighbor notification.
     *
     * @param world the world
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param z     z-coordinate
     * @param block neighbor block that changed
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int oldLevel = redstoneLevel;
        redstoneLevel = world.getStrongestIndirectPower(x, y, z);
        redstonePowered = redstoneLevel > 0;

        redstoneStateDirty = true;

        if (oldLevel != redstoneLevel) {
            notifyNeighbours = true;
        }
    }

    /**
     * Returns the localized machine name based on block type.
     *
     * @return machine name
     */
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".name");
    }

    public String getUnlocalizedName() {
        return this.worldObj.getBlock(xCoord, yCoord, zCoord)
            .getUnlocalizedName();
    }

    /**
     * Returns whether the machine is currently active.
     *
     * @return true if active
     */
    public abstract boolean isActive();

    /**
     * Checks if the machine is allowed to operate based on redstone mode.
     *
     * @return true if machine is redstone active
     */
    public boolean isRedstoneActive() {
        return RedstoneMode.isActive(redstoneMode, redstonePowered);
    }

    /**
     * Sets the redstone mode and forces client update.
     *
     * @param mode new redstone mode
     */
    public void setRedstoneMode(RedstoneMode mode) {
        redstoneMode = mode;
        forceClientUpdate = true;
    }

    /**
     * Processes machine tasks. Called each tick.
     *
     * @param redstoneCheckPassed true if redstone allows operation
     * @return true if the tile entity performed tasks
     */
    public abstract boolean processTasks(boolean redstoneCheckPassed);

    /**
     * Updates the tile entity each tick.
     * Handles client sync, redstone changes, task processing, and neighbor notifications.
     */
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

    /**
     * Updates the client-side state for visual representation.
     * Handles active state changes and dirty toggling for animations.
     */
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

        NBTTagCompound redstoneTag = new NBTTagCompound();
        redstoneTag.setInteger("level", redstoneLevel);
        redstoneTag.setBoolean("powered", redstonePowered);
        redstoneTag.setInteger("mode", redstoneMode.getIndex());
        root.setTag("redstone", redstoneTag);
    }

    @Override
    public void readCommon(NBTTagCompound root) {

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

    public void requestClientSync() {
        if (worldObj.isRemote) return;
        requestRenderUpdate();
        forceClientUpdate = true;
    }

    public void requestRenderUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void openGui(EntityPlayer player) {
        if (!worldObj.isRemote) {
            GuiFactories.tileEntity()
                .open(player, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {

    }
}
