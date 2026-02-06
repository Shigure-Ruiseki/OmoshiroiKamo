package ruiseki.omoshiroikamo.module.ids.common.network.logic.part.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
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
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.AbstractReaderPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.LogicValues;

public class InventoryReader extends AbstractReaderPart implements IInventoryPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/inventory_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/inventory_reader_back.png");

    protected int selectedSlot = 0;

    public InventoryReader() {
        super(new ItemStackHandlerBase(10));
    }

    @Override
    public String getId() {
        return "inventory_reader";
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

        IInventory inv = getInventory();

        if (inv == null) {
            clientCache = new NBTTagCompound();
            clientCache.setBoolean("isInv", false);
            return;
        }

        clientCache.setBoolean("isInv", true);

        int slots = inv.getSizeInventory();
        int filled = countFilledSlots(inv);
        int count = countItems(inv);

        clientCache.setInteger("count", count);
        clientCache.setInteger("slots", slots);
        clientCache.setInteger("filled", filled);
        clientCache.setBoolean("full", isFull(inv));

        if (selectedSlot >= 0 && selectedSlot < slots) {
            ItemStack stack = inv.getStackInSlot(selectedSlot);
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                stack.writeToNBT(tag);
                clientCache.setTag("slotItem", tag);
            } else {
                clientCache.removeTag("slotItem");
            }
        } else {
            clientCache.removeTag("slotItem");
        }

        NBTTagCompound itemsTag = new NBTTagCompound();
        int idx = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound t = new NBTTagCompound();
                stack.writeToNBT(t);
                itemsTag.setTag("item_" + idx++, t);
            }
        }

        itemsTag.setInteger("size", idx);
        clientCache.setTag("items", itemsTag);

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("SelectedSlot", selectedSlot);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        selectedSlot = tag.getInteger("SelectedSlot");
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.INVENTORY_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("block_reader");
        panel.height(196);

        // Settings panel
        IPanelHandler settingPanel = syncManager
            .syncedPanel("part_panel", true, (sm, sh) -> PartSettingPanel.build(this));
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));

        // Search
        StringValue searchValue = new StringValue("");

        Column col = new Column();
        TextFieldWidget searchWidget = new TextFieldWidget().value(searchValue)
            .width(162)
            .height(10)
            .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND);

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.width(162)
            .maxSize(85);

        // Rows
        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.isInventory")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.isInventory")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("isInv"))),
                0,
                LogicKeys.IS_INVENTORY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.inventoryFull")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.inventoryFull")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("full"))),
                3,
                LogicKeys.INV_FULL),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.inventoryEmpty")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.inventoryEmpty")
                    .get(),
                IKey.dynamic(() -> String.valueOf(!clientCache.getBoolean("full"))),
                1,
                LogicKeys.INV_EMPTY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.inventoryNotEmpty")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.inventoryNotEmpty")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("count") > 0)),
                2,
                LogicKeys.INV_NOT_EMPTY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.inventoryCount")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.inventoryCount")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("count"))),
                4,
                LogicKeys.INV_COUNT),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.inventorySlots")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.inventorySlots")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("slots"))),
                5,
                LogicKeys.INV_SLOTS),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.slotsFilled")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.slotsFilled")
                    .get(),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("filled"))),
                6,
                LogicKeys.INV_SLOTS_FILLED),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.fillRatio")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.fillRatio")
                    .get(),
                IKey.dynamic(() -> {
                    int slots = clientCache.getInteger("slots");
                    int filled = clientCache.getInteger("filled");
                    return slots == 0 ? "0.00" : String.format("%.2f", (double) filled / slots);
                }),
                7,
                LogicKeys.INV_FILL_RATIO),
            searchValue);

        IPanelHandler slotItemSetting = syncManager.syncedPanel("slotItemSetting", true, this::slotItemSetting);
        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.slotItem")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.slotItem")
                    .get(),
                IKey.dynamic(() -> {
                    if (!clientCache.hasKey("slotItem")) return IKey.lang("gui.empty")
                        .get();
                    ItemStack stack = ItemStack.loadItemStackFromNBT(clientCache.getCompoundTag("slotItem"));
                    return stack == null ? IKey.lang("gui.empty")
                        .get() : stack.getDisplayName();
                }),
                8,
                LogicKeys.SLOT_ITEM,
                slotItemSetting),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.inventoryReader.itemsList")
                .get(),
            infoRow(
                IKey.lang("gui.ids.inventoryReader.itemsList")
                    .get(),
                IKey.dynamic(this::buildItemsListText),
                9,
                LogicKeys.ITEMS_LIST),
            searchValue);

        col.coverChildren()
            .pos(7, 7)
            .childPadding(4)
            .child(searchWidget)
            .child(list);

        panel.child(col);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
    }

    private String buildItemsListText() {
        if (!clientCache.hasKey("items")) return IKey.lang("gui.empty")
            .get();

        NBTTagCompound items = clientCache.getCompoundTag("items");
        int size = items.getInteger("size");

        if (size <= 0) return IKey.lang("gui.empty")
            .get();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            ItemStack s = ItemStack.loadItemStackFromNBT(items.getCompoundTag("item_" + i));
            if (s == null) continue;

            if (sb.length() > 0) sb.append(", ");
            sb.append(s.getDisplayName())
                .append(" x")
                .append(s.stackSize);

            if (sb.length() > 256) {
                sb.append("...");
                break;
            }
        }

        if (sb.length() == 0) {
            return IKey.lang("gui.empty")
                .get();
        }

        return ellipsis(sb.toString(), 110);
    }

    private ModularPanel slotItemSetting(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        ModularPanel panel = new Dialog<>("logic_setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Column col = new Column();

        Row selectSlot = new Row();
        selectSlot.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.id")).width(162))
            .child(
                new TextFieldWidget().value(new IntSyncValue(this::getSelectedSlot, this::setSelectedSlot))
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(0)
                    .setFormatAsInteger(true));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(selectSlot);
        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
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

    @Override
    public ILogicValue read(LogicKey key) {

        boolean isInv = clientCache.getBoolean("isInv");

        if (key == LogicKeys.IS_INVENTORY) return LogicValues.of(isInv);

        if (!isInv) return LogicValues.NULL;

        if (key == LogicKeys.INV_EMPTY) return LogicValues.of(clientCache.getInteger("count") == 0);

        if (key == LogicKeys.INV_NOT_EMPTY) return LogicValues.of(clientCache.getInteger("count") > 0);

        if (key == LogicKeys.INV_FULL) return LogicValues.of(clientCache.getBoolean("full"));

        if (key == LogicKeys.INV_COUNT) return LogicValues.of(clientCache.getInteger("count"));

        if (key == LogicKeys.INV_SLOTS) return LogicValues.of(clientCache.getInteger("slots"));

        if (key == LogicKeys.INV_SLOTS_FILLED) return LogicValues.of(clientCache.getInteger("filled"));

        if (key == LogicKeys.INV_FILL_RATIO) {
            int slots = clientCache.getInteger("slots");
            int filled = clientCache.getInteger("filled");
            return LogicValues.of(slots == 0 ? 0D : (double) filled / slots);
        }

        if (key == LogicKeys.SLOT_ITEM) {
            if (!clientCache.hasKey("slotItem")) return LogicValues.NULL;

            ItemStack stack = ItemStack.loadItemStackFromNBT(clientCache.getCompoundTag("slotItem"));
            return LogicValues.of(stack);
        }

        if (key == LogicKeys.ITEMS_LIST) {
            List<ILogicValue> list = new ArrayList<>();
            NBTTagCompound items = clientCache.getCompoundTag("items");
            int size = items.getInteger("size");

            for (int i = 0; i < size; i++) {
                ItemStack s = ItemStack.loadItemStackFromNBT(items.getCompoundTag("item_" + i));
                if (s != null) list.add(LogicValues.of(s));
            }

            return LogicValues.of(list);
        }

        return LogicValues.NULL;
    }

    public void setSelectedSlot(int id) {
        this.selectedSlot = id;
        markDirty();
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    private IInventory getInventory() {
        TileEntity te = getTargetTE();

        if (te instanceof TileEntityChest) {
            if (getWorld().getBlock(targetX(), targetY(), targetZ()) instanceof BlockChest blockChest) {
                IInventory inv = blockChest.func_149951_m(getWorld(), targetX(), targetY(), targetZ());
                if (inv != null) {
                    return inv;
                }
                return null;
            }
        }

        if (te instanceof IInventory) {
            return (IInventory) te;
        }

        return null;
    }

    private int countItems(IInventory inv) {
        int count = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) count += stack.stackSize;
        }
        return count;
    }

    private int countFilledSlots(IInventory inv) {
        int filled = 0;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            if (inv.getStackInSlot(i) != null) filled++;
        }
        return filled;
    }

    private boolean isFull(IInventory inv) {
        if (inv == null) return false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack s = inv.getStackInSlot(i);
            if (s == null || s.stackSize < s.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
}
