package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.output;

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
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
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
import com.gtnewhorizon.gtnhlib.item.ItemStackPredicate;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.AbstractWriterPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemNet;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemPart;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus.IItemInterface;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus.InterfaceItemSource;

public class ItemExporter extends AbstractWriterPart implements IItemPart {

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

    private int transferLimit = 64;
    private int slot = -1;
    private boolean roundRobin = false;
    private boolean blackList = false;
    private boolean nbt = false;
    private boolean stackSize = false;
    private int rrCursor = 0;

    public ItemExporter() {
        super(new ItemStackHandlerBase(5));
    }

    @Override
    public String getId() {
        return "item_exporter";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IItemNet.class, ILogicNet.class);
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
        executeExport(value, Mode.fromSlot(activeSlot));
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.ITEM_EXPORTER.newItemStack();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("transferLimit", transferLimit);
        tag.setInteger("slot", slot);
        tag.setBoolean("roundRobin", roundRobin);
        tag.setBoolean("blackList", blackList);
        tag.setBoolean("nbt", nbt);
        tag.setBoolean("stackSize", stackSize);
        tag.setInteger("rrCursor", rrCursor);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        transferLimit = tag.getInteger("transferLimit");
        slot = tag.getInteger("slot");
        roundRobin = tag.getBoolean("roundRobin");
        blackList = tag.getBoolean("blackList");
        nbt = tag.getBoolean("nbt");
        stackSize = tag.getBoolean("stackSize");
        rrCursor = tag.getInteger("rrCursor");
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("item_exporter");
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
            .maxSize(72);

