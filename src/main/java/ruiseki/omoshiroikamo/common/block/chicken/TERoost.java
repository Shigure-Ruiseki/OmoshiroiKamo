package ruiseki.omoshiroikamo.common.block.chicken;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;

public class TERoost extends TERoostBase {

    public TERoost() {}

    @Override
    public String getMachineName() {
        return ModObject.blockRoost.unlocalisedName;
    }

    @Override
    protected void spawnChickenDrop() {
        DataChicken chicken = getChickenData(0);
        if (chicken != null) {
            putStackInOutput(chicken.createLayStack());

        }
    }

    @Override
    protected int getSizeChickenInventory() {
        return 1;
    }

    @Override
    protected int requiredSeedsForDrop() {
        return 0;
    }

    @Override
    protected double speedMultiplier() {
        return ChickenConfig.roostSpeed;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager
            .syncValue("progress", new DoubleSyncValue(() -> getProgress(), value -> setProgress((float) value)));
        syncManager.registerSlotGroup("input", 3);
        ModularPanel panel = new ModularPanel("roost_gui");
        panel.child(
            Flow.column()
                .child(
                    IKey.str(StatCollector.translateToLocal("tile." + getMachineName() + ".name"))
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .matrix("I  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(MGuiTextures.ROOST_SLOT)
                                .hoverBackground(MGuiTextures.ROOST_SLOT)
                                .slot(
                                    new ModularSlot(inv, index).slotGroup("input")
                                        .filter(stack -> isItemValidForSlot(index, stack))))
                        .key(
                            'O',
                            index -> new ItemSlot().slot(new ModularSlot(inv, index + 3).accessibility(false, true)))
                        .build()
                        .topRel(0.25f)
                        .alignX(Alignment.CENTER))
                .child(
                    new ProgressWidget().progress(this::getProgress)
                        .tooltipDynamic(richTooltip -> {
                            richTooltip.add(getFormattedProgress());
                            richTooltip.markDirty();
                        })
                        .topRel(0.25f)
                        .leftRel(0.375f)
                        .texture(GuiTextures.PROGRESS_ARROW, 20)));
        panel.bindPlayerInventory();
        return panel;
    }
}
