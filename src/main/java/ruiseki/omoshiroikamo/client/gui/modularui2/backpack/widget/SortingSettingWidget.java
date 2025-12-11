package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.BackpackSlotSH;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TabWidget.ExpandDirection;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackSettingPanel;

public class SortingSettingWidget extends ExpandedTabWidget {

    private final BackpackPanel panel;
    private final BackpackSettingPanel settingPanel;
    private final TabWidget parentTabWidget;

    public SortingSettingWidget(BackpackPanel panel, BackpackSettingPanel settingPanel, TabWidget parentTabWidget) {
        super(2, MGuiTextures.NO_SORT_ICON, "gui.sorting_settings", 80, ExpandDirection.RIGHT);

        this.panel = panel;
        this.settingPanel = settingPanel;
        this.parentTabWidget = parentTabWidget;

        Row buttonRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        ButtonWidget<?> lockAllButton = new ButtonWidget<>().size(20)
            .overlay(MGuiTextures.ALL_FOUR_SLOT_ICON)
            .onMousePressed(button -> {
                if (button == 0) {
                    BackpackHandler handler = panel.getHandler();

                    for (int i = 0; i < handler.getBackpackSlots(); i++) {
                        handler.setSlotLocked(i, true);
                    }

                    for (BackpackSlotSH syncHandler : panel.getBackpackSlotSyncHandlers()) {
                        syncHandler.syncToServer(BackpackSlotSH.UPDATE_SET_SLOT_LOCK);
                    }

                    return true;
                }
                return false;
            })
            .tooltipStatic(
                t -> t.addLine(IKey.lang("gui.lock_all_sort"))
                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

        ButtonWidget<?> unlockAllButton = new ButtonWidget<>().size(20)
            .overlay(MGuiTextures.NONE_FOUR_SLOT_ICON)
            .onMousePressed(button -> {
                if (button == 0) {
                    BackpackHandler handler = panel.getHandler();

                    for (int i = 0; i < handler.getBackpackSlots(); i++) {
                        handler.setSlotLocked(i, false);
                    }

                    for (BackpackSlotSH syncHandler : panel.getBackpackSlotSyncHandlers()) {
                        syncHandler.syncToServer(BackpackSlotSH.UPDATE_UNSET_SLOT_LOCK);
                    }

                    return true;
                }
                return false;
            })
            .tooltipStatic(
                t -> t.addLine(IKey.lang("gui.unlock_all_sort"))
                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

        buttonRow.top(28)
            .child(lockAllButton)
            .child(unlockAllButton);

        child(buttonRow);
    }

    @Override
    public void onInit() {
        getContext().getUISettings()
            .getRecipeViewerSettings()
            .addExclusionArea(this);
    }

    @Override
    public void updateTabState() {
        parentTabWidget.setShowExpanded(!parentTabWidget.isShowExpanded());
        panel.isSortingSettingTabOpened = parentTabWidget.isShowExpanded();
        settingPanel.updateTabState(2);
    }
}
