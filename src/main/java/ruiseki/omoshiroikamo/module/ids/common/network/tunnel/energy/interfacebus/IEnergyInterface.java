package ruiseki.omoshiroikamo.module.ids.common.network.tunnel.energy.interfacebus;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyInterface {

    int extract(int amount, boolean simulate);

    int insert(int amount, boolean simulate);

    boolean canConnect();

    ForgeDirection getSide();
}
