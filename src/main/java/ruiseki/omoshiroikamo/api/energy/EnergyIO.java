package ruiseki.omoshiroikamo.api.energy;

public interface EnergyIO extends EnergySource, EnergySink {

    default EnergyIO then(EnergySink next) {
        return new WrappedEnergyIO(this, EnergySink.chain(this, next));
    }
}
