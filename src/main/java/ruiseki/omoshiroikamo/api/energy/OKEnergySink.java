package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyReceiver;

public class OKEnergySink extends SimpleEnergyIO {

    private final IEnergyReceiver receiver;

    public OKEnergySink(IEnergyReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected @NotNull EnergyAccess iterator() {
        return new EnergyAccess() {

            @Override
            public int extract(ForgeDirection side, int amount, boolean simulate) {
                return 0;
            }

            @Override
            public int insert(ForgeDirection side, int amount, boolean simulate) {
                if (!canConnectEnergy(side)) {
                    return 0;
                }
                return receiver.receiveEnergy(side, amount, simulate);
            }

            @Override
            public boolean canConnectEnergy(ForgeDirection side) {
                return receiver.canConnectEnergy(side);
            }
        };
    }
}
