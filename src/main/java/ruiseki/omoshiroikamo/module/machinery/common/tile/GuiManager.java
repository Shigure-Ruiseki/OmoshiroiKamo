package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.List;
import java.util.Set;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.ErrorReason;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.ModularRecipe;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
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

        // Info Display (Black Rectangle)
        panel.child(new Column().background(new IDrawable() {

            @Override
            public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
                GuiDraw.drawRect(x, y, width, height, 0xFF000000);
            }
        })
            .pos(7, 14)
            .size(138, 60)
            .padding(4)
            .child(
                IKey.dynamic(() -> EnumChatFormatting.WHITE + this.getStructureNameText())
                    .asWidget())
            .child(
                IKey.dynamic(() -> EnumChatFormatting.WHITE + this.getStatusText())
                    .asWidget())
            .child(
                IKey.dynamic(() -> EnumChatFormatting.WHITE + this.getValidationErrorText())
                    .asWidget()));

        // Blueprint slot (Helper function)
        addBlueprintSlot(panel, 151, 8);

        // Redstone Control Button (Helper function)
        addRedstoneButton(panel, syncManager, 151, 30);

        // Sync progress values
        IntSyncValue progressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getProgress,
            controller.getProcessAgent()::setProgress);
        IntSyncValue maxProgressSyncer = new IntSyncValue(
            controller.getProcessAgent()::getMaxProgress,
            controller.getProcessAgent()::setMaxProgress);

        syncManager.syncValue("processProgress", progressSyncer);
        syncManager.syncValue("processMaxProgress", maxProgressSyncer);

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    private void addBlueprintSlot(ModularPanel panel, int x, int y) {

        panel.child(
            new ItemSlot()
                .slot(
                    new ModularSlot(controller.getInventory(), TEMachineController.BLUEPRINT_SLOT)
                        .filter(stack -> stack != null && stack.getItem() instanceof ItemMachineBlueprint))
                .background(
                    (c, x1, y1, w, h, t) -> ((ModularGuiContext) c).getTheme()
                        .getWidgetTheme(IThemeApi.ITEM_SLOT)
                        .getTheme()
                        .getBackground()
                        .draw(c, x1, y1, w, h, t))
                .pos(x, y));
    }

    private void addRedstoneButton(ModularPanel panel, PanelSyncManager syncManager, int x, int y) {

        EnumSyncValue<RedstoneMode> redstoneMode = new EnumSyncValue<>(
            RedstoneMode.class,
            controller::getRedstoneMode,
            controller::setRedstoneMode);
        syncManager.syncValue("redstoneMode", redstoneMode);

        BooleanSyncValue redstonePowered = new BooleanSyncValue(
            controller::isRedstonePowered,
            controller::setRedstonePowered);
        syncManager.syncValue("redstonePowered", redstonePowered);

        panel.child(new RedstoneModeWidget(redstoneMode).pos(x, y));
    }

    private String getStructureNameText() {
        if (controller.isFormed()) {
            String name = controller.getCustomStructureName();
            if (name != null && !name.isEmpty()) {
                // TODO: return LibMisc.LANG.localize("structure." + name + ".name");
                return name;
            }
        }
        return "";
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
        if (!hasBlueprint()) return LibMisc.LANG.localize("gui.status.insert_blueprint");

        if (!controller.isFormed()) {
            if (hasValidationError()) return LibMisc.LANG.localize("gui.status.structure_mismatch");
            return LibMisc.LANG.localize("gui.status.structure_not_formed");
        }

        if (!controller.isRedstoneActive()) {
            return LibMisc.LANG.localize(ErrorReason.PAUSED.getUnlocalizedName());
        }

        ProcessAgent agent = controller.getProcessAgent();
        if (agent.isRunning() || agent.isWaitingForOutput()) {
            return getProcessingStatusMessage(agent);
        }

        return getIdleStatusMessage();
    }

    /**
     * Returns the status message when machine is actively processing.
     */
    private String getProcessingStatusMessage(ProcessAgent agent) {
        ErrorReason lastError = controller.getLastProcessErrorReason();

        // Show energy error even during processing
        if (lastError == ErrorReason.NO_ENERGY) return LibMisc.LANG.localize(lastError.getUnlocalizedName());

        if (agent.isRunning() && !agent.isWaitingForOutput()) {
            if (agent.getMaxProgress() <= 0) return LibMisc.LANG.localize("gui.status.processing", 0);
            int percent = (int) (agent.getProgressPercent() * 100);
            return LibMisc.LANG.localize("gui.status.processing", percent);
        }

        if (agent.isWaitingForOutput()) {
            return LibMisc.LANG.localize("gui.status.output_full", diagnoseBlockedOutputs(controller.getOutputPorts()));
        }

        return LibMisc.LANG.localize("gui.status.idle");
    }

    /**
     * Returns the status message when machine is idle.
     */
    private String getIdleStatusMessage() {
        if (RecipeLoader.getInstance()
            .getRecipes(controller.getRecipeGroup())
            .isEmpty()) {
            return LibMisc.LANG.localize(ErrorReason.NO_RECIPES.getUnlocalizedName());
        }

        ErrorReason lastError = controller.getLastProcessErrorReason();

        // Check specific error reasons
        if (lastError == ErrorReason.NO_MATCHING_RECIPE || lastError == ErrorReason.NO_ENERGY
            || lastError == ErrorReason.INPUT_MISSING) {
            return LibMisc.LANG.localize(lastError.getUnlocalizedName());
        }

        // Default idle state
        return LibMisc.LANG.localize("gui.status.idle");
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

    /**
     * Diagnose which output types are blocked when waiting for output.
     */
    private String diagnoseBlockedOutputs(List<IModularPort> outputPorts) {
        ProcessAgent agent = controller.getProcessAgent();
        ModularRecipe currentRecipe = agent.getCurrentRecipe();

        if (currentRecipe != null) {
            StringBuilder blocked = new StringBuilder();
            for (IRecipeOutput output : currentRecipe.getOutputs()) {
                if (!output.process(outputPorts, true)) {
                    if (blocked.length() > 0) blocked.append(", ");
                    blocked.append(
                        LibMisc.LANG.localize(
                            "gui.port_type." + output.getPortType()
                                .name()));
                }
            }
            if (blocked.length() > 0) return blocked.toString();
        }

        // Fallback: use cached output types
        Set<IPortType.Type> cachedTypes = agent.getCachedOutputTypes();
        if (!cachedTypes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (IPortType.Type type : cachedTypes) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(LibMisc.LANG.localize("gui.port_type." + type.name()));
            }
            return sb.toString();
        }

        return LibMisc.LANG.localize("gui.error.unknown");
    }

}
