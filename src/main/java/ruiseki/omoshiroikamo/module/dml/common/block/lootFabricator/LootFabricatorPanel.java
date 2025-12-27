package ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator;

import static ruiseki.omoshiroikamo.core.client.gui.GuiTextures.DML_INVENTORY_TEXTURE;
import static ruiseki.omoshiroikamo.core.client.gui.GuiTextures.EMPTY_SLOT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.api.redstone.RedstoneMode;
import ruiseki.omoshiroikamo.core.client.gui.widget.ItemStackDrawable;
import ruiseki.omoshiroikamo.core.common.util.MathUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.dml.client.gui.widget.InventoryWidget;
import ruiseki.omoshiroikamo.module.dml.client.gui.widget.RedstoneModeButton;

public class LootFabricatorPanel extends ModularPanel {

    public static final AdaptableUITexture BASE_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/loot_fabricator")
        .imageSize(256, 256)
        .xy(0, 0, 177, 83)
        .adaptable(4)
        .tiled()
        .build();

    public static final AdaptableUITexture ENERGY_BAR = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/loot_fabricator")
        .imageSize(256, 256)
        .xy(0, 83, 7, 71)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture PROGRESS_BAR = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/loot_fabricator")
        .imageSize(256, 256)
        .xy(7, 83, 6, 35)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture ERROR_BAR = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/loot_fabricator")
        .imageSize(256, 256)
        .xy(13, 83, 6, 35)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture LEFT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_page_select")
        .imageSize(256, 256)
        .xy(0, 12, 31, 12)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture RIGHT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_page_select")
        .imageSize(256, 256)
        .xy(31, 12, 31, 12)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture HOVER_LEFT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_page_select")
        .imageSize(256, 256)
        .xy(0, 24, 31, 12)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture HOVER_RIGHT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_page_select")
        .imageSize(256, 256)
        .xy(31, 24, 31, 12)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture BUTTON_SELECT = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_select")
        .imageSize(256, 256)
        .xy(18, 18, 18, 18)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture HOVER_BUTTON_SELECT = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_select")
        .imageSize(256, 256)
        .xy(18, 36, 18, 18)
        .adaptable(1)
        .tiled()
        .build();

    @Getter
    private final TELootFabricator tileEntity;
    @Getter
    private final PosGuiData data;
    @Getter
    private final PanelSyncManager syncManager;
    @Getter
    private final UISettings settings;

    private ModelRegistryItem pristineMatterData;

    private ImmutableList<ItemStack> lootItemList;

    @Getter
    @Setter
    private int currentOutputItemPage = -1;
    @Getter
    @Setter
    private int totalOutputItemPages = 0;

    private ItemStack outputItem;

    private final List<ButtonItemSelect> outputSelectButtons = new ArrayList<>();
    private final ButtonPageSelect nextPageButton;
    private final ButtonPageSelect prevPageButton;
    private final ButtonItemDeselect deselectButton;
    private ProgressWidget processBar;
    private ProgressWidget errorBar;

    private CraftingError craftingError = CraftingError.NONE;

    private static final int ITEMS_PER_PAGE = 9;
    private static final int OUTPUT_SELECT_LIST_GUTTER = 1;
    private static final int OUTPUT_SELECT_BUTTON_SIZE = 18;
    private GenericSyncValue<ItemStack> outputSyncer;

