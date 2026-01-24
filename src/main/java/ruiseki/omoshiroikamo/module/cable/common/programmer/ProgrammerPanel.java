package ruiseki.omoshiroikamo.module.cable.common.programmer;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

import java.util.Arrays;
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
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ProgrammerSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.BooleanVariable;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.DoubleVariable;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.IntegerVariable;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.LongVariable;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.variable.StringVariable;

public class ProgrammerPanel extends ModularPanel {

    private final GuiData data;
    private final PanelSyncManager syncManager;
    private final UISettings settings;
    private final ProgrammerHandler handler;

    public ProgrammerSH syncHandler;
    public ItemSlotSH[] slots;

    private final StringValue selectedItem = new StringValue("");
    private Column detailColumn;

    public ProgrammerPanel(GuiData data, PanelSyncManager syncManager, UISettings settings, ProgrammerHandler handler) {
        super("programmer");

        this.data = data;
        this.syncManager = syncManager;
        this.settings = settings;
        this.handler = handler;

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

    private static final UITexture LIST_ITEM_BG = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/cable/logic_programmer")
        .imageSize(256, 256)
        .xy(18, 17, 58, 18)
        .adaptable(1)
        .canApplyTheme()
        .build();

    public static class ProgramItem {

        public final String name;
        private final Supplier<Widget<?>> widgetFactory;

        public ProgramItem(String name, Supplier<Widget<?>> widgetFactory) {
            this.name = name;
            this.widgetFactory = widgetFactory;
        }

        public Widget<?> createWidget() {
            return widgetFactory.get();
        }
    }

    private final List<ProgramItem> items = Arrays.asList(
        new ProgramItem("Boolean", () -> new BooleanVariable(this)),
        new ProgramItem("Integer", () -> new IntegerVariable(this)),
        new ProgramItem("Long", () -> new LongVariable(this)),
        new ProgramItem("Double", () -> new DoubleVariable(this)),
        new ProgramItem("String", () -> new StringVariable(this)));

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
                                syncHandler.clearVariableSlot();
                                selectItem(item);
                            }
                            return true;
                        });

                        return widget;
                    }));

        child(column);

    }

    public void addDetailPanel() {
        detailColumn = new Column();

        detailColumn.margin(5)
            .heightRel(0.45f)
            .pos(5, 5);

        child(detailColumn);
    }

    private void selectItem(ProgramItem item) {
        selectedItem.setStringValue(item.name);
        detailColumn.removeAll();
        detailColumn.child(item.createWidget());
        this.scheduleResize();
    }

}
