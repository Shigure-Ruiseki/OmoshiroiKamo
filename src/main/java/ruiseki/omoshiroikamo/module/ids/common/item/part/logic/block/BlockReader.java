package ruiseki.omoshiroikamo.module.ids.common.item.part.logic.block;

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
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.BlockStack;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.LogicValues;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.item.part.AbstractReaderPart;

public class BlockReader extends AbstractReaderPart implements IBlockPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    @SideOnly(Side.CLIENT)
    private static IModelCustom model;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation texture;
    @SideOnly(Side.CLIENT)
    private static ResourceLocation back_texture;

    public BlockReader() {
        super(new ItemStackHandlerBase(8));
    }

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

        World world = getWorld();
        int x = targetX();
        int y = targetY();
        int z = targetZ();

        NBTTagCompound tag = new NBTTagCompound();

        boolean hasBlock = !world.isAirBlock(x, y, z);
        tag.setBoolean("hasBlock", hasBlock);

        tag.setString("dimension", world.provider.getDimensionName());
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);

        if (hasBlock) {
            tag.setTag(
                "block",
                BlockStack.fromWorld(world, x, y, z)
                    .serializeNBT());
            tag.setString("biome", world.getBiomeGenForCoords(x, z).biomeName);
            tag.setInteger("light", world.getBlockLightValue(x, y, z));
        }

        this.clientCache = tag;
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
        return IDsItems.BLOCK_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("block_reader");
        panel.height(196);

        // Settings panel
        IPanelHandler settingPanel = syncManager
            .syncedPanel("part_panel", true, (sm, sh) -> PartSettingPanel.build(this));
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));

        // Search
        StringValue searchValue = new StringValue("");

        Column col = new Column();
        TextFieldWidget searchWidget = new TextFieldWidget().value(searchValue)
            .width(162)
            .height(10)
            .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND);

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.width(162)
            .maxSize(85);

        // Rows
        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.hasBlock"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.hasBlock"),
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("hasBlock"))),
                0,
                LogicKeys.HAS_BLOCK),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.dimension"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.dimension"),
                IKey.dynamic(() -> String.valueOf(clientCache.getString("dimension"))),
                1,
                LogicKeys.DIMENSION),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.coord.x"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.coord.x"),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("x"))),
                2,
                LogicKeys.X),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.coord.y"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.coord.y"),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("y"))),
                3,
                LogicKeys.Y),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.coord.z"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.coord.z"),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("z"))),
                4,
                LogicKeys.Z),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.block"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.block"),
                IKey.dynamic(
                    () -> String.valueOf(
                        BlockStack.deserializeNBT(clientCache.getCompoundTag("block"))
                            .getDisplayName())),
                5,
                LogicKeys.BLOCK),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.biome"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.biome"),
                IKey.dynamic(() -> String.valueOf(clientCache.getString("biome"))),
                6,
                LogicKeys.BIOME),
            searchValue);

        addSearchableRow(
            list,
            LibMisc.LANG.localize("gui.ids.blockReader.light"),
            infoRow(
                LibMisc.LANG.localize("gui.ids.blockReader.light"),
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("light"))),
                7,
                LogicKeys.LIGHT_LEVEL),
            searchValue);

        col.coverChildren()
            .pos(7, 7)
            .childPadding(4)
            .child(searchWidget)
            .child(list);

        panel.child(col);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
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
    @SideOnly(Side.CLIENT)
    public void registerModel() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/reader.obj"));
        texture = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/block_reader_front.png");
        back_texture = new ResourceLocation(LibResources.PREFIX_ITEM + "ids/part/block_reader_back.png");
    }

    @Override
    @SideOnly(Side.CLIENT)
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
    @SideOnly(Side.CLIENT)
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

        if (key == LogicKeys.HAS_BLOCK) {
            return LogicValues.of(clientCache.getBoolean("hasBlock"));
        }

        if (key == LogicKeys.DIMENSION) {
            return LogicValues.of(clientCache.getString("dimension"));
        }

        if (key == LogicKeys.X) {
            return LogicValues.of(clientCache.getInteger("x"));
        }

        if (key == LogicKeys.Y) {
            return LogicValues.of(clientCache.getInteger("y"));
        }
        if (key == LogicKeys.Z) {
            return LogicValues.of(clientCache.getInteger("z"));
        }

        if (key == LogicKeys.BLOCK) {
            if (!clientCache.getBoolean("hasBlock")) return LogicValues.NULL;
            return LogicValues.of(BlockStack.deserializeNBT(clientCache.getCompoundTag("block")));
        }

        if (key == LogicKeys.BIOME) {
            return LogicValues.of(clientCache.getString("biome"));
        }

        if (key == LogicKeys.LIGHT_LEVEL) {
            return LogicValues.of(clientCache.getInteger("light"));
        }

        return LogicValues.NULL;
    }
}
