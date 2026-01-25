package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class AndVariable extends BaseVariableWidget {

    public AndVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        Row row = new Row();
        row.coverChildren()
            .childPadding(2);
        row.child(
            new ItemSlot().name("a")
                .syncHandler("slots", 1));
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
        panel.syncHandler.syncToServer(ProgrammerSH.SET_AND_LOGIC);
    }
}
