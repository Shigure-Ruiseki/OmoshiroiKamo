package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.interfacebus;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.IEnergyPart;

public class EnergyFilterInterface extends AbstractWriterPart implements IEnergyPart, IEnergyInterface {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/base_bus.obj"));
    private static final ResourceLocation active = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/energy_filter_interface_active.png");
    private static final ResourceLocation inactive = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/energy_filter_interface_inactive.png");

    private boolean allowInsertions = true;
    private boolean allowExtractions = true;
    private boolean lastOutput = true;

    public EnergyFilterInterface() {
        super(new ItemStackHandlerBase(1));
        setTickInterval(20);
    }

    @Override
    public String getId() {
        return "energy_filter_interface";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IEnergyNet.class, ILogicNet.class);
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
        }

        ItemStack card = inv.getStackInSlot(activeSlot);
        if (card == null) return;

        ILogicValue value = evaluateLogic(card);
        if (value == null) return;
        if (value.asBoolean() != lastOutput) {
            setOutput(value.asBoolean());
        }
        updateClientCache(value);
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.ENERGY_FILTER_INTERFACE.newItemStack();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("allowInsertions", allowInsertions);
        tag.setBoolean("allowExtractions", allowExtractions);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        allowInsertions = tag.getBoolean("allowInsertions");
        allowExtractions = tag.getBoolean("allowExtractions");
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("energy_filer_interface");
        panel.height(196);

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
            .height(72)
            .maxSize(72);

        IPanelHandler allSetting = syncManager.panel("allSetting", this::allSettingPanel, true);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.energyFilterInterface.all")
                .get(),
            writerSlotRow(
                0,
                IKey.lang("gui.ids.energyFilterInterface.all")
                    .get(),
                allSetting),
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

    private ModularPanel allSettingPanel(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        ModularPanel panel = new Dialog<>("setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Column col = new Column();

        Row allowInsertions = new Row();
        allowInsertions.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.allowInsertions")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isAllowInsertions, this::setAllowInsertions)));

        Row allowExtractions = new Row();
        allowExtractions.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.allowExtractions")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isAllowExtractions, this::setAllowExtractions)));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(allowInsertions)
            .child(allowExtractions);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    public boolean isAllowInsertions() {
        return allowInsertions;
    }

    public void setAllowInsertions(boolean allowInsertions) {
        this.allowInsertions = allowInsertions;
        markDirty();
    }

    public boolean isAllowExtractions() {
        return allowExtractions;
    }

    public void setAllowExtractions(boolean allowExtractions) {
        this.allowExtractions = allowExtractions;
        markDirty();
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.BOTH;
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
    @SideOnly(Side.CLIENT)
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        RenderUtils.bindTexture(activeSlot != -1 ? active : inactive);

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

        RenderUtils.bindTexture(inactive);
        model.renderAll();

        GL11.glPopMatrix();
    }

    private IEnergyReceiver getEnergyReceiver() {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyReceiver receiver)) return null;
        return receiver;
    }

    private IEnergyProvider getEnergyProvider() {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyProvider provider)) return null;
        return provider;
    }

    private IEnergyConnection getEnergyConnection() {
        TileEntity te = getTargetTE();
        if (!(te instanceof IEnergyConnection connection)) return null;
        return connection;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!allowExtractions) return 0;
        if (!lastOutput) return 0;
        if (getEnergyProvider() == null) return 0;
        return getEnergyProvider().extractEnergy(getSide().getOpposite(), amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!allowInsertions) return 0;
        if (!lastOutput) return 0;
        if (getEnergyReceiver() == null) return 0;
        return getEnergyReceiver().receiveEnergy(getSide().getOpposite(), amount, simulate);
    }

    @Override
    public boolean canConnect() {
        if (getEnergyConnection() == null) return false;
        return getEnergyConnection().canConnectEnergy(getSide().getOpposite());
    }

    private enum Mode {

        FILTER_ALL;

        static Mode fromSlot(int slot) {
            return values()[Math.min(slot, values().length - 1)];
        }
    }

    private void setOutput(boolean value) {
        if (value == lastOutput) return;
        lastOutput = value;
        markDirty();
        notifyNeighbors();
    }

    @Override
    public void resetAll() {
        super.resetAll();
        setAllowExtractions(true);
        setAllowInsertions(true);
        setOutput(false);
        clientCache.setBoolean("hasValue", false);
    }

    private void updateClientCache(ILogicValue value) {
        clientCache.setInteger("activeSlot", activeSlot);
        if (value == null) {
            clientCache.setBoolean("hasValue", false);
            return;
        }
        clientCache.setBoolean("hasValue", true);
        clientCache.setBoolean("all", value.asBoolean());
    }

    public String getPreviewText() {
        if (!clientCache.getBoolean("hasValue")) return "";
        int slot = clientCache.getInteger("activeSlot");
        Mode mode = Mode.fromSlot(slot);

        return switch (mode) {
            case FILTER_ALL -> clientCache.getBoolean("all") ? "TRUE" : "FALSE";
        };
    }

}
