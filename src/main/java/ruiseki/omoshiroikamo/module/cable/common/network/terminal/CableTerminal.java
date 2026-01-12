package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.core.client.gui.widget.ItemStackDrawable;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexSyncHandler;
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

    @Override
    public @NotNull ModularPanel partPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ItemIndexClient.INSTANCE.destroy();
        ModularPanel panel = new ModularPanel("cable_terminal");

        ItemNetwork net = getItemNetwork();
        ItemIndexSyncHandler sh = new ItemIndexSyncHandler(() -> net);

        syncManager.syncValue("cable_terminal_sh", sh);

        buildItemGrid(panel);

        final int[] lastVersion = { -1 };

        syncManager.onClientTick(() -> {
            int v = ItemIndexClient.INSTANCE.getServerVersion();

            if (lastVersion[0] == -1) {
                lastVersion[0] = v;
                updateGrid(ItemIndexClient.INSTANCE.db);
                return;
            }

            if (cable.getWorld()
                .getWorldTime() % 20 != 0) return;

            if (v != lastVersion[0]) {
                lastVersion[0] = v;
                updateGrid(ItemIndexClient.INSTANCE.db);
            }
        });

        syncManager.onServerTick(() -> {
            if (!syncManager.isClient() && net != null) {
                int clientV = ItemIndexClient.INSTANCE.getServerVersion();
                int serverV = net.getIndexVersion();
                if (clientV != serverV) {
                    sh.requestSync();
                }
            }
        });

        return panel;
    }

    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT_COUNT = COLUMNS * ROWS;

    private final ItemStackDrawable[] slots = new ItemStackDrawable[SLOT_COUNT];

    private void buildItemGrid(ModularPanel panel) {

        int slotSize = 16;
        int padding = 2;

        for (int i = 0; i < SLOT_COUNT; i++) {
            int col = i % COLUMNS;
            int row = i / COLUMNS;

            ItemStackDrawable d = (ItemStackDrawable) new ItemStackDrawable().setItem((ItemStack) null);

            slots[i] = d;

            panel.child(
                d.asWidget()
                    .pos(col * (slotSize + padding), row * (slotSize + padding))
                    .size(slotSize, slotSize));
        }
    }

    private void updateGrid(Map<ItemStackKey, Integer> db) {

        int i = 0;
        for (var e : db.entrySet()) {
            if (i >= slots.length) break;

            ItemStack stack = e.getKey()
                .toStack(e.getValue());
            slots[i].setItem(stack);
            i++;
        }

        while (i < slots.length) {
            slots[i].setItem((ItemStack) null);
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
