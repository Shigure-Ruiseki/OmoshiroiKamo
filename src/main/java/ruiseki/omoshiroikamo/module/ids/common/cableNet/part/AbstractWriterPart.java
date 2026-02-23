package ruiseki.omoshiroikamo.module.ids.common.cableNet.part;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.LogicNetwork;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.EvalContext;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.ILogicNode;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.LogicEvaluator;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node.LogicNodeFactory;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;

public abstract class AbstractWriterPart extends AbstractPart {

    public final ItemStackHandlerBase inv;
    public NBTTagCompound clientCache = new NBTTagCompound();
    public int activeSlot = -1;

    public static final UITexture INFO_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/ids/part_reader")
        .imageSize(256, 256)
        .xy(8, 17, 162, 36)
        .adaptable(1)
        .name("partInfoBg")
        .build();

    protected AbstractWriterPart(ItemStackHandlerBase inv) {
        this.inv = inv;
    }

    public String getClientCacheNBT() {
        return clientCache.toString();
    }

    public void setClientCacheNBT(String s) {
        try {
            clientCache = (NBTTagCompound) JsonToNBT.func_150315_a(s);
        } catch (Exception e) {
            clientCache = new NBTTagCompound();
        }
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
        tag.setTag("clientCache", clientCache);
        tag.setInteger("activeSlot", activeSlot);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inv.deserializeNBT(tag.getCompoundTag("item_inv"));
        clientCache = tag.getCompoundTag("clientCache");
        activeSlot = tag.getInteger("activeSlot");
    }

    public ILogicValue getCardValue() {
        ItemStack card = inv.getStackInSlot(activeSlot);
        return card != null ? evaluateLogic(card) : null;
    }

    public LogicNetwork getLogicNetwork() {
        return getCable() != null ? (LogicNetwork) getCable().getNetwork(ILogicNet.class) : null;
    }

    public ILogicValue evaluateLogic(ItemStack variableCard) {
        LogicNetwork logicNetwork = getLogicNetwork();
        if (logicNetwork == null || logicNetwork.getNodes()
            .isEmpty()) return null;

        ILogicNode root = LogicNodeFactory.readNodeFromItem(variableCard);
        if (root == null) return null;

        EvalContext ctx = new EvalContext(logicNetwork, null);

        return LogicEvaluator.evaluate(root, ctx);
    }

    public int resolveActiveSlot() {
        int found = -1;

        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) continue;

            if (stack.getItem() == IDsItems.LOGIC_CARD.getItem()) {
                if (found != -1) {
                    return -1;
                }
                found = i;
            }
        }
        return found;
    }

    public void addSearchableRow(ListWidget<Row, ?> list, String searchKey, Row row, StringValue searchValue) {
        row.setEnabledIf(
            w -> searchKey.toLowerCase()
                .contains(
                    searchValue.getStringValue()
                        .toLowerCase()));
        list.child(row);
    }

    public Row writerSlotRow(int slot, String label) {
        return writerSlotRow(slot, label, null);
    }

    public Row writerSlotRow(int slot, String label, IPanelHandler settingPanel) {
        Row row = (Row) new Row().coverChildrenHeight()
            .width(162)
            .height(18)
            .background(INFO_BG);

        // Label
        Row labelRow = new Row();
        labelRow.align(Alignment.CenterLeft)
            .width(130)
            .child(
                IKey.str(label)
                    .asWidget()
                    .padding(4));
        row.child(labelRow);

        if (settingPanel != null) {
            ButtonWidget<?> settingBtn = new ButtonWidget<>().overlay(GuiTextures.ADD)
                .size(8)
                .top(5)
                .right(-7)
                .onMousePressed(btn -> {
                    if (btn == 0) {
                        Interactable.playButtonClickSound();
                        if (settingPanel.isPanelOpen()) {
                            settingPanel.closePanel();
                            return true;
                        }
                        settingPanel.openPanel();
                        return true;
                    }
                    return false;
                });
            labelRow.child(settingBtn);
        }

        // Slot
        row.child(
            new ItemSlot().slot(new ModularSlot(inv, slot))
                .align(Alignment.CenterRight)
                .marginRight(4)
                .background(OKGuiTextures.VARIABLE_SLOT));

        return row;
    }

    public static String ellipsis(String text, int maxPixels) {
        if (text == null || text.isEmpty()) return "";

        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

        if (fr.getStringWidth(text) <= maxPixels) {
            return text;
        }

        String dots = "...";
        int dotsWidth = fr.getStringWidth(dots);

        String trimmed = fr.trimStringToWidth(text, maxPixels - dotsWidth);
        return trimmed + dots;
    }

    public void resetAll() {
        activeSlot = -1;
    }
}
