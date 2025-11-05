package ruiseki.omoshiroikamo.client.gui.modularui2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.PageButton;
import com.cleanroommc.modularui.widgets.PagedWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.io.IoMode;
import ruiseki.omoshiroikamo.api.io.IoType;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractIOTE;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketIoMode;

public class MGuiBuilder {

    private final AbstractIOTE ioTe;
    private final PosGuiData posGuiData;
    private final PanelSyncManager syncManager;
    private final UISettings uiSettings;

    private int width = 176;
    private int height = 166;
    private boolean doesBindPlayerInventory = true;
    private boolean doesAddConfigIOItem = false;
    private boolean doesAddConfigIOFluid = false;
    private boolean doesAddConfigIOHeat = false;
    private boolean doesAddConfigR = true;

    public MGuiBuilder(AbstractIOTE te, PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        this.ioTe = te;
        this.posGuiData = data;
        this.syncManager = syncManager;
        this.uiSettings = uiSettings;
    }

    public MGuiBuilder doesBindPlayerInventory(boolean doesBindPlayerInventory) {
        this.doesBindPlayerInventory = doesBindPlayerInventory;
        return this;
    }

    public MGuiBuilder doesAddConfigIOItem(boolean doesAddConfigIOItem) {
        this.doesAddConfigIOItem = doesAddConfigIOItem;
        return this;
    }

    public MGuiBuilder doesAddConfigIOFluid(boolean doesAddConfigIOFluid) {
        this.doesAddConfigIOFluid = doesAddConfigIOFluid;
        return this;
    }

    public MGuiBuilder doesAddConfigIOHeat(boolean doesAddConfigIOHeat) {
        this.doesAddConfigIOHeat = doesAddConfigIOHeat;
        return this;
    }

    public MGuiBuilder doesAddConfigR(boolean doesAddConfigR) {
        this.doesAddConfigR = doesAddConfigR;
        return this;
    }

    public ModularPanel build() {
        ModularPanel panel = ModularPanel.defaultPanel(ioTe.getMachineName())
            .size(width, height);

        if (doesBindPlayerInventory) {
            panel.bindPlayerInventory();
        }

        if (doesAddConfigIOItem || doesAddConfigIOFluid || doesAddConfigIOHeat || doesAddConfigR) {
            panel.child(createConfig());
        }

        syncManager.addCloseListener($ -> { ioTe.markDirty(); });

        return panel;
    }

