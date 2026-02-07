package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.interfacebus;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.AbstractWriterPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemNet;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemPart;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.IItemQueryable;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.ItemIndex;
import ruiseki.omoshiroikamo.module.ids.common.network.tunnel.item.ItemNetwork;

public class ItemFilterInterface extends AbstractWriterPart implements IItemPart, IItemQueryable, IItemInterface {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/base_bus.obj"));
    private static final ResourceLocation active = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/part/item_filter_interface_active.png");
    private static final ResourceLocation inactive = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/part/item_filter_interface_inactive.png");

    private int lastHash = 0;

    private boolean allowInsertions = true;
    private boolean allowExtractions = true;
    private boolean blackList = true;
    private int transferLimit = 64;
    private boolean nbt = false;
    private boolean stackSize = false;

    private ItemStackPredicate runtimeFilter;

    public ItemFilterInterface() {
        super(new ItemStackHandlerBase(3));
        setTickInterval(100);
    }

    @Override
    public String getId() {
        return "item_filter_interface";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IItemNet.class, ILogicNet.class);
    }

    @Override
    public void doUpdate() {
        if (!shouldTickNow()) return;

        int hash = calcInventoryHash();
        if (hash != lastHash) {
            lastHash = hash;
            markNetworkDirty();
        }

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

        runtimeFilter = buildFilter(Mode.fromSlot(activeSlot), value);
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.ITEM_FILTER_INTERFACE.newItemStack();
    }

    private int calcInventoryHash() {
        IInventory inv = getInventory();
        if (inv == null) return 0;

        int hash = 1;
        int[] slots = getAccessibleSlots(inv);

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                hash = 31 * hash + ItemStackKeyPool.get(s).hash;
                hash = 31 * hash + s.stackSize;
            }
        }
        return hash;
    }

    private void markNetworkDirty() {
        ItemNetwork net = getItemNetwork();
        if (net != null) net.markDirty(getChannel());
    }

    @Override
    public void collectItems(ItemIndex index) {
        IInventory inv = getInventory();
        if (inv == null) return;

        int[] slots = getAccessibleSlots(inv);

        for (int slot : slots) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s != null && s.stackSize > 0) {
                index.add(s);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean("allowInsertions", allowInsertions);
        tag.setBoolean("allowExtractions", allowExtractions);
        tag.setBoolean("blackList", blackList);
        tag.setInteger("transferLimit", transferLimit);
        tag.setBoolean("nbt", nbt);
        tag.setBoolean("stackSize", stackSize);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        allowInsertions = tag.getBoolean("allowInsertions");
        allowExtractions = tag.getBoolean("allowExtractions");
        blackList = tag.getBoolean("blackList");
        transferLimit = tag.getInteger("transferLimit");
        nbt = tag.getBoolean("nbt");
        stackSize = tag.getBoolean("stackSize");
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("item_filer_interface");
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
            IKey.lang("gui.ids.itemFilterInterface.all")
                .get(),
            writerSlotRow(
                0,
                IKey.lang("gui.ids.itemFilterInterface.all")
                    .get(),
                allSetting),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.itemFilterInterface.item")
                .get(),
            writerSlotRow(
                1,
                IKey.lang("gui.ids.itemFilterInterface.item")
                    .get(),
                allSetting),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.itemFilterInterface.items")
                .get(),
            writerSlotRow(
                2,
                IKey.lang("gui.ids.itemFilterInterface.items")
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
        ModularPanel panel = new Dialog<>("pulse_setting").setDraggable(false)
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

        Row blackList = new Row();
        blackList.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.blackList")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isBlackList, this::setBlackList)));

        Row transferLimit = new Row();
        transferLimit.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.transferLimit")).width(162))
            .child(
                new TextFieldWidget().value(new IntSyncValue(this::getTransferLimit, this::setTransferLimit))
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(1)
                    .setFormatAsInteger(true));

        Row nbt = new Row();
        nbt.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.nbt")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isNbt, this::setNbt)));

        Row stackSize = new Row();
        stackSize.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.ids.stackSize")).width(162))
            .child(
                new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                    .right(0)
                    .size(12)
                    .value(new BooleanSyncValue(this::isStackSize, this::setStackSize)));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(allowInsertions)
            .child(allowExtractions)
            .child(blackList)
            .child(transferLimit)
            .child(nbt)
            .child(stackSize);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.BOTH;
    }

    public int getTransferLimit() {
        return transferLimit;
    }

    public void setTransferLimit(int limit) {
        transferLimit = Math.max(1, limit);
        markDirty();
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
    public ItemStack extract(ItemStack required, int amount) {
        if (amount <= 0) return null;

        int limit = Math.min(amount, transferLimit);

        IInventory inv = getInventory();
        if (inv == null) return null;

        ItemStack result = null;
        int remaining = limit;

        for (int slot : getAccessibleSlots(inv)) {
            if (remaining <= 0) break;

            ItemStack s = inv.getStackInSlot(slot);
            if (s == null || s.stackSize <= 0) continue;
            if (!allowExtractions) return null;
            if (runtimeFilter != null && !runtimeFilter.test(s)) continue;
            if (!ItemUtils.areStacksEqual(required, s)) continue;

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
        if (stack == null || stack.stackSize <= 0) return stack;
        if (!allowInsertions) return stack;
        if (runtimeFilter != null && !runtimeFilter.test(stack)) return stack;

        IInventory inv = getInventory();
        if (inv == null) return stack;

        int limit = Math.min(stack.stackSize, transferLimit);

        ItemStack remaining = stack.copy();
        remaining.stackSize = limit;

        int inserted = 0;
        boolean changed = false;

        for (int slot : getAccessibleSlots(inv)) {
            if (remaining.stackSize <= 0) break;

            ItemStack target = inv.getStackInSlot(slot);

            if (target == null) {
                int add = Math.min(remaining.getMaxStackSize(), remaining.stackSize);
                ItemStack placed = remaining.splitStack(add);
                inv.setInventorySlotContents(slot, placed);
                inserted += add;
                changed = true;
            } else if (ItemUtils.areStacksEqual(target, remaining)) {
                int space = target.getMaxStackSize() - target.stackSize;
                if (space > 0) {
                    int add = Math.min(space, remaining.stackSize);
                    target.stackSize += add;
                    remaining.stackSize -= add;
                    inserted += add;
                    changed = true;
                }
            }
        }

        if (changed) {
            inv.markDirty();
        }

        int leftover = stack.stackSize - inserted;
        if (leftover > 0) {
            ItemStack ret = stack.copy();
            ret.stackSize = leftover;
            return ret;
        }

        return null;
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

    @Override
    public IInventory getInventory() {
        TileEntity te = getTargetTE();

        if (te instanceof TileEntityChest) {
            if (getWorld().getBlock(targetX(), targetY(), targetZ()) instanceof BlockChest chest) {
                return chest.func_149951_m(getWorld(), targetX(), targetY(), targetZ());
            }
            return null;
        }

        return te instanceof IInventory ? (IInventory) te : null;
    }

    @Override
    public int[] getSlots() {
        IInventory inv = getInventory();
        if (inv == null) return new int[0];

        if (inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(getSide().ordinal());
        }

        int size = inv.getSizeInventory();
        int[] slots = new int[size];
        for (int i = 0; i < size; i++) slots[i] = i;
        return slots;
    }

    private enum Mode {

        FILTER_ALL,
        FILTER_ITEM,
        FILTER_ITEMS;

        static Mode fromSlot(int slot) {
            return values()[Math.min(slot, values().length - 1)];
        }
    }

    @Override
    public void resetAll() {
        super.resetAll();
        setTransferLimit(64);
        setNbt(false);
        setStackSize(false);
        setAllowExtractions(true);
        setAllowInsertions(true);
        setBlackList(false);
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
        clientCache.setString("item", value.asString());
        clientCache.setString("items", value.asString());
    }

    public String getPreviewText() {
        if (!clientCache.getBoolean("hasValue")) return "";
        int slot = clientCache.getInteger("activeSlot");
        Mode mode = Mode.fromSlot(slot);

        return switch (mode) {
            case FILTER_ALL -> clientCache.getBoolean("all") ? "TRUE" : "FALSE";
            case FILTER_ITEM -> String.valueOf(clientCache.getString("item"));
            case FILTER_ITEMS -> String.valueOf(clientCache.getString("items"));
        };
    }

    private boolean matches(ItemStack filter, ItemStack target) {
        if (filter == null || target == null) return false;

        if (!ItemUtils.areStacksEqual(filter, target, !nbt)) {
            return false;
        }

        return !stackSize || target.stackSize >= filter.stackSize;
    }

    private ItemStackPredicate buildFilter(Mode mode, ILogicValue value) {
        ItemStackPredicate base = switch (mode) {
            case FILTER_ALL -> stack -> value.asBoolean();

            case FILTER_ITEM -> {
                ItemStack filter = value.asItemStack();
                yield stack -> matches(filter, stack.toStack());
            }

            case FILTER_ITEMS -> {
                List<ILogicValue> filters = value.asList();
                yield stack -> {
                    for (ILogicValue filter : filters) {
                        if (matches(filter.asItemStack(), stack.toStack())) return true;
                    }
                    return false;
                };
            }
        };

        if (blackList) {
            return stack -> !base.test(stack);
        }

        return base;
    }

}
