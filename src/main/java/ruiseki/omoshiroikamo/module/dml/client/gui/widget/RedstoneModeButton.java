package ruiseki.omoshiroikamo.module.dml.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.omoshiroikamo.api.redstone.RedstoneMode;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class RedstoneModeButton extends CyclicVariantButtonWidget {

    public static final UITexture ALWAYS_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(0, 18, 18, 18)
        .build();

    public static final UITexture HOVER_ALWAYS_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(0, 36, 18, 18)
        .build();

    public static final UITexture HIGH_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(18, 18, 18, 18)
        .build();

    public static final UITexture HOVER_HIGH_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(18, 36, 18, 18)
        .build();

    public static final UITexture HIGH_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(36, 18, 18, 18)
        .build();

    public static final UITexture HOVER_HIGH_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(36, 36, 18, 18)
        .build();

    public static final UITexture ALWAYS_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(54, 18, 18, 18)
        .build();

    public static final UITexture HOVER_ALWAYS_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_redstone")
        .imageSize(256, 256)
        .xy(54, 36, 18, 18)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.always_on"), ALWAYS_ON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.high_on"), HIGH_ON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.high_off"), HIGH_OFF),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.always_off"), ALWAYS_OFF));

    private static final List<UITexture> HOVER_TEXTURES = Arrays
        .asList(HOVER_ALWAYS_ON, HOVER_HIGH_ON, HOVER_HIGH_OFF, HOVER_ALWAYS_OFF);

    public RedstoneModeButton(EnumSyncValue<RedstoneMode> syncValue) {
        super(
            VARIANTS,
            syncValue.getValue()
                .getIndex(),
            0,
            18,
            value -> { syncValue.setValue(RedstoneMode.byIndex(value)); });
        size(18);
    }

    public void setVariants(List<CyclicVariantButtonWidget.Variant> newVariants,
        EnumSyncValue<RedstoneMode> syncValue) {
        if (newVariants == null || newVariants.isEmpty()) {
            throw new IllegalArgumentException("Variants list cannot be null or empty");
        }
        this.variants.clear();
        this.variants.addAll(newVariants);

        if (getIndex() >= this.variants.size()) {
            index = 0;
            syncValue.setValue(RedstoneMode.byIndex(index));
        }

        markTooltipDirty();
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);
        if (isHovering()) {
            UITexture hoverTexture = HOVER_TEXTURES.get(getIndex());
            hoverTexture.draw(context, 0, 0, 18, 18, widgetTheme.getTheme());
        }
    }
}
