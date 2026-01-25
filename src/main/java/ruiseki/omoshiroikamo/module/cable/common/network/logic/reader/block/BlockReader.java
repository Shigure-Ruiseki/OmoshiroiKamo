package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.block;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

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

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.block.BlockStack;
import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.AbstractReaderPart;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKey;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKeys;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.value.LogicValues;

public class BlockReader extends AbstractReaderPart implements IBlockPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "cable/reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/block_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/block_reader_back.png");

    private static final int SLOT_HAS_BLOCK = 0;
    private static final int SLOT_DIMENSION = 1;
    private static final int SLOT_COORD_X = 2;
    private static final int SLOT_COORD_Y = 3;
    private static final int SLOT_COORD_Z = 4;
    private static final int SLOT_BLOCK = 5;
    private static final int SLOT_BIOME = 6;
    private static final int SLOT_LIGHT = 7;

    private final ItemStackHandlerBase inv = new ItemStackHandlerBase(8);

    @Override
    public String getId() {
        return "block_reader";
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
        if (!shouldTickNow()) return;
    }

    @Override
    public void onDetached() {
        super.onDetached();
        inv.dropAll(getCable().getWorld(), getPos().x, getPos().y, getPos().z);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("item_inv", this.inv.serializeNBT());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.inv.deserializeNBT(tag.getCompoundTag("item_inv"));
    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.BLOCK_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("block_reader");

        panel.height(200);

        // Settings panel
        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        // Search
        StringValue searchValue = new StringValue("");

        panel.child(
            new TextFieldWidget().value(searchValue)
                .pos(7, 7)
                .width(162)
                .height(16)
                .background(VANILLA_SEARCH_BACKGROUND));

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.pos(7, 25)
            .width(162)
            .maxSize(90);

        panel.child(list);

        // Rows
        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.hasBlock")
                .get(),
            infoRow(
                "gui.cable.blockReader.hasBlock",
                IKey.dynamic(() -> String.valueOf(!world().isAirBlock(readX(), readY(), readZ()))),
                0,
                LogicKeys.HAS_BLOCK,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.dimension")
                .get(),
            infoRow(
                "gui.cable.blockReader.dimension",
                IKey.dynamic(() -> world().provider.getDimensionName()),
                1,
                LogicKeys.DIMENSION,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.coord.x")
                .get(),
            infoRow("gui.cable.blockReader.coord.x", IKey.dynamic(() -> String.valueOf(readX())), 2, LogicKeys.X, inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.coord.y")
                .get(),
            infoRow("gui.cable.blockReader.coord.y", IKey.dynamic(() -> String.valueOf(readY())), 3, LogicKeys.Y, inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader..coord.z")
                .get(),
            infoRow("gui.cable.blockReader.coord.z", IKey.dynamic(() -> String.valueOf(readZ())), 4, LogicKeys.Z, inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.block")
                .get(),
            infoRow(
                "gui.cable.blockReader.block",
                IKey.dynamic(
                    () -> world().getBlock(readX(), readY(), readZ())
                        .getLocalizedName()),
                5,
                LogicKeys.BLOCK,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.biome")
                .get(),
            infoRow(
                "gui.cable.blockReader.biome",
                IKey.dynamic(() -> world().getBiomeGenForCoords(readX(), readZ()).biomeName),
                6,
                LogicKeys.BIOME,
                inv),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.blockReader.light")
                .get(),
            infoRow(
                "gui.cable.blockReader.light",
                IKey.dynamic(() -> String.valueOf(world().getBlockLightValue(readX(), readY(), readZ()))),
                7,
                LogicKeys.LIGHT_LEVEL,
                inv),
            searchValue);

        // ===== Inventory =====
        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
    }

    private World world() {
        return getCable().getWorld();
    }

    private int readX() {
        return getPos().offset(getSide()).x;
    }

    private int readY() {
        return getPos().offset(getSide()).y;
    }

    private int readZ() {
        return getPos().offset(getSide()).z;
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

    @Override
    public ILogicValue read(LogicKey key) {
        World world = world();
        int x = readX();
        int y = readY();
        int z = readZ();

        if (key == LogicKeys.HAS_BLOCK) {
            return LogicValues.of(!world.isAirBlock(x, y, z));
        }

        if (key == LogicKeys.DIMENSION) {
            return LogicValues.of(world.provider.getDimensionName());
        }

        if (key == LogicKeys.X) return LogicValues.of(x);
        if (key == LogicKeys.Y) return LogicValues.of(y);
        if (key == LogicKeys.Z) return LogicValues.of(z);

        if (key == LogicKeys.BLOCK) {
            return LogicValues.of(BlockStack.fromWorld(world, x, y, z));
        }

        if (key == LogicKeys.BIOME) {
            return LogicValues.of(world.getBiomeGenForCoords(x, z).biomeName);
        }

        if (key == LogicKeys.LIGHT_LEVEL) {
            return LogicValues.of(world.getBlockLightValue(x, y, z));
        }

        return LogicValues.NULL;
    }
}
