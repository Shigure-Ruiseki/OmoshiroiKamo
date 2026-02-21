package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type;

import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.programmer.ProgrammerPanel;

public class StringVariable extends BaseVariableWidget {

    private StringValue stringValue;

    public StringVariable(ProgrammerPanel panel) {
        super(panel);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        stringValue = new StringValue("");
        TextFieldWidget field = new TextFieldWidget().value(stringValue)
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
        panel.syncHandler.syncToServer(
            ProgrammerSH.SET_STRING_LITERAL,
            buffer -> buffer.writeStringToBuffer(stringValue.getStringValue()));
    }
}
