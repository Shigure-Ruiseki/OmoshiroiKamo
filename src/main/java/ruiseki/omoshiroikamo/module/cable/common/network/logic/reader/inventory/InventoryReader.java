package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.inventory;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

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
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.AbstractReaderPart;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKey;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKeys;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class InventoryReader extends AbstractReaderPart implements IInventoryPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "cable/reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/inventory_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/inventory_reader_back.png");

    protected int selectedSlot = 0;
    private final ItemStackHandlerBase inv = new ItemStackHandlerBase(10);

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
    public void onDetached() {
        super.onDetached();
        inv.dropAll(getCable().getWorld(), getPos().x, getPos().y, getPos().z);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("item_inv", this.inv.serializeNBT());
        tag.setInteger("SelectedSlot", selectedSlot);
        tag.setTag("ClientCache", clientCache);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inv.deserializeNBT(tag.getCompoundTag("item_inv"));
        if (tag.hasKey("SelectedSlot")) {
            selectedSlot = tag.getInteger("SelectedSlot");
        }
        if (tag.hasKey("ClientCache")) {
            clientCache = tag.getCompoundTag("ClientCache");
        }
    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.INVENTORY_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("block_reader");

        panel.height(200);

        // Settings panel
        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));

        // Search
        StringValue searchValue = new StringValue("");

        panel.child(
            new TextFieldWidget().value(searchValue)
                .pos(7, 7)
                .width(162)
                .height(10)
                .background(VANILLA_SEARCH_BACKGROUND));

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.pos(7, 20)
            .width(162)
            .maxSize(85);

        panel.child(list);

        // Rows
        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.isInventory")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.isInventory",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("isInv"))),
                0,
                LogicKeys.IS_INVENTORY,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.inventoryFull")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.inventoryFull",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("full"))),
                3,
                LogicKeys.INV_FULL,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.inventoryEmpty")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.inventoryEmpty",
                IKey.dynamic(() -> String.valueOf(!clientCache.getBoolean("full"))),
                1,
                LogicKeys.INV_EMPTY,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.inventoryNotEmpty")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.inventoryNotEmpty",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("count") > 0)),
                2,
                LogicKeys.INV_NOT_EMPTY,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.inventoryCount")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.inventoryCount",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("count"))),
                4,
                LogicKeys.INV_COUNT,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.inventorySlots")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.inventorySlots",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("slots"))),
                5,
                LogicKeys.INV_SLOTS,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.slotsFilled")
                .get(),
            infoRow(
                "gui.cable.inventoryReader.slotsFilled",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("filled"))),
                6,
                LogicKeys.INV_SLOTS_FILLED,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.fillRatio")
                .get(),
            infoRow("gui.cable.inventoryReader.fillRatio", IKey.dynamic(() -> {
                int slots = clientCache.getInteger("slots");
                int filled = clientCache.getInteger("filled");
                return slots == 0 ? "0.00" : String.format("%.2f", (double) filled / slots);
            }), 7, LogicKeys.INV_FILL_RATIO, inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.slotItem")
                .get(),
            infoRow("gui.cable.inventoryReader.slotItem", IKey.dynamic(() -> {
                if (!clientCache.hasKey("slotItem")) return "Empty";
                ItemStack stack = ItemStack.loadItemStackFromNBT(clientCache.getCompoundTag("slotItem"));
                return stack == null ? "Empty" : stack.getDisplayName();
            }), 8, LogicKeys.SLOT_ITEM, inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.inventoryReader.itemsList")
                .get(),
            infoRow("gui.cable.inventoryReader.itemsList", IKey.str(""), 9, LogicKeys.ITEMS_LIST, inv),
            searchValue);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

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
            if (world().getBlock(readX(), readY(), readZ()) instanceof BlockChest blockChest) {
                IInventory inv = blockChest.func_149951_m(world(), readX(), readY(), readZ());
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
