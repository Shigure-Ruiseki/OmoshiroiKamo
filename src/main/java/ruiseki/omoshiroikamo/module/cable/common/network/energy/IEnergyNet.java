package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import ruiseki.omoshiroikamo.api.cable.ICableNode;

public interface IEnergyNet extends ICableNode {

    default EnergyNetwork getEnergyNetwork() {
        return getCable() != null ? (EnergyNetwork) getCable().getNetwork(IEnergyNet.class) : null;
    }
}
