package ruiseki.omoshiroikamo.module.backpack;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.network.PacketHandler;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.module.backpack.common.network.PacketBackpackNBT;

public class BackpackCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketBackpackNBT.class);
    }
}
