package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class SortOrderButton extends CyclicVariantButtonWidget {

    private static final int ASC = 0;
    private static final int DESC = 1;

    public static final UITexture ASC_SORT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(198, 36, 18, 18)
        .build();

    public static final UITexture DESC_SORT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/icons")
        .imageSize(256, 256)
        .xy(180, 36, 18, 18)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_order.asc"), ASC_SORT),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.sort_order.desc"), DESC_SORT));

    public SortOrderButton(BooleanSyncValue syncValue) {
        super(VARIANTS, syncValue.getValue() ? ASC : DESC, 0, 18, value -> syncValue.setValue(value == ASC));
        size(18);
    }
}
