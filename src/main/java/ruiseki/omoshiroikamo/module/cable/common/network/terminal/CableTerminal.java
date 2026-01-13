package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.CableItemSlotSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler.ItemIndexSH;
import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableItemSlot;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public class CableTerminal extends AbstractPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 3f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public void doUpdate() {

    }

    @Override
    public void onChunkUnload() {

    }

    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT_COUNT = COLUMNS * ROWS;

    private SlotGroupWidget itemSlots;
    private final CableItemSlotSH[] itemSlotSH = new CableItemSlotSH[SLOT_COUNT];
    private final CableItemSlot[] slots = new CableItemSlot[SLOT_COUNT];

    @Override
    public @NotNull ModularPanel partPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings) {

        ModularPanel panel = new ModularPanel("cable_terminal");
        panel.size(176, 223);

        ItemIndexClient clientIndex = new ItemIndexClient();
        ItemIndexSH sh = new ItemIndexSH(this::getItemNetwork, clientIndex);
        syncManager.syncValue("cable_terminal_sh", sh);

        for (int i = 0; i < SLOT_COUNT; i++) {
            CableItemSlotSH slotSH = new CableItemSlotSH(getItemNetwork());
            itemSlotSH[i] = slotSH;
            syncManager.syncValue("itemSlot_" + i, i, slotSH);
        }

        itemSlots = new SlotGroupWidget().name("itemSlots")
            .pos(7, 14);
        panel.child(itemSlots);
        buildItemGrid();
        final int[] last = { -1 };

        syncManager.onClientTick(() -> {
            int v = clientIndex.getServerVersion();
            if (v != last[0]) {
                last[0] = v;
                updateGrid(clientIndex.view());
            }
        });

        syncManager.onServerTick(() -> {
            ItemNetwork net = getItemNetwork();
            if (net != null) {
                sh.requestSync(clientIndex.getServerVersion());
            }
        });

        syncManager.addCloseListener(p -> clientIndex.destroy());

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
    }

    private void buildItemGrid() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            CableItemSlot slot = new CableItemSlot().setStack(null)
                .syncHandler("itemSlot_" + i, i);
            slots[i] = slot;
            itemSlots.child(slot.pos(col * 18, row * 18));
        }
    }

    private void updateGrid(Map<ItemStackKey, Integer> db) {

        int i = 0;
        for (var e : db.entrySet()) {
            if (i >= slots.length) break;

            ItemStack stack = e.getKey()
                .toStack(e.getValue());
            slots[i].setStack(stack);
            i++;
        }

        while (i < slots.length) {
            slots[i].setStack(null);
            i++;
        }
    }

    @Override
    public String getId() {
        return "cable_terminal";
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return null;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.CABLE_TERMINAL.newItemStack();
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.NONE;
    }

    @Override
    public AxisAlignedBB getCollisionBox() {
        return switch (getSide()) {
            case WEST -> AxisAlignedBB.getBoundingBox(0f, W_MIN, W_MIN, DEPTH, W_MAX, W_MAX);
            case EAST -> AxisAlignedBB.getBoundingBox(1f - DEPTH, W_MIN, W_MIN, 1f, W_MAX, W_MAX);
            case DOWN -> AxisAlignedBB.getBoundingBox(W_MIN, 0f, W_MIN, W_MAX, DEPTH, W_MAX);
            case UP -> AxisAlignedBB.getBoundingBox(W_MIN, 1f - DEPTH, W_MIN, W_MAX, 1f, W_MAX);
            case NORTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 0f, W_MAX, W_MAX, DEPTH);
            case SOUTH -> AxisAlignedBB.getBoundingBox(W_MIN, W_MIN, 1f - DEPTH, W_MAX, W_MAX, 1f);
            default -> null;
        };
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/cable_terminal.png");
    }

    @Override
    public ResourceLocation getBackIcon() {
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/terminal_back.png");
    }

    public ItemNetwork getItemNetwork() {
        return (ItemNetwork) getCable().getNetwork(IItemPart.class);
    }
}
