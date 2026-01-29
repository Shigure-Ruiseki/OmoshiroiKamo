package ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.redstone;

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
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.PartSettingPanel;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.ILogicNet;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.AbstractReaderPart;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.value.LogicValues;

public class RedstoneReader extends AbstractReaderPart implements IRedstonePart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "ids/reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/redstone_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "ids/redstone_reader_back.png");

    private static final int HIGH_THRESHOLD = 8;

    private final ItemStackHandlerBase inv = new ItemStackHandlerBase(4);

    public RedstoneReader() {
        super(new ItemStackHandlerBase(4));
    }

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
        if (!shouldTickNow()) return;

        int value = getRedstoneInput();

        clientCache = new NBTTagCompound();
        clientCache.setInteger("value", value);
        clientCache.setBoolean("has", value > 0);
        clientCache.setBoolean("high", value >= HIGH_THRESHOLD);
        clientCache.setBoolean("low", value > 0 && value < HIGH_THRESHOLD);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        inv.dropAll(getCable().getWorld(), getPos().x, getPos().y, getPos().z);
    }

    @Override
    public ItemStack getItemStack() {
        return IDsItems.REDSTONE_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("redstone_reader");
        panel.height(200);

        // Settings
        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        // Sync
        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));

        // Search
        StringValue searchValue = new StringValue("");

        panel.child(
            new TextFieldWidget().value(searchValue)
                .pos(7, 7)
                .width(162)
                .height(10)
                .background(OKGuiTextures.VANILLA_SEARCH_BACKGROUND));

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.pos(7, 20)
            .width(162)
            .maxSize(85);

        panel.child(list);

        // Rows
        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneReader.redstoneValue")
                .get(),
            infoRow(
                "gui.ids.redstoneReader.redstoneValue",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("value"))),
                3,
                LogicKeys.REDSTONE_VALUE),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneReader.hasRedstone")
                .get(),
            infoRow(
                "gui.ids.redstoneReader.hasRedstone",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("has"))),
                1,
                LogicKeys.HAS_REDSTONE),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneReader.highRedstone")
                .get(),
            infoRow(
                "gui.ids.redstoneReader.highRedstone",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("high"))),
                2,
                LogicKeys.HIGH_REDSTONE),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.ids.redstoneReader.lowRedstone")
                .get(),
            infoRow(
                "gui.ids.redstoneReader.lowRedstone",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("low"))),
                0,
                LogicKeys.LOW_REDSTONE),
            searchValue);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

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

        if (key == LogicKeys.REDSTONE_VALUE) return LogicValues.of(clientCache.getInteger("value"));

        if (key == LogicKeys.HAS_REDSTONE) return LogicValues.of(clientCache.getBoolean("has"));

        if (key == LogicKeys.HIGH_REDSTONE) return LogicValues.of(clientCache.getBoolean("high"));

        if (key == LogicKeys.LOW_REDSTONE) return LogicValues.of(clientCache.getBoolean("low"));

        return LogicValues.NULL;
    }
}
