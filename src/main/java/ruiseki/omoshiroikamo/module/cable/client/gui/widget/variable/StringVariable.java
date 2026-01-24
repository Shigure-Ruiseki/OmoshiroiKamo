package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class StringVariable extends ParentWidget<StringVariable> {

    public StringVariable(ProgrammerPanel panel) {
        width(162);
        heightRel(1f);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2)
            .align(Alignment.TopCenter);

        col.child(new TextWidget<>(IKey.str("String config")));

        TextFieldWidget field = new TextFieldWidget().setNumbers()
            .width(80)
            .onUpdateListener(widget -> {
                if (!widget.getText()
                    .isEmpty()) {
                    panel.syncHandler.syncToServer(
                        ProgrammerSH.SET_STRING_LITERAL,
                        buffer -> buffer.writeStringToBuffer(widget.getText()));
                }
            });

        col.child(field);

        child(
            new ItemSlot().syncHandler(panel.slots[0])
                .bottom(0)
                .right(0));

        child(col);
    }

}
