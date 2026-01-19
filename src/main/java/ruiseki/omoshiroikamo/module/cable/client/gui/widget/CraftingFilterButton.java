package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.omoshiroikamo.api.item.CraftingFilter;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class CraftingFilterButton extends CyclicVariantButtonWidget {

    public static final UITexture STORAGE = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(180, 18, 18, 18)
        .build();

    public static final UITexture CRAFTABLE = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(198, 18, 18, 18)
        .build();

    public static final UITexture BOTH = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(162, 18, 18, 18)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.crafting_filter.storage"), STORAGE),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.crafting_filter.craftable"), CRAFTABLE),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.crafting_filter.both"), BOTH));

    public CraftingFilterButton(EnumSyncValue<CraftingFilter> syncValue) {
        super(
            VARIANTS,
            syncValue.getValue()
                .getIndex(),
            0,
            18,
            value -> { syncValue.setValue(CraftingFilter.byIndex(value)); });
        size(18);
    }
}
