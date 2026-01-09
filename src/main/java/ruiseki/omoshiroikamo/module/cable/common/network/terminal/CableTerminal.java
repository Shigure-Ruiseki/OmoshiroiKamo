package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

import java.util.Collections;
import java.util.Map;

public class CableTerminal extends AbstractPart {

    private static final float WIDTH = 10f / 16f; // 10px
    private static final float DEPTH = 3f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private Map<ItemStackKey, Integer> cachedView = Collections.emptyMap();
    private int lastNetworkVersion = -1;

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
    public void doUpdate() {
        ItemNetwork net = getItemNetwork();
        if (net == null) return;

        int ver = net.getIndexVersion();
        if (ver != lastNetworkVersion) {
            lastNetworkVersion = ver;
            cachedView = net.getItemIndexSnapshot();
        }
    }


    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.CABLE_TERMINAL.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("cable_terminal");
        return panel;
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

    private ItemNetwork getItemNetwork() {
        ICable cable = getCable();
        if (cable == null) return null;

        return (ItemNetwork) cable.getNetwork(IItemPart.class);
    }

    public Map<ItemStackKey, Integer> getVisibleItems() {
        return cachedView;
    }

    private static String formatItemMap(Map<ItemStackKey, Integer> map) {
        if (map.isEmpty()) return "<empty>";

        StringBuilder sb = new StringBuilder();
        sb.append("Items[").append(map.size()).append("]:\n");

        for (Map.Entry<ItemStackKey, Integer> e : map.entrySet()) {
            sb.append(" - ")
                .append(e.getKey())
                .append(" x ")
                .append(e.getValue())
                .append('\n');
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        ItemNetwork net = getItemNetwork();
        int ver = net != null ? net.getIndexVersion() : -1;

        return "CableTerminal{ver=" + ver + ", cached=" + lastNetworkVersion + "} "
            + formatItemMap(cachedView);
    }
}
