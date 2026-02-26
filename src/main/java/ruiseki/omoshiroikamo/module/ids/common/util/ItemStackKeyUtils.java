package ruiseki.omoshiroikamo.module.ids.common.util;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.ItemIndex;

public final class ItemStackKeyUtils {

    public static void writeDelta(PacketBuffer buf, ItemIndex oldIdx, ItemIndex newIdx, int version)
        throws IOException {

        buf.writeInt(version);

        Object2IntOpenHashMap<ItemStackKey> added = new Object2IntOpenHashMap<>();

        ObjectOpenHashSet<ItemStackKey> removed = new ObjectOpenHashSet<>();

        // added / changed
        for (Object2IntMap.Entry<ItemStackKey> e : newIdx.view()
            .object2IntEntrySet()) {
            ItemStackKey key = e.getKey();
            int newVal = e.getIntValue();
            if (oldIdx.get(key) != newVal) {
                added.put(key, newVal);
            }
        }

        // removed
        for (ItemStackKey key : oldIdx.view()
            .keySet()) {
            if (!newIdx.view()
                .containsKey(key)) {
                removed.add(key);
            }
        }

        writeMap(buf, added);
        writeKeys(buf, removed);
    }

    public static void writeMap(PacketBuffer buf, Object2IntMap<ItemStackKey> map) throws IOException {

        buf.writeVarIntToBuffer(map.size());

        for (Object2IntMap.Entry<ItemStackKey> e : map.object2IntEntrySet()) {
            e.getKey()
                .write(buf);
            buf.writeVarIntToBuffer(e.getIntValue());
        }
    }

    public static void writeKeys(PacketBuffer buf, ObjectOpenHashSet<ItemStackKey> keys) throws IOException {

        buf.writeVarIntToBuffer(keys.size());

        for (ItemStackKey key : keys) {
            key.write(buf);
        }
    }

    public static Object2IntOpenHashMap<ItemStackKey> readMap(PacketBuffer buf) throws IOException {

        int size = buf.readVarIntFromBuffer();
        Object2IntOpenHashMap<ItemStackKey> map = new Object2IntOpenHashMap<>(size);

        for (int i = 0; i < size; i++) {
            ItemStackKey key = ItemStackKey.read(buf);
            int val = buf.readVarIntFromBuffer();
            map.put(key, val);
        }
        return map;
    }

    public static ObjectOpenHashSet<ItemStackKey> readKeys(PacketBuffer buf) throws IOException {

        int size = buf.readVarIntFromBuffer();
        ObjectOpenHashSet<ItemStackKey> set = new ObjectOpenHashSet<>(size);

        for (int i = 0; i < size; i++) {
            set.add(ItemStackKey.read(buf));
        }
        return set;
    }
}
