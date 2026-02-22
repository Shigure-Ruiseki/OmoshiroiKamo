package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting;

import java.util.ArrayDeque;
import java.util.Queue;

import ruiseki.omoshiroikamo.module.ids.common.item.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.item.part.crafting.interfacebus.CraftingInterface;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.ItemNetwork;

public class CraftingNetwork extends AbstractCableNetwork<ICraftingNet> {

    private final CraftingIndex craftingIndex = new CraftingIndex();
    private final Queue<CraftingJob> jobs = new ArrayDeque<>();

    public CraftingNetwork() {
        super(ICraftingNet.class);
    }

    public CraftingIndex getIndex() {
        return craftingIndex;
    }

    @Override
    public void doNetworkTick() {

        if (!jobs.isEmpty()) {

            CraftingJob job = jobs.peek();

            if (job.tick(this)) {
                jobs.poll();
            }
        }
    }

    public void submitJob(CraftingJob job) {
        jobs.add(job);
    }

    public boolean tryExecute(CraftingStep step) {

        for (ICraftingNet node : getNodes()) {

            if (node instanceof CraftingInterface ci) {

                for (ICraftingPattern pattern : ci.getPatterns()) {

                    if (pattern == step.getPattern()) {

                        return ci.execute(pattern, step.getTimes());
                    }
                }
            }
        }

        return false;
    }

    public ItemNetwork getItemNetwork() {
        return getGrid().getNetwork(ItemNetwork.class);
    }

}
