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
    public String toString() {
        StringBuilder sb = new StringBuilder("EnergyNetwork{");
        sb.append("parts=")
            .append(parts.size())
            .append(", [");

        boolean first = true;
        for (IEnergyPart part : parts) {
            if (!first) sb.append(", ");
            first = false;

            sb.append(part.toString());
        }

        sb.append("]}");
        return sb.toString();
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
                case NONE -> interfaces.add(part);
            }
        }
    }
}
