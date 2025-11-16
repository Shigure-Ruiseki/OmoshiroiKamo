package ruiseki.omoshiroikamo.api.energy;

import org.jetbrains.annotations.Nullable;

public class WrappedEnergyIO implements EnergyIO {

    @Nullable
    public final EnergySource source;
    @Nullable
    public final EnergySink sink;

    public WrappedEnergyIO(@Nullable EnergySource source, @Nullable EnergySink sink) {
        this.source = source;
        this.sink = sink;
    }

    @Override
    public void resetSink() {
        if (sink != null) {
            sink.resetSink();
        }
    }

    @Override
    public int store(int amount) {
        return sink == null ? amount : sink.store(amount);
    }

    @Override
    public EnergyIO then(EnergySink next) {
        return new WrappedEnergyIO(source, EnergySink.chain(sink, next));
    }

    @Override
    public void resetSource() {
        if (source != null) {
            source.resetSource();
        }
    }

    @Override
    public int pull(int maxAmount, boolean simulate) {
        return source == null ? null : source.pull(maxAmount, simulate);
    }
}
