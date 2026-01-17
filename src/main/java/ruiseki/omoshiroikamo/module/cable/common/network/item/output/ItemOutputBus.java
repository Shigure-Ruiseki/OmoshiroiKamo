package ruiseki.omoshiroikamo.module.cable.common.network.item.output;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemNet;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;

public class ItemOutputBus extends AbstractPart implements IItemPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private int transferLimit = 1000;

    @Override
    public String getId() {
        return "item_output_bus";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Collections.singletonList(IItemNet.class);
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void doUpdate() {
        if (!shouldDoTickInterval()) return;

        ItemNetwork network = getItemNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        ItemTransfer transfer = new ItemTransfer();
        transfer.setMaxItemsPerTransfer(getTransferLimit());

        for (IItemNet iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;

            transfer.pull(this.getTargetTE(), iFace.getSide(), iFace.getTargetTE());
            transfer.transfer();
        }

    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ITEM_OUTPUT_BUS.newItemStack();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("transferLimit", transferLimit);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        transferLimit = tag.getInteger("transferLimit");
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.syncValue("tickSyncer", SyncHandlers.intNumber(this::getTickInterval, this::setTickInterval));
        syncManager.syncValue("prioritySyncer", SyncHandlers.intNumber(this::getPriority, this::setPriority));
        syncManager.syncValue("channelSyncer", SyncHandlers.intNumber(this::getChannel, this::setChannel));
        syncManager.syncValue("transferSyncer", SyncHandlers.intNumber(this::getTransferLimit, this::setTransferLimit));

        ModularPanel panel = new ModularPanel("item_output_bus");

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
                .setFormatAsInteger(true)
                .setNumbers(1, Integer.MAX_VALUE)
                .right(0));

        Row priorityRow = new Row();
        priorityRow.height(20);
        priorityRow.child(
            IKey.lang("gui.cable.priority")
                .asWidget());
        priorityRow.child(
            new TextFieldWidget().syncHandler("prioritySyncer")
                .setFormatAsInteger(true)
                .setNumbers(0, Integer.MAX_VALUE)
                .right(0));

        Row channelRow = new Row();
        channelRow.height(20);
        channelRow.child(
            IKey.lang("gui.cable.channel")
                .asWidget());
        channelRow.child(
            new TextFieldWidget().syncHandler("channelSyncer")
                .setFormatAsInteger(true)
                .setNumbers(0, Integer.MAX_VALUE)
                .right(0));

        Row transferRow = new Row();
        transferRow.height(20);
        transferRow.child(
            IKey.lang("gui.cable.transfer")
                .asWidget());
        transferRow.child(
            new TextFieldWidget().syncHandler("transferSyncer")
                .setFormatAsInteger(true)
                .setNumbers(0, Integer.MAX_VALUE)
                .right(0));

        Column col = new Column();
        col.padding(7)
            .child(sideRow)
            .child(tickRow)
            .child(priorityRow)
            .child(channelRow)
            .child(transferRow);
        panel.child(col);

        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.OUTPUT;
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
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/item_output_bus.png");
    }

    @Override
    public int getTransferLimit() {
        return this.transferLimit;
    }

    public void setTransferLimit(int transferLimit) {
        this.transferLimit = transferLimit;
    }
}
