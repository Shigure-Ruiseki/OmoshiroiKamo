package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy;

import ruiseki.omoshiroikamo.api.ids.ICablePart;

public interface IEnergyPart extends ICablePart, IEnergyNet {

    int getTransferLimit();
}
