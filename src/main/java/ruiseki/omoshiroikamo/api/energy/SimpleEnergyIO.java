package ruiseki.omoshiroikamo.api.energy;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleEnergyIO implements EnergyIO {

    @Override
    public int store(int amount) {
        EnergyAccess access = sinkAccess();
        if (access == null) {
            return amount;
        }

        int rejected = access.insert(null, amount, false);
        return rejected;
    }

    @Override
    public int pull(int maxAmount, boolean simulate) {
        EnergyAccess access = sourceAccess();
        if (access == null) {
            return 0;
        }

        return access.extract(null, maxAmount, simulate);
    }

    @Override
    public @Nullable EnergyAccess sinkAccess() {
        return iterator();
    }

    @Override
    public @Nullable EnergyAccess sourceAccess() {
        return iterator();
    }

    @NotNull
    protected abstract EnergyAccess iterator();
}
