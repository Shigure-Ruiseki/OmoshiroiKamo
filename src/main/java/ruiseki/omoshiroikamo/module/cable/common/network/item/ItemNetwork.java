package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.interfacebus.ItemInterfaceBus;

public class ItemNetwork extends AbstractCableNetwork<IItemPart> {

    private final ItemIndex index = new ItemIndex();
    private final ItemIndex lastSnapshot = new ItemIndex();

    private int indexVersion = 0;
    private boolean dirty = true;

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

        if (dirty) {
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
        dirty = true;
    }

    private void rebuildIndex() {
        index.clear();
        lastSnapshot.mergeFrom(index);

        Set<Long> visited = new HashSet<>();

        for (IItemPart part : parts) {
            if (!(part instanceof IItemQueryable q)) continue;

            TileEntity te = part.getTargetTE();
            if (te == null) continue;

            long posKey = new BlockPos(te.xCoord, te.yCoord, te.zCoord).toLong();
            if (!visited.add(posKey)) continue;

            q.collectItems(index);
        }

        indexVersion++;
        dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getIndexVersion() {
        return indexVersion;
    }

    public ItemIndex getIndex() {
        return index;
    }

    public ItemIndex getLastSnapshot() {
        return lastSnapshot;
    }

    public ItemStack extract(ItemStackKey key, int amount) {
        if (amount <= 0) return null;
        if (interfaces == null || interfaces.isEmpty()) return null;

        ItemStack result = null;
        int remaining = amount;

        for (IItemPart part : interfaces) {
            if (remaining <= 0) break;
            if (!(part instanceof ItemInterfaceBus interfaceBus)) continue;

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
            if (!(part instanceof ItemInterfaceBus bus)) continue;

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