    private IWidget createConfig() {
        IPanelHandler panelSyncHandler = syncManager.panel("other_panel", this::configPanel, true);
        Flow column = Flow.column()
            .name("Settings");

        column.coverChildren()
            .leftRelOffset(1f, 1)
            .background(GuiTextures.MC_BACKGROUND)
            .excludeAreaInRecipeViewer()
            .coverChildren()
            .padding(4)
            .childPadding(2)
            .bottomRel(0.5f);

        if (doesAddConfigR) {
            column.child(
                new ToggleButton().size(18, 18)
                    .overlay(true, MGuiTextures.BUTTON_REDSTONE_ON)
                    .overlay(false, MGuiTextures.BUTTON_REDSTONE_OFF)
                    .value(
                        new BooleanSyncValue(
                            () -> (ioTe.redstoneCheckPassed),
                            value -> { ioTe.redstoneCheckPassed = value; }))
                    .selectedBackground(GuiTextures.MC_BUTTON)
                    .selectedHoverBackground(GuiTextures.MC_BUTTON_HOVERED)
                    .tooltip(richTooltip -> {
                        richTooltip.showUpTimer(2);
                        richTooltip.addLine(IKey.str("Redstone Mode"));
                    }));
        }

        if (doesAddConfigIOItem || doesAddConfigIOFluid || doesAddConfigIOHeat) {
            column.child(
                new ButtonWidget<>().size(18, 18)
                    .overlay(GuiTextures.GEAR)
                    .tooltip(richTooltip -> {
                        richTooltip.showUpTimer(2);
                        richTooltip.addLine(IKey.str("Configure"));
                    })
                    .onMousePressed(mouseButton -> {
                        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                            if (panelSyncHandler.isPanelOpen()) {
                                panelSyncHandler.closePanel();
                            } else {
                                panelSyncHandler.openPanel();
                            }
                            return true;
                        }
                        return false;
                    }));
        }
        return column;
    }

    public ModularPanel configPanel(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        ModularPanel panel = new Dialog<>("second_window", null).setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true)
            .size(68, 68);

        PagedWidget.Controller tabController = new PagedWidget.Controller();
        Flow tabRow = new Row().name("Tab row")
            .coverChildren()
            .topRel(0f, 4, 1f);

        ForgeDirection blockFront = ioTe.getFacingDir();
        Map<Character, ForgeDirection> faceMap = new HashMap<>();
        faceMap.put('U', ForgeDirection.UP);
        faceMap.put('D', ForgeDirection.DOWN);
        faceMap.put('F', blockFront);
        faceMap.put('B', blockFront.getOpposite());
        faceMap.put('L', blockFront.getRotation(ForgeDirection.UP));
        faceMap.put('R', blockFront.getRotation(ForgeDirection.DOWN));

        char[][] layout = { { ' ', 'U', ' ' }, { 'L', 'F', 'R' }, { ' ', 'D', 'B' } };

        List<TabEntry> tabs = Arrays.asList(
            new TabEntry(
                doesAddConfigIOItem,
                Blocks.chest,
                -1,
                () -> createIoConfigColumn(IoType.ITEM, layout, faceMap)),
            new TabEntry(
                doesAddConfigIOFluid,
                Items.bucket,
                0,
                () -> createIoConfigColumn(IoType.FLUID, layout, faceMap)),
            new TabEntry(
                doesAddConfigIOHeat,
                Items.blaze_powder,
                0,
                () -> createIoConfigColumn(IoType.HEAT, layout, faceMap)));

        PagedWidget<?> paged = new PagedWidget<>().name("root parent")
            .controller(tabController)
            .sizeRel(1f);

        int pageIndex = 0;
        for (TabEntry tab : tabs) {
            if (!tab.enabled) {
                continue;
            }

            // Add tab button
            tabRow.child(
                new PageButton(pageIndex, tabController).tab(GuiTextures.TAB_TOP, tab.textureOffset)
                    .marginLeft(1)
                    .size(20)
                    .excludeAreaInRecipeViewer()
                    .overlay(
                        tab.getDrawable()
                            .asIcon()
                            .size(16)));

            // Add corresponding page
            paged.addPage(tab.pageSupplier.get());

            pageIndex++;
        }

        panel.child(tabRow);
        panel.child(paged);
        panel.child(ButtonWidget.panelCloseButton());

        return panel;
    }

    private static class TabEntry {

        final boolean enabled;
        final Item itemIcon;
        final Block blockIcon;
        final int textureOffset;
        final Supplier<Flow> pageSupplier; // Dùng để tạo page tương ứng

        TabEntry(boolean enabled, Item icon, int textureOffset, Supplier<Flow> pageSupplier) {
            this.enabled = enabled;
            this.itemIcon = icon;
            this.blockIcon = null;
            this.textureOffset = textureOffset;
            this.pageSupplier = pageSupplier;
        }

        TabEntry(boolean enabled, Block icon, int textureOffset, Supplier<Flow> pageSupplier) {
            this.enabled = enabled;
            this.itemIcon = null;
            this.blockIcon = icon;
            this.textureOffset = textureOffset;
            this.pageSupplier = pageSupplier;
        }

        public ItemDrawable getDrawable() {
            if (blockIcon != null) {
                return new ItemDrawable(blockIcon);
            } else if (itemIcon != null) {
                return new ItemDrawable(itemIcon);
            } else {
                return new ItemDrawable(Blocks.air);
            }
        }
    }

    private Flow createIoConfigColumn(IoType type, char[][] layout, Map<Character, ForgeDirection> faceMap) {
        Flow column = new Column().name("Side Configs")
            .padding(5);
        for (char[] row : layout) {
            Flow guiRow = new Row().coverChildren();
            for (char c : row) {
                guiRow.child(makeFaceButton(c, faceMap, type));
            }
            column.child(guiRow);
        }
        return column;
    }

    private IWidget makeFaceButton(char key, Map<Character, ForgeDirection> faceMap, IoType type) {
        if (!faceMap.containsKey(key)) {
            return new Widget<>().size(18);
        }

        ForgeDirection face = faceMap.get(key);
        return new CycleButtonWidget().size(18)
            .stateCount(IoMode.values().length)
            .stateOverlay(MGuiTextures.CYCLE_IOMODE)
            .tooltip(
                richTooltip -> richTooltip.addLine(
                    IKey.dynamic(
                        () -> type + " - "
                            + (ioTe.getIoMode(face, type)
                                .getUnlocalisedName()))))
            .value(
                new IntSyncValue(
                    () -> (ioTe.getIoMode(face, type)
                        .ordinal()),
                    val -> {
                        IoMode mode = IoMode.values()[val];

                        ioTe.setIoMode(face, mode, type);
                        PacketHandler.INSTANCE.sendToServer(new PacketIoMode(ioTe, face, type));

                    }));
    }

    public static SlotGroupWidget buildPlayerInventorySlotGroup(IItemHandler playerInventory) {
        return SlotGroupWidget.builder()
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .row("PPPPPPPPP")
            .key('P', i -> new ItemSlot().slot(new ModularSlot(playerInventory, 9 + i).slotGroup("player_inventory")))
            .build();
    }

    public static SlotGroupWidget buildPlayerHotbarSlotGroup(IItemHandler playerInventory) {
        return SlotGroupWidget.builder()
            .row("HHHHHHHHH")
            .key('H', i -> new ItemSlot().slot(new ModularSlot(playerInventory, i).slotGroup("player_inventory")))
            .build();
    }
}
