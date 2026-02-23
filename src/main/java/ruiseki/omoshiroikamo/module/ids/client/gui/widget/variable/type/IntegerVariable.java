package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type;

import com.cleanroommc.modularui.value.IntValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.programmer.ProgrammerPanel;

public class IntegerVariable extends BaseVariableWidget {

    private IntValue intValue;

    public IntegerVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        intValue = new IntValue(0);
        TextFieldWidget field = new TextFieldWidget().value(intValue)
            .setNumbers()
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
        panel.syncHandler.syncToServer(ProgrammerSH.SET_INT_LITERAL, buffer -> buffer.writeInt(intValue.getIntValue()));
    }
}
