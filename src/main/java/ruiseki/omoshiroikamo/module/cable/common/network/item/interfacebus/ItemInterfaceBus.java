package ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus;

import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemQueryable;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndex;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public class ItemInterfaceBus extends AbstractPart implements IItemPart, IItemQueryable {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private int lastHash = 0;

    @Override
    public String getId() {
        return "item_interface_bus";
    }

    @Override
    public List<Class<? extends ICablePart>> getBasePartTypes() {
        return Collections.singletonList(IItemPart.class);
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void doUpdate() {
        if (cable.getWorld()
            .getTotalWorldTime() % 20 != 0) return;

        int hash = calcInventoryHash();
        if (hash != lastHash) {
            lastHash = hash;
            markNetworkDirty();
        }
    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ITEM_INTERFACE_BUS.newItemStack();
    }

    private int calcInventoryHash() {
        TileEntity te = getTargetTE();
        if (!(te instanceof IInventory inv)) return 0;

        int hash = 1;
        int[] slots = getAccessibleSlots(inv);

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                hash = 31 * hash + ItemStackKey.of(s).hash;
                hash = 31 * hash + s.stackSize;
            }
        }
        return hash;
    }

    private void markNetworkDirty() {
        ItemNetwork net = getItemNetwork();
        if (net != null) net.markDirty();
    }

    @Override
    public void collectItems(ItemIndex index) {
        TileEntity te = getTargetTE();
        if (!(te instanceof IInventory inv)) return;

        int[] slots = getAccessibleSlots(inv);

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                index.add(s);
            }
        }
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.syncValue("tickSyncer", SyncHandlers.intNumber(this::getTickInterval, this::setTickInterval));
        syncManager.syncValue("prioritySyncer", SyncHandlers.intNumber(this::getPriority, this::setPriority));
        syncManager.syncValue("channelSyncer", SyncHandlers.intNumber(this::getChannel, this::setChannel));

        ModularPanel panel = new ModularPanel("item_interface_bus");

        Row sideRow = new Row();
        sideRow.height(20);
        sideRow.child(
            IKey.lang("gui.cable.side")
                .asWidget());
        sideRow.child(
            new TextFieldWidget().value(new StringValue(getSide().name()))
                .right(0));

        Row tickRow = new Row();
        tickRow.height(20);
        tickRow.child(
            IKey.lang("gui.cable.tick")
                .asWidget());
        tickRow.child(
            new TextFieldWidget().syncHandler("tickSyncer")
                .right(0));

        Row priorityRow = new Row();
        priorityRow.height(20);
        priorityRow.child(
            IKey.lang("gui.cable.priority")
                .asWidget());
        priorityRow.child(
            new TextFieldWidget().syncHandler("prioritySyncer")
                .right(0));

        Row channelRow = new Row();
        channelRow.height(20);
        channelRow.child(
            IKey.lang("gui.cable.channel")
                .asWidget());
        channelRow.child(
            new TextFieldWidget().syncHandler("channelSyncer")
                .right(0));

        Column col = new Column();
        col.padding(7)
            .child(sideRow)
            .child(tickRow)
            .child(priorityRow)
            .child(channelRow);
        panel.child(col);

        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.BOTH;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (getSide()) {
            case WEST -> AxisAlignedBB.getBoundingBox(0f, W_MIN, W_MIN, DEPTH, W_MAX, W_MAX);
            case EAST -> AxisAlignedBB.getBoundingBox(1f - DEPTH, W_MIN, W_MIN, 1f, W_MAX, W_MAX);
            case DOWN -> AxisAlignedBB.getBoundingBox(W_MIN, 0f, W_MIN, W_MAX, DEPTH, W_MAX);
            case UP -> AxisAlignedBB.getBoundingBox(W_MIN, 1f - DEPTH, W_MIN, W_MAX, 1f, W_MAX);
            case NORTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 0f, W_MAX, W_MAX, DEPTH);
            case SOUTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 1f - DEPTH, W_MAX, W_MAX, 1f);
            default -> null;
        };
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/item_interface_bus.png");
    }

    @Override
    public int getTransferLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ItemStack extract(ItemStackKey key, int amount) {
        if (amount <= 0) return null;

        TileEntity te = getTargetTE();
        if (!(te instanceof IInventory inv)) return null;

        ItemStack result = null;
        int remaining = amount;

        for (int slot : getAccessibleSlots(inv)) {
            if (remaining <= 0) break;

            ItemStack s = inv.getStackInSlot(slot);
            if (s == null || s.stackSize <= 0) continue;
            if (!ItemStackKey.of(s)
                .equals(key)) continue;

            int take = Math.min(remaining, s.stackSize);
            ItemStack taken = inv.decrStackSize(slot, take);

            if (taken != null) {
                if (result == null) {
                    result = taken;
                } else {
                    result.stackSize += taken.stackSize;
                }
                remaining -= taken.stackSize;
            }
        }

        if (result != null) {
            inv.markDirty();
        }

        return result;
    }

    @Override
    public ItemStack insert(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return null;

        TileEntity te = getTargetTE();
        if (!(te instanceof IInventory inv)) return stack;

        ItemStack remaining = stack.copy();
        boolean changed = false;

        for (int slot : getAccessibleSlots(inv)) {
            if (remaining.stackSize <= 0) break;

            ItemStack target = inv.getStackInSlot(slot);

            if (target == null) {
                int add = Math.min(remaining.getMaxStackSize(), remaining.stackSize);
                ItemStack placed = remaining.splitStack(add);
                inv.setInventorySlotContents(slot, placed);
                changed = true;
            } else if (ItemUtils.areStacksEqual(target, remaining)) {
                int space = target.getMaxStackSize() - target.stackSize;
                if (space > 0) {
                    int add = Math.min(space, remaining.stackSize);
                    target.stackSize += add;
                    remaining.stackSize -= add;
                    changed = true;
                }
            }
        }

        if (changed) {
            inv.markDirty();
        }

        return remaining.stackSize > 0 ? remaining : null;
    }

    private int[] getAccessibleSlots(IInventory inv) {
        if (inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(getSide().ordinal());
        }

        int size = inv.getSizeInventory();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) slots[i] = i;
        return slots;
    }
}
