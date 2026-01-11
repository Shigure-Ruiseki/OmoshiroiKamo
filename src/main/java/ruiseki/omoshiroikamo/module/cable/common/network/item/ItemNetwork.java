package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;

public class ItemNetwork extends AbstractCableNetwork<IItemPart> {

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
}
