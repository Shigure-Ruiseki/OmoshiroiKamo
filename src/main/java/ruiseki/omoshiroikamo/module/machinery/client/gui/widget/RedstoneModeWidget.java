package ruiseki.omoshiroikamo.module.machinery.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.core.client.gui.widget.RedstoneModeBaseWidget;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class RedstoneModeWidget extends RedstoneModeBaseWidget {

    public static final UITexture HIGH_OFF = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(0, 0, 16, 16)
        .build();

    public static final UITexture HIGH_ON = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(16, 0, 16, 16)
        .build();

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
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.high_on"), HIGH_ON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.high_off"), HIGH_OFF),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.redstone_mode.always_off"), ALWAYS_OFF));

    public RedstoneModeWidget(EnumSyncValue<RedstoneMode> syncValue) {
        super(VARIANTS, 1, 16, syncValue);
    }
}
