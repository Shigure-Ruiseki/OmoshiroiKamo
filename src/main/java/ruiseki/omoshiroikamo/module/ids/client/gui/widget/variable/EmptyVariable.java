package ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.TextWidget;

import ruiseki.omoshiroikamo.module.ids.common.programmer.ProgrammerPanel;

public class EmptyVariable extends BaseVariableWidget {

    public EmptyVariable(ProgrammerPanel panel) {
        super(panel);
        child(new TextWidget<>(IKey.lang("gui.empty")));
    }

    @Override
    public void writeLogicNBT() {

    }
}
