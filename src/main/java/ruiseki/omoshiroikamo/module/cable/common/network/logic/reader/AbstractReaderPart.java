package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKey;

public abstract class AbstractReaderPart extends AbstractPart implements ILogicReader {

    public void addSearchableRow(ListWidget<Row, ?> list, String searchKey, Row row, StringValue searchValue) {
        row.setEnabledIf(
            w -> searchKey.toLowerCase()
                .contains(
                    searchValue.getStringValue()
                        .toLowerCase()));
        list.child(row);
    }

    public Row infoRow(String langKey, IKey valueKey, int slot, LogicKey key, ItemStackHandler inv) {
        Row row = (Row) new Row().height(20)
            .width(162)
            .childPadding(4);

        Column textCol = new Column();
        textCol.child(
            IKey.lang(langKey)
                .asWidget())
            .child(valueKey.asWidget());

        row.child(textCol);

        row.child(new ItemSlot().slot(new ModularSlot(inv, slot).changeListener((newItem, amount, client, init) -> {
            if (init || newItem == null) return;
            if (newItem.getItem() != CableItems.LOGIC_CARD.getItem()) return;

            ItemStack copy = newItem.copy();
            writeLogicToCard(copy, key, this);
            inv.setStackInSlot(slot, copy);
        }))
            .background(OKGuiTextures.VARIABLE_SLOT)
            .right(7));

        return row;
    }

    public World world() {
        return getCable().getWorld();
    }

    public int readX() {
        return getPos().offset(getSide()).x;
    }

    public int readY() {
        return getPos().offset(getSide()).y;
    }

    public int readZ() {
        return getPos().offset(getSide()).z;
    }
}
