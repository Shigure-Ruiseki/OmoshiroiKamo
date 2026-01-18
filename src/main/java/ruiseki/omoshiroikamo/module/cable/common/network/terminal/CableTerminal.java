package ruiseki.omoshiroikamo.module.cable.common.network.terminal;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.client.gui.container.TerminalGuiContainer;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.cable.common.network.item.IItemNet;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;

public class CableTerminal extends AbstractPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 3f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    public ItemStackHandlerBase craftingStackHandler = new ItemStackHandlerBase(10);
    public String CRAFTING_MATRIX_TAG = "CraftingMatrix";
    public SortType sortType = SortType.BY_NAME;
    public String SORT_TYPE_TAG = "SortType";
    public boolean sortOrder = true;
    public String SORT_ORDER_TAG = "SortOrder";

    public CableTerminal() {
        setChannel(-1);
    }

    @Override
    public String getId() {
        return "cable_terminal";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IItemNet.class, IEnergyNet.class);
    }

    @Override
    public void doUpdate() {

    }

    @Override
    public void onChunkUnload() {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag(CRAFTING_MATRIX_TAG, craftingStackHandler.serializeNBT());
        tag.setInteger(SORT_TYPE_TAG, sortType.getIndex());
        tag.setBoolean(SORT_ORDER_TAG, sortOrder);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey(CRAFTING_MATRIX_TAG)) {
            craftingStackHandler.deserializeNBT(tag.getCompoundTag(CRAFTING_MATRIX_TAG));
        }

        if (tag.hasKey(SORT_TYPE_TAG)) {
            sortType = SortType.byIndex(tag.getInteger(SORT_TYPE_TAG));
        }

        if (tag.hasKey(SORT_ORDER_TAG)) {
            sortOrder = tag.getBoolean(SORT_ORDER_TAG);
        }
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return new TerminalPanel(data, syncManager, settings, this);
    }

    @Override
    public GuiContainerWrapper createGuiContainer(ModularContainer container, ModularScreen screen) {
        return new TerminalGuiContainer(container, screen);
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
        return (ItemNetwork) getCable().getNetwork(IItemNet.class);
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        if (this.sortType != sortType) {
            this.sortType = sortType;
            getCable().dirty();
        }
    }

    public boolean getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(boolean sortOrder) {
        if (this.sortOrder != sortOrder) {
            this.sortOrder = sortOrder;
            getCable().dirty();
        }
    }

}
