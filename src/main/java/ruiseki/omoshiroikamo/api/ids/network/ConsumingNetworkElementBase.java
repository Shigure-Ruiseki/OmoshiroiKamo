package ruiseki.omoshiroikamo.api.ids.network;

import ruiseki.omoshiroikamo.module.ids.common.network.NetworkElementBase;

/**
 * Base implementation for an energy consuming network element.
 * 
 * @author rubensworks
 */
public abstract class ConsumingNetworkElementBase<N extends INetwork> extends NetworkElementBase<N>
    implements IEnergyConsumingNetworkElement<N> {

    @Override
    public int getConsumptionRate() {
        return 0;
    }

    @Override
    public void postUpdate(N network, boolean updated) {

    }
}
