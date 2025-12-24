package ruiseki.omoshiroikamo.module.dml.common.item.deepLearner;

import static ruiseki.omoshiroikamo.core.client.gui.GuiTextures.DML_INVENTORY_TEXTURE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.PlayerInvWrapper;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.EntityDisplayWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.api.entity.dml.DataModelExperience;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.dml.client.gui.container.DeepLearnerContainer;
import ruiseki.omoshiroikamo.module.dml.client.gui.widget.InventoryWidget;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemDataModel;

public class DeepLearnerPanel extends ModularPanel {

    public static final AdaptableUITexture BASE_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/deep_learner")
        .imageSize(256, 256)
        .xy(0, 0, 256, 140)
        .adaptable(4)
        .tiled()
        .build();

    public static final AdaptableUITexture EXTRA_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/deep_learner")
        .imageSize(256, 256)
        .xy(0, 140, 75, 101)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture LEFT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_deep_learner_select")
        .imageSize(256, 256)
        .xy(0, 24, 24, 24)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture RIGHT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_deep_learner_select")
        .imageSize(256, 256)
        .xy(24, 24, 24, 24)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture HOVER_LEFT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_deep_learner_select")
        .imageSize(256, 256)
        .xy(0, 48, 24, 24)
        .adaptable(1)
        .tiled()
        .build();

    public static final AdaptableUITexture HOVER_RIGHT_BUTTON = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/buttons/button_deep_learner_select")
        .imageSize(256, 256)
        .xy(24, 48, 24, 24)
        .adaptable(1)
        .tiled()
        .build();

    public static final UITexture DEEP_LEARNER_SLOT = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/deep_learner")
        .imageSize(256, 256)
        .xy(215, 99, 18, 18)
        .build();

    public static final UITexture HEART = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/deepMobLearning/deep_learner")
        .imageSize(256, 256)
        .xy(75, 140, 9, 9)
        .build();

    public static DeepLearnerPanel defaultPanel(PanelSyncManager syncManager, UISettings settings, EntityPlayer player,
        DeepLearnerHandler handler, Integer slotIndex) {
        DeepLearnerPanel panel = new DeepLearnerPanel(player, syncManager, settings, handler);

        panel.settings.customContainer(() -> new DeepLearnerContainer(slotIndex));
        syncManager.bindPlayerInventory(player);
        panel.modifyPlayerSlot(syncManager, slotIndex, player);

        panel.addModelDisplay();
        panel.addChangeModelButton();
        panel.addInventorySlots();

        panel.child(InventoryWidget.playerInventory(true));

        return panel;
    }

    @Getter
    private final EntityPlayer player;
    @Getter
    private final PanelSyncManager syncManager;
    @Getter
    private final UISettings settings;
    @Getter
    private final DeepLearnerHandler handler;

    @Getter
    private final List<ModelDisplay> modelDisplayList;
    private ParentWidget<?> infoDisplay;
    private Row modelButtonRow;
    private final List<ItemSlot> itemSlots = new ArrayList<>();

    @Getter
    @Setter
    private int modelIndex = 0;

    private final ItemSlotSH[] itemSlotSyncHandlers;

    public DeepLearnerPanel(EntityPlayer player, PanelSyncManager syncManager, UISettings settings,
        DeepLearnerHandler handler) {
        super("deep_learner_gui");
        this.player = player;
        this.syncManager = syncManager;
        this.settings = settings;
        this.handler = handler;

        this.syncManager.syncValue("modelIndex", new IntSyncValue(this::getModelIndex, this::setModelIndex));

        size(256, 236);

        modelDisplayList = new ArrayList<>();
        this.itemSlotSyncHandlers = new ItemSlotSH[this.handler.getSlots()];
        for (int i = 0; i < this.handler.getSlots(); i++) {
            ModularSlot slot = new ModularSlot(this.handler, i);
            slot.slotGroup("inventory");
            ItemSlotSH syncHandler = new ItemSlotSH(slot);
            slot.changeListener((lastStack, currentStack, isClient, init) -> {
                if (isClient) {
                    updateModelDisplay();
                }
            })
                .filter(stack -> stack.getItem() instanceof ItemDataModel);
            this.syncManager.syncValue("learner", i, syncHandler);
            this.itemSlotSyncHandlers[i] = syncHandler;
        }
        this.syncManager.registerSlotGroup(new SlotGroup("inventory", 1, 99, true));

    }

