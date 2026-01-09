package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class EnergyNetwork extends AbstractCableNetwork<IEnergyPart> {

    public List<IEnergyPart> inputs;
    public List<IEnergyPart> interfaces;
    public List<IEnergyPart> outputs;

    public EnergyNetwork() {
        super(IEnergyPart.class);
    }

    @Override
    public void doNetworkTick() {
        super.doNetworkTick();

        if (parts.isEmpty()) return;
        inputs = new ArrayList<>();
        interfaces = new ArrayList<>();
        outputs = new ArrayList<>();

        for (IEnergyPart part : parts) {
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