    public LootFabricatorPanel(TELootFabricator tileEntity, PosGuiData data, PanelSyncManager syncManager,
        UISettings settings) {
        super("loot_fabricator_gui");
        this.tileEntity = tileEntity;
        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;

        this.size(176, 176);
        this.child(InventoryWidget.playerInventory(true));

        this.outputItem = tileEntity.getOutputItem();

        syncManager.syncValue("itemPageSyncer", new IntSyncValue(this::getCurrentOutputItemPage));
        syncManager.syncValue("energySyncer", new IntSyncValue(tileEntity::getEnergyStored));
        syncManager.syncValue("maxEnergySyncer", new IntSyncValue(tileEntity::getMaxEnergyStored));
        FloatSyncValue processSyncer = new FloatSyncValue(tileEntity::getProgress, tileEntity::setProgress);
        syncManager.syncValue("processSyncer", processSyncer);
        outputSyncer = GenericSyncValue.forItem(tileEntity::getOutputItem, tileEntity::setOutputItem);
        syncManager.syncValue("outputSyncer", outputSyncer);
        EnumSyncValue<RedstoneMode> redstoneModeSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            tileEntity::getRedstoneMode,
            tileEntity::setRedstoneMode);
        syncManager.syncValue("redstoneModeSyncer", redstoneModeSyncer);

        deselectButton = new ButtonItemDeselect(this).pos(77, 5);
        prevPageButton = new ButtonPageSelect(Direction.PREV, this).pos(12, 66)
            .tooltip(tooltip -> tooltip.addLine(LibMisc.LANG.localize("gui.button_page_select.prev")));
        nextPageButton = new ButtonPageSelect(Direction.NEXT, this).pos(43, 66)
            .tooltip(tooltip -> tooltip.addLine(LibMisc.LANG.localize("gui.button_page_select.prev")));

