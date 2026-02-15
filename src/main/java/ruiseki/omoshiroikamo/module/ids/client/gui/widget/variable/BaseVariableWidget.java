package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable;

import com.cleanroommc.modularui.widget.ParentWidget;

import ruiseki.omoshiroikamo.module.ids.common.block.programmer.ProgrammerPanel;

public abstract class BaseVariableWidget extends ParentWidget<BaseVariableWidget> {

    public final ProgrammerPanel panel;

    public BaseVariableWidget(ProgrammerPanel panel) {
        this.panel = panel;
        coverChildren();
        alignY(0.5f);
        alignX(0.5f);
    }

    public abstract void writeLogicNBT();
}
