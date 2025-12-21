package ruiseki.omoshiroikamo.common.block.deepMobLearning.lootFabricator;

import static ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures.DML_INVENTORY_TEXTURE;
import static ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures.EMPTY_SLOT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.client.gui.modularui2.base.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.handler.ItemHandlerPristineMatter;
import ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.widget.InventoryWidget;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMachine;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemPristineMatter;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketLootFabricator;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.MathUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.DeepMobLearningConfig;

public class TELootFabricator extends AbstractMachine {

    private final ItemHandlerPristineMatter input = new ItemHandlerPristineMatter() {

        @Override
        protected void onMetadataChanged() {
            if (this.pristineMatterData != null && !isValidOutputItem()) outputItem = null;
            resetCrafting();
        }
    };

    private final ItemStackHandlerBase output = new ItemStackHandlerBase(16);
    private ItemStack outputItem = null;

    public TELootFabricator() {
        super(getEnergyCapacity(), getEnergyPerTick());
    }

    private static int getEnergyCapacity() {
        int energyCost = DeepMobLearningConfig.lootFabricatorRfCost;
        long energyCapacity = 1_000_000L * (energyCost / 100);
        return (int) Math.min(energyCapacity, Integer.MAX_VALUE);
    }

    private static int getEnergyPerTick() {
        int energyCost = DeepMobLearningConfig.lootFabricatorRfCost;
        long energyPerTick = 100L * energyCost;
        return (int) Math.min(energyPerTick, Integer.MAX_VALUE);
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (input.getStackInSlot(0) != null && !isValidOutputItem()) {
            outputItem = null;
            resetCrafting();
        }

        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public boolean canStartCrafting() {
        return super.canStartCrafting() && hasPristineMatter() && hasRoomForOutput() && isValidOutputItem();
    }

    @Override
    protected void finishCrafting() {
        resetCrafting();

        if (outputItem == null) {
            Logger.warn("Loot Fabricator at {} crafted without selected output!", getPos().toString());
            return;
        }

        if (!isValidOutputItem()) {
            Logger.warn("Loot Fabricator at {} crafted with invalid output selection!", getPos().toString());
            outputItem = null;
            return;
        }

        output.addItemToAvailableSlots(outputItem.copy());
        input.voidItem();
    }

    @Override
    protected int getCraftingDuration() {
        return DeepMobLearningConfig.lootFabricatorPrecessingTime;
    }

    @Override
    public int getCraftingEnergyCost() {
        return DeepMobLearningConfig.lootFabricatorRfCost;
    }

    @Override
    protected CraftingState updateCraftingState() {
        if (!crafting && !hasPristineMatter()) {
            return CraftingState.IDLE;
        } else if (!canContinueCrafting() || (!this.isCrafting() && !canStartCrafting())) {
            return CraftingState.ERROR;
        }

        return CraftingState.RUNNING;
    }

    private boolean isValidOutputItem() {
        ModelRegistryItem pristineMatterMetadata = getPristineMatterData();
        return outputItem != null && pristineMatterMetadata != null && pristineMatterMetadata.hasLootItem(outputItem);
    }

    @Nullable
    public ModelRegistryItem getPristineMatterData() {
        return input.getPristineMatterData();
    }

    public ItemStack getOutputItem() {
        return outputItem;
    }

    public void setOutputItem(ItemStack outputItem) {
        this.outputItem = outputItem;

        if (!isValidOutputItem()) {
            this.outputItem = null;
        }

        if (worldObj.isRemote) {
            PacketHandler.sendToAllAround(new PacketLootFabricator(this), this);
        }
    }

    public boolean hasPristineMatter() {
        ItemStack stack = input.getStackInSlot(0);
        return stack != null && stack.stackSize > 0 && stack.getItem() instanceof ItemPristineMatter;
    }

    public boolean hasRoomForOutput() {
        return output.hasRoomForItem(outputItem);
    }

    @Override
    public boolean isActive() {
        return isCrafting();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        NBTTagCompound inventory = new NBTTagCompound();
        inventory.setTag("input", input.serializeNBT());
        inventory.setTag("output", output.serializeNBT());
        root.setTag("inventory", inventory);

        if (outputItem != null) {
            NBTTagCompound crafting = root.getCompoundTag("crafting");
            crafting.setTag("outputItem", ItemNBTUtils.stackToNbt(outputItem));
            root.setTag("crafting", crafting);
        }

    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        NBTTagCompound inventory = root.getCompoundTag("inventory");
        input.deserializeNBT(inventory.getCompoundTag("input"));
        output.deserializeNBT(inventory.getCompoundTag("output"));

        NBTTagCompound crafting = root.getCompoundTag("crafting");
        outputItem = ItemNBTUtils.nbtToStack(crafting.getCompoundTag("outputItem"));

    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    public static final AdaptableUITexture BASE_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/loot_fabricator")
        .imageSize(256, 256)
        .xy(0, 0, 177, 83)
        .adaptable(4)
        .tiled()
        .build();

    private ModelRegistryItem pristineMatterData;
    private ImmutableList<ItemStack> lootItemList;
    private int currentOutputItemPage = -1;
    private int totalOutputItemPages = 0;

    private final List<ButtonItemSelect> outputSelectButtons = new ArrayList<>();
    private ButtonPageSelect nextPageButton;
    private ButtonPageSelect prevPageButton;
    private ButtonItemDeselect deselectButton;

    private CraftingError craftingError = CraftingError.NONE;

    private static final int ITEMS_PER_PAGE = 9;
    private static final int OUTPUT_SELECT_LIST_PADDING = 2;
    private static final int OUTPUT_SELECT_LIST_GUTTER = 1;
    private static final int OUTPUT_SELECT_BUTTON_SIZE = 18;

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        LootFabricatorPanel panel = new LootFabricatorPanel("loot_fabricator_gui");
        panel.size(176, 176);
        panel.child(InventoryWidget.playerInventory(true));
        pristineMatterData = getPristineMatterData();

        panel.child(
            new ItemSlot().background(EMPTY_SLOT)
                .hoverBackground(EMPTY_SLOT)
                .slot(new ModularSlot(input, 0).changeListener((newItem, onlyAmountChanged, client, init) -> {
                    if (client) {
                        // updateLootItem();
                    }
                })
                    .slotGroup("input"))
                .pos(77, 61));
        syncManager.registerSlotGroup("input", 1, true);

        syncManager.onClientTick(this::updateLootItem);

        return panel;
    }

    public void updateLootItem() {
        if (getPristineMatterData() == null) return;

        ModelRegistryItem lootFabData = getPristineMatterData();
        if (lootFabData != pristineMatterData) {
            pristineMatterData = lootFabData;
            resetOutputData();
        }

        if (!isRedstoneActive()) {
            craftingError = CraftingError.REDSTONE;
        } else if (!hasPristineMatter()) {
            craftingError = CraftingError.NO_PRISTINE;
        } else if (outputItem == null) {
            craftingError = CraftingError.NO_OUTPUT_SELECTED;
        } else if (!hasRoomForOutput()) {
            craftingError = CraftingError.OUTPUT_FULL;
        } else if (!hasEnergyForCrafting()) {
            craftingError = CraftingError.NO_ENERGY;
        } else {
            craftingError = CraftingError.NONE;
        }
    }

    private void resetOutputData() {
        if (pristineMatterData == null) {
            lootItemList = ImmutableList.of();
            currentOutputItemPage = -1;
            totalOutputItemPages = 0;
            setPageButtonsEnabled(false);
        } else {
            lootItemList = ImmutableList.copyOf(getPristineMatterData().getLootItems());
            if (outputItem != null) {
                int currentOutputItemIndex = pristineMatterData.getLootItemIndex(outputItem);
                currentOutputItemPage = currentOutputItemIndex / ITEMS_PER_PAGE;
            } else {
                currentOutputItemPage = 0;
            }
            totalOutputItemPages = MathUtils.divideAndRoundUp(lootItemList.size(), ITEMS_PER_PAGE);
            setPageButtonsEnabled(totalOutputItemPages > 1);
        }
        rebuildOutputSelectButtons();
    }

    private void rebuildOutputSelectButtons() {
        outputSelectButtons.clear();
        constructOutputSelectButtonRows();
    }

    private void constructOutputSelectButtonRows() {
        if (currentOutputItemPage < 0) {
            return;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int indexRelative = row * 3 + col;
                int indexAbsolute = (currentOutputItemPage * ITEMS_PER_PAGE) + indexRelative;
                if (indexAbsolute >= lootItemList.size()) return; // End of list reached -> abort
                ItemStack stack = lootItemList.get(indexAbsolute);
                outputSelectButtons.add(
                    new ButtonItemSelect(
                        OUTPUT_SELECT_LIST_PADDING + col * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        OUTPUT_SELECT_LIST_PADDING + row * (OUTPUT_SELECT_BUTTON_SIZE + OUTPUT_SELECT_LIST_GUTTER),
                        stack,
                        ItemUtils.areStacksEqual(stack, outputItem)));
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
        setOutputItem(outputItem);
    }

    private void setPageButtonsEnabled(boolean enabled) {
        // prevPageButton.setEnabled(enabled);
        // nextPageButton.setEnabled(enabled);
    }

    public static class LootFabricatorPanel extends ModularPanel {

        public LootFabricatorPanel(@NotNull String name) {
            super(name);
        }

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            DML_INVENTORY_TEXTURE.draw(-1, this.getArea().height - 91, 177, 91);
            BASE_TEXTURE.draw(-1, 0, 177, 83);
        }
    }

    public static class ButtonItemSelect extends ButtonWidget<ButtonItemSelect> {

        @Getter
        @Setter
        private boolean selected = false;

        public ButtonItemSelect(int x, int y, ItemStack stack, boolean selected) {
            pos(x, y);
            overlay(new ItemDrawable(stack));
            setSelected(selected);
        }

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {

        }
    }

    public static class ButtonPageSelect extends ButtonWidget<ButtonPageSelect> {

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {

        }
    }

    public static class ButtonItemDeselect extends ButtonWidget<ButtonItemDeselect> {

        @Getter
        @Setter
        private ItemStack displayStack;

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {

        }
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
