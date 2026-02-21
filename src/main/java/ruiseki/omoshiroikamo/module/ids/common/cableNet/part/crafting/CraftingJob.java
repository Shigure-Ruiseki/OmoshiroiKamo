package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.crafting;

import java.util.Iterator;

public class CraftingJob {

    private final Iterator<CraftingStep> iterator;
    private CraftingStep current;

    public CraftingJob(CraftingPlan plan) {
        this.iterator = plan.getSteps()
            .iterator();
    }

    public boolean tick(CraftingNetwork network) {

        if (current == null) {
            if (!iterator.hasNext()) {
                return true;
            }
            current = iterator.next();
        }

        if (network.tryExecute(current)) {
            current = null;
        }

        return false;
    }
}
