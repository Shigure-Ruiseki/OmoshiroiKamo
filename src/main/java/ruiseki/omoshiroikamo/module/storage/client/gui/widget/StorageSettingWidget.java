package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.TabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.StorageSH;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StorageSettingPanel;

public class StorageSettingWidget extends ExpandedTabWidget {

    private final StoragePanel panel;
    private final StorageWrapper wrapper;
    private final StorageSettingPanel settingPanel;
    private final TabWidget parentTabWidget;

    private static final List<CyclicVariantButtonWidget.Variant> KEEP_TAB_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.storage.keep_tab"), OKGuiTextures.KEEP_TAB_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.storage.not_keep_tab"), OKGuiTextures.NOT_KEEP_TAB_ICON));

    public StorageSettingWidget(StoragePanel panel, StorageSettingPanel settingPanel, TabWidget parentTabWidget) {
        super(2, OKGuiTextures.BACKPACK_ICON, "gui.storage.storage_settings", 80, TabWidget.ExpandDirection.RIGHT);

        this.panel = panel;
        this.wrapper = panel.wrapper;
        this.settingPanel = settingPanel;
        this.parentTabWidget = parentTabWidget;

        Row buttonRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        CyclicVariantButtonWidget tabButton = new CyclicVariantButtonWidget(
            KEEP_TAB_VARIANTS,
            wrapper.keepTab ? 0 : 1,
            (index) -> {
                wrapper.keepTab = index == 0;
                updateWrapper();
            });

        buttonRow.top(28)
            .child(tabButton);

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
        StorageSH backpackSyncHandler = this.panel.storageSH;
        backpackSyncHandler
            .syncToServer(StorageSH.UPDATE_SETTING, buffer -> { buffer.writeBoolean(wrapper.keepTab); });
    }
}
