package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.fluid;

import static ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures.VANILLA_SEARCH_BACKGROUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

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

public class FluidReader extends AbstractReaderPart implements IFluidPart {

    private static final float WIDTH = 10f / 16f;
    private static final float DEPTH = 5f / 16f;
    private static final float W_MIN = 0.5f - WIDTH / 2f;
    private static final float W_MAX = 0.5f + WIDTH / 2f;

    private static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "cable/reader.obj"));
    private static final ResourceLocation texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/fluid_reader_front.png");
    private static final ResourceLocation back_texture = new ResourceLocation(
        LibResources.PREFIX_ITEM + "cable/fluid_reader_back.png");

    private int fluidAmountTank = 0;
    private int fluidCapacityTank = 0;
    private int tankFluid = 0;

    public FluidReader() {
        super(new ItemStackHandlerBase(14));
    }

    @Override
    public String getId() {
        return "fluid_reader";
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

        IFluidHandler tank = getTank();
        clientCache = new NBTTagCompound();

        if (tank == null) {
            clientCache.setBoolean("isTank", false);
            return;
        }

        clientCache.setBoolean("isTank", true);

        FluidTankInfo[] infos = tank.getTankInfo(getSide().getOpposite());
        if (infos == null) infos = new FluidTankInfo[0];

        int totalAmount = 0;
        int totalCapacity = 0;
        boolean anyNotEmpty = false;
        boolean allEmpty = true;
        boolean allFull = true;

        for (int i = 0; i < infos.length; i++) {
            FluidTankInfo info = infos[i];
            int amount = info.fluid == null ? 0 : info.fluid.amount;
            int capacity = info.capacity;

            totalAmount += amount;
            totalCapacity += capacity;

            if (amount > 0) {
                anyNotEmpty = true;
                allEmpty = false;
            }
            if (amount < capacity) {
                allFull = false;
            }

            NBTTagCompound tankTag = new NBTTagCompound();
            tankTag.setInteger("amount", amount);
            tankTag.setInteger("capacity", capacity);
            if (info.fluid != null) {
                NBTTagCompound fluidTag = new NBTTagCompound();
                info.fluid.writeToNBT(fluidTag);
                tankTag.setTag("fluid", fluidTag);
            }
            clientCache.setTag("tank_" + i, tankTag);
        }

        clientCache.setInteger("tankCount", infos.length);
        clientCache.setInteger("totalAmount", totalAmount);
        clientCache.setInteger("totalCapacity", totalCapacity);
        clientCache.setBoolean("anyNotEmpty", anyNotEmpty);
        clientCache.setBoolean("allEmpty", allEmpty);
        clientCache.setBoolean("allFull", allFull);
    }

    @Override
    public void onDetached() {
        super.onDetached();
        inv.dropAll(getCable().getWorld(), getPos().x, getPos().y, getPos().z);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger("fluidAmountTank", fluidAmountTank);
        tag.setInteger("fluidCapacityTank", fluidCapacityTank);
        tag.setInteger("tankFluid", tankFluid);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        fluidAmountTank = tag.getInteger("fluidAmountTank");
        fluidCapacityTank = tag.getInteger("fluidCapacityTank");
        tankFluid = tag.getInteger("tankFluid");
    }

    @Override
    public ItemStack getItemStack() {
        return CableItems.FLUID_READER.newItemStack();
    }

    @Override
    public @NotNull ModularPanel partPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("fluid_reader");
        panel.height(200);

        // Settings
        IPanelHandler settingPanel = syncManager.panel("part_panel", (sm, sh) -> PartSettingPanel.build(this), true);
        panel.child(PartSettingPanel.addSettingButton(settingPanel));

        syncManager
            .syncValue("clientCacheSyncer", new StringSyncValue(this::getClientCacheNBT, this::setClientCacheNBT));
        syncManager.syncValue("fluidAmountTank", new IntSyncValue(() -> fluidAmountTank, v -> fluidAmountTank = v));

        syncManager
            .syncValue("fluidCapacityTank", new IntSyncValue(() -> fluidCapacityTank, v -> fluidCapacityTank = v));

        syncManager.syncValue("tankFluidId", new IntSyncValue(() -> tankFluid, v -> tankFluid = v));

        // Search
        StringValue searchValue = new StringValue("");

        panel.child(
            new TextFieldWidget().value(searchValue)
                .pos(7, 7)
                .width(162)
                .height(10)
                .background(VANILLA_SEARCH_BACKGROUND));

        // List
        ListWidget<Row, ?> list = new ListWidget<>();
        list.pos(7, 20)
            .width(162)
            .maxSize(85);

        panel.child(list);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.isTank")
                .get(),
            infoRow(
                "gui.cable.fluidReader.isTank",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("isTank"))),
                0,
                LogicKeys.IS_TANK),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankEmpty")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankEmpty",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("allEmpty"))),
                1,
                LogicKeys.TANK_EMPTY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankNotEmpty")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankNotEmpty",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("anyNotEmpty"))),
                2,
                LogicKeys.TANK_NOT_EMPTY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankFull")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankFull",
                IKey.dynamic(() -> String.valueOf(clientCache.getBoolean("allFull"))),
                3,
                LogicKeys.TANK_FULL),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankCount")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankCount",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("tankCount"))),
                4,
                LogicKeys.TANK),
            searchValue);

        IPanelHandler tankAmountSetting = syncManager.panel(
            "tankSetting",
            (syncManager1, syncHandler) -> tankSettingPanel(
                syncManager1,
                syncHandler,
                new IntSyncValue(this::getFluidAmountTank, this::setFluidAmountTank)),
            true);
        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.fluidAmount")
                .get(),
            infoRow("gui.cable.fluidReader.fluidAmount", IKey.dynamic(() -> {
                NBTTagCompound t = getTankTag(fluidAmountTank);
                return t == null ? "Empty" : String.valueOf(t.getInteger("amount"));
            }), 10, LogicKeys.FLUID_AMOUNT, tankAmountSetting),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.totalAmount")
                .get(),
            infoRow(
                "gui.cable.fluidReader.totalAmount",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("totalAmount"))),
                5,
                LogicKeys.TOTAL_FLUID_AMOUNT),
            searchValue);

        IPanelHandler tankCapacitySetting = syncManager.panel(
            "tankSetting",
            (syncManager1, syncHandler) -> tankSettingPanel(
                syncManager1,
                syncHandler,
                new IntSyncValue(this::getFluidCapacityTank, this::setFluidCapacityTank)),
            true);
        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.fluidCapacity")
                .get(),
            infoRow("gui.cable.fluidReader.fluidCapacity", IKey.dynamic(() -> {
                NBTTagCompound t = getTankTag(fluidCapacityTank);
                return t == null ? "Empty" : String.valueOf(t.getInteger("capacity"));
            }), 11, LogicKeys.FLUID_CAPACITY, tankCapacitySetting),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.totalCapacity")
                .get(),
            infoRow(
                "gui.cable.fluidReader.totalCapacity",
                IKey.dynamic(() -> String.valueOf(clientCache.getInteger("totalCapacity"))),
                6,
                LogicKeys.TOTAL_FLUID_CAPACITY),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.fillRatio")
                .get(),
            infoRow("gui.cable.fluidReader.fillRatio", IKey.dynamic(() -> {
                int cap = clientCache.getInteger("totalCapacity");
                int amt = clientCache.getInteger("totalAmount");
                return cap == 0 ? "0.00" : String.format("%.2f", (double) amt / cap);
            }), 7, LogicKeys.FLUID_FILL_RATIO),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankFluids")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankFluids",
                IKey.dynamic(this::buildTankFluidsText),
                8,
                LogicKeys.TANK_FLUIDS),
            searchValue);

        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankCapacities")
                .get(),
            infoRow(
                "gui.cable.fluidReader.tankCapacities",
                IKey.dynamic(this::buildTankCapacitiesText),
                9,
                LogicKeys.TANK_CAPACITIES),
            searchValue);

        IPanelHandler tankSetting = syncManager.panel(
            "tankSetting",
            (syncManager1, syncHandler) -> tankSettingPanel(
                syncManager1,
                syncHandler,
                new IntSyncValue(this::getTankFluid, this::setTankFluid)),
            true);
        addSearchableRow(
            list,
            IKey.lang("gui.cable.fluidReader.tankFluid")
                .get(),
            infoRow("gui.cable.fluidReader.tankFluid", IKey.dynamic(() -> {
                NBTTagCompound t = getTankTag(tankFluid);
                if (t == null || !t.hasKey("fluid")) return "";
                FluidStack fs = FluidStack.loadFluidStackFromNBT(t.getCompoundTag("fluid"));
                return fs == null ? "" : fs.getLocalizedName();
            }), 12, LogicKeys.TANK_FLUID, tankSetting),
            searchValue);

        panel.bindPlayerInventory();
        syncManager.bindPlayerInventory(data.getPlayer());

        return panel;
    }

    private ModularPanel tankSettingPanel(PanelSyncManager syncManager, IPanelHandler syncHandler, IntSyncValue value) {
        ModularPanel panel = new Dialog<>("fluid_setting").setDraggable(false)
            .setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false);

        Column col = new Column();

        Row selectTank = new Row();
        selectTank.coverChildren()
            .child(new TextWidget<>(IKey.lang("gui.cable.id")).width(162))
            .child(
                new TextFieldWidget().value(value)
                    .right(0)
                    .height(12)
                    .setNumbers()
                    .setDefaultNumber(0)
                    .setFormatAsInteger(true));

        col.coverChildren()
            .marginTop(16)
            .left(6)
            .childPadding(2)
            .child(selectTank);

        panel.child(ButtonWidget.panelCloseButton())
            .child(col);

        return panel;
    }

    private String buildTankCapacitiesText() {
        if (!clientCache.getBoolean("isTank")) {
            return IKey.lang("gui.empty")
                .get();
        }

        int count = clientCache.getInteger("tankCount");
        if (count <= 0) {
            return IKey.lang("gui.empty")
                .get();
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            NBTTagCompound t = clientCache.getCompoundTag("tank_" + i);
            int cap = t.getInteger("capacity");

            if (sb.length() > 0) sb.append(", ");
            sb.append(cap)
                .append("mB");

            if (sb.length() > 256) break;
        }

        return sb.length() == 0 ? IKey.lang("gui.empty")
            .get() : ellipsis(sb.toString(), 110);
    }

    private String buildTankFluidsText() {
        if (!clientCache.getBoolean("isTank")) return IKey.lang("gui.empty")
            .get();

        int count = clientCache.getInteger("tankCount");
        if (count <= 0) return IKey.lang("gui.empty")
            .get();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            NBTTagCompound t = clientCache.getCompoundTag("tank_" + i);
            if (!t.hasKey("fluid")) continue;

            FluidStack fs = FluidStack.loadFluidStackFromNBT(t.getCompoundTag("fluid"));
            if (fs == null) continue;

            if (sb.length() > 0) sb.append(", ");
            sb.append(fs.getLocalizedName())
                .append(" ")
                .append(fs.amount)
                .append("mB");

            if (sb.length() > 256) break;
        }

        return sb.length() == 0 ? IKey.lang("gui.empty")
            .get() : ellipsis(sb.toString(), 110);
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

        if (!clientCache.getBoolean("isTank")) {
            if (key == LogicKeys.IS_TANK) return LogicValues.of(false);
            return LogicValues.NULL;
        }

        if (key == LogicKeys.IS_TANK) return LogicValues.of(true);

        if (key == LogicKeys.TANK_EMPTY) return LogicValues.of(clientCache.getBoolean("allEmpty"));

        if (key == LogicKeys.TANK_NOT_EMPTY) return LogicValues.of(clientCache.getBoolean("anyNotEmpty"));

        if (key == LogicKeys.TANK_FULL) return LogicValues.of(clientCache.getBoolean("allFull"));

        if (key == LogicKeys.FLUID_AMOUNT) {
            NBTTagCompound tank = getTankTag(fluidAmountTank);
            if (tank == null) return LogicValues.NULL;
            return LogicValues.of(tank.getInteger("amount"));
        }

        if (key == LogicKeys.TOTAL_FLUID_AMOUNT) return LogicValues.of(clientCache.getInteger("totalAmount"));

        if (key == LogicKeys.FLUID_CAPACITY) {
            NBTTagCompound tank = getTankTag(fluidCapacityTank);
            if (tank == null) return LogicValues.NULL;
            return LogicValues.of(tank.getInteger("capacity"));
        }

        if (key == LogicKeys.TOTAL_FLUID_CAPACITY) return LogicValues.of(clientCache.getInteger("totalCapacity"));

        if (key == LogicKeys.TANK) return LogicValues.of(clientCache.getInteger("tankCount"));

        if (key == LogicKeys.FLUID_FILL_RATIO) {
            int cap = clientCache.getInteger("totalCapacity");
            if (cap == 0) return LogicValues.of(0D);
            return LogicValues.of((double) clientCache.getInteger("totalAmount") / cap);
        }

        if (key == LogicKeys.TANK_FLUIDS) {
            List<ILogicValue> list = new ArrayList<>();
            int count = clientCache.getInteger("tankCount");
            for (int i = 0; i < count; i++) {
                NBTTagCompound t = clientCache.getCompoundTag("tank_" + i);
                if (t.hasKey("fluid")) {
                    list.add(LogicValues.of(FluidStack.loadFluidStackFromNBT(t.getCompoundTag("fluid"))));
                }
            }
            return LogicValues.of(list);
        }

        if (key == LogicKeys.TANK_CAPACITIES) {
            List<ILogicValue> list = new ArrayList<>();
            int count = clientCache.getInteger("tankCount");
            for (int i = 0; i < count; i++) {
                list.add(
                    LogicValues.of(
                        clientCache.getCompoundTag("tank_" + i)
                            .getInteger("capacity")));
            }
            return LogicValues.of(list);
        }

        if (key == LogicKeys.TANK_FLUID) {
            NBTTagCompound tank = getTankTag(tankFluid);
            if (tank == null || !tank.hasKey("fluid")) return LogicValues.NULL;

            FluidStack fluid = FluidStack.loadFluidStackFromNBT(tank.getCompoundTag("fluid"));
            return LogicValues.of(fluid);
        }

        return LogicValues.NULL;
    }

    private IFluidHandler getTank() {
        TileEntity te = getTargetTE();
        if (te instanceof IFluidHandler handler) {
            return handler;
        }
        return null;
    }

    private NBTTagCompound getTankTag(int id) {
        int count = clientCache.getInteger("tankCount");
        if (id < 0 || id >= count) return null;
        return clientCache.getCompoundTag("tank_" + id);
    }

    public int getFluidAmountTank() {
        return fluidAmountTank;
    }

    public int getFluidCapacityTank() {
        return fluidCapacityTank;
    }

    public int getTankFluid() {
        return tankFluid;
    }

    public void setFluidAmountTank(int fluidAmountTank) {
        this.fluidAmountTank = fluidAmountTank;
        markDirty();
    }

    public void setFluidCapacityTank(int fluidCapacityTank) {
        this.fluidCapacityTank = fluidCapacityTank;
        markDirty();
    }

    public void setTankFluid(int tankFluid) {
        this.tankFluid = tankFluid;
        markDirty();
    }
}
