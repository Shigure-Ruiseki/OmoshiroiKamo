package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import java.util.Collections;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import cpw.mods.fml.common.Optional;
import mekanism.api.lasers.ILaserReceptor;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICableEndpoint;
import ruiseki.omoshiroikamo.api.cable.ICableNode;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.EnergyUtils;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.EnergyNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.energy.IEnergyNet;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 * Also supports Mekanism laser energy when Mekanism is present.
 */
@Optional.Interface(iface = "mekanism.api.lasers.ILaserReceptor", modid = "Mekanism")
public abstract class TEEnergyInputPort extends AbstractEnergyIOPortTE
    implements IOKEnergySink, ILaserReceptor, ICableEndpoint, IEnergyNet {

    protected ICable cable;
    protected ForgeDirection side;

    public TEEnergyInputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive()) {
            EnergyTransfer transfer = new EnergyTransfer();

            EnergyNetwork network = getEnergyNetwork();
            if (!(network == null || network.interfaces == null || network.interfaces.isEmpty())) {
                for (IEnergyNet iFace : network.interfaces) {
                    if (iFace.getChannel() != this.getChannel()) {
                        continue;
                    }

                    transfer.sink(EnergyUtils.getEnergySink(this, side));
                    transfer.source(
                        EnergyUtils.getEnergySource(
                            iFace.getTargetTE(),
                            iFace.getSide()
                                .getOpposite()));
                    transfer.transfer();
                }
            }

            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canInput()) {
                    continue;
                }
                TileEntity source = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.pull(this, direction, source);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (isRedstoneActive() && this.side == side && cable != null) {
            return energyStorage.receiveEnergy(amount, simulate);
        }
        if (!isRedstoneActive() || !canInput(side)) {
            return 0;
        }
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public void receiveLaserEnergy(double amount, ForgeDirection side) {
        if (!isRedstoneActive() || !canInput(side)) {
            return;
        }
        this.receiveEnergy(side, (int) amount, false);
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public boolean canLasersDig() {
        return false;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_energyinput_" + getTier());
            }
            return IconRegistry.getIcon("overlay_energyinput_disabled");
        }
        return AbstractPortBlock.baseIcon;
    }

    @Override
    public String getId() {
        return "te_energy_input";
    }

    @Override
    public ICable getCable() {
        return cable;
    }

    @Override
    public List<Class<? extends ICableNode>> getBaseNodeTypes() {
        return Collections.singletonList(IEnergyNet.class);
    }

    @Override
    public ForgeDirection getSide() {
        return side;
    }

    @Override
    public EnumIO getIO() {
        return EnumIO.OUTPUT;
    }

    @Override
    public TileEntity getTargetTE() {
        return this;
    }

    @Override
    public void setSide(ForgeDirection side) {
        this.side = side;
    }

    @Override
    public void setCable(ICable cable, ForgeDirection side) {
        this.cable = cable;
        setSide(side);
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }

    @Override
    public int getTickInterval() {
        return 1;
    }

    @Override
    public void setTickInterval(int tickInterval) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void setPriority(int priority) {

    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public void setChannel(int chanel) {

    }

    @Override
    public @NotNull ModularPanel endpointPanel(SidedPosGuiData data, PanelSyncManager syncManager,
        UISettings settings) {
        return new ModularPanel(getId());
    }
}
