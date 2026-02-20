package ruiseki.omoshiroikamo.core.gas;

import mekanism.api.gas.GasStack;

public interface IGasTank {

    GasStack getGas();

    int getStored();

    int getMaxGas();

    GasTankInfo getInfo();

    int receive(GasStack resource, boolean doTransfer);

    GasStack draw(int maxDrain, boolean doTransfer);
}
