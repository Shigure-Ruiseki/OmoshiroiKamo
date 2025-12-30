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
        if (handler == null) return null;
        List<String> list = new ArrayList<>();
        FluidTankInfo[] tanks = getFluidInfos(handler);
        for (FluidTankInfo tank : tanks) {
            if (tank != null) {
                boolean isEmpty = tank.fluid == null;

                list.add(
                    SpecialChars.getRenderString(
                        "waila.fluid",
                        isEmpty ? "EMPTYFLUID"
                            : tank.fluid.getFluid()
                                .getName(),
                        isEmpty ? "EMPTYFLUID" : tank.fluid.getLocalizedName(),
                        String.valueOf(isEmpty ? 0 : tank.fluid.amount),
                        String.valueOf(tank.capacity)));
            }
        }
        return list;
    }

    public static FluidTankInfo[] getFluidInfos(IFluidHandler handler) {
        if (handler == null) return new FluidTankInfo[0];

        FluidTankInfo[] infos = handler.getTankInfo(ForgeDirection.UNKNOWN);
        return infos != null ? infos : new FluidTankInfo[0];
    }
}
