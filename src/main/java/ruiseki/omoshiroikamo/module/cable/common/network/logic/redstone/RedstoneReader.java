package ruiseki.omoshiroikamo.module.cable.common.network.logic.redstone;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPart;
import ruiseki.omoshiroikamo.module.cable.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicNet;

public class RedstoneReader extends AbstractPart implements IRedstonePart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 4f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private int redstoneValue = 0;
    private boolean hasRedstone = false;
    private boolean highRedstone = false;
    private boolean lowRedstone = false;

    private static final int HIGH_THRESHOLD = 8;

    @Override
    public String getId() {
        return "redstone_reader";
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Collections.singletonList(ILogicNet.class);
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.INPUT;
    }

    @Override
    public void doUpdate() {
        if (!shouldDoTickInterval()) return;

        int newValue = getRedstoneInput();
        if (newValue != this.redstoneValue) {
            setRedstone(newValue);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.REDSTONE_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {

        ModularPanel panel = new ModularPanel("redstone_reader");

        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        syncManager.syncValue("redstoneSyncer", new IntSyncValue(this::getRedstoneValue));

        Column col = new Column();

        Row lowRedRow = new Row();
        lowRedRow.child(
            IKey.lang("gui.cable.redstoneReader.lowRedstone")
                .asWidget())
            .child(
                IKey.dynamic(() -> String.valueOf(lowRedstone))
                    .asWidget())
            .height(18);

        Row hasRedRow = new Row();
        hasRedRow.child(
            IKey.lang("gui.cable.redstoneReader.hasRedstone")
                .asWidget())
            .child(
                IKey.dynamic(() -> String.valueOf(hasRedstone))
                    .asWidget())
            .height(18);

        Row highRedRow = new Row();
        highRedRow.child(
            IKey.lang("gui.cable.redstoneReader.highRedstone")
                .asWidget())
            .child(
                IKey.dynamic(() -> String.valueOf(highRedstone))
                    .asWidget())
            .height(18);

        Row valueRedRow = new Row();
        valueRedRow.child(
            IKey.lang("gui.cable.redstoneReader.redstoneValue")
                .asWidget())
            .child(
                IKey.dynamic(() -> String.valueOf(redstoneValue))
                    .asWidget())
            .height(18);

        col.padding(7)
            .child(highRedRow)
            .child(hasRedRow)
            .child(lowRedRow)
            .child(valueRedRow);
        panel.child(col);

        return panel;
    }

    @Override
    public int getRedstoneOutput() {
        return 0;
    }

    @Override
    public int getRedstoneInput() {
        if (cable == null || cable.getWorld() == null) return 0;

        ForgeDirection side = getSide();
        World world = cable.getWorld();

        int x = getPos().x + side.offsetX;
        int y = getPos().y + side.offsetY;
        int z = getPos().z + side.offsetZ;

        int weak = world.getIndirectPowerLevelTo(
            x,
            y,
            z,
            side.getOpposite()
                .ordinal());

        int strong = world.getStrongestIndirectPower(x, y, z);

        return Math.max(weak, strong);
    }

    @Override
    public int getRedstoneValue() {
        return redstoneValue;
    }

    @Override
    public boolean hasRedstone() {
        return hasRedstone;
    }

    @Override
    public boolean isHighRedstone() {
        return highRedstone;
    }

    @Override
    public boolean isLowRedstone() {
        return lowRedstone;
    }

    public void setRedstone(int value) {
        this.redstoneValue = value;
        this.hasRedstone = value > 0;
        this.highRedstone = value >= HIGH_THRESHOLD;
        this.lowRedstone = value > 0 && value < HIGH_THRESHOLD;
        cable.dirty();
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

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "cable/redstone_reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/redstone_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/redstone_reader_back.png");

    @Override
    public void renderPart(Tessellator tess, float partialTicks) {
        GL11.glPushMatrix();

        rotateForSide(getSide());

        RenderUtils.bindTexture(texture);
        model.renderAllExcept("back");

        RenderUtils.bindTexture(back_texture);
        model.renderOnly("back");

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
        model.renderAllExcept("back");

        RenderUtils.bindTexture(back_texture);
        model.renderOnly("back");

        GL11.glPopMatrix();
    }

}
