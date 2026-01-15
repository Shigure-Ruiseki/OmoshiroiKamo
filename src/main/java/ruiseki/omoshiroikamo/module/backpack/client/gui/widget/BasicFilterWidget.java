package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.FilterSlot;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IBasicFilterable;

public class BasicFilterWidget extends ParentWidget<BasicFilterWidget> {

    private static final List<CyclicVariantButtonWidget.Variant> FILTER_TYPE_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.whitelist"), OKGuiTextures.CHECK_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.blacklist"), OKGuiTextures.CROSS_ICON));

    @Getter
    private final CyclicVariantButtonWidget filterTypeButton;
    @Getter
    private final List<FilterSlot> filterSlots;
    @Getter
    private UpgradeSlotSH slotSyncHandler = null;

    public BasicFilterWidget(IBasicFilterable filterableWrapper, int slotIndex, String syncKey) {

        this.syncHandler("upgrades", slotIndex);

        this.filterTypeButton = new CyclicVariantButtonWidget(
            FILTER_TYPE_VARIANTS,
            filterableWrapper.getFilterType()
                .ordinal(),
            index -> {
                filterableWrapper.setFilterType(IBasicFilterable.FilterType.values()[index]);
                if (slotSyncHandler != null) {
                    slotSyncHandler.syncToServer(
                        UpgradeSlotSH.UPDATE_BASIC_FILTERABLE,
                        buf -> {
                            buf.writeInt(
                                filterableWrapper.getFilterType()
                                    .ordinal());
                        });
                }
            }).size(20, 20);

        SlotGroupWidget slotGroup = new SlotGroupWidget().name(syncKey + "s");
        slotGroup.coverChildren()
            .top(26);

        this.filterSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            FilterSlot slot = new FilterSlot();
            slot.name(syncKey + "_" + slotIndex)
                .syncHandler(syncKey + "_" + slotIndex, i)
                .pos(i % 3 * 18, i / 3 * 18);

            this.filterSlots.add(slot);
            slotGroup.child(slot);
        }

        child(filterTypeButton).child(slotGroup);
    }

    public BasicFilterWidget(IBasicFilterable filterableWrapper, int slotIndex) {
        this(filterableWrapper, slotIndex, "common_filter");
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        if (syncHandler instanceof UpgradeSlotSH) {
            this.slotSyncHandler = (UpgradeSlotSH) syncHandler;
        }
        return this.slotSyncHandler != null;
    }

}
