package ruiseki.omoshiroikamo.api.gas;

import mekanism.api.gas.GasStack;

public class GasTankInfo {

    public final GasStack fluid;
    public final int capacity;

    public GasTankInfo(GasStack fluid, int capacity) {
        this.fluid = fluid;
        this.capacity = capacity;
    }

    public GasTankInfo(IGasTank tank) {
        this.fluid = tank.getGas();
        this.capacity = tank.getMaxGas();
    }
}
