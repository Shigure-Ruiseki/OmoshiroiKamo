package ruiseki.omoshiroikamo.module.cows;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.module.cows.common.network.PacketStall;

public class CowsCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);
        packetHandler.register(PacketStall.class);
    }
}
