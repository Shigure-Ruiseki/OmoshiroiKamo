package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.BackpackSH;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackSettingPanel;

public class BackpackSettingWidget extends ExpandedTabWidget {

    private final BackpackPanel panel;
    private final BackpackHandler handler;
    private final BackpackSettingPanel settingPanel;
    private final TabWidget parentTabWidget;

    private static final List<CyclicVariantButtonWidget.Variant> KEEP_TAB_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.keep_tab"), GuiTextures.KEEP_TAB_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.not_keep_tab"), GuiTextures.NOT_KEEP_TAB_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> SEARCH_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.unlock_search"), GuiTextures.UNLOCK_SEARCH_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.lock_search"), GuiTextures.LOCK_SEARCH_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> LOCK_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.lock_backpack"), GuiTextures.LOCK_BACKPACK_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.unlock_backpack"),
            GuiTextures.UNLOCK_BACKPACK_ICON));

    public BackpackSettingWidget(BackpackPanel panel, BackpackSettingPanel settingPanel, TabWidget parentTabWidget) {
        super(2, GuiTextures.BACKPACK_ICON, "gui.backpack.backpack_settings", 80, TabWidget.ExpandDirection.RIGHT);

        this.panel = panel;
        this.handler = panel.getHandler();
        this.settingPanel = settingPanel;
        this.parentTabWidget = parentTabWidget;

        Row buttonRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        CyclicVariantButtonWidget tabButton = new CyclicVariantButtonWidget(
            KEEP_TAB_VARIANTS,
            handler.isKeepTab() ? 0 : 1,
            (index) -> {
                handler.setKeepTab(index == 0);
                updateWrapper();
            });

        CyclicVariantButtonWidget searchButton = new CyclicVariantButtonWidget(
            SEARCH_VARIANTS,
            handler.isSearchBackpack() ? 0 : 1,
            (index) -> {
                handler.setSearchBackpack(index == 0);
                updateWrapper();
            });

        CyclicVariantButtonWidget lockButton = new CyclicVariantButtonWidget(
            LOCK_VARIANTS,
            handler.isLockBackpack() ? 0 : 1,
            (index) -> {
                handler.setLockBackpack(index == 0);
                updateWrapper();
            });

        buttonRow.top(28)
            .child(tabButton)
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

    private void updateWrapper() {
        BackpackSH backpackSyncHandler = this.panel.getBackpackSyncHandler();
        backpackSyncHandler.syncToServer(BackpackSH.UPDATE_SETTING, buffer -> {
            buffer.writeBoolean(handler.isLockBackpack());
            buffer.writeStringToBuffer(
                panel.getPlayer()
                    .getUniqueID()
                    .toString());
            buffer.writeBoolean(handler.isSearchBackpack());
            buffer.writeBoolean(handler.isKeepTab());
        });
    }
}