    public void modifyPlayerSlot(PanelSyncManager syncManager, int slotIndex, EntityPlayer player) {
        ModularSlot slot = new ModularSlot(new PlayerInvWrapper(player.inventory), slotIndex) {

            @Override
            public boolean canTakeStack(EntityPlayer playerIn) {
                return false;
            }
        }.slotGroup("player_inventory");

        syncManager.itemSlot("player", slotIndex, slot);
    }

    public void addInventorySlots() {
        SlotGroupWidget widget = SlotGroupWidget.builder()
            .row("II")
            .row("II")
            .key('I', index -> {
                ItemSlot slot = new ItemSlot().syncHandler("learner", index)
                    .background(DEEP_LEARNER_SLOT)
                    .hoverBackground(DEEP_LEARNER_SLOT);
                itemSlots.add(slot);
                return slot;
            })
            .build();
        widget.pos(215, 99);
        child(widget);
    }

    public void addModelDisplay() {
        for (int i = 0; i < handler.getSlots(); i++) {
            ModelDisplay tab = new ModelDisplay().name("model_display_" + i);
            tab.setEnabled(false);
            modelDisplayList.add(tab);
            child(tab);
        }
    }

    public void addInfoDisplay() {
        ItemStack stack = getHandler().getHandler()
            .getStackInSlot(modelIndex);
        if (stack == null) return;
        ModelRegistryItem model = DataModel.getDataFromStack(stack);
        if (model == null) return;
        float numberOfHearts = model.getNumberOfHearts();
        String name = model.getDisplayName();
        String[] trivia = model.getMobTrivia();
        int tier = DataModel.getTier(stack);
        int totalKillCount = DataModel.getTotalKillCount(stack);
        int killsThisTier = DataModel.getKillCount(stack);
        int simulationsThisTier = DataModel.getSimulationCount(stack);
        boolean isMaxTier = tier >= DataModelExperience.getMaxTier();

        TextWidget<?> heartTile = IKey.lang("gui.deep_learner.health_points")
            .scale(1f)
            .color(0xFF7CCDDB)
            .asWidget();

        TextWidget<?> hearts = IKey.str(String.valueOf(numberOfHearts))
            .scale(1f)
            .color(0xFFFFFFFF)
            .asWidget();

        Widget<?> heart = new Widget<>().background(HEART)
            .size(12);

        Row heartRow = (Row) new Row().coverChildren()
            .left(0)
            .childPadding(2)
            .child(heart)
            .child(hearts);

        Column heartCol = (Column) new Column().name("info_display_" + modelIndex)
            .pos(185, 10)
            .coverChildren()
            .childPadding(2)
            .child(heartTile)
            .child(heartRow);

        TextWidget<?> nameTile = IKey.lang("gui.deep_learner.heading_name")
            .scale(1f)
            .color(0xFF7CCDDB)
            .asWidget()
            .left(0);

        TextWidget<?> nameText = IKey.lang(name)
            .scale(1f)
            .color(0xFFFFFFFF)
            .asWidget()
            .left(0);

        Column nameCol = (Column) new Column().name("info_display_" + modelIndex)
            .pos(10, 10)
            .coverChildren()
            .childPadding(2)
            .child(nameTile)
            .child(nameText);

        TextWidget<?> infoTile = IKey.lang("gui.deep_learner.heading_information")
            .scale(1f)
            .color(0xFF7CCDDB)
            .asWidget()
            .left(0);

        ListWidget<Column, ?> info = new ListWidget<>();
        Column mobTrivia = (Column) new Column().coverChildren();

        for (String string : trivia) {
            TextWidget<?> text = IKey.str(string)
                .scale(1f)
                .color(0xFFFFFFFF)
                .alignment(Alignment.CenterLeft)
                .asWidget()
                .left(0)
                .width(200);
            mobTrivia.child(text);
        }
        info.child(mobTrivia)
            .width(200)
            .maxSize(45);

        ListWidget<Column, ?> kill = new ListWidget<>();
        Column killInfo = (Column) new Column().coverChildren()
            .childPadding(2);
        TextWidget<?> tierText = IKey
            .lang("gui.deep_learner.model_tier", IKey.lang(DataModelExperience.getTierName(tier)))
            .scale(1f)
            .color(0xFFFFFFFF)
            .alignment(Alignment.CenterLeft)
            .asWidget()
            .left(0)
            .width(200);

        TextWidget<?> totalKillText = IKey.lang("gui.deep_learner.defeated", totalKillCount)
            .scale(1f)
            .color(0xFFFFFFFF)
            .alignment(Alignment.CenterLeft)
            .asWidget()
            .left(0)
            .width(200);

        TextWidget<?> defeatedMoreText = null;
        if (!isMaxTier) {
            int killsRemaining = (int) Math
                .ceil(DataModelExperience.getKillsToNextTier(tier, killsThisTier, simulationsThisTier));

            defeatedMoreText = IKey
                .lang("gui.deep_learner.required", killsRemaining, IKey.lang(DataModelExperience.getTierName(tier + 1)))
                .scale(1f)
                .color(0xFFFFFFFF)
                .alignment(Alignment.CenterLeft)
                .asWidget()
                .left(0)
                .width(200);
        }
        killInfo.child(tierText)
            .child(totalKillText);

        if (defeatedMoreText != null) {
            killInfo.child(defeatedMoreText);
        }
        kill.child(killInfo)
            .pos(0, 60)
            .width(200)
            .maxSize(32);

        Column infoCol = (Column) new Column().name("info_display_" + modelIndex)
            .pos(10, 36)
            .coverChildren()
            .childPadding(2)
            .child(infoTile)
            .child(info)
            .child(kill);

        infoDisplay = new ParentWidget<>();
        infoDisplay.child(nameCol);
        infoDisplay.child(infoCol);
        infoDisplay.child(heartCol);

        child(infoDisplay);
    }

