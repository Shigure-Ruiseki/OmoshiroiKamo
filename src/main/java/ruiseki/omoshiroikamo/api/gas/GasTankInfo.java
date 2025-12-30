package ruiseki.omoshiroikamo.api.gas;

import mekanism.api.gas.GasStack;

public class GasTankInfo {

    public final GasStack gas;
    public final int capacity;

    public GasTankInfo(GasStack fluid, int capacity) {
        this.gas = fluid;
        this.capacity = capacity;
    }

    public GasTankInfo(IGasTank tank) {
        this.gas = tank.getGas();
        this.capacity = tank.getMaxGas();
    }
}
