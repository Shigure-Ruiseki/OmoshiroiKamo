package ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.common.programmer.ProgrammerPanel;

public class BooleanVariable extends ParentWidget<BooleanVariable> {

    public BooleanVariable(ProgrammerPanel panel) {
        width(162);
        heightRel(1f);

        Column col = new Column();
        col.coverChildren()
            .childPadding(2)
            .align(Alignment.TopCenter);

        col.child(new TextWidget<>(IKey.str("Boolean config")));

        col.child(
            new ToggleButton().overlay(GuiTextures.BOOKMARK)
                .size(12)
                .onUpdateListener(widget -> {
                    panel.syncHandler.syncToServer(
                        ProgrammerSH.SET_BOOLEAN_LITERAL,
                        buffer -> buffer.writeBoolean(widget.isValueSelected()));
                }));

        child(
            new ItemSlot().syncHandler(panel.slots[0])
                .bottom(0)
                .right(0));

        child(col);
    }

}
