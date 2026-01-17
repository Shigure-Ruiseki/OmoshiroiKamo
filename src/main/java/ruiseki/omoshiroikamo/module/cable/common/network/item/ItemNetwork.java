package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class ItemNetwork extends AbstractCableNetwork<IItemPart> {

    private final ItemIndex allIndex = new ItemIndex();
    private final ItemIndex lastAllSnapshot = new ItemIndex();

    private final Int2IntOpenHashMap channelVersions = new Int2IntOpenHashMap();
    private final Int2ObjectOpenHashMap<ItemIndex> channelIndexes = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<ItemIndex> lastChannelSnapshots = new Int2ObjectOpenHashMap<>();

    private boolean allDirty = true;
    private final Int2BooleanOpenHashMap channelDirty = new Int2BooleanOpenHashMap();

    private int indexChannel = -1;
    private int indexVersion = 0;

    public List<IItemPart> inputs;
    public List<IItemPart> interfaces;
    public List<IItemPart> outputs;

    public ItemNetwork() {
        super(IItemPart.class);
    }

    @Override
    public void doNetworkTick() {
        super.doNetworkTick();

        if (parts.isEmpty()) return;

        rebuildPartLists();

        if (allDirty) {
            rebuildIndex();
        }
    }

    private void rebuildPartLists() {
        inputs = new ArrayList<>();
        interfaces = new ArrayList<>();
        outputs = new ArrayList<>();

        for (IItemPart part : parts) {
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

        for (IItemPart part : parts) {
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

    public ItemStack extract(ItemStackKey key, int amount) {
        if (amount <= 0) return null;
        if (interfaces == null || interfaces.isEmpty()) return null;

        ItemStack result = null;
        int remaining = amount;

        for (IItemPart part : interfaces) {
            if (remaining <= 0) break;
            if (!(part instanceof IItemQueryable interfaceBus)) continue;

            ItemStack got = interfaceBus.extract(key, remaining);
            if (got == null || got.stackSize <= 0) continue;

            remaining -= got.stackSize;
            result = merge(result, got);
        }

        if (result != null) {
            markDirty();
        }

        return result;
    }

    public ItemStack insert(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return null;
        if (interfaces == null || interfaces.isEmpty()) return stack;

        ItemStack remaining = stack.copy();

        for (IItemPart part : interfaces) {
            if (!(part instanceof IItemQueryable bus)) continue;

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

        markDirty();
        return null;
    }

    private static ItemStack merge(ItemStack a, ItemStack b) {
        if (a == null) return b;
        a.stackSize += b.stackSize;
        return a;
    }

}
