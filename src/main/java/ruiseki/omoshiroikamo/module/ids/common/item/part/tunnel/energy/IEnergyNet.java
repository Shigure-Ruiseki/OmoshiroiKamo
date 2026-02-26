package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy;

import ruiseki.omoshiroikamo.api.ids.ICableNode;

public interface IEnergyNet extends ICableNode {

    default EnergyNetwork getEnergyNetwork() {
        return getCable() != null ? (EnergyNetwork) getCable().getNetwork(IEnergyNet.class) : null;
    }
}
