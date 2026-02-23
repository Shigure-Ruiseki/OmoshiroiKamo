package ruiseki.omoshiroikamo.module.chickens.common.block;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
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
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.chickens.client.gui.container.ChickenContainer;

public class TERoost extends TERoostBase implements IGuiHolder<PosGuiData> {

    public TERoost() {}

    @Override
    protected void spawnChickenDrop() {
        DataChicken chicken = getChickenData(0);
        ItemStack foodStack = getStackInSlot(2);

        if (chicken == null || foodStack == null) {
            return;
        }

        ItemStack template = chicken.getItem()
            .getOutputFromFood(foodStack);
        ItemStack drop = template != null ? template.copy() : chicken.createLayStack();

        if (template != null) {
            // Apply gain factor to the custom drop
            int factor = DataChicken.calculateLayStackFactor(chicken.getGainStat());
            drop.stackSize *= factor;
        }

        if (drop != null && drop.stackSize > 0) {
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
        return 1;
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
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
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
                        .matrix("SI  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(OKGuiTextures.ROOST_SLOT)
                                .hoverBackground(OKGuiTextures.ROOST_SLOT)
                                .slot(
                                    new ModularSlot(inv, index).slotGroup("input")
                                        .filter(stack -> isItemValidForSlot(index, stack))))
                        .key(
                            'S',
                            new ItemSlot().slot(
                                new ModularSlot(inv, 2).slotGroup("input")
                                    .filter(stack -> isItemValidForSlot(2, stack))))
                        .key(
                            'O',
                            index -> new ItemSlot().slot(new ModularSlot(inv, index + 3).accessibility(false, true)))
                        .build()
                        .topRel(0.25f)
                        .alignX(Alignment.CENTER))
                .child(
                    new ProgressWidget().value(new DoubleSyncValue(this::getProgress))
                        .tooltipDynamic(richTooltip -> {
                            richTooltip.add(WailaUtils.getProgress(this));
                            richTooltip.markDirty();
                        })
                        .topRel(0.25f)
                        .leftRel(0.45f)
                        .texture(GuiTextures.PROGRESS_ARROW, 20)));
        panel.bindPlayerInventory();
        return panel;
    }
}
