package ruiseki.omoshiroikamo.module.cable.common.network.energy;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class EnergyNetwork extends AbstractCableNetwork<IEnergyPart> {

    public EnergyNetwork() {
        super(IEnergyPart.class);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EnergyNetwork[");
        for (IEnergyPart part : parts) {
            sb.append(part.getId())
                .append("@")
                .append(part.getSide())
                .append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
