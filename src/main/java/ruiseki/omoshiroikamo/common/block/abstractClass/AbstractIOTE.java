package ruiseki.omoshiroikamo.common.block.abstractClass;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.InventoryWrapper;
import com.enderio.core.common.util.ItemUtil;

import ruiseki.omoshiroikamo.api.io.IIoConfigurable;
import ruiseki.omoshiroikamo.api.io.IoMode;
import ruiseki.omoshiroikamo.api.io.IoType;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuis;

public abstract class AbstractIOTE extends AbstractStorageTE implements IIoConfigurable {

    protected Map<IoType, Map<ForgeDirection, IoMode>> ioConfigs;

    public AbstractIOTE(SlotDefinition slotDefinition) {
        super(slotDefinition);
    }

    @Override
    public IoMode toggleIoModeForFace(ForgeDirection faceHit, IoType type) {
        IoMode curMode = getIoMode(faceHit, type);
        IoMode mode = curMode.next();
        while (!supportsMode(faceHit, mode)) {
            mode = mode.next();
        }
        setIoMode(faceHit, mode, type);
        return mode;
    }

    @Override
    public boolean supportsMode(ForgeDirection faceHit, IoMode mode) {
        return true;
    }

    @Override
    public void setIoMode(ForgeDirection faceHit, IoMode mode, IoType type) {
        if (mode == IoMode.NONE && ioConfigs == null) {
            return;
        }

        if (ioConfigs == null) {
            ioConfigs = new EnumMap<IoType, Map<ForgeDirection, IoMode>>(IoType.class);
        }

        Map<ForgeDirection, IoMode> faceMap = ioConfigs.get(type);
        if (faceMap == null) {
            faceMap = new EnumMap<ForgeDirection, IoMode>(ForgeDirection.class);
            ioConfigs.put(type, faceMap);
        }

        faceMap.put(faceHit, mode);

        forceClientUpdate = true;
        notifyNeighbours = true;

        updateBlock();
    }

    @Override
    public void clearAllIoModes(IoType type) {
        if (ioConfigs != null && ioConfigs.containsKey(type)) {
            ioConfigs.remove(type);
            forceClientUpdate = true;
            notifyNeighbours = true;
            updateBlock();
        }
    }

