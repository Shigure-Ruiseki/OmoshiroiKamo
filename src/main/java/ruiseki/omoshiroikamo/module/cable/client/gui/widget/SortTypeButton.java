package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class SortTypeButton extends CyclicVariantButtonWidget {

    public static final UITexture NAME_SORT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(18, 18, 18, 18)
        .build();

    public static final UITexture ID_SORT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(0, 18, 18, 18)
        .build();

    public static final UITexture COUNT_SORT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(36, 18, 18, 18)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_type.by_name"), NAME_SORT),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_type.by_mod_id"), ID_SORT),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_type.by_count"), COUNT_SORT));

    public SortTypeButton(EnumSyncValue<SortType> syncValue) {
        super(
            VARIANTS,
            syncValue.getValue()
                .getIndex(),
            0,
            18,
            value -> { syncValue.setValue(SortType.byIndex(value)); });
        size(18);
    }
}
