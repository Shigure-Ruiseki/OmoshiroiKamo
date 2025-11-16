package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyProvider;

public class OKEnergySource extends SimpleEnergyIO {

    private final IEnergyProvider provider;

    public OKEnergySource(IEnergyProvider provider) {
        this.provider = provider;
    }

    @Override
    protected @NotNull EnergyAccess iterator() {
        return new EnergyAccess() {

            @Override
            public int extract(ForgeDirection side, int amount, boolean simulate) {
                if (!canConnectEnergy(side)) {
                    return 0;
                }
                return provider.extractEnergy(side, amount, simulate);
            }

            @Override
            public int insert(ForgeDirection side, int amount, boolean simulate) {
                return 0;
            }

            @Override
            public boolean canConnectEnergy(ForgeDirection side) {
                return provider.canConnectEnergy(side);
            }
        };
    }
}
