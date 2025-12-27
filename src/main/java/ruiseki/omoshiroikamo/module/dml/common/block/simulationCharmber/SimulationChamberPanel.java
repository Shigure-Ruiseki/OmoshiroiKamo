package ruiseki.omoshiroikamo.module.dml.common.block.simulationCharmber;

import static ruiseki.omoshiroikamo.core.client.gui.GuiTextures.DML_INVENTORY_TEXTURE;
import static ruiseki.omoshiroikamo.core.client.gui.GuiTextures.EMPTY_SLOT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.mojang.realmsclient.gui.ChatFormatting;

import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.api.redstone.RedstoneMode;
import ruiseki.omoshiroikamo.core.common.util.StringAnimator;
import ruiseki.omoshiroikamo.core.common.util.StringUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.dml.client.gui.widget.InventoryWidget;
import ruiseki.omoshiroikamo.module.dml.client.gui.widget.RedstoneModeButton;

public class SimulationChamberPanel extends ModularPanel {

    public static final AdaptableUITexture BASE_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/simulation_chamber")
        .imageSize(256, 256)
        .xy(0, 0, 216, 141)
        .adaptable(4)
        .tiled()
        .build();

    public static final AdaptableUITexture ENERGY_BAR = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/simulation_chamber")
        .imageSize(256, 256)
        .xy(25, 141, 7, 87)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture DATA_BAR = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/simulation_chamber")
        .imageSize(256, 256)
        .xy(18, 141, 7, 87)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture DATA_MODEL_SLOT = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/simulation_chamber")
        .imageSize(256, 256)
        .xy(0, 141, 18, 18)
        .adaptable(1)
        .tiled()
        .build();

    @Getter
    private final TESimulationChamber tileEntity;
    @Getter
    private final PosGuiData data;
    @Getter
    private final PanelSyncManager syncManager;
    @Getter
    private final UISettings settings;

    private static final int REDSTONE_DEACTIVATED_LINE_LENGTH = 28;
    private static final int BLINKING_CURSOR_SPEED = 16;
    private static final int ROW_SPACING = 12;
    private FloatSyncValue processSyncer;

    private final StringAnimator<AnimatedString> progressAnimator = new StringAnimator<>(); // Used to display
                                                                                            // simulation progress
    private final StringAnimator<AnimatedString> emptyDisplayAnimator = new StringAnimator<>(); // Used to display empty
                                                                                                // screen ("blinking //
                                                                                                // cursor")
    private final StringAnimator<AnimatedString> dataModelErrorAnimator = new StringAnimator<>(); // Used to display
                                                                                                  // error messages
    // relating to data model
    private final StringAnimator<AnimatedString> simulationErrorAnimator = new StringAnimator<>(); // Used to display
                                                                                                   // other errors (no
    // polymer/energy, output full)

    private ItemStack dataModel;

    private DataModelError dataModelError = DataModelError.NONE; // Error with model (missing/faulty)?
    private SimulationError simulationError = SimulationError.NONE; // Other error (missing polymer/low energy/output
                                                                    // full)?
    private boolean redstoneDeactivated = false; // Is simulation chamber deactivated by redstone signal?
    private int currentIteration; // Saves data model's current iteration so we don't update display if iteration hasn't
                                  // changed
    private boolean currentPristineSuccess; // Saves data model's current pristine success state so we don't update
                                            // display if iteration hasn't changed

    protected int currentTick = 0; // Ticks since GUI was opened
    protected float lastRedrawTime = 0; // Time when GUI was last drawn (= ticks + partial tick)
    protected float deltaTime = 0; // Time since last redraw (partial ticks)

    protected final FontRenderer fontRenderer;

    public SimulationChamberPanel(TESimulationChamber tileEntity, PosGuiData data, PanelSyncManager syncManager,
        UISettings settings) {
        super("simulation_chamber_gui");

        this.tileEntity = tileEntity;
        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;

        dataModel = tileEntity.getDataModel();
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
        prepareStringAnimators();

        size(216, 234);
        this.child(InventoryWidget.playerInventory(true));

        syncManager.syncValue("energySyncer", new IntSyncValue(tileEntity::getEnergyStored));
        syncManager.syncValue("maxEnergySyncer", new IntSyncValue(tileEntity::getMaxEnergyStored));
        processSyncer = new FloatSyncValue(tileEntity::getProgress, tileEntity::setProgress);
        syncManager.syncValue("processSyncer", processSyncer);
        EnumSyncValue<RedstoneMode> redstoneModeSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            tileEntity::getRedstoneMode,
            tileEntity::setRedstoneMode);
        syncManager.syncValue("redstoneModeSyncer", redstoneModeSyncer);

