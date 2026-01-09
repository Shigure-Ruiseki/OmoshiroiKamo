package ruiseki.omoshiroikamo.module.cable.common.network.item.output;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.client.gui.data.PosSideGuiData;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemPart;

public class ItemOutputBus extends AbstractPart implements IItemPart {

    private static final float WIDTH = 6f / 16f; // 6px
    private static final float DEPTH = 4f / 16f; // 4px

    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @Override
    public String getId() {
        return "item_output_bus";
    }

    @Override
    public Class<? extends ICablePart> getBasePartType() {
        return IItemPart.class;
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public void doUpdate() {

    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.ITEM_INPUT_BUS.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(PosSideGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("item_output_bus");
        return panel;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.OUTPUT;
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
        return new ResourceLocation(LibResources.PREFIX_ITEM + "cable/item_output_bus.png");
    }
}
