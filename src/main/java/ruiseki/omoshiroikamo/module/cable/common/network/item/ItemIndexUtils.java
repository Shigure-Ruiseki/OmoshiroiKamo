package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class ItemIndexUtils {

    public static void write(PacketBuffer buf, ItemNetwork net) throws IOException {
        buf.writeInt(net.getIndexVersion());

        Object2IntOpenHashMap<ItemStackKey> map = net.index.view();
        buf.writeVarIntToBuffer(map.size());

        for (Object2IntMap.Entry<ItemStackKey> e : map.object2IntEntrySet()) {
            e.getKey()
                .write(buf);
            buf.writeVarIntToBuffer(e.getIntValue());
        }
    }

    public static Object2IntOpenHashMap<ItemStackKey> read(PacketBuffer buf, int[] versionOut) throws IOException {

        int version = buf.readInt();
        versionOut[0] = version;

        int size = buf.readVarIntFromBuffer();
        Object2IntOpenHashMap<ItemStackKey> map = new Object2IntOpenHashMap<>(size);

        for (int i = 0; i < size; i++) {
            ItemStackKey key = ItemStackKey.read(buf);
            int count = buf.readVarIntFromBuffer();
            map.put(key, count);
        }

        return map;
    }
}
