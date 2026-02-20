package ruiseki.omoshiroikamo.core.gas;

import net.minecraftforge.common.util.ForgeDirection;

import mekanism.api.gas.GasStack;

public interface IGasHandler extends mekanism.api.gas.IGasHandler {

    @Override
    default GasStack drawGas(ForgeDirection forgeDirection, int i) {
        return drawGas(forgeDirection, i, false);
    }

    @Override
    default int receiveGas(ForgeDirection forgeDirection, GasStack gasStack) {
        return receiveGas(forgeDirection, gasStack, false);
    }

    GasTankInfo[] getTankInfo(ForgeDirection from);
}
