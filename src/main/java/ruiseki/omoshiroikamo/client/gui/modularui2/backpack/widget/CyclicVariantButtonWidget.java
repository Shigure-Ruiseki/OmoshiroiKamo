package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.List;
import java.util.function.IntConsumer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.ButtonWidget;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class CyclicVariantButtonWidget extends ButtonWidget<CyclicVariantButtonWidget> {

    private final List<Variant> variants;
    private int iconOffset = 2;
    private int iconSize = 16;
    private final IntConsumer mousePressedUpdater;

    @Getter
    private int index;
    @Getter
    @Setter
    private boolean inEffect = true;

    public CyclicVariantButtonWidget(List<Variant> variants, int index, int iconOffset, int iconSize,
        IntConsumer mousePressedUpdater) {
        this.variants = variants;
        this.index = index;
        this.iconOffset = iconOffset;
        this.iconSize = iconSize;
        this.mousePressedUpdater = mousePressedUpdater;

        this.size(20, 20)
            .onMousePressed(mouseButton -> {
                if (mouseButton == 1) {
                    this.index = (this.index - 1 + variants.size()) % variants.size();
                } else {
                    this.index = (this.index + 1) % variants.size();
                }
                this.mousePressedUpdater.accept(this.index);
                this.markTooltipDirty();
                return true;
            })
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(variants.get(this.index).name);
                if (!inEffect) {
                    tooltip.addLine(
                        IKey.lang(LibMisc.MOD_ID + ".gui.not_in_effect")
                            .style(IKey.RED));
                }
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            });
    }

    public CyclicVariantButtonWidget(List<Variant> variants, int index, IntConsumer mousePressedUpdater) {
        this(variants, index, 2, 16, mousePressedUpdater);
    }

    public CyclicVariantButtonWidget(List<Variant> variants, IntConsumer mousePressedUpdater) {
        this(variants, 0, 2, 16, mousePressedUpdater);
    }

    @Override
    public void drawOverlay(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawOverlay(context, widgetTheme);
        IDrawable drawable = variants.get(index).drawable;
        drawable.draw(context, iconOffset, iconOffset, iconSize, iconSize, widgetTheme.getTheme());
    }

    public static class Variant {

        public final IKey name;
        public final IDrawable drawable;

        public Variant(IKey name, IDrawable drawable) {
            this.name = name;
            this.drawable = drawable;
        }
    }

}
