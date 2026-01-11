package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class ItemNetwork extends AbstractCableNetwork<IItemPart> {

    private final Map<ItemKey, Long> itemDB = new HashMap<>();

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
        rebuildDatabase();
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

    private void rebuildDatabase() {
        itemDB.clear();

        for (IItemPart part : interfaces) {
            if (part instanceof IQueries queries)
                queries.collectItems(itemDB);
        }
    }

    public Map<ItemKey, Long> getItemDB() {
        return itemDB;
    }
}
