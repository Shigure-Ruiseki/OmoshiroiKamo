package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.common.programmer.ProgrammerPanel;

public class NorVariable extends BaseVariableWidget {

    public NorVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        Row row = new Row();
        row.coverChildren()
            .padding(2)
            .background(VANILLA_SEARCH_BACKGROUND)
            .childPadding(2);
        row.child(
            new ItemSlot().name("a")
                .syncHandler("slots", 1));
        row.child(
            new TextWidget<>(IKey.str("!||")).size(18)
                .alignment(Alignment.CENTER));
        row.child(
            new ItemSlot().name("b")
                .syncHandler("slots", 2));

        panel.slots[1].getSlot()
            .changeListener((newItem, amountChanged, client, init) -> {
                if (init) return;
                writeLogicNBT();
            });
        panel.slots[2].getSlot()
            .changeListener((newItem, amountChanged, client, init) -> {
                if (init) return;
                writeLogicNBT();
            });

        col.child(row);

        child(col);
    }

    @Override
    public void writeLogicNBT() {
        panel.syncHandler.syncToServer(ProgrammerSH.SET_NOR_LOGIC);
    }
}
