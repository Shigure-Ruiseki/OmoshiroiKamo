package ruiseki.omoshiroikamo.common.block.backpack.widget;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.SyncHandler;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.common.block.backpack.capabilities.IBasicFilterable;
import ruiseki.omoshiroikamo.common.block.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class BasicFilterWidget extends ParentWidget<BasicFilterWidget> {

    private static final List<CyclicVariantButtonWidget.Variant> FILTER_TYPE_VARIANTS = new ArrayList<>();

    static {
        FILTER_TYPE_VARIANTS.add(
            new CyclicVariantButtonWidget.Variant(
                IKey.lang(LibMisc.MOD_ID + ".gui.whitelist"),
                MGuiTextures.CHECK_ICON));
        FILTER_TYPE_VARIANTS.add(
            new CyclicVariantButtonWidget.Variant(
                IKey.lang(LibMisc.MOD_ID + ".gui.blacklist"),
                MGuiTextures.CROSS_ICON));
    }

    private final IBasicFilterable filterableWrapper;
    @Getter
    private final CyclicVariantButtonWidget filterTypeButton;
    @Getter
    private final List<PhantomItemSlot> filterSlots;
    @Getter
    private UpgradeSlotSH slotSyncHandler = null;

    public BasicFilterWidget(IBasicFilterable filterableWrapper, int slotIndex, String syncKey) {
        this.filterableWrapper = filterableWrapper;

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
            PhantomItemSlot slot = new PhantomItemSlot();
            slot.name(syncKey + "_" + slotIndex);
            slot.syncHandler(syncKey + "_" + slotIndex, i);
            slot.pos(i % 3 * 18, i / 3 * 18);

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
