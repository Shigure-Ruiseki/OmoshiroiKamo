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
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;
import ruiseki.omoshiroikamo.module.cable.common.network.item.PacketItemIndex;

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
    public @NotNull ModularPanel partPanel(PosSideGuiData data, PanelSyncManager sync, UISettings settings) {
        ItemNetwork net = getItemNetwork();
        if (net != null) {
            PacketHandler.INSTANCE.sendToAll(
                new PacketItemIndex(net.getItemIndexSnapshot())
            );
        }

        ModularPanel panel = new ModularPanel("cable_terminal");
        buildItemGrid(panel, ItemIndexClient.INSTANCE.db);
        return panel;
    }


    private void buildItemGrid(ModularPanel panel, Map<ItemStackKey, Integer> db) {
        panel.removeAll();

        int columns = 9;
        int slotSize = 18;
        int padding = 2;

        int index = 0;
        for (var e : db.entrySet()) {
            ItemStackKey key = e.getKey();
            int amount = e.getValue();

            ItemStack stack = key.toStack(amount);

            int col = index % columns;
            int row = index / columns;

            panel.child(
                new ItemStackDrawable()
                    .setItem(stack)
                    .asWidget()
                    .pos(col * (slotSize + padding), row * (slotSize + padding))
                    .size(slotSize, slotSize)
            );

            index++;
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
