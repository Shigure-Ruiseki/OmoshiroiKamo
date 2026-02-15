package ruiseki.omoshiroikamo.module.ids.common.item.part;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.part.ILogicReader;

public abstract class AbstractReaderPart extends AbstractPart implements ILogicReader {

    public NBTTagCompound clientCache = new NBTTagCompound();
    public final ItemStackHandlerBase inv;

    public AbstractReaderPart(ItemStackHandlerBase inv) {
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
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("item_inv", this.inv.serializeNBT());
        tag.setTag("clientCache", clientCache);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inv.deserializeNBT(tag.getCompoundTag("item_inv"));
        clientCache = tag.getCompoundTag("clientCache");
    }

    @Override
    public void onDetached() {
        super.onDetached();
        inv.dropAll(getCable().getWorld(), getPos().x, getPos().y, getPos().z);
    }

    public static final UITexture INFO_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/ids/part_reader")
        .imageSize(256, 256)
        .xy(8, 17, 162, 36)
        .adaptable(1)
        .name("partInfoBg")
        .build();

    public void addSearchableRow(ListWidget<Row, ?> list, String searchKey, Row row, StringValue searchValue) {
        row.setEnabledIf(
            w -> searchKey.toLowerCase()
                .contains(
                    searchValue.getStringValue()
                        .toLowerCase()));
        list.child(row);
    }

    public Row infoRow(String label, IKey valueKey, int slot, LogicKey key) {
        return infoRow(label, valueKey, slot, key, null);
    }

    public Row infoRow(String label, IKey valueKey, int slot, LogicKey key, IPanelHandler settingPanel) {
        Row row = (Row) new Row().coverChildrenHeight()
            .width(162)
            .childPadding(4)
            .background(INFO_BG);

        Column textCol = new Column();
        Row tile = new Row();
        tile.coverChildren()
            .child(
                IKey.str(label)
                    .asWidget()
                    .left(2)
                    .width(128)
                    .maxWidth(120));

        if (settingPanel != null) {
            ButtonWidget<?> settingBtn = new ButtonWidget<>().overlay(GuiTextures.ADD)
                .size(8)
                .right(0)
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
            tile.child(settingBtn);
        }

        textCol.coverChildren()
            .childPadding(2)
            .padding(2)
            .child(tile)
            .child(
                valueKey.alignment(Alignment.CENTER)
                    .color(Color.WHITE.main)
                    .shadow(false)
                    .asWidget()
                    .height(12)
                    .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND)
                    .width(130)
                    .maxWidth(120));
        row.child(textCol);

        row.child(new ItemSlot().slot(new ModularSlot(inv, slot).changeListener((newItem, amount, client, init) -> {
            if (init || newItem == null) return;
            if (newItem.getItem() != IDsItems.LOGIC_CARD.getItem()) return;
            writeLogicToCard(newItem, key, this);
            inv.setStackInSlot(slot, newItem);
        }))
            .background(OKGuiTextures.VARIABLE_SLOT)
            .top(7)
            .right(7));

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

}
