package ruiseki.omoshiroikamo.module.chickens.common.block;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.module.chickens.client.gui.container.ChickenContainer;

public class TERoost extends TERoostBase implements IGuiHolder<PosGuiData> {

    public TERoost() {}

    @Override
    protected void spawnChickenDrop() {
        DataChicken chicken = getChickenData(0);
        ItemStack chickenStack = getStackInSlot(0);

        if (chicken == null || chickenStack == null) {
            return;
        }

        ItemStack drop = chicken.createLayStack();
        if (drop == null) {
            return;
        }

        if (drop.stackSize > 0) {
            putStackInOutput(drop);
            playSpawnSound();
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
    protected boolean hasFreeOutputSlot() {
        return !outputIsFull();
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.registerSlotGroup("input", 3);
        settings.customContainer(ChickenContainer::new);
        ModularPanel panel = new ModularPanel("roost_gui");
        panel.child(
            Flow.column()
                .child(
                    IKey.str(getLocalizedName())
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .matrix("I  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(GuiTextures.ROOST_SLOT)
                                .hoverBackground(GuiTextures.ROOST_SLOT)
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
                            richTooltip.add(WailaUtils.getProgress(this));
                            richTooltip.markDirty();
                        })
                        .topRel(0.25f)
                        .leftRel(0.375f)
                        .texture(com.cleanroommc.modularui.drawable.GuiTextures.PROGRESS_ARROW, 20)));
        panel.bindPlayerInventory();
        return panel;
    }
}
