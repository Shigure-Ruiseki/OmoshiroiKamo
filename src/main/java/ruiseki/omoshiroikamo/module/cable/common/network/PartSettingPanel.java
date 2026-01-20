package ruiseki.omoshiroikamo.module.cable.common.network;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncHandler;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.cable.ICablePart;

public final class PartSettingPanel {

    public static ModularPanel build(ICablePart part) {
        ModularPanel panel = new Dialog<>("part_setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Row sideRow = new Row();
        sideRow.child(
            IKey.lang("gui.cable.side")
                .asWidget())
            .child(
                new TextFieldWidget().value(
                    new StringValue(
                        part.getSide()
                            .name()))
                    .right(0))
            .height(20);

        Row tickRow = new Row();
        tickRow.child(
            IKey.lang("gui.cable.tick")
                .asWidget())
            .child(
                new TextFieldWidget().value(new IntSyncValue(part::getTickInterval, part::setTickInterval))
                    .setFormatAsInteger(true)
                    .setScrollValues(1, 5, 10)
                    .setDefaultNumber(1)
                    .setNumbers(1, Integer.MAX_VALUE)
                    .right(0))
            .height(20);

        Row priorityRow = new Row();
        priorityRow.child(
            IKey.lang("gui.cable.priority")
                .asWidget())
            .child(
                new TextFieldWidget().value(new IntSyncValue(part::getPriority, part::setPriority))
                    .setFormatAsInteger(true)
                    .setScrollValues(1, 5, 10)
                    .setDefaultNumber(0)
                    .setNumbers(0, Integer.MAX_VALUE)
                    .right(0))
            .height(20);

        Row channelRow = new Row();
        channelRow.child(
            IKey.lang("gui.cable.channel")
                .asWidget())
            .child(
                new TextFieldWidget().value(new IntSyncValue(part::getChannel, part::setChannel))
                    .setFormatAsInteger(true)
                    .setScrollValues(1, 5, 10)
                    .setDefaultNumber(0)
                    .setNumbers(0, Integer.MAX_VALUE)
                    .right(0))
            .height(20);

        panel.child(
            new Column().padding(7)
                .child(sideRow)
                .child(tickRow)
                .child(priorityRow)
                .child(channelRow));
        return panel;
    }

    public static ButtonWidget<?> addSettingButton(IPanelHandler settingPanel) {
        return
            new ButtonWidget<>().size(18)
                .pos(-20, 0)
                .tooltip(tooltip -> { tooltip.addLine(IKey.lang("gui.cable.part_settings")); })
                .onMousePressed(btn -> {
                    if (settingPanel.isPanelOpen()) {
                        settingPanel.closePanel();
                        return true;
                    }
                    settingPanel.openPanel();
                    return true;
                });
    }
}
