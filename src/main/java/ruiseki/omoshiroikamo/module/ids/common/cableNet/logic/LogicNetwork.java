package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.datastructure.BlockPos;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKey;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.part.ILogicReader;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.ILogicValue;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value.LogicValues;

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

    public ILogicValue readAt(int x, int y, int z, ForgeDirection side, LogicKey key) {
        ILogicNet node = getNodeAt(x, y, z, side);
        if (node instanceof ILogicReader reader) {
            return reader.read(key);
        }
        return LogicValues.NULL;
    }

}
