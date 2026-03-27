package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import static ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel.LAYERED_TAB_TEXTURE;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.TabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.MemorySettingWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.SortingSettingWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.StorageSettingWidget;

public class StorageSettingPanel extends ModularPanel {

    private final StoragePanel parent;

    private final TabWidget backpackTab;
    private final TabWidget memoryTab;
    private final TabWidget sortTab;

    public StorageSettingPanel(StoragePanel parent) {
        super("settings");
        this.parent = parent;

        size(6, parent.getArea().height).relative(parent)
            .top(0)
            .right(0);

        backpackTab = new TabWidget(1, TabWidget.ExpandDirection.RIGHT);
        backpackTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.backpack.backpack_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        backpackTab.setExpandedWidget(new StorageSettingWidget(parent, this, backpackTab));
        backpackTab.setTabIcon(OKGuiTextures.BACKPACK_ICON);

        memoryTab = new TabWidget(2, TabWidget.ExpandDirection.RIGHT);
        memoryTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.backpack.memory_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        memoryTab.setExpandedWidget(new MemorySettingWidget(parent, this, memoryTab));
        memoryTab.setTabIcon(OKGuiTextures.BRAIN_ICON);

        sortTab = new TabWidget(3, TabWidget.ExpandDirection.RIGHT);
        sortTab.tooltipStatic(
            tooltip -> tooltip.addLine(IKey.lang("gui.backpack.sorting_settings"))
                .pos(RichTooltip.Pos.NEXT_TO_MOUSE));
        sortTab.setExpandedWidget(new SortingSettingWidget(parent, this, sortTab));
        sortTab.setTabIcon(OKGuiTextures.NO_SORT_ICON);

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
        parent.upgradeSlotGroupWidget.setEnabled(false);
    }

    @Override
    public void onClose() {
        super.onClose();
        parent.isMemorySettingTabOpened = false;
        parent.shouldMemorizeRespectNBT = false;
        parent.isSortingSettingTabOpened = false;
        parent.updateUpgradeWidgets();
        parent.upgradeSlotGroupWidget.setEnabled(true);
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
