package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.input;

import java.util.Arrays;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.core.energy.EnergyUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.item.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.item.part.AbstractWriterPart;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.IEnergyPart;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus.IEnergyInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.interfacebus.InterfaceEnergySink;

public class EnergyImporter extends AbstractWriterPart implements IEnergyPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @SideOnly(Side.CLIENT)
    private static IModelCustom model;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation active;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation inactive;

    private int transferLimit = 10000;

    public EnergyImporter() {
        super(new ItemStackHandlerBase(2));
    }

    @Override
    public String getId() {
        return "energy_importer";
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
        updateClientCache(value);
        executeImport(value, Mode.fromSlot(activeSlot));
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.ENERGY_IMPORTER.newItemStack();
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
        ModularPanel panel = new ModularPanel("energy_importer");
        panel.height(196);

        IPanelHandler settingPanel = syncManager
            .syncedPanel("part_panel", true, (sm, sh) -> PartSettingPanel.build(this));
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

        IPanelHandler allSetting = syncManager.syncedPanel("allSetting", true, this::allSettingPanel);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.energyImporter.boolean"),
            writerSlotRow(0, LibMisc.LANG.localize("gui.ids.energyImporter.boolean"), allSetting),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.energyImporter.amount"),
            writerSlotRow(1, LibMisc.LANG.localize("gui.ids.energyImporter.amount"), allSetting),
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

        Row transferLimit = new Row();
        transferLimit.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.transferLimit")).width(162))
            .child(
                new TextFieldWidget().value(new IntSyncValue(this::getTransferLimit, this::setTransferLimit))
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(1)
                    .setFormatAsInteger(true));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(transferLimit);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.INPUT;
    }

    public int getTransferLimit() {
        return this.transferLimit;
    }

    public void setTransferLimit(int transferLimit) {
        this.transferLimit = transferLimit;
        markDirty();
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
    @SideOnly(Side.CLIENT)
    public void registerModel() {

        model = AdvancedModelLoader.loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/base_bus.obj"));
        active = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/energy_importer_active.png");
        inactive = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/energy_importer_inactive.png");
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
    @SideOnly(Side.CLIENT)
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

    private enum Mode {

        IMPORT_ENERGY_BOOL,
        IMPORT_ENERGY_AMOUNT_INT;

        static Mode fromSlot(int slot) {
            return values()[Math.min(slot, values().length - 1)];
        }
    }

    private void executeImport(ILogicValue value, Mode mode) {
        EnergyNetwork network = getEnergyNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        int limit;

        switch (mode) {
            case IMPORT_ENERGY_BOOL -> {
                if (!value.asBoolean()) return;
                limit = getTransferLimit();
            }
            case IMPORT_ENERGY_AMOUNT_INT -> {
                limit = value.asInt();
                if (limit <= 0) return;
            }
            default -> {
                return;
            }
        }
        setTransferLimit(limit);

        EnergyTransfer transfer = new EnergyTransfer();
        transfer.setMaxEnergyPerTransfer(getTransferLimit());

        for (IEnergyNet iFace : network.interfaces) {
            if (iFace.getChannel() != this.getChannel()) continue;

            transfer.source(EnergyUtils.getEnergySource(getTargetTE(), side.getOpposite()));
            transfer.sink(new InterfaceEnergySink((IEnergyInterface) iFace));

            transfer.transfer();
        }
    }

    @Override
    public void resetAll() {
        super.resetAll();
        setTransferLimit(10000);
    }

    private void updateClientCache(ILogicValue value) {
        clientCache.setInteger("activeSlot", activeSlot);
        if (value == null) {
            clientCache.setBoolean("hasValue", false);
            return;
        }

        clientCache.setBoolean("hasValue", true);
        clientCache.setBoolean("boolean", value.asBoolean());
        clientCache.setInteger("amount", value.asInt());
    }

    public String getPreviewText() {
        if (!clientCache.getBoolean("hasValue")) return "";
        int slot = clientCache.getInteger("activeSlot");
        Mode mode = Mode.fromSlot(slot);

        return switch (mode) {
            case IMPORT_ENERGY_BOOL -> clientCache.getBoolean("boolean") ? "TRUE" : "FALSE";
            case IMPORT_ENERGY_AMOUNT_INT -> String.valueOf(clientCache.getInteger("amount"));
        };
    }
}
