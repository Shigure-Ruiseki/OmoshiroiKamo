package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class ItemNetwork extends AbstractCableNetwork<IItemPart> {

    private final ItemIndex index = new ItemIndex();
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

        for (IItemPart part : parts) {
            if (part instanceof IItemQueryable q) {
                q.collectItems(index);
            }
        }

        indexVersion++;
        dirty = false;
    }

    public int getIndexVersion() {
        return indexVersion;
    }

    public Map<ItemStackKey, Integer> getItemIndexView() {
        return index.view();
    }

    public Map<ItemStackKey, Integer> getItemIndexSnapshot() {
        return index.snapshot();
    }
}
