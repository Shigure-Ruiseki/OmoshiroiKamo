package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.TabTexture;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widget.Widget;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class SettingTabWidget extends Widget<SettingTabWidget> implements Interactable {

    public static final TabTexture TAB_TEXTURE = GuiTextures.TAB_RIGHT;

    public SettingTabWidget() {
        size(TAB_TEXTURE.getWidth(), TAB_TEXTURE.getHeight()).right(-TAB_TEXTURE.getWidth() + 4)
            .top(0)
            .background(TAB_TEXTURE.get(-1, false))
            .tooltipAutoUpdate(true)
            .tooltipDynamic(tooltip -> {
                BackpackPanel panel = (BackpackPanel) getPanel();
                if (panel.getSettingPanel()
                    .isPanelOpen()) {
                    tooltip.addLine(IKey.lang("gui.backpack"))
                        .pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                } else {
                    tooltip.addLine(IKey.lang("gui.backpack.settings"))
                        .pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                }
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            });
    }

    @Override
    public void onInit() {
        getContext().getUISettings()
            .getRecipeViewerSettings()
            .addExclusionArea(this);
    }

    @Override
    public @NotNull Result onMousePressed(int mouseButton) {
        if (mouseButton == 0) {
            BackpackPanel panel = (BackpackPanel) getPanel();

            Interactable.playButtonClickSound();
            if (panel.getSettingPanel()
                .isPanelOpen()) {
                panel.getSettingPanel()
                    .closePanel();
                panel.updateUpgradeWidgets();
            } else {
                panel.getSettingPanel()
                    .openPanel();
                panel.disableAllTabWidgets();
            }
            return Result.SUCCESS;
        }

        return Result.IGNORE;
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.draw(context, widgetTheme);
        BackpackPanel panel = (BackpackPanel) getPanel();
        if (panel.getSettingPanel()
            .isPanelOpen()) {
            MGuiTextures.BACK_ICON.draw(context, 8, 6, 16, 16, widgetTheme.getTheme());
        } else {
            MGuiTextures.SETTING_ICON.draw(context, 8, 6, 16, 16, widgetTheme.getTheme());
        }
    }
}
