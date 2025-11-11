package ruiseki.omoshiroikamo.common.init;

import net.minecraftforge.fluids.IFluidTank;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ModFluids {

    public static String toCapactityString(IFluidTank tank) {
        if (tank == null) {
            return "0/0 " + MB();
        }
        return tank.getFluidAmount() + "/" + tank.getCapacity() + " " + MB();
    }

    public static String MB() {
        return LibMisc.LANG.localize("fluid.millibucket.abr");
    }

    public static void preInit() {}

}
