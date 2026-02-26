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
import ruiseki.omoshiroikamo.module.ids.common.block.programmer.ProgrammerPanel;

public class IfVariable extends BaseVariableWidget {

    public IfVariable(ProgrammerPanel panel) {
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
            new TextWidget<>(IKey.str("IF")).size(18)
                .alignment(Alignment.CENTER));
        row.child(
            new ItemSlot().name("cond")
                .syncHandler(panel.slots[1]));
        row.child(
            new TextWidget<>(IKey.str("THEN")).size(24, 18)
                .alignment(Alignment.CENTER));
        row.child(
            new ItemSlot().name("then")
                .syncHandler(panel.slots[2]));
        row.child(
            new TextWidget<>(IKey.str("ELSE")).size(24, 18)
                .alignment(Alignment.CENTER));
        row.child(
            new ItemSlot().name("else")
                .syncHandler(panel.slots[3]));

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
        panel.slots[3].getSlot()
            .changeListener((newItem, amountChanged, client, init) -> {
                if (init) return;
                writeLogicNBT();
            });

        col.child(row);

        child(col);
    }

    @Override
    public void writeLogicNBT() {
        panel.syncHandler.syncToServer(ProgrammerSH.SET_IF_LOGIC);
    }
}
