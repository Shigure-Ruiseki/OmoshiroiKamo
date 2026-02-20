package ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.item.CraftingFilter;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.client.gui.container.TerminalGuiContainer;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPart;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.item.part.crafting.CraftingNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.crafting.ICraftingNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.IItemNet;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.ItemNetwork;

public class StorageTerminal extends AbstractPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 3f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @SideOnly(Side.CLIENT)
    private static IModelCustom model;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation texture;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation back_texture;

    public ItemStackHandlerBase craftingStackHandler = new ItemStackHandlerBase(10);
    public String CRAFTING_MATRIX_TAG = "CraftingMatrix";
    public SortType sortType = SortType.BY_NAME;
    public String SORT_TYPE_TAG = "SortType";
    public boolean sortOrder = true;
    public String SORT_ORDER_TAG = "SortOrder";
    public String search = "";
    public String SEARCH_TAG = "Search";
    public boolean syncNEI = false;
    public String SYNC_NEI_TAG = "SyncNEI";
    public CraftingFilter craftingFilter = CraftingFilter.BOTH;
    public String CRAFTING_FILTER_TAG = "CraftingFilter";

    public StorageTerminal() {
        setChannel(-1);
    }

    @Override
    public String getId() {
        return "storage_terminal";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Arrays.asList(IItemNet.class, IEnergyNet.class, ICraftingNet.class);
    }

    @Override
    public void doUpdate() {

    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag(CRAFTING_MATRIX_TAG, craftingStackHandler.serializeNBT());
        tag.setInteger(SORT_TYPE_TAG, sortType.getIndex());
        tag.setBoolean(SORT_ORDER_TAG, sortOrder);
        tag.setString(SEARCH_TAG, search);
        tag.setBoolean(SYNC_NEI_TAG, syncNEI);
        tag.setInteger(CRAFTING_FILTER_TAG, craftingFilter.getIndex());
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

        if (tag.hasKey(SEARCH_TAG)) {
            search = tag.getString(SEARCH_TAG);
        }

        if (tag.hasKey(SYNC_NEI_TAG)) {
            syncNEI = tag.getBoolean(SYNC_NEI_TAG);
        }

        if (tag.hasKey(CRAFTING_FILTER_TAG)) {
            craftingFilter = CraftingFilter.byIndex(tag.getInteger(CRAFTING_FILTER_TAG));
        }
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        settings.customGui(() -> TerminalGuiContainer::new);
        return new StorageTerminalPanel(data, syncManager, settings, this);
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.STORAGE_TERMINAL.newItemStack();
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.NONE;
    }

    public ItemNetwork getItemNetwork() {
        return (ItemNetwork) getCable().getNetwork(IItemNet.class);
    }

    public CraftingNetwork getCraftingNetwork() {
        return (CraftingNetwork) getCable().getNetwork(ICraftingNet.class);
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        if (this.sortType != sortType) {
            this.sortType = sortType;
            markDirty();
        }
    }

    public boolean getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(boolean sortOrder) {
        if (this.sortOrder != sortOrder) {
            this.sortOrder = sortOrder;
            markDirty();
        }
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        if (!Objects.equals(this.search, search)) {
            this.search = search;
            markDirty();
        }
    }

    public boolean getSyncNEI() {
        return syncNEI;
    }

    public void setSyncNEI(boolean syncNEI) {
        if (this.syncNEI != syncNEI) {
            this.syncNEI = syncNEI;
            markDirty();
        }
    }

    public CraftingFilter getCraftingFilter() {
        return craftingFilter;
    }

    public void setCraftingFilter(CraftingFilter craftingFilter) {
        if (this.craftingFilter != craftingFilter) {
            this.craftingFilter = craftingFilter;
            markDirty();
        }
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
    public void registerModel() {
        model = AdvancedModelLoader
            .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/storage_terminal.obj"));
        texture = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/storage_terminal.png");
        back_texture = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/terminal_back.png");
    }

    @Override
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderPart("front");

        RenderUtils.bindTexture(back_texture);
        model.renderPart("back");

        GL11.glPopMatrix();
    }

    @Override
    public void renderItemPart(IItemRenderer.ItemRenderType type, ItemStack stack, Tessellator tess) {
        GL11.glPushMatrix();

        switch (type) {
            case ENTITY:
                GL11.glTranslatef(0f, 0f, -0.5f);
                break;
            case EQUIPPED, EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.25f, 0.5f, 0.25f);
                break;
            case INVENTORY:
                GL11.glTranslatef(0.5f, 0f, 0f);
                break;
            default:
                GL11.glTranslatef(0f, 0f, 0f);
                break;
        }

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderPart("front");

        RenderUtils.bindTexture(back_texture);
        model.renderPart("back");

        GL11.glPopMatrix();
    }
}
