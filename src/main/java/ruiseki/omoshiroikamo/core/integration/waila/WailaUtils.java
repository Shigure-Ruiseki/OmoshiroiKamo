package ruiseki.omoshiroikamo.core.integration.waila;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import mcp.mobius.waila.api.SpecialChars;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;
import ruiseki.omoshiroikamo.api.gas.GasTankInfo;
import ruiseki.omoshiroikamo.api.gas.IGasHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class WailaUtils {

    public static String getProgress(IProgressTile tile) {
        float progress = tile.getProgress();
        return LibMisc.LANG.localize("gui.progress", progress * 100);
    }

    public static String getCraftingState(ICraftingTile tile) {
        return LibMisc.LANG.localize(
            "gui.craftingState." + tile.getCraftingState()
                .getName());
    }

    public static String getEnergyTransfer(IEnergyTile tile) {
        return LibMisc.LANG.localize("gui.energy_transfer", tile.getEnergyTransfer());
    }

    public static List<String> getTankTooltip(IFluidHandler handler) {
        if (handler == null) return new ArrayList<>();

        List<String> list = new ArrayList<>();
        FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);

        if (tanks == null) return list;

        for (FluidTankInfo tank : tanks) {
            if (tank == null) continue;

            boolean empty = tank.fluid == null;

            list.add(
                SpecialChars.getRenderString(
                    "waila.fluid",
                    empty ? "EMPTYFLUID"
                        : tank.fluid.getFluid()
                            .getName(),
                    empty ? "EMPTYFLUID" : tank.fluid.getLocalizedName(),
                    String.valueOf(empty ? 0 : tank.fluid.amount),
                    String.valueOf(tank.capacity)));
        }
        return list;
    }

    public static List<String> getGasTooltip(IGasHandler handler) {
        if (handler == null) return new ArrayList<>();

        List<String> list = new ArrayList<>();
        GasTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);

        if (tanks == null) return list;

        for (GasTankInfo tank : tanks) {
            if (tank == null) continue;

            boolean empty = tank.gas == null;

            list.add(
                SpecialChars.getRenderString(
                    "waila.fluid",
                    empty ? "EMPTYFLUID"
                        : tank.gas.getGas()
                            .getName(),
                    empty ? "EMPTYFLUID"
                        : tank.gas.getGas()
                            .getLocalizedName(),
                    String.valueOf(empty ? 0 : tank.gas.amount),
                    String.valueOf(tank.capacity)));
        }
        return list;
    }
}
