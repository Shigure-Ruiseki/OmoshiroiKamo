package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class DoubleVariable extends BaseVariableWidget {

    private DoubleValue doubleValue;

    public DoubleVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        doubleValue = new DoubleValue(0);
        TextFieldWidget field = new TextFieldWidget().value(doubleValue)
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
            .syncToServer(ProgrammerSH.SET_DOUBLE_LITERAL, buffer -> buffer.writeDouble(doubleValue.getDoubleValue()));
    }
}
