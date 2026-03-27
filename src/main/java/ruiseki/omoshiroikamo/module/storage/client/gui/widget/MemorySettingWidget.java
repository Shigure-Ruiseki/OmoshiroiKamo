package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.ExpandedTabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.TabWidget;
import ruiseki.omoshiroikamo.module.storage.client.gui.widget.TabWidget.ExpandDirection;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.StorageSlotSH;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StorageSettingPanel;

public class MemorySettingWidget extends ExpandedTabWidget {

    private static final List<CyclicVariantButtonWidget.Variant> RESPECT_NBT_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.ignore_nbt"), OKGuiTextures.IGNORE_NBT_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.match_nbt"), OKGuiTextures.MATCH_NBT_ICON));

    private final StoragePanel panel;
    private final StorageSettingPanel settingPanel;
    private final TabWidget parentTabWidget;

    private final CyclicVariantButtonWidget respectNBTButton;

    public MemorySettingWidget(StoragePanel panel, StorageSettingPanel settingPanel, TabWidget parentTabWidget) {
        super(2, OKGuiTextures.BRAIN_ICON, "gui.storage.memory_settings", 80, ExpandDirection.RIGHT);

        this.panel = panel;
        this.settingPanel = settingPanel;
        this.parentTabWidget = parentTabWidget;

        Row buttonRow = (Row) new Row().leftRel(0.5f)
            .height(20)
            .coverChildrenWidth()
            .childPadding(2);

        ButtonWidget<?> memorizeAllButton = new ButtonWidget<>().size(20)
            .overlay(OKGuiTextures.ALL_FOUR_SLOT_ICON)
            .onMousePressed(button -> {
                if (button == 0) {
                    var wrapper = panel.wrapper;

                    for (int i = 0; i < wrapper.storageSlots; i++) {
                        wrapper.setMemoryStack(i, panel.shouldMemorizeRespectNBT);
                    }

                    for (StorageSlotSH syncHandler : panel.storageSlotSHs) {
                        syncHandler.syncToServer(
                            StorageSlotSH.UPDATE_SET_MEMORY_STACK,
                            buf -> buf.writeBoolean(panel.isMemorySettingTabOpened));
                    }

                    return true;
                }
                return false;
            })
            .tooltipStatic(
                t -> t.addLine(IKey.lang("gui.storage.memorize_all"))
                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

        ButtonWidget<?> unmemorizeAllButton = new ButtonWidget<>().size(20)
            .overlay(OKGuiTextures.NONE_FOUR_SLOT_ICON)
            .onMousePressed(button -> {
                if (button == 0) {
                    var wrapper = panel.wrapper;

                    for (int i = 0; i < wrapper.storageSlots; i++) {
                        wrapper.unsetMemoryStack(i);
                    }

                    for (StorageSlotSH syncHandler : panel.storageSlotSHs) {
                        syncHandler.syncToServer(StorageSlotSH.UPDATE_UNSET_MEMORY_STACK);
                    }

                    return true;
                }
                return false;
            })
            .tooltipStatic(
                t -> t.addLine(IKey.lang("gui.storage.unmemorize_all"))
                    .pos(RichTooltip.Pos.NEXT_TO_MOUSE));

        respectNBTButton = new CyclicVariantButtonWidget(
            RESPECT_NBT_VARIANTS,
            index -> this.panel.shouldMemorizeRespectNBT = (index != 0));

        buttonRow.top(28)
            .child(memorizeAllButton)
            .child(unmemorizeAllButton)
            .child(respectNBTButton);

        child(buttonRow);
    }

    public boolean isRespectNBT() {
        return respectNBTButton.getIndex() != 0;
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
        panel.isMemorySettingTabOpened = parentTabWidget.isShowExpanded();
        settingPanel.updateTabState(1);
    }

}
