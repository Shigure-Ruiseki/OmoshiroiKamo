package ruiseki.omoshiroikamo.module.machinery.common.tile.proxy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

public class ExternalItemProxy implements IExternalPortProxy, IInventory {

    private final TEMachineController controller;
    private final ChunkCoordinates targetPosition;
    private TileEntity targetTileEntity;
    private EnumIO ioMode;
    private boolean errorNotified = false;

    public ExternalItemProxy(TEMachineController controller, ChunkCoordinates targetPosition, EnumIO ioMode) {
        this.controller = controller;
        this.targetPosition = targetPosition;
        this.ioMode = ioMode;
    }

    // --- IExternalPortProxy Implementation ---

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

    // --- IModularPort / ISidedIO Implementation ---

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

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

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return ioMode;
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        this.ioMode = state;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {}

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        return null;
    }

    // --- ITile Implementation ---

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

    // --- IInventory Implementation (Delegate to Target TileEntity) ---

    private void notifyError() {
        if (!errorNotified && getWorld() != null && !getWorld().isRemote) {
            errorNotified = true;
            String msg = LibMisc.LANG.localize(
                "chat.omoshiroikamo.port_error",
                controller.xCoord,
                controller.yCoord,
                controller.zCoord,
                targetPosition.posX,
                targetPosition.posY,
                targetPosition.posZ);
            for (Object obj : getWorld().playerEntities) {
                if (obj instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) obj;
                    if (player.getDistanceSq(controller.xCoord, controller.yCoord, controller.zCoord) < 1024.0D) {
                        player.addChatMessage(new ChatComponentText(msg));
                    }
                }
            }
        }
    }

    private IInventory getInv() {
        TileEntity te = getTargetTileEntity();
        if (te instanceof IInventory) {
            return (IInventory) te;
        }
        notifyError();
        return null;
    }

    @Override
    public int getSizeInventory() {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.getSizeInventory() : 0;
        } catch (Exception e) {
            notifyError();
            return 0;
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.getStackInSlot(slot) : null;
        } catch (Exception e) {
            notifyError();
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.decrStackSize(slot, amount) : null;
        } catch (Exception e) {
            notifyError();
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.getStackInSlotOnClosing(slot) : null;
        } catch (Exception e) {
            notifyError();
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        try {
            IInventory inv = getInv();
            if (inv != null) {
                inv.setInventorySlotContents(slot, stack);
            }
        } catch (Exception e) {
            notifyError();
        }
    }

    @Override
    public String getInventoryName() {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.getInventoryName() : "External Inventory Proxy";
        } catch (Exception e) {
            notifyError();
            return "External Inventory Proxy";
        }
    }

    @Override
    public boolean hasCustomInventoryName() {
        try {
            IInventory inv = getInv();
            return inv != null && inv.hasCustomInventoryName();
        } catch (Exception e) {
            notifyError();
            return false;
        }
    }

    @Override
    public int getInventoryStackLimit() {
        try {
            IInventory inv = getInv();
            return inv != null ? inv.getInventoryStackLimit() : 64;
        } catch (Exception e) {
            notifyError();
            return 0;
        }
    }

    @Override
    public void markDirty() {
        try {
            IInventory inv = getInv();
            if (inv != null) {
                inv.markDirty();
            }
        } catch (Exception e) {
            notifyError();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        try {
            IInventory inv = getInv();
            return inv != null && inv.isUseableByPlayer(player);
        } catch (Exception e) {
            notifyError();
            return false;
        }
    }

    @Override
    public void openInventory() {
        try {
            IInventory inv = getInv();
            if (inv != null) {
                inv.openInventory();
            }
        } catch (Exception e) {
            notifyError();
        }
    }

    @Override
    public void closeInventory() {
        try {
            IInventory inv = getInv();
            if (inv != null) {
                inv.closeInventory();
            }
        } catch (Exception e) {
            notifyError();
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        try {
            IInventory inv = getInv();
            return inv != null && inv.isItemValidForSlot(slot, stack);
        } catch (Exception e) {
            notifyError();
            return false;
        }
    }
}