        this.child(deselectButton)
            .child(prevPageButton)
            .child(nextPageButton);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int x = 15 + col * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER);
                int y = 8 + row * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER);

                ButtonItemSelect button = new ButtonItemSelect(this).pos(x, y);
                outputSelectButtons.add(button);
                this.child(button);
            }
        }

        processBar = new ProgressWidget()
            .value(new DoubleSyncValue(processSyncer::getDoubleValue, processSyncer::setDoubleValue))
            .texture(null, PROGRESS_BAR, 35)
            .direction(ProgressWidget.Direction.UP)
            .size(6, 35)
            .pos(83, 23)
            .tooltipAutoUpdate(true)
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(LibMisc.LANG.localize("gui.progress", tileEntity.getProgress() * 100f));
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            });
        processBar.setEnabled(false);

        errorBar = new ProgressWidget().value(new DoubleSyncValue(() -> 1.0f))
            .texture(null, ERROR_BAR, 35)
            .size(6, 35)
            .pos(83, 23)
            .tooltipAutoUpdate(true)
            .tooltipDynamic(tooltip -> {
                tooltip.addLine(getErrorTooltip());
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            });
        errorBar.setEnabled(false);

        this.child(processBar);
        this.child(errorBar);
        this.child(
            new ProgressWidget()
                .value(
                    new DoubleSyncValue(() -> (double) tileEntity.getEnergyStored() / tileEntity.getMaxEnergyStored()))
                .texture(null, ENERGY_BAR, 71)
                .direction(ProgressWidget.Direction.UP)
                .size(7, 71)
                .pos(3, 6)
                .tooltipAutoUpdate(true)
                .tooltipDynamic(tooltip -> {
                    tooltip.addLine(tileEntity.getEnergyStored() + "/" + tileEntity.getMaxEnergyStored() + " RF");
                    tooltip.addLine(
                        LibMisc.LANG
                            .localize("gui.loot_fabricator.tooltip.crafting_cost", tileEntity.getCraftingEnergyCost()));
                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                }));

        this.child(
            new ItemSlot().background(EMPTY_SLOT)
                .hoverBackground(EMPTY_SLOT)
                .slot(new ModularSlot(tileEntity.input, 0).slotGroup("input"))
                .pos(77, 61));
        syncManager.registerSlotGroup("input", 1, true);

        this.child(
            new RedstoneModeButton(redstoneModeSyncer).pos(-21, 0)
                .excludeAreaInRecipeViewer());

        this.child(
            SlotGroupWidget.builder()
                .matrix("OOOO", "OOOO", "OOOO", "OOOO")
                .key(
                    'O',
                    index -> new ItemSlot().background(EMPTY_SLOT)
                        .hoverBackground(EMPTY_SLOT)
                        .slot(new ModularSlot(tileEntity.output, index).accessibility(false, true)))
                .build()
                .pos(99, 6));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        updateLootItem();
    }

    public void updateLootItem() {
        ModelRegistryItem lootFabData = tileEntity.getPristineMatterData();
        if (lootFabData != pristineMatterData) {
            pristineMatterData = lootFabData;
            resetOutputData();
        }

        if (!tileEntity.isRedstoneActive()) {
            craftingError = CraftingError.REDSTONE;
        } else if (!tileEntity.hasPristineMatter()) {
            craftingError = CraftingError.NO_PRISTINE;
        } else if (outputItem == null) {
            craftingError = CraftingError.NO_OUTPUT_SELECTED;
        } else if (!tileEntity.hasRoomForOutput()) {
            craftingError = CraftingError.OUTPUT_FULL;
        } else if (!tileEntity.hasEnergyForCrafting()) {
            craftingError = CraftingError.NO_ENERGY;
        } else {
            craftingError = CraftingError.NONE;
        }

        boolean hasError = craftingError != CraftingError.NONE;

        processBar.setEnabled(!hasError);
        errorBar.setEnabled(hasError);
    }

    private void resetOutputData() {
        if (pristineMatterData == null) {
            lootItemList = ImmutableList.of();
            currentOutputItemPage = -1;
            totalOutputItemPages = 0;
            setPageButtonsEnabled(false);
        } else {
            lootItemList = ImmutableList.copyOf(
                tileEntity.getPristineMatterData()
                    .getLootItems());
            if (outputItem != null) {
                int currentOutputItemIndex = pristineMatterData.getLootItemIndex(outputItem);
                currentOutputItemPage = currentOutputItemIndex / ITEMS_PER_PAGE;
            } else {
                currentOutputItemPage = 0;
            }
            totalOutputItemPages = MathUtils.divideAndRoundUp(lootItemList.size(), ITEMS_PER_PAGE);
            setPageButtonsEnabled(totalOutputItemPages > 1);
        }
        updateOutputSelectButtons();
    }

    private void updateOutputSelectButtons() {
        if (currentOutputItemPage < 0) {
            outputSelectButtons.forEach(b -> b.setItem(-1, null, false));
            return;
        }

        int pageStart = currentOutputItemPage * ITEMS_PER_PAGE;

        for (int i = 0; i < ITEMS_PER_PAGE; i++) {
            int absoluteIndex = pageStart + i;
            ButtonItemSelect button = outputSelectButtons.get(i);

            if (absoluteIndex < lootItemList.size()) {
                ItemStack stack = lootItemList.get(absoluteIndex);
                button.setItem(absoluteIndex, stack, ItemUtils.areStacksEqual(stack, outputItem));
            } else {
                button.setItem(-1, null, false);
            }
        }
    }

    private void setOutputItem(int index) {
        for (int i = 0; i < outputSelectButtons.size(); i++) {
            outputSelectButtons.get(i)
                .setSelected(index != -1 && (i == (index % ITEMS_PER_PAGE)));
        }

        if (index < 0 || index >= lootItemList.size()) {
            outputItem = null;
        } else {
            outputItem = lootItemList.get(index);
        }

        deselectButton.setDisplayStack(outputItem);
        outputSyncer.setValue(outputItem);
    }

    private void setPageButtonsEnabled(boolean enabled) {
        prevPageButton.setEnabled(enabled);
        nextPageButton.setEnabled(enabled);
    }

    private void changePage(int delta) {
        if (totalOutputItemPages <= 0) return;

        currentOutputItemPage = (currentOutputItemPage + delta + totalOutputItemPages) % totalOutputItemPages;

        updateOutputSelectButtons();
    }

    private String getErrorTooltip() {
        switch (craftingError) {
            case REDSTONE:
                return LibMisc.LANG.localize("gui.loot_fabricator.error.redstone");
            case NO_PRISTINE:
                return LibMisc.LANG.localize("gui.loot_fabricator.error.no_pristine");
            case NO_OUTPUT_SELECTED:
                return LibMisc.LANG.localize("gui.loot_fabricator.error.no_output_selected");
            case OUTPUT_FULL:
                return LibMisc.LANG.localize("gui.loot_fabricator.error.output_full");
            case NO_ENERGY:
                return LibMisc.LANG.localize("gui.loot_fabricator.error.no_energy");
            default:
                return "";
        }
    }

    public static class ButtonItemSelect extends ButtonWidget<ButtonItemSelect> {

        @Getter
        private int index = -1;

        @Getter
        @Setter
        private boolean selected = false;

        public ButtonItemSelect(LootFabricatorPanel panel) {
            size(18);
            setEnabled(false);
            onMousePressed(btn -> {
                if (btn == 0 && this.index >= 0) {
                    panel.setOutputItem(this.index);
                    return true;
                }
                return false;
            });
        }

        public void setItem(int index, ItemStack stack, boolean selected) {
            this.index = index;
            if (stack == null || stack.getItem() == null) {
                overlay(new ItemStackDrawable());
            } else {
                overlay(new ItemStackDrawable(stack));
            }
            setSelected(selected);
            setEnabled(stack != null);
        }

        public @Nullable IDrawable getCurrentBackground(ITheme theme, WidgetThemeEntry<?> widgetTheme) {
            if (isHovering()) {
                return HOVER_BUTTON_SELECT;
            }
            return BUTTON_SELECT;
        }

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            IDrawable bg = getCurrentBackground(context.getTheme(), widgetTheme);
            if (bg != null && selected) {
                bg.draw(context, 0, 0, 18, 18, widgetTheme.getTheme());
            }
        }
    }

    public static class ButtonItemDeselect extends ButtonWidget<ButtonItemDeselect> {

        public ButtonItemDeselect(LootFabricatorPanel panel) {
            size(18);
            setDisplayStack(panel.outputItem);
            onMousePressed(btn -> {
                if (btn == 0) {
                    panel.setOutputItem(-1);
                    return true;
                }
                return false;
            });
            background(EMPTY_SLOT);
            hoverBackground(EMPTY_SLOT);
        }

        public void setDisplayStack(ItemStack stack) {
            if (stack == null || stack.getItem() == null) {
                overlay(new ItemStackDrawable());
            } else {
                overlay(new ItemStackDrawable(stack));
            }
        }
    }

    public static class ButtonPageSelect extends ButtonWidget<ButtonPageSelect> {

        public final Direction direction;

        public ButtonPageSelect(Direction direction, LootFabricatorPanel panel) {
            this.direction = direction;

            size(31, 12);
            name(direction.name());
            setEnabled(false);

            switch (direction) {
                case PREV:
                    background(LEFT_BUTTON);
                    hoverBackground(HOVER_LEFT_BUTTON);
                    break;
                case NEXT:
                    background(RIGHT_BUTTON);
                    hoverBackground(HOVER_RIGHT_BUTTON);
                    break;
            }

            onMousePressed(btn -> {
                if (btn == 0) {
                    if (direction == Direction.PREV) {
                        panel.changePage(-1);
                    }

                    if (direction == Direction.NEXT) {
                        panel.changePage(1);
                    }
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        DML_INVENTORY_TEXTURE.draw(-1, this.getArea().height - 91, 177, 91);
        BASE_TEXTURE.draw(-1, 0, 177, 83);
    }

    public enum Direction {
        PREV,
        NEXT
    }

    private enum CraftingError {
        NONE,
        NO_ENERGY,
        REDSTONE,
        NO_PRISTINE,
        NO_OUTPUT_SELECTED,
        OUTPUT_FULL
    }
}
