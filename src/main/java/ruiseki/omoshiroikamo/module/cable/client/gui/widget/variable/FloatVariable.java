package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class FloatVariable extends BaseVariableWidget {

    private DoubleValue floatValue;

    public FloatVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        floatValue = new DoubleValue(0);
        TextFieldWidget field = new TextFieldWidget().value(floatValue)
            .setDefaultNumber(0.0)
            .setNumbersDouble(d -> d)
            .width(80)
            .onUpdateListener(
                widget -> {
                    if (!widget.getText()
                        .isEmpty()) {}
                });

        col.child(field);

        child(col);
    }

    @Override
    public void writeLogicNBT() {
        panel.syncHandler
            .syncToServer(ProgrammerSH.SET_FLOAT_LITERAL, buffer -> buffer.writeFloat(floatValue.getFloatValue()));
    }
}
