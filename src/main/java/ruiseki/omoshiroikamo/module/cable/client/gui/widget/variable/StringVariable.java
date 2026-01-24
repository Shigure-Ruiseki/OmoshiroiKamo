package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;

import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class StringVariable extends ParentWidget<StringVariable> {

    public StringVariable(ProgrammerPanel panel) {
        width(162);
        heightRel(1f);

        Column col = new Column();
        col.coverChildren();

        col.child(new TextWidget<>(IKey.str("String config")));

        child(col);
    }

}