        this.child(new ProgressWidget().value(new DoubleSyncValue(() -> {
            if (!tileEntity.hasDataModel()) return 0.0;

            if (DataModel.isMaxTier(dataModel)) return 1.0;

            double current = DataModel.getCurrentTierSimulationCountWithKills(dataModel);
            double max = DataModel.getTierRoof(dataModel);

            if (max <= 0) return 0.0;

            return Math.min(1.0, Math.max(0.0, current / max));
        }))
            .texture(null, DATA_BAR, 87)
            .direction(ProgressWidget.Direction.UP)
            .size(7, 87)
            .pos(6, 48)
            .tooltipAutoUpdate(true)
            .tooltipDynamic(tooltip -> {
                if (tileEntity.hasDataModel()) {
                    if (!DataModel.isMaxTier(dataModel)) {
                        String currentData = String
                            .valueOf(DataModel.getCurrentTierSimulationCountWithKills(dataModel));
                        String maxData = String.valueOf(DataModel.getTierRoof(dataModel));
                        tooltip.addLine(
                            LibMisc.LANG
                                .localize("gui.simulation_chamber.tooltip.model_data", currentData + "/" + maxData));
                    } else {
                        tooltip.add(LibMisc.LANG.localize("gui.simulation_chamber.tooltip.model_max"));
                    }
                    if (!DataModel.canSimulate(dataModel)) {
                        tooltip.addLine(
                            ChatFormatting.RED
                                + LibMisc.LANG.localize("gui.simulation_chamber.tooltip.model_cannot_simulate")
                                + ChatFormatting.RESET);
                    }
                } else {
                    tooltip.addLine(LibMisc.LANG.localize("gui.simulation_chamber.tooltip.model_missing"));
                }
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            }));

