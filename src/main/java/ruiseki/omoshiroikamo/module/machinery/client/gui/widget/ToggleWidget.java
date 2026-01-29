package ruiseki.omoshiroikamo.module.machinery.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class ToggleWidget extends CyclicVariantButtonWidget {

    public static final UITexture ALWAYS_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(32, 0, 16, 16)
        .build();
    public static final UITexture ALWAYS_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(48, 0, 16, 16)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.always_on"), ALWAYS_ON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.always_off"), ALWAYS_OFF));

    public ToggleWidget(BooleanSyncValue syncer) {
        super(VARIANTS, syncer.getValue() ? 0 : 1, 1, 16, value -> { syncer.setValue(value == 0); });
        size(18);
    }

    public ToggleWidget(List<CyclicVariantButtonWidget.Variant> variants, BooleanSyncValue syncer, int iconOffset,
        int iconSize) {
        super(variants, syncer.getValue() ? 0 : 1, 1, 16, value -> { syncer.setValue(value == 0); });
        size(18);
    }
}
