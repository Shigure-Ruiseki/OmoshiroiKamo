package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type;

import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;

import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.programmer.ProgrammerPanel;

public class BooleanVariable extends BaseVariableWidget {

    private BoolValue booleanValue;

    public BooleanVariable(ProgrammerPanel panel) {
        super(panel);

        booleanValue = new BoolValue(false);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        col.child(
            new ToggleButton().overlay(GuiTextures.CROSS_TINY)
                .size(12)
                .value(booleanValue)
                .onUpdateListener(widget -> writeLogicNBT()));
        child(col);
    }

    @Override
    public void writeLogicNBT() {
        panel.syncHandler.syncToServer(
            ProgrammerSH.SET_BOOLEAN_LITERAL,
            buffer -> buffer.writeBoolean(!booleanValue.getBoolValue()));
    }
}
