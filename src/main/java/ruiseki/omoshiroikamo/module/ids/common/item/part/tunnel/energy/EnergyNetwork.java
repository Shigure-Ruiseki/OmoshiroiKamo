package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.energy;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.module.ids.common.network.AbstractCableNetwork;

public class EnergyNetwork extends AbstractCableNetwork<IEnergyNet> {

    public List<IEnergyNet> inputs;
    public List<IEnergyNet> interfaces;
    public List<IEnergyNet> outputs;

    public EnergyNetwork() {
        super(IEnergyNet.class);
    }

    @Override
    public void doNetworkTick() {

        if (nodes.isEmpty()) return;
        interfaces = new ArrayList<>();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();

        for (IEnergyNet part : nodes) {
            switch (part.getIO()) {
                case INPUT -> inputs.add(part);
                case OUTPUT -> outputs.add(part);
                case BOTH -> interfaces.add(part);
            }
        }

        inputs.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        interfaces.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        outputs.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
    }
}