        IPanelHandler allSetting = syncManager.syncedPanel("allSetting", true, this::allSettingPanel);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.itemExporter.allItem"),
            writerSlotRow(0, LibMisc.LANG.localize("gui.ids.itemExporter.allItem"), allSetting),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.itemExporter.itemAmount"),
            writerSlotRow(1, LibMisc.LANG.localize("gui.ids.itemExporter.itemAmount"), allSetting),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.itemExporter.itemSlot"),
            writerSlotRow(2, LibMisc.LANG.localize("gui.ids.itemExporter.itemSlot"), allSetting),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.itemExporter.item"),
            writerSlotRow(3, LibMisc.LANG.localize("gui.ids.itemExporter.item"), allSetting),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.itemExporter.items"),
            writerSlotRow(4, LibMisc.LANG.localize("gui.ids.itemExporter.items"), allSetting),
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
        ModularPanel panel = new Dialog<>("pulse_setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Column col = new Column();

        Row slot = new Row();
        slot.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.id")).width(162))
            .child(
                new TextFieldWidget().value(new IntSyncValue(this::getSlot, this::setSlot))
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(-1)
                    .setFormatAsInteger(true));

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

        Row roundRobin = new Row();
        roundRobin.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.roundRobin")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isRoundRobin, this::setRoundRobin)));

        Row blackList = new Row();
        blackList.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.blackList")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isBlackList, this::setBlackList)));

        Row nbt = new Row();
        nbt.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.nbt")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isNbt, this::setNbt)));

        Row stackSize = new Row();
        stackSize.coverChildren()
            .child(new TextWidget<>(LibMisc.LANG.localize("gui.ids.stackSize")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isStackSize, this::setStackSize)));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(slot)
            .child(transferLimit)
            .child(roundRobin)
            .child(nbt)
            .child(stackSize);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.OUTPUT;
    }

    public int getTransferLimit() {
        return transferLimit;
    }

    public void setTransferLimit(int limit) {
        transferLimit = Math.max(1, limit);
        markDirty();
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        markDirty();
    }

    public boolean isRoundRobin() {
        return roundRobin;
    }

    public void setRoundRobin(boolean roundRobin) {
        this.roundRobin = roundRobin;
        markDirty();
    }

    public boolean isBlackList() {
        return blackList;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
        markDirty();
    }

    public boolean isNbt() {
        return nbt;
    }

    public void setNbt(boolean nbt) {
        this.nbt = nbt;
        markDirty();
    }

    public boolean isStackSize() {
        return stackSize;
    }

    public void setStackSize(boolean stackSize) {
        this.stackSize = stackSize;
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
        active = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/item_exporter_active.png");
        inactive = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/item_exporter_inactive.png");
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

        EXPORT_ALL_BOOL,
        EXPORT_AMOUNT_INT,
        EXPORT_SLOT_INT,
        EXPORT_ITEM,
        EXPORT_ITEMS;

        static Mode fromSlot(int slot) {
            return values()[Math.min(slot, values().length - 1)];
        }
    }

    private void executeExport(ILogicValue value, Mode mode) {
        ItemNetwork network = getItemNetwork();
        if (network == null || network.interfaces == null || network.interfaces.isEmpty()) return;

        ItemTransfer transfer = new ItemTransfer();

        List<IItemNet> sources = network.getInterfacesForChannel(getChannel());
        if (sources.isEmpty()) return;

        if (roundRobin) {
            int size = sources.size();
            IItemNet iFace = sources.get(rrCursor % size);

            transfer.source(new InterfaceItemSource((IItemInterface) iFace));

            transfer.sink(ItemUtils.getItemSink(getTargetTE(), side.getOpposite()));

            boolean success = executeTransferByMode(transfer, value, mode);
            if (success) rrCursor = (rrCursor + 1) % size;

        } else {
            for (IItemNet iFace : sources) {

                transfer.source(new InterfaceItemSource((IItemInterface) iFace));

                transfer.sink(ItemUtils.getItemSink(getTargetTE(), side.getOpposite()));

                executeTransferByMode(transfer, value, mode);
            }
        }
    }

    private boolean executeTransferByMode(ItemTransfer transfer, ILogicValue value, Mode mode) {
        switch (mode) {
            case EXPORT_ALL_BOOL -> {
                if (!value.asBoolean()) return false;
                transfer.setMaxItemsPerTransfer(transferLimit);
                return transfer.transfer() > 0;
            }

            case EXPORT_AMOUNT_INT -> {
                int limit = Math.max(0, value.asInt());
                if (limit <= 0) return false;

                setTransferLimit(limit);
                transfer.setMaxItemsPerTransfer(limit);
                if (slot != -1) transfer.setSinkSlots(slot);
                return transfer.transfer() > 0;
            }

            case EXPORT_SLOT_INT -> {
                int s = value.asInt();
                if (s < 0 || transferLimit <= 0) return false;

                transfer.setMaxItemsPerTransfer(transferLimit);
                transfer.setSinkSlots(s);
                return transfer.transfer() > 0;
            }

            case EXPORT_ITEM -> {
                ItemStack stack = value.asItemStack();
                if (stack == null) return false;

                transfer.setFilter(buildPredicate(stack));
                transfer.setMaxItemsPerTransfer(transferLimit);
                if (slot != -1) transfer.setSinkSlots(slot);
                return transfer.transfer() > 0;
            }

            case EXPORT_ITEMS -> {
                List<ILogicValue> list = value.asList();
                if (list == null || list.isEmpty()) return false;

                boolean moved = false;
                for (ILogicValue v : list) {
                    if (v == null) continue;
                    ItemStack stack = v.asItemStack();
                    if (stack == null) continue;

                    transfer.setFilter(buildPredicate(stack));
                    transfer.setMaxItemsPerTransfer(transferLimit);
                    if (slot != -1) transfer.setSinkSlots(slot);

                    moved |= transfer.transfer() > 0;
                }
                return moved;
            }
        }
        return false;
    }

    private ItemStackPredicate buildPredicate(ItemStack stack) {
        ItemStackPredicate match;

        if (nbt) {
            match = ItemStackPredicate.matches(stack);
        } else {
            match = ItemStackPredicate.matches(new ItemStack(stack.getItem(), 1, stack.getItemDamage()));
        }

        ItemStackPredicate itemPredicate = blackList ? s -> !match.test(s) : match;

        if (stackSize) {
            itemPredicate = itemPredicate.withStackSize(transferLimit, Integer.MAX_VALUE);
        }

        return itemPredicate;
    }

    @Override
    public void resetAll() {
        super.resetAll();
        rrCursor = 0;
        setTransferLimit(64);
        setNbt(false);
        setStackSize(false);
        setRoundRobin(false);
        setBlackList(false);
        setSlot(-1);
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
        clientCache.setInteger("amount", value.asInt());
        clientCache.setInteger("slot", value.asInt());
        clientCache.setString("item", value.asString());
        clientCache.setString("items", value.asString());
    }

    public String getPreviewText() {
        if (!clientCache.getBoolean("hasValue")) return "";
        int slot = clientCache.getInteger("activeSlot");
        Mode mode = Mode.fromSlot(slot);

        return switch (mode) {
            case EXPORT_ALL_BOOL -> clientCache.getBoolean("all") ? "TRUE" : "FALSE";
            case EXPORT_AMOUNT_INT -> String.valueOf(clientCache.getInteger("amount"));
            case EXPORT_SLOT_INT -> String.valueOf(clientCache.getInteger("slot"));
            case EXPORT_ITEM -> String.valueOf(clientCache.getString("item"));
            case EXPORT_ITEMS -> String.valueOf(clientCache.getString("items"));
        };
    }

}
