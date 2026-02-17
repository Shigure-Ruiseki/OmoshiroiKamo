package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type;

import com.cleanroommc.modularui.value.LongValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.common.block.programmer.ProgrammerPanel;

public class LongVariable extends BaseVariableWidget {

    private LongValue longValue;

    public LongVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        longValue = new LongValue(0);
        TextFieldWidget field = new TextFieldWidget().value(longValue)
            .setDefaultNumber(0.0)
            .setNumbersDouble(d -> d)
            .width(80)
            .onUpdateListener(widget -> {
                if (!widget.getText()
                    .isEmpty()) {
                    writeLogicNBT();
                }
            });

        col.child(field);

        child(col);
    }

    @Override
    public void writeLogicNBT() {
        panel.syncHandler
            .syncToServer(ProgrammerSH.SET_LONG_LITERAL, buffer -> buffer.writeLong(longValue.getLongValue()));
    }
}
