package ruiseki.omoshiroikamo.module.cable.common.network.logic;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class LogicNetwork extends AbstractCableNetwork<ILogicNet> {

    public LogicNetwork() {
        super(ILogicNet.class);
    }

    @Override
    public void doNetworkTick() {
        // NO OP
    }
}
