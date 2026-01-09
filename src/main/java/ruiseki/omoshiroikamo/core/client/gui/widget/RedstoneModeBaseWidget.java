package ruiseki.omoshiroikamo.core.client.gui.widget;

import java.util.List;

import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;

public class RedstoneModeBaseWidget extends CyclicVariantButtonWidget {

    public RedstoneModeBaseWidget(List<Variant> variants, int iconOffset, int iconSize,
        EnumSyncValue<RedstoneMode> syncValue) {
        super(
            variants,
            syncValue.getValue()
                .getIndex(),
            iconOffset,
            iconSize,
            value -> { syncValue.setValue(RedstoneMode.byIndex(value)); });
        size(18);
    }
}