    public void addChangeModelButton() {
        ButtonWidget<?> right = new ButtonWidget<>().size(24)
            .right(13)
            .background(RIGHT_BUTTON)
            .hoverBackground(HOVER_RIGHT_BUTTON)
            .tooltip(tooltip -> {
                tooltip.addLine(LibMisc.LANG.localize("gui.deep_learner.button_next"));
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            })
            .onMousePressed(button -> {
                if (button == 0) {
                    Interactable.playButtonClickSound();
                    nextModel();
                    return true;
                }
                return false;
            });
        ButtonWidget<?> left = new ButtonWidget<>().size(24)
            .left(13)
            .background(LEFT_BUTTON)
            .hoverBackground(HOVER_LEFT_BUTTON)
            .tooltip(tooltip -> {
                tooltip.addLine(LibMisc.LANG.localize("gui.deep_learner.button_prev"));
                tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
            })
            .onMousePressed(button -> {
                if (button == 0) {
                    Interactable.playButtonClickSound();
                    prevModel();
                    return true;
                }
                return false;
            });

        modelButtonRow = (Row) new Row().size(75, 24)
            .pos(-80, 106);
        modelButtonRow.child(left)
            .child(right)
            .setEnabled(false);
        child(modelButtonRow);
    }

    public void updateModelDisplay() {
        disableAllModelDisplays();

        List<Integer> validIndexes = getValidModelIndexes();
        if (modelButtonRow != null) {
            modelButtonRow.setEnabled(validIndexes.size() >= 2);
        }

        if (validIndexes.isEmpty()) {
            return;
        }

        if (!validIndexes.contains(modelIndex)) {
            setModelIndex(validIndexes.get(0));
        }

        ItemSlot slotWidget = itemSlots.get(modelIndex);
        ItemStack stack = slotWidget.getSlot()
            .getStack();

        if (stack == null || !(stack.getItem() instanceof ItemDataModel)) {
            return;
        }

        ModelRegistryItem model = DataModel.getDataFromStack(stack);
        Class<? extends Entity> entityClass = DataModel.getEntityClass(stack);
        if (entityClass == null) return;

        Entity entity;
        try {
            entity = entityClass.getConstructor(World.class)
                .newInstance(player.worldObj);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!(entity instanceof EntityLivingBase livingBase)) return;

        ModelDisplay display = modelDisplayList.get(modelIndex);
        display.setEnabled(true);

        float scale = model.getInterfaceScale();
        int offsetX = model.getInterfaceOffsetX();
        int offsetY = model.getInterfaceOffsetY();

        Widget<?> widget = new EntityDisplayWidget(() -> livingBase).doesLookAtMouse(true)
            .asWidget()
            .size(Math.round(74 * scale))
            .pos(offsetX, 20 + offsetY);

        display.setWidget(widget);
        addInfoDisplay();
        this.scheduleResize();
    }

