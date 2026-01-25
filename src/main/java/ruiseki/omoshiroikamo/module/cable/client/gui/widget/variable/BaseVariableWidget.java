package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.widget.ParentWidget;

import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public abstract class BaseVariableWidget extends ParentWidget<BaseVariableWidget> {

    public final ProgrammerPanel panel;

    public BaseVariableWidget(ProgrammerPanel panel) {
        this.panel = panel;
        coverChildren();
    }

    public abstract void writeLogicNBT();
}
