package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIndexSyncHandler extends SyncHandler {

    public static final int SYNC_FULL = 0;

    private final Supplier<ItemNetwork> network;

    public ItemIndexSyncHandler(Supplier<ItemNetwork> network) {
        this.network = network;
    }

    public void requestSync() {
        ItemNetwork net = network.get();
        if (net == null) return;

        syncToClient(SYNC_FULL, buf -> ItemIndexIO.write(buf, net));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readOnClient(int id, PacketBuffer buf) throws IOException {
        if (id == SYNC_FULL) {
            int[] version = new int[1];
            Map<ItemStackKey, Integer> db = ItemIndexIO.read(buf, version);
            ItemIndexClient.INSTANCE.update(db, version[0]);
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {

    }
}