    @Override
    public IoMode getIoMode(ForgeDirection face, IoType type) {
        if (ioConfigs == null) {
            return IoMode.NONE;
        }

        Map<ForgeDirection, IoMode> faceMap = ioConfigs.get(type);
        if (faceMap == null) {
            return IoMode.NONE;
        }

        IoMode res = faceMap.get(face);
        if (res == null) {
            return IoMode.NONE;
        }

        return res;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        NBTTagCompound ioConfigsTag = new NBTTagCompound();
        if (ioConfigs != null && !ioConfigs.isEmpty()) {

            for (Map.Entry<IoType, Map<ForgeDirection, IoMode>> entry : ioConfigs.entrySet()) {
                IoType type = entry.getKey();
                Map<ForgeDirection, IoMode> faceMap = entry.getValue();

                if (faceMap != null && !faceMap.isEmpty()) {
                    NBTTagCompound faceTag = new NBTTagCompound();

                    for (Map.Entry<ForgeDirection, IoMode> faceEntry : faceMap.entrySet()) {
                        faceTag.setShort(
                            "face" + faceEntry.getKey()
                                .ordinal(),
                            (short) faceEntry.getValue()
                                .ordinal());
                    }

                    ioConfigsTag.setTag(type.name(), faceTag);
                }
            }

            root.setTag("ioConfigs", ioConfigsTag);
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        if (root.hasKey("ioConfigs")) {
            NBTTagCompound ioConfigsTag = root.getCompoundTag("ioConfigs");
            for (IoType type : IoType.values()) {
                if (ioConfigsTag.hasKey(type.name())) {
                    NBTTagCompound faceTag = ioConfigsTag.getCompoundTag(type.name());
                    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                        String key = "face" + dir.ordinal();
                        if (faceTag.hasKey(key)) {
                            short modeId = faceTag.getShort(key);
                            IoMode mode = IoMode.values()[modeId];
                            setIoMode(dir, mode, type);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (isSideDisabled(side, IoType.ITEM)) {
            return new int[0];
        }
        return super.getAccessibleSlotsFromSide(side);
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemstack, int side) {
        if (isSideDisabled(side, IoType.ITEM) || !slotDefinition.isInputSlot(slot)) {
            return false;
        }
        return super.canInsertItem(slot, itemstack, side);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
        if (isSideDisabled(side, IoType.ITEM)) {
            return false;
        }
        return super.canExtractItem(slot, itemstack, side);
    }

    @Override
    public void doUpdate() {
        if (!isServerSide()) {
            updateEntityClient();
            return;
        } // else is server, do all logic only on the server

        boolean requiresClientSync = forceClientUpdate;
        boolean prevRedCheck = redstoneCheckPassed;
        if (redstoneStateDirty) {
            redstoneCheckPassed = true;
            redstoneStateDirty = false;
        }

        if (shouldDoWorkThisTick(5)) {
            requiresClientSync |= doSideIo();
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

    protected boolean doSideIo() {
        if (ioConfigs == null || ioConfigs.isEmpty()) {
            return false;
        }

        boolean res = false;

        for (Map<ForgeDirection, IoMode> faceMap : ioConfigs.values()) {
            if (faceMap == null || faceMap.isEmpty()) {
                continue;
            }

            for (Map.Entry<ForgeDirection, IoMode> entry : faceMap.entrySet()) {
                ForgeDirection side = entry.getKey();
                IoMode mode = entry.getValue();

                if (mode.inputs()) {
                    res |= doPull(side);
                }
                if (mode.outputs()) {
                    res |= doPush(side);
                }
            }
        }

        return res;
    }

    protected boolean doPush(ForgeDirection dir) {

        if (slotDefinition.getItemOutputs() <= 0) {
            return false;
        }
        if (!shouldDoWorkThisTick(20)) {
            return false;
        }

        BlockCoord loc = getLocation().getLocation(dir);
        TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);

        return doPush(dir, te, slotDefinition.getMinItemInput(), slotDefinition.getMaxItemInput());
    }

    protected boolean doPush(ForgeDirection dir, TileEntity te, int minSlot, int maxSlot) {
        if (te == null) {
            return false;
        }

        for (int i = minSlot; i <= maxSlot; i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (item != null) {
                int num = ItemUtil.doInsertItem(te, item, dir.getOpposite());
                if (num > 0) {
                    item.stackSize -= num;
                    if (item.stackSize <= 0) {
                        item = null;
                    }
                    inv.setStackInSlot(i, item);
                    markDirty();
                }
            }
        }
        return false;
    }

    protected boolean doPull(ForgeDirection dir) {
        if (slotDefinition.getItemInputs() <= 0) {
            return false;
        }
        if (!shouldDoWorkThisTick(20)) {
            return false;
        }

        boolean hasSpace = false;
        for (int slot = slotDefinition.getMinItemInput(); slot <= slotDefinition.getMaxItemInput()
            && !hasSpace; slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            hasSpace = (stack == null || stack.stackSize < Math.min(stack.getMaxStackSize(), getInventoryStackLimit()));
        }

        if (!hasSpace) {
            return false;
        }

        BlockCoord loc = getLocation().getLocation(dir);
        TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);
        if (te == null) {
            return false;
        }

        if (!(te instanceof IInventory)) {
            return false;
        }

        ISidedInventory target = (te instanceof ISidedInventory) ? (ISidedInventory) te
            : new InventoryWrapper((IInventory) te);

        int[] targetSlots = target.getAccessibleSlotsFromSide(
            dir.getOpposite()
                .ordinal());
        if (targetSlots == null) {
            return false;
        }

        for (int inputSlot = slotDefinition.getMinItemInput(); inputSlot
            <= slotDefinition.getMaxItemInput(); inputSlot++) {
            if (doPull(inputSlot, target, targetSlots, dir)) {
                return true;
            }
        }

        return false;
    }

    protected boolean doPull(int inputSlot, ISidedInventory target, int[] targetSlots, ForgeDirection side) {
        for (int i = 0; i < targetSlots.length; i++) {
            int tSlot = targetSlots[i];
            ItemStack targetStack = target.getStackInSlot(tSlot);

            if (targetStack != null && target.canExtractItem(
                tSlot,
                targetStack,
                side.getOpposite()
                    .ordinal())) {
                ItemStack copy = targetStack.copy();
                int inserted = ItemUtil.doInsertItem(this, copy, side);

                if (inserted > 0) {
                    targetStack.stackSize -= inserted;
                    if (targetStack.stackSize <= 0) {
                        target.setInventorySlotContents(tSlot, null);
                    } else {
                        target.setInventorySlotContents(tSlot, targetStack);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSideDisabled(int side, IoType type) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        IoMode mode = getIoMode(dir, type);
        if (mode == IoMode.DISABLED) {
            return true;
        }
        return false;
    }

    public void onNeighborBlockChange(Block blockId) {
        redstoneStateDirty = true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return MGuis.mteTemplatePanelBuilder(this, data, syncManager, settings)
            .build();
    }

}
