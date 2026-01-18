package ruiseki.omoshiroikamo.module.cable.common.network.energy.output;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.EnergyUtils;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyPart;

public class EnergyOutputBus extends AbstractPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private int transferLimit = 1000;

    @Override
    public String getId() {
        return "energy_output_bus";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Collections.singletonList(IEnergyNet.class);
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

        EnergyNetwork network = getEnergyNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        EnergyTransfer transfer = new EnergyTransfer();
        transfer.setMaxEnergyPerTransfer(getTransferLimit());

        for (IEnergyNet iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;

            transfer.sink(EnergyUtils.getEnergySink(getTargetTE(), side.getOpposite()));
            transfer.source(
                EnergyUtils.getEnergySource(
                    iFace.getTargetTE(),
                    iFace.getSide()
                        .getOpposite()));
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
                .setFormatAsInteger(true)
                .setScrollValues(1, 5, 10)
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
                .setScrollValues(1, 5, 10)
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
                .setScrollValues(1, 5, 10)
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
                .setScrollValues(1, 5, 10)
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
    public int getTransferLimit() {
        return transferLimit;
    }

    public void setTransferLimit(int transferLimit) {
        this.transferLimit = transferLimit;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return 0;
    }

    private static IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "cable/energy_output_bus.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/energy_output_bus.png");

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        RenderUtils.bindTexture(texture);

        rotateForSide(getSide());

        model.renderAll();

        GL11.glPopMatrix();
    }

    @Override
    public void renderItemPart(IItemRenderer.ItemRenderType type, ItemStack stack, Tessellator tess) {
        GL11.glPushMatrix();

        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0f, 0f, -0.5f);
                break;
            case EQUIPPED, EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.25f, 0.5f, 0.25f);
                break;
            case INVENTORY:
                GL11.glTranslatef(0.5f, 0f, 0f);
                break;
            default:
                GL11.glTranslatef(0f, 0f, 0f);
                break;
        }

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderAll();

        GL11.glPopMatrix();
    }
}
