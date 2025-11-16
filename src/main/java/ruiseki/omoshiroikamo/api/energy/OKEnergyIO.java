package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyHandler;

public class OKEnergyIO extends SimpleEnergyIO {

    private final IEnergyHandler handler;

    public OKEnergyIO(IEnergyHandler handler) {
        this.handler = handler;
    }

    @Override
    protected @NotNull EnergyAccess iterator() {
        return new EnergyAccess() {

            @Override
            public int extract(ForgeDirection side, int amount, boolean simulate) {
                return handler.extractEnergy(side, amount, simulate);
            }

            @Override
            public int insert(ForgeDirection side, int amount, boolean simulate) {
                return handler.receiveEnergy(side, amount, simulate);
            }

        };
    }
}
