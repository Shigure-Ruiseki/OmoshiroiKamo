package ruiseki.omoshiroikamo.common.block.backpack;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel.LAYERED_TAB_TEXTURE;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.BackpackSettingWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.MemorySettingWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.SortingSettingWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TabWidget;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget.TabWidget.ExpandDirection;

public class BackpackSettingPanel extends ModularPanel {

    private final BackpackPanel parent;

    private final TabWidget backpackTab;
    private final TabWidget memoryTab;
    private final TabWidget sortTab;

    public BackpackSettingPanel(BackpackPanel parent) {
        super("backpack_settings");
        this.parent = parent;

        size(6, parent.getArea().height).relative(parent)
            .top(0)
            .right(0);

        backpackTab = new TabWidget(1, ExpandDirection.RIGHT);
        backpackTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.backpack_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        backpackTab.setExpandedWidget(new BackpackSettingWidget(parent, this, backpackTab));
        backpackTab.setTabIcon(MGuiTextures.BACKPACK_ICON);

        memoryTab = new TabWidget(2, ExpandDirection.RIGHT);
        memoryTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.memory_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        memoryTab.setExpandedWidget(new MemorySettingWidget(parent, this, memoryTab));
        memoryTab.setTabIcon(MGuiTextures.BRAIN_ICON);

        sortTab = new TabWidget(3, ExpandDirection.RIGHT);
        sortTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.sorting_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        sortTab.setExpandedWidget(new SortingSettingWidget(parent, this, sortTab));
        sortTab.setTabIcon(MGuiTextures.NO_SORT_ICON);

        child(backpackTab).child(memoryTab)
            .child(sortTab);
    }

    public void updateTabState(int openIndex) {
        backpackTab.setEnabled(true);
        memoryTab.setEnabled(true);
        sortTab.setEnabled(true);

        switch (openIndex) {
            case 0:
                memoryTab.setShowExpanded(false);
                sortTab.setShowExpanded(false);
                parent.isMemorySettingTabOpened = false;
                parent.isSortingSettingTabOpened = false;
                memoryTab.setEnabled(!backpackTab.isShowExpanded());
                break;

            case 1:
                backpackTab.setShowExpanded(false);
                sortTab.setShowExpanded(false);
                parent.isSortingSettingTabOpened = false;
                sortTab.setEnabled(!memoryTab.isShowExpanded());
                break;

            case 2:
                backpackTab.setShowExpanded(false);
                memoryTab.setShowExpanded(false);
                parent.isMemorySettingTabOpened = false;
                break;
        }
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public void onOpen(ModularScreen screen) {
        super.onOpen(screen);
        parent.isMemorySettingTabOpened = memoryTab.isShowExpanded();
        parent.shouldMemorizeRespectNBT = ((MemorySettingWidget) memoryTab.getExpandedWidget()).isRespectNBT();
        parent.isSortingSettingTabOpened = sortTab.isShowExpanded();
        parent.getUpgradeSlotGroupWidget()
            .setEnabled(false);
    }

    @Override
    public void onClose() {
        super.onClose();
        parent.isMemorySettingTabOpened = false;
        parent.shouldMemorizeRespectNBT = false;
        parent.isSortingSettingTabOpened = false;
        parent.updateUpgradeWidgets();
        parent.getUpgradeSlotGroupWidget()
            .setEnabled(true);
    }

    @Override
    public void postDraw(ModularGuiContext context, boolean transformed) {
        super.postDraw(context, transformed);
        LAYERED_TAB_TEXTURE.draw(
            context,
            0,
            0,
            6,
            getArea().height,
            WidgetTheme.getDefault()
                .getTheme());
    }

}
