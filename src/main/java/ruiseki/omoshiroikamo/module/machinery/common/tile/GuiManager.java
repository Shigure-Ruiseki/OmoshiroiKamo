package ruiseki.omoshiroikamo.module.machinery.common.tile;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.recipe.ErrorReason;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;
import ruiseki.omoshiroikamo.module.machinery.common.item.ItemMachineBlueprint;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

/**
 * Handles GUI construction and display logic for {@link TEMachineController}.
 */
public class GuiManager {

    private final TEMachineController controller;

    public GuiManager(TEMachineController controller) {
        this.controller = controller;
    }

    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("machine_controller_gui");

        // Title
        panel.child(new TileWidget(controller.getLocalizedName()));

        // Blueprint slot (right side)
        panel.child(
            new ItemSlot()
                .slot(
                    new ModularSlot(controller.getInventory(), TEMachineController.BLUEPRINT_SLOT)
                        .filter(stack -> stack != null && stack.getItem() instanceof ItemMachineBlueprint))
                .background(OKGuiTextures.EMPTY_SLOT)
                .background(OKGuiTextures.EMPTY_SLOT)
                .pos(151, 8));

        // Redstone Mode Sync
        EnumSyncValue<RedstoneMode> redstoneMode = new EnumSyncValue<>(
            RedstoneMode.class,
            controller::getRedstoneMode,
            controller::setRedstoneMode);
        syncManager.syncValue("redstoneMode", redstoneMode);

        // Redstone Control Button
        panel.child(new RedstoneModeWidget(redstoneMode).pos(151, 30));

        // Status display
        panel.child(
            IKey.dynamic(this::getStatusText)
                .asWidget()
                .pos(8, 25));

        // Error display (validation)
        panel.child(
            IKey.dynamic(this::getErrorText)
                .asWidget()
                .pos(8, 35));

        IntSyncValue progressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getProgress,
            controller.getProcessAgent()::setProgress);
        IntSyncValue maxProgressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getMaxProgress,
            controller.getProcessAgent()::setMaxProgress);
        BooleanSyncValue runningSyncer = new BooleanSyncValue(
            controller.getProcessAgent()::isRunning,
            controller.getProcessAgent()::setRunning);
        BooleanSyncValue waitingSyncer = new BooleanSyncValue(
            controller.getProcessAgent()::isWaitingForOutput,
            controller.getProcessAgent()::setWaitingForOutput);

        syncManager.syncValue("processProgressSyncer", progressSyncer);
        syncManager.syncValue("processMaxSyncer", maxProgressSyncer);
        syncManager.syncValue("processRunningSyncer", runningSyncer);
        syncManager.syncValue("processWaitingSyncer", waitingSyncer);

        // Progress bar
        panel.child(
            new ProgressWidget().value(new DoubleSyncValue(() -> (double) getProgressPercent()))
                .texture(OKGuiTextures.SOLID_UP_ARROW_ICON, 20)
                .pos(80, 45));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    private String getStatusText() {
        if (controller.getCustomStructureName() == null || controller.getCustomStructureName()
            .isEmpty()) {
            return "Insert Blueprint";
        }
        if (!controller.isFormed()) {
            if (controller.getLastValidationError() != null && !controller.getLastValidationError()
                .isEmpty()) {
                return "Structure Mismatch";
            }
            return "Structure Not Formed";
        }
        if (controller.getProcessAgent()
            .isRunning()
            || controller.getProcessAgent()
                .isWaitingForOutput()) {
            if (controller.getLastProcessErrorReason() == ErrorReason.NO_ENERGY) {
                return ErrorReason.NO_ENERGY.getMessage();
            }
            return controller.getProcessAgent()
                .getStatusMessage(controller.getOutputPorts());
        }

        ErrorReason reason = getIdleErrorReason();
        if (reason == ErrorReason.NONE) {
            return "Idle";
        }
        return reason.getMessage();
    }

    private ErrorReason getIdleErrorReason() {
        if (RecipeLoader.getInstance()
            .getRecipes(controller.getRecipeGroup())
            .isEmpty()) {
            return ErrorReason.NO_RECIPES;
        }
        if (controller.getLastProcessErrorReason() == ErrorReason.NO_MATCHING_RECIPE) {
            return ErrorReason.NO_MATCHING_RECIPE;
        }
        if (controller.getLastProcessErrorReason() == ErrorReason.NO_ENERGY) {
            return ErrorReason.NO_ENERGY;
        }
        if (!controller.isRedstoneActive()) {
            return ErrorReason.PAUSED;
        }
        return ErrorReason.IDLE;
    }

    private String getErrorText() {
        if (controller.getCustomStructureName() == null || controller.getCustomStructureName()
            .isEmpty()) return "";
        if (controller.isFormed()) return "";
        if (controller.getLastValidationError() == null || controller.getLastValidationError()
            .isEmpty()) return "";
        return controller.getLastValidationError();
    }

    private float getProgressPercent() {
        return controller.getProcessAgent()
            .getProgressPercent();
    }
}
