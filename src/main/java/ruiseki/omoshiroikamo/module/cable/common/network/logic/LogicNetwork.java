package ruiseki.omoshiroikamo.module.cable.common.network.logic;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class LogicNetwork extends AbstractCableNetwork<ILogicNet> {

    public LogicNetwork() {
        super(ILogicNet.class);
    }

    @Override
    public void doNetworkTick() {
        // NO OP
    }

    public ILogicNet getNodeAt(int x, int y, int z, ForgeDirection side) {
        for (ILogicNet node : nodes) {
            BlockPos pos = node.getPos();

            if (pos.x == x && pos.y == y && pos.z == z && node.getSide() == side) {
                return node;
            }
        }
        return null;
    }

    public LogicValue getValueAt(int x, int y, int z, ForgeDirection side) {
        ILogicNet node = getNodeAt(x, y, z, side);
        return node != null ? node.getLogicValue() : null;
    }
}
