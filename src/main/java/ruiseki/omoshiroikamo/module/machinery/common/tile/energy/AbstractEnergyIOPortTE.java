package ruiseki.omoshiroikamo.module.machinery.common.tile.energy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.io.ISidedIO;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractEnergyTE;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Extends AbstractEnergyTE to leverage existing energy management system.
 */
public abstract class AbstractEnergyIOPortTE extends AbstractEnergyTE implements ISidedIO {

    protected final IO[] sides = new IO[6];

    public AbstractEnergyIOPortTE(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
        for (int i = 0; i < 6; i++) {
            sides[i] = IO.NONE;
        }
    }

    public abstract int getTier();

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
        Logger.info(getSideIO(side).name());
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        int[] sideData = new int[6];
        for (int i = 0; i < 6; i++) {
            sideData[i] = sides[i].ordinal();
        }
        root.setIntArray("sideIO", sideData);
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
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("energy_gui");
        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }
}
