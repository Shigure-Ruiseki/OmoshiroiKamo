package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.type;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class BooleanVariable extends BaseVariableWidget {

    private BoolValue booleanValue;

    public BooleanVariable(ProgrammerPanel panel) {
        super(panel);

        booleanValue = new BoolValue(false);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2);

        col.child(
            new ToggleButton().overlay(CLEAR)
                .size(12)
                .value(booleanValue)
                .onUpdateListener(widget -> writeLogicNBT()));
        child(col);
    }

    private static final UITexture CLEAR = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(0, 44, 8, 8)
        .adaptable(1)
        .build();

    @Override
    public void writeLogicNBT() {
        panel.syncHandler.syncToServer(
            ProgrammerSH.SET_BOOLEAN_LITERAL,
            buffer -> buffer.writeBoolean(!booleanValue.getBoolValue()));
    }
}
