package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.BackpackSH;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackSettingPanel;

public class BackpackSettingWidget extends ExpandedTabWidget {

    private final BackpackPanel panel;
    private final BackpackHandler handler;
    private final BackpackSettingPanel settingPanel;
    private final TabWidget parentTabWidget;

    private final Row buttonRow;

    private static final List<CyclicVariantButtonWidget.Variant> SEARCH_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.lock_search"), MGuiTextures.LOCK_SEARCH_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.unlock_search"), MGuiTextures.UNLOCK_SEARCH_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> LOCK_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.lock_backpack"), MGuiTextures.LOCK_BACKPACK_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.unlock_backpack"), MGuiTextures.UNLOCK_BACKPACK_ICON));

    public BackpackSettingWidget(BackpackPanel panel, BackpackSettingPanel settingPanel, TabWidget parentTabWidget) {
        super(2, MGuiTextures.BACKPACK_ICON, "gui.backpack_settings", 80, TabWidget.ExpandDirection.RIGHT);

        this.panel = panel;
        this.handler = panel.getHandler();
        this.settingPanel = settingPanel;
        this.parentTabWidget = parentTabWidget;

        buttonRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        CyclicVariantButtonWidget searchButton = new CyclicVariantButtonWidget(
            SEARCH_VARIANTS,
            handler.isSearchBackpack() ? 0 : 1,
            (index) -> {
                BackpackSH backpackSyncHandler = this.panel.getBackpackSyncHandler();
                handler.setSearchBackpack(index == 0);
                backpackSyncHandler.syncToServer(
                    BackpackSH.UPDATE_SEARCH,
                    buffer -> { buffer.writeBoolean(handler.isSearchBackpack()); });
            });

        CyclicVariantButtonWidget lockButton = new CyclicVariantButtonWidget(
            LOCK_VARIANTS,
            handler.isLockBackpack() ? 0 : 1,
            (index) -> {
                BackpackSH backpackSyncHandler = this.panel.getBackpackSyncHandler();
                handler.setLockBackpack(index == 0);
                backpackSyncHandler.syncToServer(BackpackSH.UPDATE_LOCK, buffer -> {
                    buffer.writeBoolean(handler.isLockBackpack());
                    buffer.writeStringToBuffer(
                        panel.getPlayer()
                            .getUniqueID()
                            .toString());
                });
            });

        buttonRow.top(28)
            .child(searchButton)
            .child(lockButton);

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
        settingPanel.updateTabState(0);
    }
}
