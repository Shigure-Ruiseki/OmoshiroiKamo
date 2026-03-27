package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IExternalPortProxy;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Abstract base class for external port proxies.
 * Provides common functionality for proxying external TileEntities as modular ports.
 *
 * Design Pattern: Adapter + Template Method
 * - Adapts external TileEntities to the IModularPort interface
 * - Provides template methods for error handling and delegation
 */
public abstract class AbstractExternalProxy implements IExternalPortProxy {

    protected final TEMachineController controller;
    protected final ChunkCoordinates targetPosition;
    protected TileEntity targetTileEntity;
    protected EnumIO ioMode;
    protected ForgeDirection cachedSide = ForgeDirection.UNKNOWN;

    public AbstractExternalProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        this.controller = controller;
        this.targetPosition = targetPosition;
        this.ioMode = ioMode;
    }

    // ========== IExternalPortProxy Implementation ==========

    @Override
    public TEMachineController getController() {
        return controller;
    }

    @Override
    public ChunkCoordinates getTargetPosition() {
        return targetPosition;
    }

    @Override
    public TileEntity getTargetTileEntity() {
        if (targetTileEntity == null || targetTileEntity.isInvalid()) {
            if (getWorld() != null) {
                targetTileEntity = getWorld().getTileEntity(getX(), getY(), getZ());
            }
        }
        return targetTileEntity;
    }

    @Override
    public void setTargetTileEntity(TileEntity tileEntity) {
        this.targetTileEntity = tileEntity;
    }

    // ========== IPortType Implementation ==========

    @Override
    public IPortType.Direction getPortDirection() {
        switch (ioMode) {
            case INPUT:
                return IPortType.Direction.INPUT;
            case OUTPUT:
                return IPortType.Direction.OUTPUT;
            case BOTH:
                return IPortType.Direction.BOTH;
            default:
                return IPortType.Direction.NONE;
        }
    }

    // ========== ISidedIO Implementation ==========

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return ioMode;
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        this.ioMode = state;
    }

    // ========== IModularPort Implementation ==========

    @Override
    public void accept(IRecipeVisitor visitor) {
        // Proxies don't directly participate in recipe processing
        // The actual target TileEntity handles the operations
    }

    @Override
    public int getTier() {
        // External proxies wrap external TileEntities, not tiered ports
        // Return default tier 1 (0-indexed = 0)
        return 0;
    }

    @Override
    public void setTier(int tier) {
        // No-op: External proxies don't have mutable tiers
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        // Proxies don't have their own texture
        return null;
    }

    // ========== ITile Implementation ==========

    @Override
    public World getWorld() {
        return controller.getWorldObj();
    }

    @Override
    public int getX() {
        return targetPosition.posX;
    }

    @Override
    public int getY() {
        return targetPosition.posY;
    }

    @Override
    public int getZ() {
        return targetPosition.posZ;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(getX(), getY(), getZ());
    }

    @Override
    public DimPos getDimPos() {
        World w = getWorld();
        if (w == null) return DimPos.of(0, getPos());
        return DimPos.of(w, getPos());
    }

    @Override
    public void mark() {
        if (getWorld() != null) {
            getWorld().markBlockForUpdate(getX(), getY(), getZ());
        }
    }

    @Override
    public int getWorldID() {
        return getWorld() != null ? getWorld().provider.dimensionId : 0;
    }

    @Override
    public TileEntity getTile() {
        return getTargetTileEntity();
    }

    @Override
    public int getMeta() {
        return getWorld() != null ? getWorld().getBlockMetadata(getX(), getY(), getZ()) : 0;
    }

    @Override
    public void updateTEState() {
        mark();
    }

    @Override
    public void updateTELight() {
        if (getWorld() != null) {
            getWorld().func_147451_t(getX(), getY(), getZ());
        }
    }

    @Override
    public Block getBlock() {
        return getWorld() != null ? getWorld().getBlock(getX(), getY(), getZ()) : null;
    }

    // ========== Error Handling ==========

    /**
     * Notify nearby players of a proxy error (only once per target TileEntity).
     * Uses a flag to prevent spam.
     */
    protected void notifyError() {
        if (getWorld() != null && !getWorld().isRemote && controller.shouldNotifyError(targetPosition)) {
            String msg = buildErrorMessage();
            notifyNearbyPlayers(msg);
        }
    }

    /**
     * Build the error message to display to players.
     * Can be overridden by subclasses for custom messages.
     */
    protected String buildErrorMessage() {
        return LangHelpers.localize(
            "chat.omoshiroikamo.port_error",
            controller.xCoord,
            controller.yCoord,
            controller.zCoord,
            targetPosition.posX,
            targetPosition.posY,
            targetPosition.posZ);
    }

    /**
     * Notify all nearby players (within 32 blocks) of an error message.
     */
    private void notifyNearbyPlayers(String msg) {
        for (Object obj : getWorld().playerEntities) {
            if (obj instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) obj;
                if (player.getDistanceSq(controller.xCoord, controller.yCoord, controller.zCoord) < 1024.0D) {
                    player.addChatMessage(new ChatComponentText(msg));
                }
            }
        }
    }

    protected <T> T getTargetAs(Class<T> type) {
        return getTargetAs(type, false);
    }

    /**
     * Get the target TileEntity as a specific type.
     *
     * @param type   The expected type of the target TileEntity
     * @param silent If true, do not notify error on failure
     * @return The target TileEntity cast to the expected type, or null if not
     *         applicable
     */
    protected <T> T getTargetAs(Class<T> type, boolean silent) {
        TileEntity te = getTargetTileEntity();
        if (type.isInstance(te)) {
            return type.cast(te);
        }
        if (!silent) {
            notifyError();
        }
        return null;
    }

    protected <T, R> R delegate(Class<T> targetType, Function<T, R> operation, R defaultValue) {
        return delegate(targetType, operation, defaultValue, false);
    }

    /**
     * Safely delegate an operation to the target TileEntity with error handling.
     *
     * @param targetType   The expected interface type of the target
     * @param operation    The operation to perform on the target
     * @param defaultValue The value to return if the operation fails
     * @param silent       If true, do not notify error on failure
     * @return The result of the operation, or the default value on failure
     */
    protected <T, R> R delegate(Class<T> targetType, Function<T, R> operation, R defaultValue, boolean silent) {
        try {
            T target = getTargetAs(targetType, silent);
            return target != null ? operation.apply(target) : defaultValue;
        } catch (Exception e) {
            if (!silent) {
                notifyError();
            }
            return defaultValue;
        }
    }

    protected <T> void delegateVoid(Class<T> targetType, Consumer<T> operation) {
        delegateVoid(targetType, operation, false);
    }

    /**
     * Safely delegate a void operation to the target TileEntity with error handling.
     *
     * @param targetType The expected interface type of the target
     * @param operation  The operation to perform on the target
     * @param silent     If true, do not notify error on failure
     */
    protected <T> void delegateVoid(Class<T> targetType, Consumer<T> operation, boolean silent) {
        try {
            T target = getTargetAs(targetType, silent);
            if (target != null) {
                operation.accept(target);
            }
        } catch (Exception e) {
            if (!silent) {
                notifyError();
            }
        }
    }
}
