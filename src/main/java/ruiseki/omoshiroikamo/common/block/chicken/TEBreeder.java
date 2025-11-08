package ruiseki.omoshiroikamo.common.block.chicken;

import static codechicken.core.ClientUtils.getWorld;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.config.general.ChickenConfigs;

public class TEBreeder extends TERoostBase {

    public TEBreeder() {}

    @Override
    public String getMachineName() {
        return ModObject.blockBreeder.unlocalisedName;
    }

    @Override
    protected void spawnChickenDrop() {
        DataChicken left = getChickenData(0);
        DataChicken right = getChickenData(1);
        if (left != null && right != null) {
            putStackInOutput(left.createChildStack(right, getWorld()));
            playSpawnSound();
        }
    }

    @Override
    public int getSizeChickenInventory() {
        return 2;
    }

    @Override
    protected int requiredSeedsForDrop() {
        return 2;
    }

    @Override
    protected double speedMultiplier() {
        return ChickenConfigs.roostSpeed;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.registerSlotGroup("input", 3);
        ModularPanel panel = new ModularPanel("breeder_gui");
        panel.child(
            Flow.column()
                .child(
                    IKey.str(StatCollector.translateToLocal("tile." + getMachineName() + ".name"))
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .matrix("S+II  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(MGuiTextures.ROOST_SLOT)
                                .hoverBackground(MGuiTextures.ROOST_SLOT)
                                .slot(
                                    new ModularSlot(inv, index).slotGroup("input")
                                        .filter(stack -> isMachineItemValidForSlot(index, stack))))
                        .key(
                            'S',
                            new ItemSlot().slot(
                                new ModularSlot(inv, 2).slotGroup("input")
                                    .filter(stack -> isMachineItemValidForSlot(2, stack))))
                        .key('+', new Widget<>().background(GuiTextures.ADD))
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
                        .leftRel(0.57f)
                        .size(36, 18)
                        .texture(MGuiTextures.BREEDER_PROGRESS, 36)));
        panel.bindPlayerInventory();
        return panel;
    }
}