        this.child(
            new ProgressWidget()
                .value(
                    new DoubleSyncValue(() -> (double) tileEntity.getEnergyStored() / tileEntity.getMaxEnergyStored()))
                .texture(null, ENERGY_BAR, 87)
                .direction(ProgressWidget.Direction.UP)
                .size(7, 87)
                .pos(203, 48)
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    tooltip.addLine(tileEntity.getEnergyStored() + "/" + tileEntity.getMaxEnergyStored() + " RF");
                    tooltip.addLine(
                        LibMisc.LANG
                            .localize("gui.simulation_chamber.tooltip.sim_cost", tileEntity.getCraftingEnergyCost()));
                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                }));

        this.child(
            new RedstoneModeButton(redstoneModeSyncer).pos(-20, 20)
                .excludeAreaInRecipeViewer());

        this.child(
            new ItemSlot().slot(new ModularSlot(tileEntity.inputDataModel, 0))
                .background(EMPTY_SLOT)
                .background(EMPTY_SLOT)
                .pos(-20, 0)
                .excludeAreaInRecipeViewer());

        this.child(
            new ItemSlot().slot(new ModularSlot(tileEntity.inputPolymer, 0))
                .background(EMPTY_SLOT)
                .background(EMPTY_SLOT)
                .pos(183, 6)
                .excludeAreaInRecipeViewer());

        this.child(
            new ItemSlot().slot(new ModularSlot(tileEntity.outputLiving, 0).accessibility(false, true))
                .background(EMPTY_SLOT)
                .background(EMPTY_SLOT)
                .pos(173, 26)
                .excludeAreaInRecipeViewer());

        this.child(
            new ItemSlot().slot(new ModularSlot(tileEntity.outputPristine, 0).accessibility(false, true))
                .background(EMPTY_SLOT)
                .background(EMPTY_SLOT)
                .pos(193, 26)
                .excludeAreaInRecipeViewer());
    }

    private void updatePanelString() {
        currentTick++;
        dataModel = tileEntity.getDataModel();

        if (!tileEntity.hasDataModel()) {
            if (dataModelError == DataModelError.MISSING) return;

            dataModelErrorAnimator.setString(
                AnimatedString.ERROR_DATA_MODEL_TEXT_1,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.no_data_model_1"));
            dataModelErrorAnimator.setString(
                AnimatedString.ERROR_DATA_MODEL_TEXT_2,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.no_data_model_2"));
            dataModelErrorAnimator.reset();
            dataModelError = DataModelError.MISSING;
            return;
        }

        if (!tileEntity.canDataModelSimulate()) {
            if (dataModelError == DataModelError.FAULTY) return;

            dataModelErrorAnimator.setString(
                AnimatedString.ERROR_DATA_MODEL_TEXT_1,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.model_cannot_simulate_1"));
            dataModelErrorAnimator.setString(
                AnimatedString.ERROR_DATA_MODEL_TEXT_2,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.model_cannot_simulate_2"));
            dataModelErrorAnimator.reset();
            dataModelError = DataModelError.FAULTY;
            return;
        }

        dataModelError = DataModelError.NONE;
        emptyDisplayAnimator.reset();
        redstoneDeactivated = !tileEntity.isRedstoneActive();
        if (redstoneDeactivated) {
            return;
        }

        if (!tileEntity.hasPolymerClay() && !tileEntity.isCrafting()) {
            // Polymer error only shown if simulation hasn't started already
            // (remaining polymer can be removed while simulation is running, which should not cause an error display)
            if (simulationError == SimulationError.POLYMER) return;

            simulationErrorAnimator.setString(
                AnimatedString.ERROR_SIMULATION_TEXT,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.no_polymer"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.POLYMER;
            return;
        }

        if (!tileEntity.hasEnergyForCrafting() || !tileEntity.isCrafting() && !tileEntity.canStartCrafting()) {
            if (simulationError == SimulationError.ENERGY) {
                return;
            }

            simulationErrorAnimator.setString(
                AnimatedString.ERROR_SIMULATION_TEXT,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.no_energy"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.ENERGY;
            return;
        }

        if ((tileEntity.isPristineMatterOutputFull() || tileEntity.isLivingMatterOutputFull())) {
            if (simulationError == SimulationError.OUTPUT) {
                return;
            }

            simulationErrorAnimator.setString(
                AnimatedString.ERROR_SIMULATION_TEXT,
                LibMisc.LANG.localize("gui.simulation_chamber.error_text.output_full"));
            simulationErrorAnimator.reset();
            simulationError = SimulationError.OUTPUT;
            return;
        }

        simulationError = SimulationError.NONE;

        int iteration = DataModel.getTotalSimulationCount(dataModel) + 1;
        boolean pristineSuccess = tileEntity.isPristineSuccess();
        if ((iteration == currentIteration) && (pristineSuccess == currentPristineSuccess)) {
            return; // Already updated, no need to do it again
        }

        currentIteration = iteration;
        currentPristineSuccess = pristineSuccess;

        String iterationString = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.iteration", iteration);
        progressAnimator.setString(AnimatedString.SIMULATION_ITERATION, iterationString);

        String pristineString = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.pristine");
        String successString = ChatFormatting.GREEN
            + LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.pristine_success")
            + ChatFormatting.RESET;
        String failureString = ChatFormatting.RED
            + LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.pristine_failure")
            + ChatFormatting.RESET;
        pristineString += " " + (pristineSuccess ? successString : failureString);
        progressAnimator.setString(AnimatedString.SIMULATION_PRISTINE, pristineString);
        progressAnimator.reset();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updatePanelString();
    }

    private void drawInfoboxText(float advanceAmount, int left, int top) {
        List<String> strings = new ArrayList<>();

        if (dataModelError == DataModelError.NONE) {
            String tier = LibMisc.LANG.localize("gui.simulation_chamber.data_model_info.tier");
            String iterations = LibMisc.LANG.localize("gui.simulation_chamber.data_model_info.iterations");
            String pristine = LibMisc.LANG.localize("gui.simulation_chamber.data_model_info.pristine");
            strings.add(tier + ": " + DataModel.getTierName(dataModel));
            strings.add(iterations + ": " + DataModel.getTotalSimulationCount(dataModel));
            strings.add(pristine + ": " + DataModel.getPristineChance(dataModel));
        } else {
            dataModelErrorAnimator.advance(advanceAmount);
            strings = dataModelErrorAnimator.getCurrentStrings();
        }

        int y = top;
        for (String s : strings) {
            fontRenderer.drawString(s, left, y, Color.WHITE.main);
            y += ROW_SPACING;
        }
    }

    private void drawConsoleText(float advanceAmount, int left, int top) {
        List<String> strings;

        if (dataModelError != DataModelError.NONE) {
            emptyDisplayAnimator.advance(advanceAmount);
            strings = emptyDisplayAnimator.getCurrentStrings();
        } else if (redstoneDeactivated) {
            strings = new ArrayList<>();
            strings.add(
                ChatFormatting.RED + StringUtils.getDashedLine(REDSTONE_DEACTIVATED_LINE_LENGTH)
                    + ChatFormatting.RESET);
            strings.add(
                ChatFormatting.RED + StringUtils.pad(
                    LibMisc.LANG.localize("gui.simulation_chamber.redstone_deactivated_1"),
                    REDSTONE_DEACTIVATED_LINE_LENGTH) + ChatFormatting.RESET);
            strings.add(
                ChatFormatting.RED + StringUtils.pad(
                    LibMisc.LANG.localize("gui.simulation_chamber.redstone_deactivated_2"),
                    REDSTONE_DEACTIVATED_LINE_LENGTH) + ChatFormatting.RESET);
            strings.add(
                ChatFormatting.RED + StringUtils.getDashedLine(REDSTONE_DEACTIVATED_LINE_LENGTH)
                    + ChatFormatting.RESET);
        } else if (simulationError != SimulationError.NONE) {
            simulationErrorAnimator.advance(advanceAmount);
            strings = simulationErrorAnimator.getCurrentStrings();
        } else {
            progressAnimator.goToRelativePosition(processSyncer.getFloatValue());
            strings = progressAnimator.getCurrentStrings();
        }

        int y = top;
        for (String s : strings) {
            fontRenderer.drawString(s, left, y, Color.WHITE.main);
            y += ROW_SPACING;
        }
    }

    private void prepareStringAnimators() {
        String blinkingCursor = " _"; // Space so this looks like it's blinking

        String simulationLaunching = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.launching");
        String simulationLoading = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.loading");
        String simulationAssessing = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.assessing");
        String simulationEngaged = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.engaged");
        String simulationProcessing = LibMisc.LANG.localize("gui.simulation_chamber.simulation_text.processing")
            + " . . . . ."; // Padding so this line is displayed a little longer
        String error = LibMisc.LANG.localize("gui.simulation_chamber.error_text.error");

        progressAnimator.addString(AnimatedString.SIMULATION_LAUNCHING, simulationLaunching);
        progressAnimator.addString(AnimatedString.SIMULATION_ITERATION, ""); // gets set in update method
        progressAnimator.addString(AnimatedString.SIMULATION_LOADING, simulationLoading);
        progressAnimator.addString(AnimatedString.SIMULATION_ASSESSING, simulationAssessing);
        progressAnimator.addString(AnimatedString.SIMULATION_ENGAGED, simulationEngaged);
        progressAnimator.addString(AnimatedString.SIMULATION_PRISTINE, ""); // gets set in update method
        progressAnimator.addString(AnimatedString.SIMULATION_PROCESSING, simulationProcessing);

        emptyDisplayAnimator.addString(AnimatedString.UNDERLINE, blinkingCursor, BLINKING_CURSOR_SPEED, true);

        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_HEADING, error);
        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_TEXT_1, ""); // gets set in update method
        dataModelErrorAnimator.addString(AnimatedString.ERROR_DATA_MODEL_TEXT_2, ""); // gets set in update method

        simulationErrorAnimator.addString(AnimatedString.ERROR_SIMULATION_HEADING, error);
        simulationErrorAnimator.addString(AnimatedString.ERROR_SIMULATION_TEXT, ""); // gets set in update method
        simulationErrorAnimator.addString(AnimatedString.UNDERLINE, blinkingCursor, BLINKING_CURSOR_SPEED, true);
    }

    private void getDeltaTime(float partialTicks) {
        float currentPartial = currentTick + partialTicks;
        deltaTime = currentPartial - lastRedrawTime;
        lastRedrawTime = currentPartial;
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        DML_INVENTORY_TEXTURE.draw(19, this.getArea().height - 91, 177, 91);
        BASE_TEXTURE.draw(0, 0, 216, 141);
        DATA_MODEL_SLOT.draw(-20, 0, 18, 18);
        getDeltaTime(context.getPartialTicks());

        drawConsoleText(deltaTime, 22, 52);
        drawInfoboxText(deltaTime, 9, 9);
    }

    public enum AnimatedString {
        UNDERLINE,
        SIMULATION_LAUNCHING,
        SIMULATION_ITERATION,
        SIMULATION_LOADING,
        SIMULATION_ASSESSING,
        SIMULATION_ENGAGED,
        SIMULATION_PRISTINE,
        SIMULATION_PROCESSING,
        ERROR_DATA_MODEL_HEADING,
        ERROR_DATA_MODEL_TEXT_1,
        ERROR_DATA_MODEL_TEXT_2,
        ERROR_SIMULATION_HEADING,
        ERROR_SIMULATION_TEXT
    }

    private enum DataModelError {
        NONE,
        MISSING,
        FAULTY
    }

    private enum SimulationError {
        NONE,
        ENERGY,
        POLYMER,
        OUTPUT
    }
}
