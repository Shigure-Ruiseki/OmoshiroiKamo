package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexUtils;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;

public class ItemIndexSH extends SyncHandler {

    public static final int SYNC_FULL = 0;
    public static final int SYNC_DELTA = 1;

    private final Supplier<ItemNetwork> network;
    private final ItemIndexClient clientIndex;

    public ItemIndexSH(Supplier<ItemNetwork> network, ItemIndexClient clientIndex) {
        this.network = network;
        this.clientIndex = clientIndex;
    }

    public void requestSync(int clientVersion) {
        ItemNetwork net = network.get();
        if (net == null) return;

        int serverVer = net.getIndexVersion();

        if (clientVersion < 0) {
            syncToClient(SYNC_FULL, b -> ItemIndexUtils.writeFull(b, net));
            return;
        }

        if (clientVersion != serverVer) {
            syncToClient(
                SYNC_DELTA,
                b -> ItemIndexUtils.writeDelta(b, net.getLastSnapshot(), net.getIndex(), serverVer));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

        if (id == SYNC_FULL) {
            int version = buf.readInt();
            clientIndex.replace(ItemIndexUtils.readMap(buf), version);
            return;
        }

        if (id == SYNC_DELTA) {
            int version = buf.readInt();
            clientIndex.applyDelta(ItemIndexUtils.readMap(buf), ItemIndexUtils.readKeys(buf), version);
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

    }
}
