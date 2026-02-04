package ruiseki.omoshiroikamo.module.ids.common.network.logic.part.redstone;

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

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.AbstractWriterPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.ILogicWriterPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;

public class RedstoneWriter extends AbstractWriterPart implements ILogicWriterPart, IRedstoneWriter {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/writer.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/redstone_writer_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/redstone_reader_back.png");

    private int lastOutput = 0;
    private int pulseBooleanLength = 2;
    private int pulseIntLength = 2;

    public RedstoneWriter() {
        super(new ItemStackHandlerBase(4));
    }

    @Override
    public String getId() {
        return "redstone_writer";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Collections.singletonList(ILogicNet.class);
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.INPUT;
    }

    @Override
    public void doUpdate() {
        if (!shouldTickNow()) return;

        int resolved = resolveActiveSlot();

        if (resolved == -1) {
            resetAll();
            return;
        }

        if (activeSlot != resolved) {
            activeSlot = resolved;
            setOutput(0);
        }

        ILogicValue value = getCardValue();
        if (value == null) return;
        int newOutput = computeOutput();
        if (newOutput != lastOutput) {
            setOutput(newOutput);
        }

        updateClientCache(value);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("lastOutput", lastOutput);
        tag.setInteger("pulseBoolLen", pulseBooleanLength);
        tag.setInteger("pulseIntLen", pulseIntLength);

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        lastOutput = tag.getInteger("lastOutput");
        pulseBooleanLength = tag.getInteger("pulseBoolLen");
        pulseIntLength = tag.getInteger("pulseIntLen");
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.REDSTONE_WRITER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("redstone_writer");
        panel.height(196);

        // Settings
        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        // Sync
        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));

        StringValue searchValue = new StringValue("");

        Column col = new Column();
        TextFieldWidget searchWidget = new TextFieldWidget().value(searchValue)
            .width(162)
            .height(10)
            .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND);

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.width(162)
            .maxSize(72);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneWriter.redstoneBoolean")
                .get(),
            writerSlotRow(
                0,
                IKey.lang("gui.ids.redstoneWriter.redstoneBoolean")
                    .get()),
            searchValue);
        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneWriter.redstoneInt")
                .get(),
            writerSlotRow(
                1,
                IKey.lang("gui.ids.redstoneWriter.redstoneInt")
                    .get()),
            searchValue);

        IPanelHandler pulseBooleanSetting = syncManager.panel(
            "pulseBooleanSetting",
            (syncManager1, syncHandler) -> pulseSettingPanel(
                syncManager1,
                syncHandler,
                new IntSyncValue(this::getPulseBooleanLength, this::setPulseBooleanLength)),
            true);
        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneWriter.pulseBoolean")
                .get(),
            writerSlotRow(
                2,
                IKey.lang("gui.ids.redstoneWriter.pulseBoolean")
                    .get(),
                pulseBooleanSetting),
            searchValue);

        IPanelHandler pulseIntSetting = syncManager.panel(
            "pulseIntSetting",
            (syncManager1, syncHandler) -> pulseSettingPanel(
                syncManager1,
                syncHandler,
                new IntSyncValue(this::getPulseIntLength, this::setPulseIntLength)),
            true);
        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneWriter.pulseInt")
                .get(),
            writerSlotRow(
                3,
                IKey.lang("gui.ids.redstoneWriter.pulseInt")
                    .get(),
                pulseIntSetting),
            searchValue);

        TextWidget<?> valueWidget = IKey.dynamic(() -> ellipsis(getPreviewText(), 110))
            .alignment(Alignment.CENTER)
            .color(Color.WHITE.main)
            .shadow(false)
            .asWidget()
            .height(10)
            .width(162)
            .left(7)
            .bottom(90)
            .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND);

        col.coverChildren()
            .pos(7, 7)
            .childPadding(4)
            .child(searchWidget)
            .child(list);

        panel.child(col);
        panel.child(valueWidget);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
    }

    private ModularPanel pulseSettingPanel(PanelSyncManager syncManager, IPanelHandler syncHandler,
        IntSyncValue value) {
        ModularPanel panel = new Dialog<>("pulse_setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Column col = new Column();

        Row selectTank = new Row();
        selectTank.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.length")).width(162))
            .child(
                new TextFieldWidget().value(value)
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(1)
                    .setFormatAsInteger(true));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(selectTank);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    public int getPulseBooleanLength() {
        return pulseBooleanLength;
    }

    public void setPulseBooleanLength(int v) {
        pulseBooleanLength = Math.max(1, v);
        markDirty();
    }

    public int getPulseIntLength() {
        return pulseIntLength;
    }

    public void setPulseIntLength(int v) {
        pulseIntLength = Math.max(1, v);
        markDirty();
    }

    @Override
    public int getRedstoneOutput() {
        return lastOutput;
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
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderAllExcept("back");

        RenderUtils.bindTexture(back_texture);
        model.renderOnly("back");

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
        model.renderAllExcept("back");

        RenderUtils.bindTexture(back_texture);
        model.renderOnly("back");

        GL11.glPopMatrix();
    }

    private void updateClientCache(ILogicValue value) {
        clientCache.setInteger("activeSlot", activeSlot);
        clientCache.setInteger("output", lastOutput);

        if (value == null) {
            clientCache.setBoolean("hasValue", false);
            return;
        }

        clientCache.setBoolean("hasValue", true);
        clientCache.setBoolean("boolValue", value.asBoolean());
        clientCache.setInteger("intValue", value.asInt());
    }

    private boolean isPulseActive(int length) {
        if (length <= 0) return false;

        long t = getWorld().getTotalWorldTime();
        long period = length * 2L;

        return (t % period) < length;
    }

    private enum Mode {

        REDSTONE_BOOL,
        REDSTONE_INT,
        PULSE_BOOL,
        PULSE_INT;

        static Mode fromSlot(int slot) {
            return values()[Math.min(slot, values().length - 1)];
        }
    }

    private int computeOutput() {
        if (activeSlot == -1) return 0;

        ItemStack card = inv.getStackInSlot(activeSlot);
        if (card == null) return 0;

        ILogicValue value = evaluateLogic(card);
        if (value == null) return 0;

        Mode mode = Mode.fromSlot(activeSlot);

        return switch (mode) {

            case REDSTONE_BOOL -> value.asBoolean() ? 15 : 0;

            case REDSTONE_INT -> Math.max(0, Math.min(15, value.asInt()));

            case PULSE_BOOL -> {
                if (value.asBoolean() && isPulseActive(pulseBooleanLength)) {
                    yield 15;
                }
                yield 0;
            }

            case PULSE_INT -> {
                int v = Math.max(0, Math.min(15, value.asInt()));
                if (v > 0 && isPulseActive(pulseIntLength)) {
                    yield v;
                }
                yield 0;
            }
        };
    }

    private void setOutput(int value) {
        value = Math.max(0, Math.min(15, value));

        if (value == lastOutput) return;
        lastOutput = value;
        markDirty();
        notifyNeighbors();
    }

    @Override
    public void resetAll() {
        super.resetAll();
        if (lastOutput != 0) setOutput(0);
        clientCache.setBoolean("hasValue", false);
    }

    public String getPreviewText() {
        if (!clientCache.getBoolean("hasValue")) return "";

        int slot = clientCache.getInteger("activeSlot");
        Mode mode = Mode.fromSlot(slot);

        return switch (mode) {
            case REDSTONE_BOOL, PULSE_BOOL -> clientCache.getBoolean("boolValue") ? "TRUE" : "FALSE";

            case REDSTONE_INT, PULSE_INT -> String.valueOf(clientCache.getInteger("intValue"));
        };
    }
}
