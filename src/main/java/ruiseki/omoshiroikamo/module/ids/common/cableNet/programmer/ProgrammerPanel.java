package ruiseki.omoshiroikamo.module.ids.common.cableNet.programmer;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.BaseVariableWidget;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.EmptyVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator.AndVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator.IfVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator.NandVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator.NorVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.operator.OrVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.BooleanVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.DoubleVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.FloatVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.IntegerVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.LongVariable;
import ruiseki.omoshiroikamo.module.ids.client.gui.widget.variable.type.StringVariable;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.ItemVariableCard;

public class ProgrammerPanel extends ModularPanel {

    private final GuiData data;
    private final PanelSyncManager syncManager;
    private final UISettings settings;
    private final ProgrammerHandler handler;

    public ProgrammerSH syncHandler;
    public ItemSlotSH[] slots;

    private final StringValue selectedItem = new StringValue("");
    private Column detailColumn;
    private BaseVariableWidget detailWidget;

    public ProgrammerPanel(GuiData data, PanelSyncManager syncManager, UISettings settings, ProgrammerHandler handler) {
        super("programmer");

        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;
        this.handler = handler;

        height(184);

        syncHandler = new ProgrammerSH(handler, this);
        syncManager.syncValue("programmer_sh", syncHandler);

        this.slots = new ItemSlotSH[handler.getHandler()
            .getSlots()];
        for (int i = 0; i < handler.getHandler()
            .getSlots(); i++) {
            ModularSlot slot = new ModularSlot(handler.getHandler(), i);
            ItemSlotSH syncer = new ItemSlotSH(slot);
            syncManager.syncValue("slots", i, syncer);
            slots[i] = syncer;
        }

        registerDefaultItems();
        addVariableList();

        addDetailPanel();

        syncManager.bindPlayerInventory(data.getPlayer());
        bindPlayerInventory();

        syncManager.addCloseListener(player -> {
            for (int i = 0; i < handler.getHandler()
                .getSlots(); i++) {
                ItemStack stack = handler.getHandler()
                    .getStackInSlot(i);
                if (stack != null) {
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropPlayerItemWithRandomChoice(stack, false);
                    }
                    handler.getHandler()
                        .setStackInSlot(i, null);
                }
            }
        });

    }

    private void registerDefaultItems() {

        // Type
        addItem(LibMisc.LANG.localize("gui.ids.type.boolean"), () -> new BooleanVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.type.int"), () -> new IntegerVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.type.long"), () -> new LongVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.type.float"), () -> new FloatVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.type.double"), () -> new DoubleVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.type.string"), () -> new StringVariable(this));

        // Controller
        addItem(LibMisc.LANG.localize("gui.ids.op.if"), () -> new IfVariable(this));

        // Operator
        addItem(LibMisc.LANG.localize("gui.ids.op.and"), () -> new AndVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.op.nand"), () -> new NandVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.op.or"), () -> new OrVariable(this));
        addItem(LibMisc.LANG.localize("gui.ids.op.nor"), () -> new NorVariable(this));
    }

    private static final UITexture LIST_ITEM_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/ids/logic_programmer")
        .imageSize(256, 256)
        .xy(18, 17, 58, 18)
        .adaptable(1)
        .canApplyTheme()
        .build();

    public static class ProgramItem {

        public final String name;
        private final Supplier<BaseVariableWidget> widgetFactory;

        public ProgramItem(String name, Supplier<BaseVariableWidget> widgetFactory) {
            this.name = name;
            this.widgetFactory = widgetFactory;
        }

        public BaseVariableWidget createWidget() {
            return widgetFactory.get();
        }
    }

    private final List<ProgramItem> items = new ArrayList<>();

    public void addItem(String name, Supplier<BaseVariableWidget> factory) {
        items.add(new ProgramItem(name, factory));
    }

    public void addVariableList() {
        StringValue searchValue = new StringValue("");
        Column column = new Column();

        column.padding(5)
            .width(80)
            .pos(-80, 0)
            .background(GuiTextures.MC_BACKGROUND)
            .excludeAreaInRecipeViewer()
            .child(
                new TextFieldWidget().value(searchValue)
                    .height(16)
                    .background(VANILLA_SEARCH_BACKGROUND)
                    .widthRel(1f))
            .child(
                new ListWidget<>().collapseDisabledChild()
                    .expanded()
                    .widthRel(1f)
                    .children(items.size(), i -> {
                        ProgramItem item = items.get(i);
                        ButtonWidget<?> widget = new ButtonWidget<>().overlay(
                            IKey.str(item.name)
                                .color(Color.BLACK.main)
                                .shadow(false))
                            .widthRel(1f)
                            .height(18)
                            .background(LIST_ITEM_BG)
                            .hoverBackground(LIST_ITEM_BG)
                            .setEnabledIf(
                                w -> item.name.toLowerCase()
                                    .contains(
                                        searchValue.getStringValue()
                                            .toLowerCase()));
                        widget.onMousePressed(btn -> {
                            if (btn == 0) {
                                Interactable.playButtonClickSound();
                                selectItem(item);
                            }
                            return true;
                        });

                        return widget;
                    }));

        child(column);

    }

    public void addDetailPanel() {
        Column col = new Column();

        detailColumn = new Column();
        detailWidget = new EmptyVariable(this);

        detailColumn.margin(5)
            .height(74)
            .pos(5, 5)
            .child(detailWidget);
        col.child(detailColumn);

        Row row = new Row();
        ItemSlot slot = new ItemSlot().syncHandler("slots", 0)
            .right(7)
            .background(OKGuiTextures.VARIABLE_SLOT)
            .background(OKGuiTextures.VARIABLE_SLOT);
        TextWidget<?> textWidget = new TextWidget<>(IKey.dynamic(selectedItem::getStringValue)).height(18)
            .left(7);
        row.size(176, 18)
            .bottom(86)
            .child(textWidget)
            .child(slot);

        slots[0].getSlot()
            .filter(stack -> stack.getItem() instanceof ItemVariableCard)
            .changeListener((newItem, onlyAmountChanged, client, init) -> {
                if (init) return;

                if (detailWidget != null) {
                    detailWidget.writeLogicNBT();
                }
            });

        child(row);
        child(col);
    }

    private void selectItem(ProgramItem item) {
        selectedItem.setStringValue(item.name);

        syncHandler.clearVariableSlot();
        detailColumn.removeAll();
        detailWidget = item.createWidget();

        detailColumn.child(detailWidget);

        scheduleResize();
    }

}
