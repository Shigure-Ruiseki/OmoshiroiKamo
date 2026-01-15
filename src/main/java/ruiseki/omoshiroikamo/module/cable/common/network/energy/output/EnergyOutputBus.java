package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import net.minecraft.item.ItemStack;
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
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class EnergyOutputBus extends AbstractPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public String getId() {
        return "energy_output_bus";
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IEnergyPart.class;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void doUpdate() {
        tickCounter++;
        if (tickCounter < tickInterval) return;
        tickCounter = 0;

        EnergyNetwork network = (EnergyNetwork) getNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        EnergyTransfer transfer = new EnergyTransfer();
        transfer.setMaxEnergyPerTransfer(getTransferLimit());

        for (IEnergyPart iFace : network.interfaces) {
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
        return CableItems.ENERGY_OUTPUT_BUS.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.syncValue("tickSyncer", SyncHandlers.intNumber(this::getTickInterval, this::setTickInterval));
        syncManager.syncValue("prioritySyncer", SyncHandlers.intNumber(this::getPriority, this::setPriority));
        syncManager.syncValue("channelSyncer", SyncHandlers.intNumber(this::getChannel, this::setChannel));

        ModularPanel panel = new ModularPanel("energy_output_bus");

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
        return EnumIO.OUTPUT;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (side) {
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
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/energy_output_bus.png");
    }

    @Override
    public int getTransferLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        EnergyNetwork network = (EnergyNetwork) getNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return 0;

        return 0;
    }

}
