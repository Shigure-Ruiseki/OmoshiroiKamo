package ruiseki.omoshiroikamo.module.machinery.common.tile.energy;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;
import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractEnergyTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.ToggleWidget;

/**
 * Extends AbstractEnergyTE to leverage existing energy management system.
 */
public abstract class AbstractEnergyIOPortTE extends AbstractEnergyTE implements IModularPort {

    protected final IO[] sides = new IO[6];

    @Getter
    @Setter
    private EnergyMode energyMode = EnergyMode.RF;

    @Getter
    @Setter
    public boolean useIC2Compat = false;

    public AbstractEnergyIOPortTE(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
        for (int i = 0; i < 6; i++) {
            sides[i] = IO.NONE;
        }
    }

    public abstract int getTier();

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ENERGY;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * Extract energy for machine processing.
     *
     * @param amount requested amount
     * @return amount actually extracted
     */
    public int extractEnergy(int amount) {
        int extracted = Math.min(energyStorage.getEnergyStored(), amount);
        energyStorage.voidEnergy(extracted);
        return extracted;
    }

    @Override
    public IO getSideIO(ForgeDirection side) {
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, IO state) {
        sides[side.ordinal()] = state;
        requestRenderUpdate();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        int[] sideData = new int[6];
        for (int i = 0; i < 6; i++) {
            sideData[i] = sides[i].ordinal();
        }
        root.setIntArray("sideIO", sideData);
        root.setInteger("energyMode", energyMode.ordinal());
        root.setBoolean("useIC2", useIC2Compat);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        if (root.hasKey("sideIO")) {
            int[] sideData = root.getIntArray("sideIO");
            for (int i = 0; i < 6 && i < sideData.length; i++) {
                sides[i] = IO.values()[sideData[i]];
            }
        }
        energyMode = EnergyMode.byIndex(root.getInteger("energyMode"));
        useIC2Compat = root.getBoolean("useIC2");
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    public String getEnergyText() {
        if (energyMode == EnergyMode.EU) {
            double eu = (double) getEnergyStored() / EnergyConfig.rftToEU;
            double max = (double) getMaxEnergyStored() / EnergyConfig.rftToEU;
            return String.format("%.1f / %.1f EU", eu, max);
        } else {
            return getEnergyStored() + " / " + getMaxEnergyStored() + " RF";
        }
    }

    public String getEnergyUsedText() {
        if (energyMode == EnergyMode.EU) {
            double eu = (double) energyStorage.getMaxReceive() / EnergyConfig.rftToEU;
            return LibMisc.LANG.localize("gui.machinery.energy_used", String.format("%.1f EU/t", eu));
        } else {
            return LibMisc.LANG.localize("gui.machinery.energy_used", energyStorage.getMaxReceive() + " RF/t");
        }
    }

    public static final UITexture EU_MODE = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(16, 16, 16, 16)
        .build();
    public static final UITexture RF_MODE = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/machinery/icons")
        .imageSize(256, 256)
        .xy(0, 16, 16, 16)
        .build();

    private static final List<CyclicVariantButtonWidget.Variant> ENERGY_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.energy_mode.rf"), RF_MODE),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.energy_mode.eu"), EU_MODE));

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("energy_port_gui");

        EnumSyncValue<RedstoneMode> redstoneSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            this::getRedstoneMode,
            this::setRedstoneMode);
        syncManager.syncValue("redstoneSyncer", redstoneSyncer);
        IntSyncValue energySyncer = new IntSyncValue(this::getEnergyStored, this::setEnergyStored);
        syncManager.syncValue("energySyncer", energySyncer);
        syncManager.syncValue("maxEnergySyncer", new IntSyncValue(this::getMaxEnergyStored));
        BooleanSyncValue ic2CompatSyncer = new BooleanSyncValue(this::isUseIC2Compat, this::setUseIC2Compat);
        syncManager.syncValue("ic2CompatSyncer", ic2CompatSyncer);

        EnumSyncValue<EnergyMode> modeSyncer = new EnumSyncValue<>(
            EnergyMode.class,
            this::getEnergyMode,
            this::setEnergyMode);
        syncManager.syncValue("modeSyncer", modeSyncer);

        panel.child(
            new RedstoneModeWidget(redstoneSyncer).pos(-20, 2)
                .size(18)
                .excludeAreaInRecipeViewer());

        panel.child(
            new CyclicVariantButtonWidget(
                ENERGY_VARIANTS,
                modeSyncer.getValue()
                    .ordinal(),
                1,
                16,
                value -> { modeSyncer.setValue(EnergyMode.byIndex(value)); }).size(18)
                    .pos(-20, 22)
                    .excludeAreaInRecipeViewer());

        panel.child(
            new ProgressWidget().texture(GuiTextures.ENERGY_BAR, 64)
                .size(16, 64)
                .value(new DoubleSyncValue(() -> (double) this.getEnergyStored() / this.getMaxEnergyStored()))
                .tooltipAutoUpdate(true)
                .direction(ProgressWidget.Direction.UP)
                .tooltipDynamic(tooltip -> {
                    tooltip.addLine(this.getEnergyText());
                    tooltip.addLine(getEnergyUsedText());
                    tooltip.pos(RichTooltip.Pos.NEXT_TO_MOUSE);
                })
                .pos(8, 6));

        panel.child(new TileWidget(this.getLocalizedName()));

        panel.child(
            IKey.lang(data.getPlayer().inventory.getInventoryName())
                .asWidget()
                .pos(8, 72));

        Column column = (Column) new Column().coverChildren()
            .pos(28, 6)
            .childPadding(2);

        panel.child(column);

        column.child(
            IKey.dynamic(this::getEnergyText)
                .asWidget()
                .left(0));
        column.child(
            IKey.dynamic(this::getEnergyUsedText)
                .asWidget()
                .left(0));

        column.child(
            new Row().coverChildren()
                .left(0)
                .childPadding(2)
                .child(new ToggleWidget(ic2CompatSyncer))
                .child(
                    IKey.lang("gui.machinery.use_ic2")
                        .asWidget()));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

    public enum EnergyMode {

        RF,
        EU;

        private static final ImmutableList<EnergyMode> values = ImmutableList.copyOf(values());

        public static EnergyMode byIndex(int index) {
            if (index < 0 || index >= values.size()) return RF;

            return values.get(index);
        }
    }

}
