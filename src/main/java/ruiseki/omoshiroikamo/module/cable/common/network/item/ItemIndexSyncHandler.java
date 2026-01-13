package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIndexSyncHandler extends SyncHandler {

    public static final int SYNC_FULL = 0;

    private final Supplier<ItemNetwork> network;
    private final ItemIndexClient clientIndex;

    public ItemIndexSyncHandler(Supplier<ItemNetwork> network, ItemIndexClient clientIndex) {
        this.network = network;
        this.clientIndex = clientIndex;
    }

    public void requestSync(int clientVersion) {
        ItemNetwork net = network.get();
        if (net == null) return;

        if (clientVersion != net.getIndexVersion()) {
            syncToClient(SYNC_FULL, buf -> ItemIndexUtils.write(buf, net));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readOnClient(int id, PacketBuffer buf) throws IOException {
        if (id == SYNC_FULL) {
            int[] version = new int[1];
            var map = ItemIndexUtils.read(buf, version);
            clientIndex.update(map, version[0]);
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

    }
}
