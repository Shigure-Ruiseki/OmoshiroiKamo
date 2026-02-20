package ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.interfacebus.IItemInterface;

public class ItemNetwork extends AbstractCableNetwork<IItemNet> {

    private final ItemIndex allIndex = new ItemIndex();
    private final ItemIndex lastAllSnapshot = new ItemIndex();

    private final Int2IntOpenHashMap channelVersions = new Int2IntOpenHashMap();
    private final Int2ObjectOpenHashMap<ItemIndex> channelIndexes = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<ItemIndex> lastChannelSnapshots = new Int2ObjectOpenHashMap<>();

    private boolean allDirty = true;
    private final Int2BooleanOpenHashMap channelDirty = new Int2BooleanOpenHashMap();

    private int indexChannel = -1;
    private int indexVersion = 0;

    public List<IItemNet> inputs;
    public List<IItemNet> interfaces;
    public List<IItemNet> outputs;

    public ItemNetwork() {
        super(IItemNet.class);
    }

    @Override
    public void doNetworkTick() {

        if (nodes.isEmpty()) return;

        rebuildPartLists();

        if (allDirty) {
            rebuildIndex();
        }
    }

    private void rebuildPartLists() {
        inputs = new ArrayList<>();
        interfaces = new ArrayList<>();
        outputs = new ArrayList<>();

        for (IItemNet part : nodes) {
            switch (part.getIO()) {
                case INPUT -> inputs.add(part);
                case OUTPUT -> outputs.add(part);
                case BOTH -> interfaces.add(part);
            }
        }

        inputs.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        interfaces.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
        outputs.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
    }

    public void markDirty() {
        markDirty(-1);
    }

    public void markDirty(int channel) {
        allDirty = true;

        if (channel != -1) {
            channelDirty.put(channel, true);
        }
    }

    private void rebuildIndex() {
        if (allDirty) {
            lastAllSnapshot.clear();
            lastAllSnapshot.mergeFrom(allIndex);
            allIndex.clear();
        }

        for (int ch : channelDirty.keySet()) {
            ItemIndex prev = channelIndexes.get(ch);
            if (prev != null) {
                ItemIndex snap = new ItemIndex();
                snap.mergeFrom(prev);
                lastChannelSnapshots.put(ch, snap);
                prev.clear();
            }
        }

        for (IItemNet part : nodes) {
            if (!(part instanceof IItemQueryable q)) continue;

            int ch = part.getChannel();

            if (allDirty) {
                q.collectItems(allIndex);
            }

            if (ch != -1 && channelDirty.get(ch)) {
                ItemIndex idx = channelIndexes.computeIfAbsent(ch, k -> new ItemIndex());
                q.collectItems(idx);
            }
        }

        for (int ch : channelDirty.keySet()) {
            channelVersions.put(ch, channelVersions.getOrDefault(ch, 0) + 1);
        }

        indexVersion++;
        allDirty = false;
        channelDirty.clear();
    }

    public int getIndexVersion() {
        if (indexChannel == -1) return indexVersion;
        return channelVersions.getOrDefault(indexChannel, indexVersion);
    }

    public ItemIndex getIndex() {
        if (indexChannel == -1) {
            return allIndex;
        }
        return channelIndexes.getOrDefault(indexChannel, ItemIndex.EMPTY);
    }

    public ItemIndex getLastSnapshot() {
        if (indexChannel == -1) {
            return lastAllSnapshot;
        }
        return lastChannelSnapshots.getOrDefault(indexChannel, ItemIndex.EMPTY);
    }

    public void setIndexChannel(int indexChanel) {
        this.indexChannel = indexChanel;
    }

    public int getIndexChannel() {
        return indexChannel;
    }

    public ItemStack extract(ItemStack required, int amount) {
        return extract(required, amount, -1);
    }

    public ItemStack insert(ItemStack stack) {
        return insert(stack, -1);
    }

    public ItemStack extract(ItemStack required, int amount, int channel) {
        if (amount <= 0) return null;
        if (interfaces == null || interfaces.isEmpty()) return null;

        ItemStack result = null;
        int remaining = amount;

        for (IItemNet part : interfaces) {
            if (channel != -1 && channel != part.getChannel()) continue;
            if (remaining <= 0) break;
            if (!(part instanceof IItemInterface interfaceBus)) continue;

            ItemStack got = interfaceBus.extract(required, remaining);
            if (got == null || got.stackSize <= 0) continue;

            remaining -= got.stackSize;
            result = ItemUtils.merge(result, got);
        }

        if (result != null) {
            markDirty(channel);
        }

        return result;
    }

    public ItemStack insert(ItemStack stack, int channel) {
        if (stack == null || stack.stackSize <= 0) return null;
        if (interfaces == null || interfaces.isEmpty()) return stack;

        ItemStack remaining = stack.copy();

        for (IItemNet part : interfaces) {
            if (channel != -1 && channel != part.getChannel()) continue;
            if (!(part instanceof IItemInterface bus)) continue;

            ItemStack leftover = bus.insert(remaining);
            if (leftover == null || leftover.stackSize <= 0) {
                remaining = null;
                break;
            } else {
                remaining = leftover;
            }
        }

        if (remaining != null) {
            return remaining;
        }

        markDirty(channel);
        return null;
    }

    public List<IItemNet> getInterfacesForChannel(int channel) {
        if (interfaces == null || interfaces.isEmpty()) return Collections.emptyList();

        if (channel == -1) return interfaces;

        List<IItemNet> list = new ArrayList<>();
        for (IItemNet part : interfaces) {
            if (part.getChannel() == channel) {
                list.add(part);
            }
        }
        return list;
    }

    public long getAmount(Object key) {

        if (!(key instanceof ItemStackKey itemKey)) {
            return 0;
        }

        return getIndex().get(itemKey);
    }

}
