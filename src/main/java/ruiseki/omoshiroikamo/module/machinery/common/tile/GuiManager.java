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
import ruiseki.omoshiroikamo.module.machinery.common.recipe.ProcessAgent;
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

        // Blueprint slot
        panel.child(
            new ItemSlot()
                .slot(
                    new ModularSlot(controller.getInventory(), TEMachineController.BLUEPRINT_SLOT)
                        .filter(stack -> stack != null && stack.getItem() instanceof ItemMachineBlueprint))
                .background(OKGuiTextures.EMPTY_SLOT)
                .pos(151, 8));

        // Redstone Mode Sync
        EnumSyncValue<RedstoneMode> redstoneMode = new EnumSyncValue<>(
            RedstoneMode.class,
            controller::getRedstoneMode,
            controller::setRedstoneMode);
        syncManager.syncValue("redstoneMode", redstoneMode);

        BooleanSyncValue redstonePowered = new BooleanSyncValue(
            controller::isRedstonePowered,
            controller::setRedstonePowered);
        syncManager.syncValue("redstonePowered", redstonePowered);

        // Redstone Control Button
        panel.child(new RedstoneModeWidget(redstoneMode).pos(151, 30));

        // Status display
        panel.child(
            IKey.dynamic(this::getStatusText)
                .asWidget()
                .pos(8, 25));

        // Error display (validation)
        panel.child(
            IKey.dynamic(this::getValidationErrorText)
                .asWidget()
                .pos(8, 35));

        // Sync progress values
        IntSyncValue progressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getProgress,
            controller.getProcessAgent()::setProgress);
        IntSyncValue maxProgressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getMaxProgress,
            controller.getProcessAgent()::setMaxProgress);

        syncManager.syncValue("processProgress", progressSyncer);
        syncManager.syncValue("processMaxProgress", maxProgressSyncer);

        // Progress bar
        panel.child(
            new ProgressWidget().value(new DoubleSyncValue(() -> (double) getProgressPercent()))
                .texture(OKGuiTextures.SOLID_UP_ARROW_ICON, 20)
                .pos(80, 45));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    /**
     * Determines the primary status message to display in the GUI.
     * Priority order:
     * 1. Blueprint missing
     * 2. Structure not formed
     * 3. Paused by redstone
     * 4. Processing status (running/waiting)
     * 5. Idle status (with error reasons)
     */
    private String getStatusText() {
        // Check for blueprint
        if (!hasBlueprint()) return "Insert Blueprint";

        // Check structure formation
        if (!controller.isFormed()) {
            if (hasValidationError()) return "Structure Mismatch";
            return "Structure Not Formed";
        }

        ProcessAgent agent = controller.getProcessAgent();

        // Check if machine is paused by redstone (highest priority for active machines)
        if (!controller.isRedstoneActive()) {
            return ErrorReason.PAUSED.getMessage();
        }

        // Check processing status
        if (agent.isRunning() || agent.isWaitingForOutput()) {
            return getProcessingStatusMessage(agent);
        }

        // Machine is idle - check for specific error reasons
        return getIdleStatusMessage();
    }

    /**
     * Returns the status message when machine is actively processing.
     */
    private String getProcessingStatusMessage(ProcessAgent agent) {
        ErrorReason lastError = controller.getLastProcessErrorReason();

        // Show energy error even during processing
        if (lastError == ErrorReason.NO_ENERGY) return ErrorReason.NO_ENERGY.getMessage();

        // Default to agent's status message (e.g., "Processing 45%")
        return agent.getStatusMessage(controller.getOutputPorts());
    }

    /**
     * Returns the status message when machine is idle.
     */
    private String getIdleStatusMessage() {
        // Check if no recipes are registered for this group
        if (RecipeLoader.getInstance()
            .getRecipes(controller.getRecipeGroup())
            .isEmpty()) {
            return ErrorReason.NO_RECIPES.getMessage();
        }

        ErrorReason lastError = controller.getLastProcessErrorReason();

        // Check specific error reasons
        if (lastError == ErrorReason.NO_MATCHING_RECIPE) return lastError.getMessage();
        if (lastError == ErrorReason.NO_ENERGY) return lastError.getMessage();
        if (lastError == ErrorReason.INPUT_MISSING) return lastError.getMessage();

        // Default idle state
        return "Idle";
    }

    /**
     * Returns validation error text for display below status.
     * Only shown when structure is not formed and there's a specific error.
     */
    private String getValidationErrorText() {
        if (!hasBlueprint()) return "";
        if (controller.isFormed()) return "";
        if (!hasValidationError()) return "";
        return controller.getLastValidationError();
    }

    private boolean hasBlueprint() {
        String name = controller.getCustomStructureName();
        return name != null && !name.isEmpty();
    }

    private boolean hasValidationError() {
        String error = controller.getLastValidationError();
        return error != null && !error.isEmpty();
    }

    private float getProgressPercent() {
        return controller.getProcessAgent()
            .getProgressPercent();
    }
}