    public void disableAllModelDisplays() {
        for (int i = 0; i < handler.getSlots(); i++) {
            ModelDisplay display = modelDisplayList.get(i);
            if (display != null) {
                display.setEnabled(false);
                display.removeAll();
                this.remove(infoDisplay);
            }
        }
        this.scheduleResize();
    }

    private List<Integer> getValidModelIndexes() {
        List<Integer> valid = new ArrayList<>();

        for (int i = 0; i < itemSlots.size(); i++) {
            ItemStack stack = itemSlots.get(i)
                .getSlot()
                .getStack();
            if (stack == null) continue;
            if (!(stack.getItem() instanceof ItemDataModel)) continue;
            Class<? extends Entity> entity = DataModel.getEntityClass(stack);
            if (entity != null) {
                valid.add(i);
            }
        }

        return valid;
    }

    private void nextModel() {
        List<Integer> valid = getValidModelIndexes();
        if (valid.isEmpty()) return;

        int pos = valid.indexOf(modelIndex);
        if (pos == -1) {
            setModelIndex(valid.get(0));
        } else {
            setModelIndex(valid.get((pos + 1) % valid.size()));
        }

        updateModelDisplay();
    }

    private void prevModel() {
        List<Integer> valid = getValidModelIndexes();
        if (valid.isEmpty()) return;

        int pos = valid.indexOf(modelIndex);
        if (pos == -1) {
            setModelIndex(valid.get(0));
        } else {
            setModelIndex(valid.get((pos - 1 + valid.size()) % valid.size()));
        }

        updateModelDisplay();
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        DML_INVENTORY_TEXTURE.draw(39, this.getArea().height - 91, 177, 91);
        BASE_TEXTURE.draw(0, 0, 256, 140);
    }

    public static class ModelDisplay extends ParentWidget<ModelDisplay> {

        public ModelDisplay() {
            pos(-80, 0);
            size(75, 101);
        }

        @Override
        public void onInit() {
            getContext().getUISettings()
                .getRecipeViewerSettings()
                .addExclusionArea(this);
        }

        public void setWidget(Widget<?> widget) {
            removeAll();

            if (widget != null) {
                child(widget);
            }
        }

        @Override
        public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            super.draw(context, widgetTheme);
        }

        @Override
        public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
            EXTRA_TEXTURE.draw(0, 0, 74, 100);
        }
    }
}
