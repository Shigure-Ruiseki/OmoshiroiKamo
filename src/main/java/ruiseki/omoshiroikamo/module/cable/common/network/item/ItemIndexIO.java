package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.PacketBuffer;

public class ItemIndexIO {

    public static void write(PacketBuffer buf, ItemNetwork net) throws IOException {
        buf.writeVarIntToBuffer(net.getIndexVersion());
        writeIndex(buf, net.index);
    }

    private static void writeIndex(PacketBuffer buf, ItemIndex index) throws IOException {
        Map<ItemStackKey, Integer> map = index.snapshot();
        buf.writeVarIntToBuffer(map.size());
        for (var e : map.entrySet()) {
            e.getKey()
                .write(buf);
            buf.writeVarIntToBuffer(e.getValue());
        }
    }

    public static Map<ItemStackKey, Integer> read(PacketBuffer buf, int[] outVersion) throws IOException {

        outVersion[0] = buf.readVarIntFromBuffer();
        int size = buf.readVarIntFromBuffer();

        Map<ItemStackKey, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            ItemStackKey key = ItemStackKey.read(buf);
            int amount = buf.readVarIntFromBuffer();
            map.put(key, amount);
        }
        return map;
    }
}
